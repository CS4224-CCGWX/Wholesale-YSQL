package transactions;

import utils.IO;
import utils.OutputFormatter;
import utils.PreparedQueries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

public class OrderStatusTransaction extends AbstractTransaction {


    private final int customerWarehouseId;
    private final int customerDistrictId;
    private final int customerId;

    private PreparedStatement formattedGetCustomerLastOrderInfo, formattedGetCustomerFullNameAndBalance,
            formattedGetCustomerLastOrderItemsInfo;

    public OrderStatusTransaction(Connection connection, IO io, int cwid, int cdid, int cid) throws SQLException {
        super(connection, io);
        customerWarehouseId = cwid;
        customerDistrictId = cdid;
        customerId = cid;

        formattedGetCustomerLastOrderInfo = connection.prepareStatement(PreparedQueries.getCustomerLastOrderInfo);
        formattedGetCustomerFullNameAndBalance = connection.prepareStatement(PreparedQueries.getCustomerFullNameAndBalance);
        formattedGetCustomerLastOrderItemsInfo = connection.prepareStatement(PreparedQueries.getCustomerLastOrderItemsInfo);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("*** Order Status Transaction Information ***\n");
        sb.append(String.format("C_W_ID:%d, C_D_ID:%d, C_ID:%d\n", customerWarehouseId, customerDistrictId, customerId));
        return sb.toString();
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
        formattedGetCustomerFullNameAndBalance.setInt(1, customerWarehouseId);
        formattedGetCustomerFullNameAndBalance.setInt(2, customerDistrictId);
        formattedGetCustomerFullNameAndBalance.setInt(3, customerId);
        ResultSet customerRes = this.executeQuery(formattedGetCustomerFullNameAndBalance);

        io.println("*** Order Status Transaction Summary ***");

        String customerInfo = OutputFormatter.formatCustomerFullNameAndBalance(customerRes);
        io.println(customerInfo);

        // Output Customer Last Order
        formattedGetCustomerLastOrderInfo.setInt(1, customerWarehouseId);
        formattedGetCustomerLastOrderInfo.setInt(2, customerDistrictId);
        formattedGetCustomerLastOrderInfo.setInt(3, customerId);
        ResultSet lastOrderInfo = this.executeQuery(formattedGetCustomerLastOrderInfo);

        if (!lastOrderInfo.next()) {
            error("formattedGetCustomerLastOrderInfo");
        }

        int lastOrderId = lastOrderInfo.getInt("O_ID");
        int carrierId = lastOrderInfo.getInt("O_CARRIER_ID");
        Instant orderDateTime = lastOrderInfo.getTimestamp("O_ENTRY_D").toInstant();
        String customerLastOrder = OutputFormatter.formatLastOrderInfo(lastOrderId, carrierId, orderDateTime);
        io.println(customerLastOrder);


        // Output Customer Last Order for each item
        formattedGetCustomerLastOrderItemsInfo.setInt(1, customerWarehouseId);
        formattedGetCustomerLastOrderItemsInfo.setInt(2, customerDistrictId);
        formattedGetCustomerLastOrderItemsInfo.setInt(3, lastOrderId);
        ResultSet itemsInfo = this.executeQuery(formattedGetCustomerLastOrderItemsInfo);

        io.println("Items of last order:");
        while(itemsInfo.next()) {
            io.println(OutputFormatter.formatItemInfo(itemsInfo));
        }

    }

    public void error(String s) {
        System.err.println("[Error]: OrderStatus " + s + " are missing");
    }

}