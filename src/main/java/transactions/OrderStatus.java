package transactions;

import utils.OutputFormatter;
import utils.PreparedQueries;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class OrderStatus extends AbstractTransaction {


    private final int customerWarehouseId;
    private final int customerDistrictId;
    private final int customerId;

    public OrderStatus(Connection connection, int cwid, int cdid, int cid) {
        super(connection);
        customerWarehouseId = cwid;
        customerDistrictId = cdid;
        customerId = cid;
    }


    public void execute() throws SQLException {

        /*
        This transaction queries the status of the last order of a customer.
        Input: Customer identifier (C W ID, C D ID, C ID)
        Output the following information:
        1. Customer’s name (C FIRST, C MIDDLE, C LAST), balance C BALANCE
        2. For the customer’s last order
            (a) Order number O ID
            (b) Entry date and time O ENTRY D
            (c) Carrier identifier O CARRIER ID
        3. For each item in the customer’s last order
            (a) Item number OL I ID
            (b) Supplying warehouse number OL SUPPLY W ID
            (c) Quantity ordered OL QUANTITY
            (d) Total price for ordered item OL AMOUNT
            (e) Data and time of delivery OL DELIVERY D
         */

        // Output Customer Name
        String formattedGetCustomerFullNameAndBalance = this.stringFormatter(PreparedQueries.getCustomerFullNameAndBalance, customerWarehouseId, customerDistrictId, customerId);
        ResultSet customerRes = this.executeQuery(formattedGetCustomerFullNameAndBalance);
        String customerInfo = OutputFormatter.formatCustomerFullNameAndBalance(customerRes);
        System.out.println(customerInfo);


        // Output Customer Last Order
        String formattedGetCustomerLastOrderInfo = this.stringFormatter(PreparedQueries.getCustomerLastOrderInfo, customerWarehouseId, customerDistrictId, customerId);
        ResultSet lastOrderInfo = this.executeQuery(formattedGetCustomerLastOrderInfo);
        int lastOrderId = lastOrderInfo.getInt("O_ID");
        int carrierId = lastOrderInfo.getInt("O_CARRIER_ID");
        Date orderDateTime =  lastOrderInfo.getTimestamp("O_ENTRY_D");
        String customerLastOrder = OutputFormatter.formatLastOrderInfo(lastOrderId, carrierId, orderDateTime);
        System.out.println(customerLastOrder);



        // Output Customer Last Order for each item
        String formattedGetCustomerLastOrderItemsInfo = this.stringFormatter(PreparedQueries.getCustomerLastOrderItemsInfo, customerWarehouseId, customerDistrictId, lastOrderId);
        ResultSet itemsInfo = this.executeQuery(formattedGetCustomerLastOrderItemsInfo);

        while(itemsInfo.next()) {
            System.out.println(OutputFormatter.formatItemInfo(itemsInfo));
        }

    }


}
