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

    private PreparedStatement formattedGetOrderToDeliverInDistrict, formattedUpdateOrderIdToDeliver,
            formattedUpdateCarrierIdInOrder, formattedGetOrderTotalPrice,
            formattedUpdateDeliveryDateInOrderLine, formattedGetCustomerBalance,
            formattedUpdateCustomerDeliveryInfo, formattedRevertNextDeliveryOrderId;


    public DeliveryTransaction(Connection connection, IO io, int wid, int cid) throws SQLException {
        super(connection, io);
        warehouseId = wid;
        carrierId = cid;

        formattedGetOrderToDeliverInDistrict = connection.prepareStatement(PreparedQueries.getNextDeliveryOrderId);
        formattedUpdateOrderIdToDeliver = connection.prepareStatement(PreparedQueries.updateOrderIdToDeliver);
        formattedUpdateCarrierIdInOrder = connection.prepareStatement(PreparedQueries.updateCarrierIdInOrder);
        formattedGetOrderTotalPrice = connection.prepareStatement(PreparedQueries.getOrderLineInOrder);
        formattedUpdateDeliveryDateInOrderLine = connection.prepareStatement(PreparedQueries.updateDeliveryDateInOrderLine);
        formattedGetCustomerBalance = connection.prepareStatement(PreparedQueries.getCustomerBalance);
        formattedUpdateCustomerDeliveryInfo = connection.prepareStatement(PreparedQueries.updateCustomerBalanceAndDcount);
        formattedRevertNextDeliveryOrderId = connection.prepareStatement(PreparedQueries.revertNextDeliveryOrderId);

    }

    public void error(String s) {
        System.err.println("[Error]: Delivery " + s + " are missing");
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
            formattedGetOrderToDeliverInDistrict.setInt(1, warehouseId);
            formattedGetOrderToDeliverInDistrict.setInt(2, districtNo);
            res = this.executeQuery(formattedGetOrderToDeliverInDistrict);


            formattedUpdateOrderIdToDeliver.setInt(1, warehouseId);
            formattedUpdateOrderIdToDeliver.setInt(2, districtNo);
            this.executeUpdate(formattedUpdateOrderIdToDeliver);

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

            formattedUpdateCarrierIdInOrder.setInt(1, carrierId);
            formattedUpdateCarrierIdInOrder.setInt(2, warehouseId);
            formattedUpdateCarrierIdInOrder.setInt(3, districtNo);
            formattedUpdateCarrierIdInOrder.setInt(4, orderId);
            this.executeUpdate(formattedUpdateCarrierIdInOrder);

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

            formattedGetOrderTotalPrice.setInt(1, warehouseId);
            formattedGetOrderTotalPrice.setInt(2, districtNo);
            formattedGetOrderTotalPrice.setInt(3, orderId);
            res = this.executeQuery(formattedGetOrderTotalPrice);

            if (!res.next()) {
                error("getOrderLineInOrder");
                formattedRevertNextDeliveryOrderId.setInt(1, warehouseId);
                formattedRevertNextDeliveryOrderId.setInt(2, districtNo);
                this.executeUpdate(formattedRevertNextDeliveryOrderId);
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
                formattedUpdateDeliveryDateInOrderLine.setTimestamp(1, Timestamp.from(TimeFormatter.getCurrentDate().toInstant()));
                formattedUpdateDeliveryDateInOrderLine.setInt(2, warehouseId);
                formattedUpdateDeliveryDateInOrderLine.setInt(3, districtNo);
                formattedUpdateDeliveryDateInOrderLine.setInt(4, orderId);
                formattedUpdateDeliveryDateInOrderLine.setInt(5, olNum);
                this.executeUpdate(formattedUpdateDeliveryDateInOrderLine);
                System.out.println(String.format("Updated order line warehouse %d, district %d, order %d, order line %d", warehouseId, districtNo, orderId, olNum));

            }

            formattedGetCustomerBalance.setInt(1, warehouseId);
            formattedGetCustomerBalance.setInt(2, districtNo);
            formattedGetCustomerBalance.setInt(3, customerId);
            ResultSet customers = this.executeQuery(formattedGetCustomerBalance);

            System.out.println("  warehouseId: "  + warehouseId + "  districtNo:  " + districtNo
            + "  customerId:  " + customerId);
            if (!customers.next()) {
                error("getCustomerBalance");
                throw new SQLException();
            }

            double updatedBalance = customers.getBigDecimal(1).doubleValue() + orderAmount;

            formattedUpdateCustomerDeliveryInfo.setBigDecimal(1, BigDecimal.valueOf(updatedBalance));
            formattedUpdateCustomerDeliveryInfo.setInt(2, warehouseId);
            formattedUpdateCustomerDeliveryInfo.setInt(3, districtNo);
            formattedUpdateCustomerDeliveryInfo.setInt(4, customerId);
            this.executeUpdate(formattedUpdateCustomerDeliveryInfo);
            io.println(String.format("Updated the info of customer (%d, %d, %d)", warehouseId, districtNo, customerId));
        }

    }

    @Override
    public String toString() {
        return String.format("Delivery Transaction info: warehouseId: %d, carrierId: %d", warehouseId, carrierId);
    }

}
