package utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QueryUtils {
    Connection conn;

    PreparedStatement getNextAvailableOrderNumberStmt, getCustomerNamesStmt,
            getItemNamesStmt, getPopularItemsWithinOrderStmt,
            getRelatedCustomerStmt, getPastOrdersFromOrderLineStmt,
            getPastOrdersFromOrderStmt;

    public QueryUtils(Connection conn) throws SQLException {
        this.conn = conn;

        getNextAvailableOrderNumberStmt = conn.prepareStatement(PreparedQueries.getNextAvailableOrderNumber);
        getCustomerNamesStmt = conn.prepareStatement(PreparedQueries.getCustomerNameByID);
        getItemNamesStmt = conn.prepareStatement(PreparedQueries.getItemById);
        getPopularItemsWithinOrderStmt = conn.prepareStatement(PreparedQueries.getPopularItemsFromOrder);
        getRelatedCustomerStmt = conn.prepareStatement(PreparedQueries.getRelatedCustomers);
        getPastOrdersFromOrderLineStmt = conn.prepareStatement(PreparedQueries.getLastOrdersFromOrderLine);
        getPastOrdersFromOrderStmt = conn.prepareStatement(PreparedQueries.getLastOrdersFromOrder);
    }

    public ResultSet executeQuery(String query) throws SQLException {
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        return stmt.executeQuery(query);
    }

    public int getNextAvailableOrderNumber(int warehouseID, int districtID) throws SQLException {
        getNextAvailableOrderNumberStmt.setInt(1, warehouseID);
        getNextAvailableOrderNumberStmt.setInt(2, districtID);

        ResultSet nextAvailableOrder = getNextAvailableOrderNumberStmt.executeQuery();
        if (nextAvailableOrder.next()) {
            return nextAvailableOrder.getInt("D_NEXT_O_ID");
        } else {
            throw new SQLException();
        }
    }

    public ResultSet getPastOrdersFromOrderLine(int warehouseID, int districtID, int nextOrderNumber, int pastOrders) throws SQLException {
        return getPastOrders(getPastOrdersFromOrderLineStmt, warehouseID, districtID, nextOrderNumber, pastOrders);
    }

    public ResultSet getPastOrdersFromOrder(int warehouseID, int districtID, int nextOrderNumber, int pastOrders) throws SQLException {
        return getPastOrders(getPastOrdersFromOrderStmt, warehouseID, districtID, nextOrderNumber, pastOrders);
    }

    private ResultSet getPastOrders(PreparedStatement getPastOrdersStmt, int warehouseID, int districtID, int nextOrderNumber, int pastOrders) throws SQLException {
        getPastOrdersStmt.setInt(1, nextOrderNumber - pastOrders);
        getPastOrdersStmt.setInt(2, nextOrderNumber);
        getPastOrdersStmt.setInt(3, warehouseID);
        getPastOrdersStmt.setInt(4, districtID);
        return getPastOrdersStmt.executeQuery();
    }

    public String getCustomerNameById(int customerID) throws SQLException {
        getCustomerNamesStmt.setInt(1, customerID);
        ResultSet result = getCustomerNamesStmt.executeQuery();

        if (result.next()) {
            return String.format("%s, %s, %s",
                    result.getString("C_FIRST"),
                    result.getString("C_MIDDLE"),
                    result.getString("C_LAST")
            );
        } else {
            throw new SQLException();
        }
    }


    public String getItemNameById(int itemID) throws SQLException {
        getItemNamesStmt.setInt(1, itemID);
        ResultSet result = getCustomerNamesStmt.executeQuery();

        if (result.next()) {
            return result.getString("I_NAME");
        } else {
            throw new SQLException();
        }
    }

    public List<Integer> getPopularItemWithinOrder(int warehouseID, int districtID, int orderID,
                                                   HashMap<Integer, Integer> itemFrequency) throws SQLException {
        getPopularItemsWithinOrderStmt.setInt(1, warehouseID);
        getPopularItemsWithinOrderStmt.setInt(2, districtID);
        getPopularItemsWithinOrderStmt.setInt(3, orderID);
        ResultSet result = getPopularItemsWithinOrderStmt.executeQuery();
        ArrayList<Integer> popularItems = new ArrayList<>();

        int maxQuantity = 0, itemId = 0;
        if (result.next()) {
            maxQuantity = result.getInt("OL_QUANTITY");
            itemId = result.getInt("OL_I_ID");
            itemFrequency.put(itemId, itemFrequency.getOrDefault(itemId, 0) + 1);
        }


        while (result.next() && result.getInt("OL_QUANTITY") >= maxQuantity) {
            itemId = result.getInt("OL_I_ID");
            popularItems.add(itemId);
            itemFrequency.put(itemId, itemFrequency.getOrDefault(itemId, 0) + 1);
        }

        popularItems.add(maxQuantity);
        return popularItems;
    }

    public List<Integer> getRelatedCustomers(int warehouseID, int districtID, int customerID) throws SQLException {
        getRelatedCustomerStmt.setInt(1, warehouseID);
        getRelatedCustomerStmt.setInt(2, districtID);
        getRelatedCustomerStmt.setInt(3, customerID);
        ResultSet records = getCustomerNamesStmt.executeQuery();

        List<Integer> result = new ArrayList<>();
        while (records.next()) {
            result.add(records.getInt("customerID"));
        }

        return result;
    }

}
