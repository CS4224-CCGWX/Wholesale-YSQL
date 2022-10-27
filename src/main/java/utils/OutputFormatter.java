package utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class OutputFormatter {
    private final static String delimiter = "\n";

    public static String formatFullCustomerInfo(ResultSet customerInfo) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("Customer info: ");
        sb.append(delimiter);

        sb.append(String.format("Customer's Identifier: (%d, %d, %d)",
                customerInfo.getInt("C_W_ID"),
                customerInfo.getInt("C_D_ID"),
                customerInfo.getInt("C_ID")));
        sb.append(delimiter);

        sb.append(String.format("Customer's Name: (%s, %s, %s)",
                customerInfo.getString("C_FIRST"),
                customerInfo.getString("C_MIDDLE"),
                customerInfo.getString("C_LAST")));
        sb.append(delimiter);

        sb.append(String.format("Customer's Address: (%s, %s, %s, %s, %s)",
                customerInfo.getString("C_STREET_1"),
                customerInfo.getString("C_STREET_2"),
                customerInfo.getString("C_CITY"),
                customerInfo.getString("C_STATE"),
                customerInfo.getString("C_ZIP")));
        sb.append(delimiter);

        sb.append(String.format("Customer's Phone: %s", customerInfo.getString("C_PHONE")));
        sb.append(delimiter);

        sb.append(String.format("Customer's Since: %s", customerInfo.getString("C_SINCE")));
        sb.append(delimiter);

        sb.append(String.format("Customer's Credit status: %s", customerInfo.getString("C_CREDIT")));
        sb.append(delimiter);

        sb.append(String.format("Customer's Credit limit: %f", customerInfo.getBigDecimal("C_CREDIT_LIM").doubleValue()));
        sb.append(delimiter);

        sb.append(String.format("Customer's Discount: %f", customerInfo.getBigDecimal("C_DISCOUNT").doubleValue()));
        sb.append(delimiter);

        sb.append(String.format("Customer's Balance: %f", customerInfo.getBigDecimal("C_BALANCE").doubleValue()));

        return sb.toString();
    }

    public static String formatWarehouseAddress(ResultSet warehouseAddress) throws SQLException {
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
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Customer name: （%s, %s, %s), balance: %.2f",
                cInfo.getString("C_FIRST"),
                cInfo.getString("C_MIDDLE"),
                cInfo.getString("C_LAST"),
                cInfo.getBigDecimal("C_BALANCE").doubleValue()));
        return sb.toString();
    }

    public static String formatLastOrderInfo(int lastOrderId, int carrierId, Date datetime)   {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Last order ID: %d, Carrier ID: %d, Datetime: %s",
                lastOrderId, carrierId, datetime.toString()));
        return sb.toString();
    }

    public static String formatItemInfo(ResultSet itemInfo) throws SQLException {

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\tItem number: %d, Supply warehouse ID: %d, Quantity: %d, Price: %.2f, Datetime: %s",
                itemInfo.getInt("OL_I_ID"),
                itemInfo.getInt("OL_SUPPLY_W_ID"),
                itemInfo.getInt("OL_QUANTITY"),
                itemInfo.getBigDecimal("OL_AMOUNT").doubleValue(),
                itemInfo.getTimestamp("OL_DELIVERY_D").toString()));
        return sb.toString();
    }

    public String formatStockLevelTransactionOutput(long result, String transactionInfo) {
        StringBuilder sb = new StringBuilder(transactionInfo);
        sb.append(delimiter);
        sb.append(result);
        return sb.toString();
    }
}
