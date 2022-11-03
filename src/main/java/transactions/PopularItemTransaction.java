package transactions;

import utils.IO;
import utils.PreparedQueries;
import utils.QueryUtils;

import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PopularItemTransaction extends AbstractTransaction {
    int warehouseID, districtID, pastNumberOfOrders;
    QueryUtils queryUtils;

    public PopularItemTransaction(Connection conn, IO io, QueryUtils utils, int warehouseID, int districtID, int pastOrders) throws SQLException {
        super(conn, io);
        this.warehouseID = warehouseID;
        this.districtID = districtID;
        this.pastNumberOfOrders = pastOrders;
        this.queryUtils = utils;
    }

    public String toString() {
        return String.format("Pop *** Popular Item transaction info: warehouse: %d, district: %d, pastNumberOfOrder: %d *** \n",
                warehouseID, districtID, pastNumberOfOrders);
    }

    @Override
    public void execute() throws SQLException {
        try {
            connection.setReadOnly(true);
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            int nextOrderNumber = queryUtils.getNextAvailableOrderNumber(warehouseID, districtID);
            ResultSet pastOrders = queryUtils.getPastOrdersFromOrder(
                    warehouseID, districtID, nextOrderNumber, this.pastNumberOfOrders);

            PreparedQueries.getMaxQuantity.setInt(1, warehouseID);
            PreparedQueries.getMaxQuantity.setInt(2, districtID);
            PreparedQueries.getMaxQuantity.setInt(3, nextOrderNumber);
            HashMap<Integer, Integer> maxQuantityForEachOrder = new HashMap<>();
            PreparedQueries.getMaxQuantity.setInt(4, nextOrderNumber - pastNumberOfOrders);
            ResultSet maxQuantity = PreparedQueries.getMaxQuantity.executeQuery();
            while (maxQuantity.next()) {
                int orderId = maxQuantity.getInt("OL_O_ID");
                maxQuantityForEachOrder.put(orderId, maxQuantity.getInt("max_quantity"));
            }


            HashMap<Integer, String> allPopularItems = new HashMap<>();
            List<Set<Integer>> ItemsAmongAllOrders = new ArrayList<>();

            StringBuilder sb = new StringBuilder();

            while (pastOrders.next()) {
                int orderId = pastOrders.getInt("O_ID");
                PreparedQueries.getCustomerNameByID.setInt(1, warehouseID);
                PreparedQueries.getCustomerNameByID.setInt(2, districtID);
                PreparedQueries.getCustomerNameByID.setInt(3, orderId);
                ResultSet customerName = PreparedQueries.getCustomerNameByID.executeQuery();

                sb.append(String.format("order id: %d", orderId));
                if (customerName.next()) {
                    sb.append(String.format("Customer name: %s, %s, %s", customerName.getString("C_FIRST"),
                            customerName.getString("C_MIDDLE"), customerName.getString("C_LAST")));
                }

                int currentOrdermaxQuantity = maxQuantityForEachOrder.get(orderId);
                PreparedQueries.getPopularItemInOrderLine.setInt(1, warehouseID);
                PreparedQueries.getPopularItemInOrderLine.setInt(2, districtID);
                PreparedQueries.getPopularItemInOrderLine.setInt(3, orderId);
                PreparedQueries.getPopularItemInOrderLine.setInt(4, currentOrdermaxQuantity);
                ResultSet popularItems = PreparedQueries.getPopularItemInOrderLine.executeQuery();

                Set<Integer> popularItemPerOrder = new HashSet<>();
                while (popularItems.next()) {
                    int itemId = popularItems.getInt("OL_I_ID");
                    popularItemPerOrder.add(itemId);
                }
                Array popularItemPerOrderArray = PreparedQueries.getItemById.getConnection().createArrayOf("INTEGER", popularItemPerOrder.toArray());
                PreparedQueries.getItemById.setArray(1, popularItemPerOrderArray);
                ResultSet items = PreparedQueries.getItemById.executeQuery();

                while (items.next()) {
                    String itemName = items.getString("I_NAME");
                    int itemId = items.getInt("I_ID");
                    allPopularItems.putIfAbsent(itemId, itemName);
                    sb.append(String.format("popular item: %s quantity:%d\n", itemName, currentOrdermaxQuantity));
                }
                ItemsAmongAllOrders.add(popularItemPerOrder);
            }
            connection.commit();
            for (Map.Entry<Integer, String> item: allPopularItems.entrySet()) {
                int count = 0;
                for (Set<Integer> order : ItemsAmongAllOrders) {
                    if (order.contains(item.getKey())) count++;
                }

                double ratio = count * 1.0 / allPopularItems.size();
                sb.append(String.format("item name:%s, ratio: %f\n", item.getValue(), ratio));
            }
            io.println(sb.toString());
        } catch (Exception e) {
            connection.rollback();
        }


    }
}
