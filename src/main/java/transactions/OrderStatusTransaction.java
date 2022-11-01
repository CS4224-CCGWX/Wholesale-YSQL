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

    public OrderStatusTransaction(Connection connection, IO io, int cwid, int cdid, int cid) throws SQLException {
        super(connection, io);
        customerWarehouseId = cwid;
        customerDistrictId = cdid;
        customerId = cid;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Ord *** Order Status Transaction Information ***\n");
        sb.append(String.format("C_W_ID:%d, C_D_ID:%d, C_ID:%d\n", customerWarehouseId, customerDistrictId, customerId));
        return sb.toString();
    }

    public void execute() throws SQLException {

        this.executeUpdate(beginTransaction);
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
        PreparedQueries.getCustomerFullNameAndBalance.setInt(1, customerWarehouseId);
        PreparedQueries.getCustomerFullNameAndBalance.setInt(2, customerDistrictId);
        PreparedQueries.getCustomerFullNameAndBalance.setInt(3, customerId);
        ResultSet customerRes = this.executeQuery(PreparedQueries.getCustomerFullNameAndBalance);

        io.println("*** Order Status Transaction Summary ***");

        String customerInfo = OutputFormatter.formatCustomerFullNameAndBalance(customerRes);
        io.println(customerInfo);

        // Output Customer Last Order
        PreparedQueries.getCustomerLastOrderInfo.setInt(1, customerWarehouseId);
        PreparedQueries.getCustomerLastOrderInfo.setInt(2, customerDistrictId);
        PreparedQueries.getCustomerLastOrderInfo.setInt(3, customerId);
        ResultSet lastOrderInfo = this.executeQuery(PreparedQueries.getCustomerLastOrderInfo);

        if (!lastOrderInfo.next()) {
            error("PreparedQueries.getCustomerLastOrderInfo");
            throw new SQLException();
        }

        int lastOrderId = lastOrderInfo.getInt("O_ID");
        int carrierId = lastOrderInfo.getInt("O_CARRIER_ID");
        Instant orderDateTime = lastOrderInfo.getTimestamp("O_ENTRY_D").toInstant();
        String customerLastOrder = OutputFormatter.formatLastOrderInfo(lastOrderId, carrierId, orderDateTime);
        io.println(customerLastOrder);


        // Output Customer Last Order for each item
        PreparedQueries.getCustomerLastOrderItemsInfo.setInt(1, customerWarehouseId);
        PreparedQueries.getCustomerLastOrderItemsInfo.setInt(2, customerDistrictId);
        PreparedQueries.getCustomerLastOrderItemsInfo.setInt(3, lastOrderId);
        ResultSet itemsInfo = this.executeQuery(PreparedQueries.getCustomerLastOrderItemsInfo);

        io.println("Items of last order:");
        while(itemsInfo.next()) {
            io.println(OutputFormatter.formatItemInfo(itemsInfo));
        }

        this.executeUpdate(endTransaction);

    }

    public void error(String s) {
        System.err.println("[Error]: OrderStatus " + s + " are missing");
    }

}
