package transactions;

import utils.IO;
import utils.PreparedQueries;
import utils.QueryUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class StockLevelTransaction extends AbstractTransaction {
    int warehouseId, districtID, threshold, lastOrders;
    QueryUtils queryUtils;
    int counter = 0;

    public StockLevelTransaction(Connection conn, IO io, QueryUtils utils, int warehouseId, int districtID,
                                 int threshold, int lastOrders) throws SQLException {
        super(conn, io);
        this.warehouseId = warehouseId;
        this.districtID = districtID;
        this.threshold = threshold;
        this.lastOrders = lastOrders;
        queryUtils = utils;
    }

    public String toString() {
        return String.format("Sto *** Stock  Level transaction info: warehouse: %d, district: %d, threshold: %d, lastOrders: %d *** \n",
                warehouseId, districtID, threshold, lastOrders);
    }

    public void execute() throws SQLException {

        try {
            counter = counter + 1;
            connection.setReadOnly(true);
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            int nextOrderNumber = queryUtils.getNextAvailableOrderNumber(warehouseId, districtID);

            ResultSet pastOrders = queryUtils.getPastOrdersFromOrderLine(warehouseId, districtID, nextOrderNumber, lastOrders);
            ArrayList<Integer> itemIds = new ArrayList<>();
            while (pastOrders.next()) {
                itemIds.add(pastOrders.getInt("OL_I_ID"));
            }

            PreparedQueries.getItemStock.setInt(1, warehouseId);
            PreparedQueries.getItemStock.setArray(2, connection.createArrayOf("INTEGER", itemIds.toArray()));
            ResultSet stocks = PreparedQueries.getItemStock.executeQuery();
            connection.commit();
            int total = 0;
            while (stocks.next()) {
                if (stocks.getInt("S_QUANTITY") < threshold) {
                    total++;
                }
            }

            io.println(total);
        } catch (Exception e) {

            e.printStackTrace();

            System.out.println("[Error out]:  Stock level Abort " + " Counter: " + counter + this.toString());
            System.err.println("[Error out]:  Stock level Abort " + " Counter: " + counter + this.toString());
            if (counter >= 3) {
                System.out.println( "[Out] transaction " + this.toString() + "fails after 3 attempts");
                System.err.println( "[err] transaction " + this.toString() + "fails after 3 attempts");
            }

            connection.rollback();
        }
    }
}
