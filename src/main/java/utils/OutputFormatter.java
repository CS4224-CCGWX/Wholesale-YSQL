package utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

public class OutputFormatter {
    private final static String delimiter = "\n";

    public final static String linebreak = "=======================================";

    public static String formatFullCustomerInfo(ResultSet customerInfo, double balance) throws SQLException {
        if (!customerInfo.next()) {
            throw new SQLException();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Customer info: ");
        sb.append(delimiter);

        sb.append(String.format("Identifier: (%d, %d, %d)",
                customerInfo.getInt("C_W_ID"),
                customerInfo.getInt("C_D_ID"),
                customerInfo.getInt("C_ID")));
        sb.append(delimiter);

        sb.append(String.format("Name: (%s, %s, %s)",
                customerInfo.getString("C_FIRST"),
                customerInfo.getString("C_MIDDLE"),
                customerInfo.getString("C_LAST")));
        sb.append(delimiter);

        sb.append(String.format("Address: (%s, %s, %s, %s, %s)",
                customerInfo.getString("C_STREET_1"),
                customerInfo.getString("C_STREET_2"),
                customerInfo.getString("C_CITY"),
                customerInfo.getString("C_STATE"),
                customerInfo.getString("C_ZIP")));
        sb.append(delimiter);

        sb.append(String.format("Phone: %s", customerInfo.getString("C_PHONE")));
        sb.append(delimiter);

        sb.append(String.format("Since: %s", customerInfo.getTimestamp("C_SINCE").toInstant()));
        sb.append(delimiter);

        sb.append(String.format("Credit status: %s", customerInfo.getString("C_CREDIT")));
        sb.append(delimiter);

        sb.append(String.format("Credit limit: %s", customerInfo.getBigDecimal("C_CREDIT_LIM").doubleValue()));
        sb.append(delimiter);

        sb.append(String.format("Discount: %s", customerInfo.getBigDecimal("C_DISCOUNT").doubleValue()));
        sb.append(delimiter);

        sb.append(String.format("Balance: %s", balance));

        return sb.toString();
    }

    public static String formatWarehouseAddress(ResultSet warehouseAddress) throws SQLException {
        if (!warehouseAddress.next()) {
            throw new SQLException();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Warehouse address: (%s, %s, %s, %s, %s)",
                warehouseAddress.getString("W_STREET_1"),
                warehouseAddress.getString("W_STREET_2"),
                warehouseAddress.getString("W_CITY"),
                warehouseAddress.getString("W_STATE"),
                warehouseAddress.getString("W_ZIP")));
        return sb.toString();
    }

    public static String formatDistrictAddress(ResultSet districtAddress) throws SQLException {
        if (!districtAddress.next()) {
            throw new SQLException();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("district address: (%s, %s, %s, %s, %s)",
                districtAddress.getString("D_STREET_1"),
                districtAddress.getString("D_STREET_2"),
                districtAddress.getString("D_CITY"),
                districtAddress.getString("D_STATE"),
                districtAddress.getString("D_ZIP")));
        return sb.toString();
    }

    public static String formatCustomerFullNameAndBalance(ResultSet cInfo) throws SQLException {
        if (!cInfo.next()) {
            throw new SQLException();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Customer name: （%s, %s, %s), balance: %.2f",
                cInfo.getString("C_FIRST"),
                cInfo.getString("C_MIDDLE"),
                cInfo.getString("C_LAST"),
                cInfo.getBigDecimal("C_BALANCE").doubleValue()));
        return sb.toString();
    }

    public static String formatLastOrderInfo(int lastOrderId, int carrierId, Instant datetime)   {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Last order ID: %d, Carrier ID: %d, Datetime: %s",
                lastOrderId, carrierId, datetime.toString()));
        return sb.toString();
    }

    public static String formatItemInfo(ResultSet itemInfo) throws SQLException {
//        if (!itemInfo.next()) {
//            throw new SQLException();
//        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\tItem number: %d, Supply warehouse ID: %d, Quantity: %d, Price: %.2f, Datetime: %s",
                itemInfo.getInt("OL_I_ID"),
                itemInfo.getInt("OL_SUPPLY_W_ID"),
                itemInfo.getInt("OL_QUANTITY"),
                itemInfo.getBigDecimal("OL_AMOUNT").doubleValue(),
                itemInfo.getTimestamp("OL_DELIVERY_D").toString()));
        return sb.toString();
    }

    public String formatTransactionID(int i) {
        return String.format("Transaction ID: %d", i);
    }

    public String formatStockLevelTransactionOutput(long result, String transactionInfo) {
        StringBuilder sb = new StringBuilder(transactionInfo);
        sb.append(delimiter);
        sb.append(result);
        return sb.toString();
    }

    public String formatTotalTransactions(int count) {
        return String.format("Total number of transactions: %d\n", count);
    }

    public String formatTotalElapsedTime(long totalTime) {
        return String.format("Total elapsed time: %ds\n", totalTime);
    }

    public String formatThroughput(double throughput) {
        return String.format("Transaction throughput: %.2f per second\n", throughput);
    }

    public String formatAverage(double latency) {
        return String.format("Average latency: %.2fms\n", latency);
    }

    public String formatMedian(long latency) {
        return String.format("Median latency: %dms\n", latency);
    }

    public String formatPercentile(int percentile, long latency) {
        return String.format("%dth percentile transaction latency: %dms\n", percentile, latency);
    }

}
