package transactions;

import utils.IO;
import utils.PreparedQueries;
import utils.QueryUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class RelatedCustomerTransaction extends AbstractTransaction {
    Connection conn;
    int warehouseID, districtID, customerID;
    QueryUtils queryUtils;

    public RelatedCustomerTransaction(Connection conn, IO io, QueryUtils utils, int warehouseID,
                                      int districtID, int customerID) throws SQLException {
        super(conn, io);
        this.warehouseID = warehouseID;
        this.districtID = districtID;
        this.customerID = customerID;
        this.queryUtils = utils;
    }

    public String toString() {
        return String.format("Rel *** Related  Customer transaction info: warehouse: %d, district: %d, customer id: %d *** \n",
                warehouseID, districtID, customerID);
    }

    @Override
    public void execute() throws SQLException {
        PreparedQueries.getOrderedItemsByCustomerStmt.setInt(1, warehouseID);
        PreparedQueries.getOrderedItemsByCustomerStmt.setInt(2, districtID);
        PreparedQueries.getOrderedItemsByCustomerStmt.setInt(3, customerID);

        // get items purchased by the customer and the associated order id
        HashMap<Integer, HashSet<Integer>> orderToItemsMap = new HashMap<>();
        HashMap<String, HashMap<Integer, HashSet<Integer>>> customerToItemsMap = new HashMap<>();
        getTargetCustomerOrderItems(orderToItemsMap);
        for (int id = 1; id <= 10; id += 2){
            if (id == warehouseID) {
                getPossibleCustomersOrderItems(customerToItemsMap, id + 1, id + 1);
            } else if (id + 1 == warehouseID){
                getPossibleCustomersOrderItems(customerToItemsMap, id, id);
            } else {
                getPossibleCustomersOrderItems(customerToItemsMap, id, id + 1);
            }
        }

        HashSet<String> result = new HashSet<>();
        for (Map.Entry<String, HashMap<Integer, HashSet<Integer>>> entry : customerToItemsMap.entrySet()) {
            if (isRelatedCustomer(orderToItemsMap, entry.getValue())) {
                result.add(entry.getKey());
            }
        }

        StringBuilder sb = new StringBuilder();
        for (String c : result) {
            sb.append(c);
            sb.append('\n');
        }
        io.println(sb);
    }

    private boolean isRelatedCustomer(HashMap<Integer, HashSet<Integer>> itemIdByCustomer,
                                      HashMap<Integer, HashSet<Integer>> itemsByPossibleCustomer) {
        for (HashSet<Integer> itemsInEachOrder : itemsByPossibleCustomer.values()) {
            for (HashSet<Integer> customerOrderedItems : itemIdByCustomer.values()) {
                itemsInEachOrder.retainAll(customerOrderedItems);
                if (itemsInEachOrder.size() >= 2) return true;
            }
        }
        return false;
    }

    private void getTargetCustomerOrderItems(HashMap<Integer, HashSet<Integer>> orderToItemsMap) throws SQLException {
        ResultSet itemsPurchasedByCustomer = PreparedQueries.getOrderedItemsByCustomerStmt.executeQuery();
        while (itemsPurchasedByCustomer.next()) {
            int itemId = itemsPurchasedByCustomer.getInt("OL_I_ID"),
                    orderId = itemsPurchasedByCustomer.getInt("OL_O_ID");
            if (!orderToItemsMap.containsKey(orderId)) {
                orderToItemsMap.put(orderId, new HashSet<>());
            }
            orderToItemsMap.get(orderId).add(itemId);
        }
    }

    private void getPossibleCustomersOrderItems(HashMap<String,
            HashMap<Integer, HashSet<Integer>>> customerToItemsMap, int id1, int id2) throws SQLException {
        PreparedQueries.getPossibleCustomerStmt.setInt(1, id1);
        PreparedQueries.getPossibleCustomerStmt.setInt(2, id2);


        ResultSet possibleRelatedCustomerResult = PreparedQueries.getPossibleCustomerStmt.executeQuery();

        while (possibleRelatedCustomerResult.next()) {
            int wid = possibleRelatedCustomerResult.getInt("OL_W_ID"),
                    did = possibleRelatedCustomerResult.getInt("OL_D_ID"),
                    cid = possibleRelatedCustomerResult.getInt("OL_C_ID"),
                    oid = possibleRelatedCustomerResult.getInt("OL_O_ID"),
                    iid = possibleRelatedCustomerResult.getInt("OL_I_ID");

            String c = String.format("(%d, %d, %d)", wid, did, cid);
            if (!customerToItemsMap.containsKey(c)) {
                customerToItemsMap.put(c, new HashMap<>());
            }

            if (!customerToItemsMap.get(c).containsKey(oid)) {
                customerToItemsMap.get(c).put(oid, new HashSet<>());
            }

            customerToItemsMap.get(c).get(oid).add(iid);
        }
    }
}
