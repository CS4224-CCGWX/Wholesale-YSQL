package transactions;

import utils.IO;
import utils.OutputFormatter;
import utils.PreparedQueries;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentTransaction extends AbstractTransaction{

    private int warehouseId;
    private int districtId;
    private int customerId;

    private double payment;

    private static OutputFormatter outputFormatter = new OutputFormatter();

    private static final String delimiter = "\n";
    int counter = 1;

    public PaymentTransaction(Connection connection, IO io, int cwid, int cdid, int cid, double p) throws SQLException {
        super(connection, io);
        customerId = cid;
        warehouseId = cwid;
        districtId = cdid;
        payment = p;
    }


    public void execute() throws SQLException {

        try {
            counter = counter + 1;
            connection.setReadOnly(false);
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            // 1.  Update the warehouse C W ID by incrementing W YTD by PAYMENT

            // Output Customer Last Order for each item
           PreparedQueries.getWarehouseAddressAndYtd.setInt(1, warehouseId);
//
//            double warehouseYtd = warehouseResult.getBigDecimal("W_YTD").doubleValue();
//            warehouseYtd += payment;

            PreparedQueries.updateWarehouseYearToDateAmount.setBigDecimal(1, BigDecimal.valueOf(payment));
            PreparedQueries.updateWarehouseYearToDateAmount.setInt(2, warehouseId);
            this.executeUpdate(PreparedQueries.updateWarehouseYearToDateAmount);


            // 2. Update the district (C W ID,C D ID) by incrementing D YTD by PAYMENT
            PreparedQueries.updateDistrictYearToDateAmount.setBigDecimal(1, BigDecimal.valueOf(payment));
            PreparedQueries.updateDistrictYearToDateAmount.setInt(2, warehouseId);
            PreparedQueries.updateDistrictYearToDateAmount.setInt(3, districtId);
            this.executeUpdate(PreparedQueries.updateDistrictYearToDateAmount);


        /*
        3. Update the customer (C W ID, C D ID, C ID) as follows:
            • Decrement C BALANCE by PAYMENT
            • Increment C YTD PAYMENT by PAYMENT
            • Increment C PAYMENT CNT by 1
         */

            PreparedQueries.updateCustomerPaymentInfo.setBigDecimal(1, BigDecimal.valueOf(payment));
            PreparedQueries.updateCustomerPaymentInfo.setBigDecimal(2, BigDecimal.valueOf(payment));
            PreparedQueries.updateCustomerPaymentInfo.setInt(3, warehouseId);
            PreparedQueries.updateCustomerPaymentInfo.setInt(4, districtId);
            PreparedQueries.updateCustomerPaymentInfo.setInt(5, customerId);
            this.executeUpdate(PreparedQueries.updateCustomerPaymentInfo);


            PreparedQueries.getDistrictAddressAndYtd.setInt(1, warehouseId);
            PreparedQueries.getDistrictAddressAndYtd.setInt(2, districtId);
            ResultSet districtResult = this.executeQuery(PreparedQueries.getDistrictAddressAndYtd);

            if (!districtResult.next()) {
                error("formattedGetDistrictAddressAndYtd");
                throw new SQLException();
            }

            ResultSet warehouseResult = this.executeQuery(PreparedQueries.getWarehouseAddressAndYtd);

            if (!warehouseResult.next()) {
                error("formattedGetWarehouseAddressAndYtd");
                throw new SQLException();
            }

            PreparedQueries.getFullCustomerInfo.setInt(1, warehouseId);
            PreparedQueries.getFullCustomerInfo.setInt(2, districtId);
            PreparedQueries.getFullCustomerInfo.setInt(3, customerId);
            ResultSet customerRes = this.executeQuery(PreparedQueries.getFullCustomerInfo);
            if (!customerRes.next()) {
                error("formattedGetFullCustomerInfo");
                throw new SQLException();
            }

            double customerBalance = customerRes.getBigDecimal("C_BALANCE").doubleValue();


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

            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[Error out]: Payment Abort " + " Counter: " + counter + this.toString());
            System.err.println("[Error err]: Payment Abort " + " Counter: " + counter+ this.toString());
            if (counter >= 3) {
                System.out.println( "[Out] transaction " + this.toString() + "fails after 3 attempts");
                System.err.println( "[err] transaction " + this.toString() + "fails after 3 attempts");
            }
            connection.rollback();
        }

    }

    public String toString() {
        return String.format("Pay *** Payment transaction info: warehouse: %d, district: %d, customer: %d, payment: %f *** \n", warehouseId, districtId, customerId, payment);
    }

    public void error(String s) {
        System.err.println("[Error]: Payment " + s + " are missing");
    }
}
