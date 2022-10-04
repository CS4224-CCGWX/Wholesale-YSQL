package transactions;

import utils.PreparedQueries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class StockLevelTransaction extends AbstractTransaction{
    int warehouseId, districtID, threshold, lastOrders;

    public StockLevelTransaction(Connection conn, int warehouseId, int districtID, int threshold, int lastOrders) {
        super(conn);
        this.warehouseId = warehouseId;
        this.districtID = districtID;
        this.threshold = threshold;
        this.lastOrders = lastOrders;
    }

    public void execute() {
        try {
            PreparedStatement getNextAvailableOrderNumberStmt = connection.prepareStatement(PreparedQueries.getNextAvailableOrderNumber);
            getNextAvailableOrderNumberStmt.setInt(1, warehouseId);
            getNextAvailableOrderNumberStmt.setInt(2, districtID);

            ResultSet nextAvailableOrder = this.executeQuery(getNextAvailableOrderNumberStmt);
            int nextOrderNumber = 0;
            if (nextAvailableOrder.next()) {
                nextOrderNumber = nextAvailableOrder.getInt("D_NEXT_O_ID");
            } else {
                throw new SQLException();
            }

            PreparedStatement getPastOrdersStmt = connection.prepareStatement(PreparedQueries.getLastOrdersFromDistrict);
            getPastOrdersStmt.setInt(1, nextOrderNumber - lastOrders);
            getPastOrdersStmt.setInt(2, nextOrderNumber);
            ResultSet pastOrders = this.executeQuery(getPastOrdersStmt);

            ArrayList<Integer> itemIds = new ArrayList<>();
            while (pastOrders.next()) {
                itemIds.add(pastOrders.getInt("OL_I_ID"));
            }

            PreparedStatement getStockStmt = connection.prepareStatement(PreparedQueries.getItemStock);
            getStockStmt.setInt(1, warehouseId);
            getStockStmt.setArray(2, connection.createArrayOf("INTEGER", itemIds.toArray()));
            ResultSet stocks = this.executeQuery(getStockStmt);

            int total = 0;
            while (stocks.next()) {
                if (stocks.getInt("S_QUANTITY") < threshold) {
                    total++;
                }
            }

            System.out.println(total);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
