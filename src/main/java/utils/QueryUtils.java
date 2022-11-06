package utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QueryUtils {
    Connection conn;


    public QueryUtils(Connection conn) {
        this.conn = conn;
    }

    public int getNextAvailableOrderNumber(int warehouseID, int districtID) throws SQLException {
        PreparedQueries.getNextAvailableOrderNumber.setInt(1, warehouseID);
        PreparedQueries.getNextAvailableOrderNumber.setInt(2, districtID);

        ResultSet nextAvailableOrder = PreparedQueries.getNextAvailableOrderNumber.executeQuery();
        if (nextAvailableOrder.next()) {
            return nextAvailableOrder.getInt("D_NEXT_O_ID");
        } else {
            throw new SQLException();
        }
    }

    public ResultSet getPastOrdersFromOrderLine(int warehouseID, int districtID, int nextOrderNumber, int pastOrders) throws SQLException {
        return getPastOrders(PreparedQueries.getLastOrdersFromOrderLine, warehouseID, districtID, nextOrderNumber, pastOrders);
    }

    public ResultSet getPastOrdersFromOrder(int warehouseID, int districtID, int nextOrderNumber, int pastOrders) throws SQLException {
        return getPastOrders(PreparedQueries.getLastOrdersFromOrder, warehouseID, districtID, nextOrderNumber, pastOrders);
    }

    private ResultSet getPastOrders(PreparedStatement getPastOrdersStmt, int warehouseID, int districtID, int nextOrderNumber, int pastOrders) throws SQLException {
        getPastOrdersStmt.setInt(1, nextOrderNumber - pastOrders);
        getPastOrdersStmt.setInt(2, nextOrderNumber);
        getPastOrdersStmt.setInt(3, warehouseID);
        getPastOrdersStmt.setInt(4, districtID);
        return getPastOrdersStmt.executeQuery();
    }

    public String getCustomerNameById(int customerID) throws SQLException {
        PreparedQueries.getCustomerNameByID.setInt(1, customerID);
        ResultSet result = PreparedQueries.getCustomerNameByID.executeQuery();

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

}
