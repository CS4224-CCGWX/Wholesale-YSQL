package transactions;

import utils.IO;
import utils.OutputFormatter;
import utils.PreparedQueries;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentTransaction extends AbstractTransaction{

    private int warehouseId;
    private int districtId;
    private int customerId;

    private double payment;

    private static OutputFormatter outputFormatter = new OutputFormatter();

    private static final String delimiter = "\n";

    private PreparedStatement formattedGetWarehouseAddressAndYtd, formattedUpdateWarehouseYearToDateAmount,
            formattedGetDistrictAddressAndYtd, formattedUpdateDistrictYearToDateAmount,
            formattedGetFullCustomerInfo, formattedUpdateCustomerPaymentInfo;


    public PaymentTransaction(Connection connection, IO io, int cwid, int cdid, int cid, double p) throws SQLException {
        super(connection, io);
        customerId = cid;
        warehouseId = cwid;
        districtId = cdid;
        payment = p;

        formattedGetWarehouseAddressAndYtd = connection.prepareStatement(PreparedQueries.getWarehouseAddressAndYtd);
        formattedUpdateWarehouseYearToDateAmount = connection.prepareStatement(PreparedQueries.updateWarehouseYearToDateAmount);
        formattedGetDistrictAddressAndYtd = connection.prepareStatement(PreparedQueries.getDistrictAddressAndYtd);
        formattedUpdateDistrictYearToDateAmount = connection.prepareStatement(PreparedQueries.updateDistrictYearToDateAmount);
        formattedGetFullCustomerInfo = connection.prepareStatement(PreparedQueries.getFullCustomerInfo);
        formattedUpdateCustomerPaymentInfo = connection.prepareStatement(PreparedQueries.updateCustomerPaymentInfo);
    }


    public void execute() throws SQLException {

        // 1.  Update the warehouse C W ID by incrementing W YTD by PAYMENT

        // Output Customer Last Order for each item
        formattedGetWarehouseAddressAndYtd.setInt(1, warehouseId);

        ResultSet warehouseResult = this.executeQuery(formattedGetWarehouseAddressAndYtd);

        if (!warehouseResult.next()) {
            error("formattedGetWarehouseAddressAndYtd");
            return ;
        }
        double warehouseYtd = warehouseResult.getBigDecimal("W_YTD").doubleValue();
        warehouseYtd += payment;

        formattedUpdateWarehouseYearToDateAmount.setBigDecimal(1, BigDecimal.valueOf(warehouseYtd));
        formattedUpdateWarehouseYearToDateAmount.setInt(2, warehouseId);
        this.executeUpdate(formattedUpdateWarehouseYearToDateAmount);


        formattedGetDistrictAddressAndYtd.setInt(1, warehouseId);
        formattedGetDistrictAddressAndYtd.setInt(2, districtId);
        ResultSet districtResult = this.executeQuery(formattedGetDistrictAddressAndYtd);

        if (!districtResult.next()) {
            error("formattedGetDistrictAddressAndYtd");
            return ;
        }
        double districtYtd = districtResult.getBigDecimal("D_YTD").doubleValue();
        districtYtd += payment;

        // 2. Update the district (C W ID,C D ID) by incrementing D YTD by PAYMENT
        formattedUpdateDistrictYearToDateAmount.setBigDecimal(1, BigDecimal.valueOf(districtYtd));
        formattedUpdateDistrictYearToDateAmount.setInt(2, warehouseId);
        formattedUpdateDistrictYearToDateAmount.setInt(3, districtId);
        this.executeUpdate(formattedUpdateDistrictYearToDateAmount);


        /*
        3. Update the customer (C W ID, C D ID, C ID) as follows:
            • Decrement C BALANCE by PAYMENT
            • Increment C YTD PAYMENT by PAYMENT
            • Increment C PAYMENT CNT by 1
         */
        formattedGetFullCustomerInfo.setInt(1, warehouseId);
        formattedGetFullCustomerInfo.setInt(2, districtId);
        formattedGetFullCustomerInfo.setInt(3, customerId);
        ResultSet customerRes = this.executeQuery(formattedGetFullCustomerInfo);
        if (!customerRes.next()) {
            error("formattedGetFullCustomerInfo");
            return ;
        }

        double customerBalance = customerRes.getBigDecimal("C_BALANCE").doubleValue();
        customerBalance -= payment;
        float customerYtd = customerRes.getFloat("C_YTD_PAYMENT");
        customerYtd += payment;

        formattedUpdateCustomerPaymentInfo.setBigDecimal(1, BigDecimal.valueOf(customerBalance));
        formattedUpdateCustomerPaymentInfo.setFloat(2, customerYtd);
        formattedUpdateCustomerPaymentInfo.setInt(3, warehouseId);
        formattedUpdateCustomerPaymentInfo.setInt(4, districtId);
        formattedUpdateCustomerPaymentInfo.setInt(5, customerId);
        this.executeUpdate(formattedUpdateCustomerPaymentInfo);

        // Output Customer Information

        StringBuilder sb = new StringBuilder();

        /*
         *  1. Customer’s identifier (C W ID, C D ID, C ID), name (C FIRST, C MIDDLE, C LAST), address
         *    (C STREET 1, C STREET 2, C CITY, C STATE, C ZIP), C PHONE, C SINCE, C CREDIT,
         *     C CREDIT LIM, C DISCOUNT, C BALANCE
         */
        sb.append("********** Payment Transaction *********\n");
        sb.append(outputFormatter.formatFullCustomerInfo(customerRes, customerBalance));
        sb.append(delimiter);

        // 2. Warehouse’s address (W STREET 1, W STREET 2, W CITY, W STATE, W ZIP)
        sb.append(outputFormatter.formatWarehouseAddress(warehouseResult));
        sb.append(delimiter);

        // 3. District’s address (D STREET 1, D STREET 2, D CITY, D STATE, D ZIP)
        sb.append(outputFormatter.formatDistrictAddress(districtResult));
        sb.append(delimiter);

        // 4. Payment amount PAYMENT
        sb.append(String.format("Payment: %.2f", payment));
        sb.append(delimiter);

        io.println(sb);
    }

    public String toString() {
        return String.format("Payment transaction info: warehouse: %d, district: %d, customer: %d, payment: %f", warehouseId, districtId, customerId, payment);
    }

    public void error(String s) {
        System.err.println("[Error]: Payment " + s + " are missing");
    }
}
