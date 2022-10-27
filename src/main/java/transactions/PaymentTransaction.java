package transactions;

import utils.PreparedQueries;
import utils.OutputFormatter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PaymentTransaction extends AbstractTransaction{

    private int warehouseId;
    private int districtId;
    private int customerId;

    private double payment;

    public PaymentTransaction(Connection connection, int cwid, int cdid, int cid, double p) {
        super(connection);
        customerId = cid;
        warehouseId = cwid;
        districtId = cdid;
        payment = p;
    }


    public void execute() throws SQLException {

        // 1.  Update the warehouse C W ID by incrementing W YTD by PAYMENT
        String formattedUpdateWarehouseYearToDateAmount = this.stringFormatter(PreparedQueries.updateWarehouseYearToDateAmount, payment, warehouseId);
        this.executeQuery(formattedUpdateWarehouseYearToDateAmount);

        // 2. Update the district (C W ID,C D ID) by incrementing D YTD by PAYMENT
        String formattedUpdateDistrictYearToDateAmount = this.stringFormatter(PreparedQueries.updateDistrictYearToDateAmount, payment, warehouseId, districtId);
        this.executeQuery(formattedUpdateDistrictYearToDateAmount);


        /*
        3. Update the customer (C W ID, C D ID, C ID) as follows:
            • Decrement C BALANCE by PAYMENT
            • Increment C YTD PAYMENT by PAYMENT
            • Increment C PAYMENT CNT by 1
         */
        String formattedUpdateCustomerPaymentInfo = this.stringFormatter(PreparedQueries.updateCustomerPaymentInfo, payment, payment, warehouseId, districtId, customerId);
        this.executeQuery(formattedUpdateCustomerPaymentInfo);


        // Output Customer Information
        String formattedGetFullCustomerInfo = this.stringFormatter(PreparedQueries.getFullCustomerInfo, warehouseId, districtId, customerId);
        ResultSet customerRes = this.executeQuery(formattedGetFullCustomerInfo);
        String customerInfo = OutputFormatter.formatFullCustomerInfo(customerRes);
        System.out.println(customerInfo);

        // Output Warehouse Address
        String formattedGetWarehouseAddress = this.stringFormatter(PreparedQueries.getWarehouseAddress, warehouseId);
        ResultSet warehouseRes = this.executeQuery(formattedGetWarehouseAddress);
        String warehouseInfo = OutputFormatter.formatWarehouseAddress(warehouseRes);
        System.out.println(warehouseInfo);


        // Output District Address
        String formattedGetDistrictAddress = this.stringFormatter(PreparedQueries.getDistrictAddress, warehouseId, districtId);
        ResultSet districtRes = this.executeQuery(formattedGetDistrictAddress);
        String districtInfo = OutputFormatter.formatDistrictAddress(districtRes);
        System.out.println(districtInfo);


        // Output Payment Amount
        String formattedPayment = this.stringFormatter("Payment: %f", payment);
        System.out.println(formattedPayment);
    }
}
