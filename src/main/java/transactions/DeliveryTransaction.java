package transactions;

import utils.IO;
import utils.PreparedQueries;
import utils.TimeFormatter;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;

public class DeliveryTransaction extends AbstractTransaction {
    private int warehouseId;
    private int carrierId;
    private static final int TOTAL_DISTRICT = 10;

    public DeliveryTransaction(Connection connection, IO io, int wid, int cid) throws SQLException {
        super(connection, io);
        warehouseId = wid;
        carrierId = cid;
    }

    public void error(String s) {
        System.err.println("[Error]: Delivery " + s + " are missing");
    }

    public void execute() throws SQLException {
        this.executeUpdate(beginTransaction);
        ResultSet res;
        /*
        (a) Let N denote the value of the smallest order number O ID for district (W ID,DISTRICT NO)
        with O CARRIER ID = null; i.e.,
                N = min{t.O ID ∈ Order | t.O W ID = W ID, t.D ID = DISTRICT NO, t.O CARRIER ID = null}
        Let X denote the order corresponding to order number N, and let C denote the customer
        who placed this order
         */

        for (int districtNo = 1; districtNo <= TOTAL_DISTRICT; districtNo = districtNo + 1) {
            PreparedQueries.getNextDeliveryOrderId.setInt(1, warehouseId);
            PreparedQueries.getNextDeliveryOrderId.setInt(2, districtNo);
            res = this.executeQuery(PreparedQueries.getNextDeliveryOrderId);


            PreparedQueries.updateOrderIdToDeliver.setInt(1, warehouseId);
            PreparedQueries.updateOrderIdToDeliver.setInt(2, districtNo);
            this.executeUpdate(PreparedQueries.updateOrderIdToDeliver);

            int orderId = -1;
            if (!res.next()) {
                error("updateOrderIdToDeliver");
                throw new SQLException();
            }
            orderId = res.getInt("D_NEXT_DELIVER_O_ID");
            io.println("********** Delivery Transaction *********\n");
            io.println(String.format("The next order to deliver in (%d, %d) is %d", warehouseId, districtNo, orderId));

            /*
            (b) Update the order X by setting O CARRIER ID to CARRIER ID
             */

            PreparedQueries.updateCarrierIdInOrder.setInt(1, carrierId);
            PreparedQueries.updateCarrierIdInOrder.setInt(2, warehouseId);
            PreparedQueries.updateCarrierIdInOrder.setInt(3, districtNo);
            PreparedQueries.updateCarrierIdInOrder.setInt(4, orderId);
            this.executeUpdate(PreparedQueries.updateCarrierIdInOrder);

            /*
            (c) Update all the order-lines in X by setting OL DELIVERY D to the current date and time
             */


            /*
            (d) Update customer C as follows:
            • Increment C BALANCE by B, where B denote the sum of OL AMOUNT for all the
            items placed in order X
            • Increment C DELIVERY CNT by 1
             */

            double orderAmount = 0;
            ArrayList<Integer> orderLineNums = new ArrayList<>();

            PreparedQueries.getOrderLineInOrder.setInt(1, warehouseId);
            PreparedQueries.getOrderLineInOrder.setInt(2, districtNo);
            PreparedQueries.getOrderLineInOrder.setInt(3, orderId);
            res = this.executeQuery(PreparedQueries.getOrderLineInOrder);

            if (!res.next()) {
                error("getOrderLineInOrder");
                PreparedQueries.revertNextDeliveryOrderId.setInt(1, warehouseId);
                PreparedQueries.revertNextDeliveryOrderId.setInt(2, districtNo);
                this.executeUpdate(PreparedQueries.revertNextDeliveryOrderId);
                continue;
            }

            int customerId = res.getInt("OL_C_ID");
            orderAmount += res.getBigDecimal("OL_AMOUNT").doubleValue();
            orderLineNums.add(res.getInt("OL_NUMBER"));
            while (res.next()) {
                orderAmount += res.getBigDecimal("OL_AMOUNT").doubleValue();
                orderLineNums.add(res.getInt("OL_NUMBER"));
            }

            for (int olNum : orderLineNums) {
                PreparedQueries.updateDeliveryDateInOrderLine.setTimestamp(1, Timestamp.from(TimeFormatter.getCurrentDate().toInstant()));
                PreparedQueries.updateDeliveryDateInOrderLine.setInt(2, warehouseId);
                PreparedQueries.updateDeliveryDateInOrderLine.setInt(3, districtNo);
                PreparedQueries.updateDeliveryDateInOrderLine.setInt(4, orderId);
                PreparedQueries.updateDeliveryDateInOrderLine.setInt(5, olNum);
                this.executeUpdate(PreparedQueries.updateDeliveryDateInOrderLine);
                System.out.println(String.format("Updated order line warehouse %d, district %d, order %d, order line %d", warehouseId, districtNo, orderId, olNum));

            }

            PreparedQueries.getCustomerBalance.setInt(1, warehouseId);
            PreparedQueries.getCustomerBalance.setInt(2, districtNo);
            PreparedQueries.getCustomerBalance.setInt(3, customerId);
            ResultSet customers = this.executeQuery(PreparedQueries.getCustomerBalance);

            if (!customers.next()) {
                error("getCustomerBalance");
                throw new SQLException();
            }

            double updatedBalance = customers.getBigDecimal(1).doubleValue() + orderAmount;

            PreparedQueries.updateCustomerBalanceAndDcount.setBigDecimal(1, BigDecimal.valueOf(updatedBalance));
            PreparedQueries.updateCustomerBalanceAndDcount.setInt(2, warehouseId);
            PreparedQueries.updateCustomerBalanceAndDcount.setInt(3, districtNo);
            PreparedQueries.updateCustomerBalanceAndDcount.setInt(4, customerId);
            this.executeUpdate(PreparedQueries.updateCustomerBalanceAndDcount);
            io.println(String.format("Updated the info of customer (%d, %d, %d)", warehouseId, districtNo, customerId));
        }

        this.executeUpdate(endTransaction);
    }

    @Override
    public String toString() {
        return String.format("Del **** Delivery Transaction info: warehouseId: %d, carrierId: %d ****\n", warehouseId, carrierId);
    }

}
