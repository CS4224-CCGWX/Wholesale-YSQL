package transactions;

import utils.IO;
import utils.PreparedQueries;
import utils.QueryUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class StockLevelTransaction extends AbstractTransaction {
    int warehouseId, districtID, threshold, lastOrders;
    QueryUtils queryUtils;

    PreparedStatement getStockStmt;

    public StockLevelTransaction(Connection conn, IO io, QueryUtils utils, int warehouseId, int districtID,
                                 int threshold, int lastOrders) throws SQLException {
        super(conn, io);
        this.warehouseId = warehouseId;
        this.districtID = districtID;
        this.threshold = threshold;
        this.lastOrders = lastOrders;
        queryUtils = utils;

        getStockStmt = connection.prepareStatement(PreparedQueries.getItemStock);
    }

    public void execute() {
        try {
            int nextOrderNumber = queryUtils.getNextAvailableOrderNumber(warehouseId, districtID);

            ResultSet pastOrders = queryUtils.getPastOrdersFromOrderLine(warehouseId, districtID, nextOrderNumber, lastOrders);
            ArrayList<Integer> itemIds = new ArrayList<>();
            while (pastOrders.next()) {
                itemIds.add(pastOrders.getInt("OL_I_ID"));
            }

            getStockStmt.setInt(1, warehouseId);
            getStockStmt.setArray(2, connection.createArrayOf("INTEGER", itemIds.toArray()));
            ResultSet stocks = getStockStmt.executeQuery();

            int total = 0;
            while (stocks.next()) {
                if (stocks.getInt("S_QUANTITY") < threshold) {
                    total++;
                }
            }

            io.println(total);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
