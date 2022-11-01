package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PreparedQueries {
    public static PreparedStatement getItemById, getDistrictWithIDs, getWarehouseWithIDs,
            getCustomerNameByID, getCustomerWithTopBalance, getDistrictNextOrderIdAndTax,
            incrementDistrictNextOrderId, createNewOrder, getStockQty, updateStockQtyIncrRemoteCnt,
            updateStockQty, getItemPriceAndName, getStockDistInfo, createNewOrderLine,
            getWarehouseTax, getCustomerLastAndCreditAndDiscount, updateWarehouseYearToDateAmount,
            getDistrictAddressAndYtd, getWarehouseAddressAndYtd, updateDistrictYearToDateAmount,
            updateCustomerPaymentInfo, getFullCustomerInfo, getNextDeliveryOrderId, updateOrderIdToDeliver,
            updateCarrierIdInOrder, updateDeliveryDateInOrderLine, getPossibleCustomerStmt,
            getMaxQuantity, getOrderedItemsByCustomerStmt, getItemStock, getLastOrdersFromOrder,
            getLastOrdersFromOrderLine, getNextAvailableOrderNumber, getCustomerLastOrderItemsInfo,
            getCustomerLastOrderInfo, getCustomerFullNameAndBalance, updateCustomerBalanceAndDcount,
            getOrderLineInOrder, getCustomerBalance, revertNextDeliveryOrderId, getPopularItemInOrderLine;


    public static void init(Connection conn) throws SQLException {
        getItemById = conn.prepareStatement("SELECT I_NAME, I_ID FROM Item WHERE I_ID = ANY (?);");
        getDistrictWithIDs = conn.prepareStatement("SELECT D_W_ID, D_ID, D_NAME FROM District WHERE D_ID = ANY (?);");
        getWarehouseWithIDs = conn.prepareStatement("SELECT W_ID, W_NAME FROM Warehouse WHERE W_ID = ANY (?);");
        getCustomerNameByID = conn.prepareStatement("SELECT C_ID, C_FIRST, C_MIDDLE, C_LAST " +
                "FROM Customer WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?;");
        getCustomerWithTopBalance = conn.prepareStatement("SELECT C_W_ID, C_D_ID, C_FIRST, C_MIDDLE, C_LAST, C_BALANCE " +
                "FROM CUSTOMER ORDER BY C_BALANCE DESC LIMIT 10;", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        getDistrictNextOrderIdAndTax = conn.prepareStatement("SELECT D_NEXT_O_ID, D_TAX FROM district WHERE D_W_ID = ? AND D_ID = ?;");
        getPossibleCustomerStmt = conn.prepareStatement( "SELECT OL_W_ID, OL_D_ID, OL_C_ID, OL_O_ID, OL_I_ID " +
                "FROM order_line WHERE OL_W_ID <> ?;");
        getOrderedItemsByCustomerStmt = conn.prepareStatement("SELECT OL_I_ID, OL_O_ID FROM order_line " +
                "WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_C_ID = ?;");
        getMaxQuantity = conn.prepareStatement("SELECT OL_O_ID, MAX(OL_QUANTITY) AS max_quantity FROM Order_Line " +
                "WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID < ? AND OL_O_ID >= ? GROUP BY OL_O_ID;");
        incrementDistrictNextOrderId = conn.prepareStatement("UPDATE district SET D_NEXT_O_ID = D_NEXT_O_ID + 1" +
                "WHERE D_W_ID = ? AND D_ID = ?;");
        createNewOrder = conn.prepareStatement("INSERT INTO \"order\"(O_ID, O_D_ID, O_W_ID, O_C_ID, O_ENTRY_D, " +
                "O_OL_CNT, O_ALL_LOCAL) VALUES (?, ?, ?, ?, ?, ?, ?);");
        getStockQty = conn.prepareStatement("SELECT S_QUANTITY, S_YTD FROM stock WHERE S_W_ID = ? AND S_I_ID = ?;");
        updateStockQtyIncrRemoteCnt = conn.prepareStatement("UPDATE stock SET S_QUANTITY = ?, S_YTD = ?, S_ORDER_CNT = S_ORDER_CNT + 1, " +
                "S_REMOTE_CNT = S_REMOTE_CNT + 1 WHERE S_W_ID = ? AND S_I_ID = ?;");
        getItemStock = conn.prepareStatement("SELECT S_QUANTITY FROM STOCK WHERE S_W_ID = ? AND S_I_ID = ANY (?);");
        getLastOrdersFromOrder = conn.prepareStatement("SELECT O_ID, O_W_ID, O_D_ID, O_C_ID, O_W_ID, O_D_ID, O_C_ID, O_ENTRY_D FROM \"order\" " +
                "WHERE O_ID >= ? AND O_ID < ? AND O_W_ID = ? AND O_D_ID = ?;");
        updateStockQty = conn.prepareStatement(" UPDATE stock SET S_QUANTITY = ?, S_YTD = ?, S_ORDER_CNT = S_ORDER_CNT + 1 "
                + "WHERE S_W_ID = ? AND S_I_ID = ?;");
        getItemPriceAndName = conn.prepareStatement("SELECT I_PRICE, I_NAME FROM item WHERE I_ID = ?;");
        getStockDistInfo = conn.prepareStatement("SELECT ? FROM stock WHERE S_W_ID = ? AND S_I_ID = ?;");
        getLastOrdersFromOrderLine = conn.prepareStatement("SELECT OL_I_ID FROM Order_Line " +
                "WHERE OL_O_ID >= ? AND OL_O_ID < ? AND OL_W_ID = ? AND OL_D_ID = ?;");
        getNextAvailableOrderNumber = conn.prepareStatement("SELECT D_NEXT_O_ID FROM District WHERE D_W_ID = ? AND D_ID = ?;");
        createNewOrderLine =  conn.prepareStatement("INSERT INTO order_line "
                + "(OL_O_ID, OL_D_ID, OL_W_ID, OL_C_ID, OL_NUMBER, OL_I_ID, OL_SUPPLY_W_ID, OL_QUANTITY, OL_AMOUNT, OL_DIST_INFO) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
        getWarehouseTax =  conn.prepareStatement("SELECT W_TAX FROM warehouse WHERE W_ID = ?;");
        getCustomerLastAndCreditAndDiscount = conn.prepareStatement("SELECT C_LAST, C_CREDIT, C_DISCOUNT FROM customer " +
                "WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?;");
        updateWarehouseYearToDateAmount = conn.prepareStatement("UPDATE warehouse SET W_YTD = ? WHERE W_ID = ?;");
        getDistrictAddressAndYtd = conn.prepareStatement("SELECT D_STREET_1, D_STREET_2, D_CITY, D_STATE, D_ZIP, D_YTD "
                + "FROM district WHERE D_W_ID = ? AND D_ID = ?;");
        getWarehouseAddressAndYtd = conn.prepareStatement("SELECT W_STREET_1, W_STREET_2, W_CITY, W_STATE, W_ZIP, W_YTD "
                        + "FROM warehouse WHERE W_ID = ?;");
        getCustomerLastOrderItemsInfo = conn.prepareStatement("SELECT OL_I_ID, OL_SUPPLY_W_ID, OL_QUANTITY, OL_AMOUNT, OL_DELIVERY_D " +
                "FROM order_line WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?;");
        getCustomerLastOrderInfo = conn.prepareStatement("SELECT O_ID, O_CARRIER_ID, O_ENTRY_D FROM \"order\"" +
                "WHERE O_W_ID = ? AND O_D_ID = ? AND O_C_ID = ? ORDER BY O_ID DESC LIMIT 1;");
        getCustomerFullNameAndBalance = conn.prepareStatement("SELECT C_FIRST, C_MIDDLE, C_LAST, C_BALANCE FROM customer" +
                " WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?;");
        updateCustomerBalanceAndDcount = conn.prepareStatement("UPDATE customer SET C_BALANCE = ? , C_DELIVERY_CNT = C_DELIVERY_CNT + 1 "
                + "WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?;");
        updateDistrictYearToDateAmount =  conn.prepareStatement("UPDATE district SET D_YTD = ? "
                + "WHERE D_W_ID = ? AND D_ID = ?;");
        getOrderLineInOrder = conn.prepareStatement("SELECT OL_AMOUNT, OL_C_ID, OL_NUMBER FROM order_line " +
                "WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?;");
        getCustomerBalance = conn.prepareStatement("SELECT C_BALANCE FROM customer WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?;");
        updateCustomerPaymentInfo =  conn.prepareStatement("UPDATE customer "
                + "SET C_BALANCE = ?, C_YTD_PAYMENT = ?, C_PAYMENT_CNT = C_PAYMENT_CNT + 1 "
                + "WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?;");
        getFullCustomerInfo = conn.prepareStatement("SELECT C_W_ID, C_D_ID, C_ID, C_FIRST, C_MIDDLE, C_LAST, C_STREET_1, C_STREET_2, "
                        + "C_CITY, C_STATE, C_ZIP, C_PHONE, C_SINCE, C_CREDIT, C_CREDIT_LIM, C_DISCOUNT, C_BALANCE, C_YTD_PAYMENT "
                        + "FROM customer WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?;");
        revertNextDeliveryOrderId = conn.prepareStatement("UPDATE district SET D_NEXT_DELIVER_O_ID = D_NEXT_DELIVER_O_ID - 1 "
                        + "WHERE D_W_ID = ? AND D_ID = ?;");
        getNextDeliveryOrderId = conn.prepareStatement("SELECT D_NEXT_DELIVER_O_ID FROM district " +
                "WHERE D_W_ID = ? AND D_ID = ?;");
        updateOrderIdToDeliver = conn.prepareStatement("UPDATE district SET D_NEXT_DELIVER_O_ID = D_NEXT_DELIVER_O_ID + 1 "
                + "WHERE D_W_ID = ? AND D_ID = ?;");
        updateCarrierIdInOrder = conn.prepareStatement("UPDATE \"order\" SET O_CARRIER_ID = ?" +
                "WHERE O_W_ID = ? AND O_D_ID = ? AND O_ID = ?;");
        updateDeliveryDateInOrderLine = conn.prepareStatement("UPDATE order_line SET OL_DELIVERY_D = ? " +
                "WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ? AND OL_NUMBER = ?;");
        updateDeliveryDateInOrderLine = conn.prepareStatement("UPDATE order_line SET OL_DELIVERY_D = ? " +
                "WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ? AND OL_NUMBER = ?;");
        getPopularItemInOrderLine = conn.prepareStatement("SELECT OL_I_ID FROM order_line WHERE OL_W_ID = ? " +
                "AND OL_D_ID = ? AND OL_O_ID = ? AND OL_QUANTITY = ?;");
    }
}
