package transactions;

import utils.PreparedQueries;
import utils.QueryUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


class Customer {
    int warehouseId, districtId, customerId;

    Customer(int warehouseId, int districtId, int customerID) {
        this.warehouseId = warehouseId;
        this.districtId = districtId;
        this.customerId = customerID;
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(warehouseId).hashCode()
                + Integer.valueOf(districtId).hashCode()
                + Integer.valueOf(customerId).hashCode();
    }

    @Override
    public boolean equals(Object c) {
        if (!(c instanceof Customer)) return false;
        Customer customer = (Customer) c;

        return customer.warehouseId == this.warehouseId &&
                customer.districtId == this.districtId &&
                customer.customerId == this.customerId;
    }

    @Override
    public String toString() {
        return String.format("%d, %d, %d", warehouseId, districtId, customerId);
    }
}

public class RelatedCustomerTransaction extends AbstractTransaction {
    Connection conn;
    int warehouseID, districtID, customerID;
    QueryUtils queryUtils;

    PreparedStatement getOrderedItemsByCustomerStmt, getPossibleCustomerStmt;

    public RelatedCustomerTransaction(Connection conn, QueryUtils utils, int warehouseID,
                                      int districtID, int customerID) throws SQLException {
        super(conn);
        this.warehouseID = warehouseID;
        this.districtID = districtID;
        this.customerID = customerID;
        this.queryUtils = utils;

        getOrderedItemsByCustomerStmt = conn.prepareStatement(PreparedQueries.getOrderedItemsByCustomerStmt);
        getPossibleCustomerStmt = conn.prepareStatement(PreparedQueries.getPossibleCustomerStmt);
    }

    @Override
    public void execute() throws SQLException {
        getOrderedItemsByCustomerStmt.setInt(1, warehouseID);
        getOrderedItemsByCustomerStmt.setInt(2, districtID);
        getOrderedItemsByCustomerStmt.setInt(3, customerID);
        ResultSet itemsPurchasedByCustomer = getOrderedItemsByCustomerStmt.executeQuery();

        // get items purchased by the customer and the associated order id
        HashSet<Integer> itemIdByCustomer = new HashSet<>();
        HashMap<Integer, HashSet<Integer>> orderToItemsMap = new HashMap<>();
        while (itemsPurchasedByCustomer.next()) {
            int itemId = itemsPurchasedByCustomer.getInt("OL_I_ID"),
                    orderId = itemsPurchasedByCustomer.getInt("OL_O_ID");
            itemIdByCustomer.add(itemId);
            if (!orderToItemsMap.containsKey(orderId)) {
                orderToItemsMap.put(orderId, new HashSet<>());
            }
            orderToItemsMap.get(orderId).add(itemId);
        }

        getPossibleCustomerStmt.setInt(1, warehouseID);
        ResultSet possibleRelatedCustomerResult = getPossibleCustomerStmt.executeQuery();

        HashMap<Customer, HashMap<Integer, HashSet<Integer>>> customerToItemsMap = new HashMap<>();
        while (possibleRelatedCustomerResult.next()) {
            int wid = possibleRelatedCustomerResult.getInt("OL_W_ID"),
                    did = possibleRelatedCustomerResult.getInt("OL_D_ID"),
                    cid = possibleRelatedCustomerResult.getInt("OL_C_ID"),
                    oid = possibleRelatedCustomerResult.getInt("OL_O_ID"),
                    iid = possibleRelatedCustomerResult.getInt("OL_I_ID");

            Customer c = new Customer(wid, did, cid);
            if (!customerToItemsMap.containsKey(c)) {
                customerToItemsMap.put(c, new HashMap<>());
            }

            if (!customerToItemsMap.get(c).containsKey(oid)) {
                customerToItemsMap.get(c).put(oid, new HashSet<>());
            }

            customerToItemsMap.get(c).get(oid).add(iid);
        }

        HashSet<Customer> result = new HashSet<>();
        for (Map.Entry<Customer, HashMap<Integer, HashSet<Integer>>> entry : customerToItemsMap.entrySet()) {
            if (isRelatedCustomer(itemIdByCustomer, entry.getValue())) {
                result.add(entry.getKey());
            }
        }

        StringBuilder sb = new StringBuilder();
        for (Customer c : result) {
            sb.append(c.toString());
            sb.append('\n');
        }
        System.out.println(sb);
    }

    private boolean isRelatedCustomer(HashSet<Integer> itemIdByCustomer, HashMap<Integer, HashSet<Integer>> itemsByPossibleCustomer) {
        for (HashSet<Integer> itemsInEachOrder : itemsByPossibleCustomer.values()) {
            itemsInEachOrder.retainAll(itemIdByCustomer);
            if (itemsInEachOrder.size() >= 2) return true;
        }
        return false;
    }
}
