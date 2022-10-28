package transactions;

import utils.PreparedQueries;
import utils.TimeFormatter;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DeliveryTransaction extends AbstractTransaction {

    private int warehouseId;
    private int carrierId;

    private static final int TOTAL_DISTRICT = 10;


    public DeliveryTransaction(Connection connection, int wid, int cid) {
        super(connection);
        warehouseId = wid;
        carrierId = cid;
    }

    public void error(String s) {
        System.out.println("[Error]: Delivery " + s + " are missing");
    }

    public void execute() throws SQLException {

        ResultSet res;
        /*
        (a) Let N denote the value of the smallest order number O ID for district (W ID,DISTRICT NO)
        with O CARRIER ID = null; i.e.,
                N = min{t.O ID ∈ Order | t.O W ID = W ID, t.D ID = DISTRICT NO, t.O CARRIER ID = null}
        Let X denote the order corresponding to order number N, and let C denote the customer
        who placed this order
         */

        for (int districtNo = 1; districtNo <= TOTAL_DISTRICT; districtNo = districtNo + 1) {
            String formattedGetOrderToDeliverInDistrict = this.stringFormatter(PreparedQueries.getOrderIdToDeliver, warehouseId, districtNo);
            res = this.executeQuery(formattedGetOrderToDeliverInDistrict);

            String formattedUpdateOrderIdToDeliver = this.stringFormatter(PreparedQueries.updateOrderIdToDeliver, warehouseId, districtNo);
            this.executeQuery(formattedUpdateOrderIdToDeliver);

            int orderId = -1;
            if (!res.next()) {
                error("updateOrderIdToDeliver");
                return;
            }
            orderId = res.getInt("D_NEXT_DELIVER_O_ID");
            System.out.println("********** Delivery Transaction *********\n");
            System.out.println(String.format("The next order to deliver in (%d, %d) is %d", warehouseId, districtNo, orderId));

            /*
            (b) Update the order X by setting O CARRIER ID to CARRIER ID

             */

            String formattedUpdateCarrierIdInOrder = this.stringFormatter(PreparedQueries.updateCarrierIdInOrder, carrierId, warehouseId, districtNo, orderId);
            this.executeQuery(formattedUpdateCarrierIdInOrder);

            /*
            (c) Update all the order-lines in X by setting OL DELIVERY D to the current date and time
             */

//            String formattedUpdateDeliveryDateInOrderLine = this.stringFormatter(PreparedQueries.updateDeliveryDateInOrderLine, TimeFormatter.getCurrentTimestamp(), warehouseId, districtNo, orderId);
//            this.executeQuery(formattedUpdateDeliveryDateInOrderLine);

            /*
            (d) Update customer C as follows:
            • Increment C BALANCE by B, where B denote the sum of OL AMOUNT for all the
            items placed in order X
            • Increment C DELIVERY CNT by 1
             */

            double orderAmount = 0;
            ArrayList<Integer> orderLineNums = new ArrayList<>();
            String formattedGetOrderTotalPrice = this.stringFormatter(PreparedQueries.getOrderLineInOrder, warehouseId, districtNo, orderId);
            res = this.executeQuery(formattedGetOrderTotalPrice);

            if (!res.next()) {
                error("getOrderLineInOrder");
                return;
            }

            int customerId = res.getInt("OL_C_ID");
            orderAmount += res.getBigDecimal("OL_AMOUNT").doubleValue();
            orderLineNums.add(res.getInt("OL_NUMBER"));
            while (res.next()) {
                orderAmount += res.getBigDecimal("OL_AMOUNT").doubleValue();
                orderLineNums.add(res.getInt("OL_NUMBER"));
            }

            for (int olNum : orderLineNums) {
                String formattedUpdateDeliveryDateInOrderLine = this.stringFormatter(PreparedQueries.updateDeliveryDateInOrderLine, TimeFormatter.getCurrentDate().toInstant(), warehouseId, districtNo, orderId, olNum);
                this.executeQuery(formattedUpdateDeliveryDateInOrderLine);
            }

            String formattedGetCustomerBalance = this.stringFormatter(PreparedQueries.getCustomerBalance, warehouseId, districtNo, customerId);
            ResultSet customers = this.executeQuery(formattedGetCustomerBalance);

            if (!customers.next()) {
                error("getCustomerBalance");
                return ;
            }

            double updatedBalance = customers.getBigDecimal(0).doubleValue() + orderAmount;

            String formattedUpdateCustomerDeliveryInfo = this.stringFormatter(PreparedQueries.updateCustomerBalanceAndDcount, BigDecimal.valueOf(updatedBalance), warehouseId, districtNo, customerId);
            this.executeQuery(formattedUpdateCustomerDeliveryInfo);
            System.out.println(String.format("Updated the info of customer (%d, %d, %d)", warehouseId, districtNo, customerId));
        }

    }

    @Override
    public String toString() {
        return String.format("Delivery Transaction info: warehouseId: %d, carrierId: %d", warehouseId, carrierId);
    }

}
