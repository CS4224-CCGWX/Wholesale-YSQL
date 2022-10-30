package transactions;

import utils.IO;
import utils.QueryUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class PopularItemTransaction extends AbstractTransaction {
    int warehouseID, districtID, pastNumberOfOrders;
    QueryUtils queryUtils;

    public PopularItemTransaction(Connection conn, IO io, QueryUtils utils, int warehouseID, int districtID, int pastOrders) {
        super(conn, io);
        this.warehouseID = warehouseID;
        this.districtID = districtID;
        this.pastNumberOfOrders = pastOrders;
        this.queryUtils = utils;
    }

    @Override
    public void execute() {
        try {
            int nextOrderNumber = queryUtils.getNextAvailableOrderNumber(warehouseID, districtID);
            ResultSet pastOrders = queryUtils.getPastOrdersFromOrder(
                    warehouseID, districtID, nextOrderNumber, this.pastNumberOfOrders);
            HashMap<Integer, Integer> itemFrequency = new HashMap<>();
            HashMap<Integer, String> itemIDToName = new HashMap<>();
            StringBuilder sb = new StringBuilder();

            while (pastOrders.next()) {
                String customerName = queryUtils.getCustomerNameById(pastOrders.getInt("O_C_ID"));
                List<Integer> popularItems = queryUtils.getPopularItemWithinOrder(warehouseID, districtID,
                        pastOrders.getInt("O_ID"), itemFrequency);

                sb.append(String.format(
                        "Order Examined: %s, %s\n Customer placed: %s",
                        pastOrders.getInt("O_ID"),
                        pastOrders.getString("O_ENTRY_D"),
                        customerName
                ));

                int maxQuantity = popularItems.get(popularItems.size() - 1);
                for (int i = 0; i < popularItems.size() - 1; i++) {
                    int itemId = popularItems.get(i);
                    String itemName;
                    if (itemIDToName.containsKey(itemId)) {
                        itemName = itemIDToName.get(itemId);
                    } else {
                        itemName = queryUtils.getItemNameById(itemId);
                        itemIDToName.put(itemId, itemName);
                    }
                    sb.append(String.format("Popular item: %s, %s\n", itemName, maxQuantity));
                }
                sb.append("\n");
            }

            sb.append("\n\n Percentage of popular items");
            for (int itemId: itemFrequency.keySet()) {
                String itemName = itemIDToName.get(itemId);
                double percentage = itemFrequency.get(itemId) * 1.0 / pastNumberOfOrders;
                sb.append(String.format("%s, %s\n", itemName, percentage));
            }

            io.println(sb);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
