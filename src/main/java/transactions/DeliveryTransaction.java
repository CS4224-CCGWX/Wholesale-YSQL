package transactions;

import com.datastax.driver.core.Row;
import utils.PreparedQueries;
import utils.OutputFormatter;
import utils.TimeFormatter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DeliveryTransaction extends AbstractTransaction {

    private int warehouseId;
    private int carrierId;

    private static final int TOTAL_DISTRICT = 10;


    public DeliveryTransaction(Connection connection, int wid, int cid) {
        super(connection);
        warehouseId = wid;
        carrierId = cid;
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
            String formattedGetOrderToDeliverInDistrict = this.stringFormatter(PreparedQueries.getOrderToDeliverInDistrict, warehouseId, districtNo);
            res = this.executeQuery(formattedGetOrderToDeliverInDistrict);

            int orderId = res.getInt("O_ID");

            /*
            (b) Update the order X by setting O CARRIER ID to CARRIER ID

             */

            String formattedUpdateCarrierIdInOrder = this.stringFormatter(PreparedQueries.updateCarrierIdInOrder, carrierId, warehouseId, districtNo, orderId);
            this.executeQuery(formattedUpdateCarrierIdInOrder);

            /*
            (c) Update all the order-lines in X by setting OL DELIVERY D to the current date and time
             */

            String formattedUpdateDeliveryDateInOrderLine = this.stringFormatter(PreparedQueries.updateDeliveryDateInOrderLine, TimeFormatter.getCurrentTimestamp(), warehouseId, districtNo, orderId);
            this.executeQuery(formattedUpdateDeliveryDateInOrderLine);

            /*
            (d) Update customer C as follows:
            • Increment C BALANCE by B, where B denote the sum of OL AMOUNT for all the
            items placed in order X
            • Increment C DELIVERY CNT by 1
             */

            String formattedGetOrderTotalPrice = this.stringFormatter(PreparedQueries.getOrderTotalPrice, warehouseId, districtNo, orderId);
            res = this.executeQuery(formattedGetOrderTotalPrice);

            double totalPrice = res.getDouble("total_price");


            String formattedUpdateCustomerDeliveryInfo = this.stringFormatter(PreparedQueries.updateCustomerDeliveryInfo, totalPrice, warehouseId, districtNo, orderId);
            this.executeQuery(formattedUpdateCustomerDeliveryInfo);
        }

    }

}
