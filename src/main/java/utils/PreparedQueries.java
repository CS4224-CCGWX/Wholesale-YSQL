package utils;

public class PreparedQueries {
    public final static String getItemById = "BEGIN TRANSACTION; SELECT I_NAME, I_ID FROM Item WHERE I_ID = ?; END TRANSACTION;";
    public final static String getDistrictWithIDs = "BEGIN TRANSACTION; SELECT D_W_ID, D_ID, D_NAME FROM District WHERE D_ID = ANY (?); END TRANSACTION;";

    public final static String getWarehouseWithIDs = "BEGIN TRANSACTION; SELECT W_ID, W_NAME FROM Warehouse WHERE W_ID = ANY (?); END TRANSACTION;";

    public final static String getCustomerNameByID = "BEGIN TRANSACTION; SELECT C_ID, C_FIRST, C_MIDDLE, C_LAST FROM Customer WHERE C_ID = ?; END TRANSACTION;";

    public final static String getCustomerWithTopBalance = "BEGIN TRANSACTION; SELECT C_W_ID, C_D_ID, C_FIRST, C_MIDDLE, C_LAST, C_BALANCE " +
            "FROM CUSTOMER ORDER BY C_BALANCE DESC LIMIT 10; END TRANSACTION;";
    public final static String getDistrictNextOrderIdAndTax =  "BEGIN TRANSACTION; SELECT D_NEXT_O_ID, D_TAX FROM district "
            + "WHERE D_W_ID = ? AND D_ID = ?; END TRANSACTION;";

    public final static String incrementDistrictNextOrderId = "BEGIN TRANSACTION; UPDATE district SET D_NEXT_O_ID = D_NEXT_O_ID + 1" +
            "WHERE D_W_ID = ? AND D_ID = ?; END TRANSACTION;";

    public final static String createNewOrder = "BEGIN TRANSACTION; INSERT INTO \"order\"(O_ID, O_D_ID, O_W_ID, O_C_ID, O_ENTRY_D, " +
            "O_OL_CNT, O_ALL_LOCAL) VALUES (?, ?, ?, ?, ?, ?, ?); END TRANSACTION;";

    public final static String getStockQty = "BEGIN TRANSACTION; SELECT S_QUANTITY, S_YTD FROM stock WHERE S_W_ID = ? AND S_I_ID = ?; END TRANSACTION;";

    public final static String updateStockQtyIncrRemoteCnt = "BEGIN TRANSACTION; UPDATE stock "
            + "SET S_QUANTITY = ?, S_YTD = ?, S_ORDER_CNT = S_ORDER_CNT + 1, S_REMOTE_CNT = S_REMOTE_CNT + 1 "
            + "WHERE S_W_ID = ? AND S_I_ID = ?; END TRANSACTION;";

    // update stock qty that NOT increments remote count
    public final static String updateStockQty = "BEGIN TRANSACTION; UPDATE stock "
            + "SET S_QUANTITY = ?, S_YTD = ?, S_ORDER_CNT = S_ORDER_CNT + 1 "
            + "WHERE S_W_ID = ? AND S_I_ID = ?; END TRANSACTION;";

    public final static String getItemPriceAndName = "BEGIN TRANSACTION; SELECT I_PRICE, I_NAME FROM item WHERE I_ID = ?; END TRANSACTION;";

    public final static String getStockDistInfo = "BEGIN TRANSACTION; SELECT ? FROM stock WHERE S_W_ID = ? AND S_I_ID = ?; END TRANSACTION;";

    public final static String createNewOrderLine =  "BEGIN TRANSACTION; INSERT INTO order_line "
            + "(OL_O_ID, OL_D_ID, OL_W_ID, OL_C_ID, OL_NUMBER, OL_I_ID, OL_SUPPLY_W_ID, OL_QUANTITY, OL_AMOUNT, OL_DIST_INFO) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?); END TRANSACTION;";

    public final static String getWarehouseTax =  "BEGIN TRANSACTION; SELECT W_TAX FROM warehouse WHERE W_ID = ?; END TRANSACTION;";

    public final static String getCustomerLastAndCreditAndDiscount = "BEGIN TRANSACTION; SELECT C_LAST, C_CREDIT, C_DISCOUNT FROM customer " +
            "WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?; END TRANSACTION;";

    public final static String updateWarehouseYearToDateAmount = "BEGIN TRANSACTION; UPDATE warehouse SET W_YTD = ? WHERE W_ID = ?; END TRANSACTION;";

    public final static String getDistrictAddressAndYtd = "BEGIN TRANSACTION; SELECT D_STREET_1, D_STREET_2, D_CITY, D_STATE, D_ZIP, D_YTD "
                    + "FROM district WHERE D_W_ID = ? AND D_ID = ?; END TRANSACTION;";

    public final static String getWarehouseAddressAndYtd =
            "BEGIN TRANSACTION; SELECT W_STREET_1, W_STREET_2, W_CITY, W_STATE, W_ZIP, W_YTD "
                    + "FROM warehouse WHERE W_ID = ?; END TRANSACTION;";

    public final static String updateDistrictYearToDateAmount =  "BEGIN TRANSACTION; UPDATE district SET D_YTD = ? "
            + "WHERE D_W_ID = ? AND D_ID = ?; END TRANSACTION;";

    public final static String updateCustomerPaymentInfo =  "BEGIN TRANSACTION; UPDATE customer "
            + "SET C_BALANCE = ?, C_YTD_PAYMENT = ?, C_PAYMENT_CNT = C_PAYMENT_CNT + 1 "
            + "WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?; END TRANSACTION;";

    public final static String getFullCustomerInfo =
            "BEGIN TRANSACTION; SELECT C_W_ID, C_D_ID, C_ID, C_FIRST, C_MIDDLE, C_LAST, C_STREET_1, C_STREET_2, "
                    + "C_CITY, C_STATE, C_ZIP, C_PHONE, C_SINCE, C_CREDIT, C_CREDIT_LIM, C_DISCOUNT, C_BALANCE, C_YTD_PAYMENT "
                    + "FROM customer WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?; END TRANSACTION;";

    public final static String getWarehouseAddress = "BEGIN TRANSACTION; SELECT W_STREET_1, W_STREET_2, W_CITY, W_STATE, W_ZIP " +
            "FROM warehouse WHERE W_ID = ?; END TRANSACTION;";

    public final static String getDistrictAddress = "BEGIN TRANSACTION; SELECT D_STREET_1, D_STREET_2, D_CITY, D_STATE, D_ZIP" +
                "FROM district WHERE D_W_ID = ? AND D_ID = ?; END TRANSACTION;";

    public final static String getNextDeliveryOrderId = "BEGIN TRANSACTION; SELECT D_NEXT_DELIVER_O_ID FROM district " +
                    "WHERE D_W_ID = ? AND D_ID = ?; END TRANSACTION;";

    public final static String updateOrderIdToDeliver = "BEGIN TRANSACTION; UPDATE district "
                    + "SET D_NEXT_DELIVER_O_ID = D_NEXT_DELIVER_O_ID + 1 "
                    + "WHERE D_W_ID = ? AND D_ID = ?; END TRANSACTION;";
    public final static String updateCarrierIdInOrder = "BEGIN TRANSACTION; UPDATE \"order\" SET O_CARRIER_ID = ?" +
                "WHERE O_W_ID = ? AND O_D_ID = ? AND O_ID = ?; END TRANSACTION;";

    public final static String revertNextDeliveryOrderId =
            "BEGIN TRANSACTION; UPDATE district "
                    + "SET D_NEXT_DELIVER_O_ID = D_NEXT_DELIVER_O_ID - 1 "
                    + "WHERE D_W_ID = ? AND D_ID = ?; END TRANSACTION;";
    public final static String updateDeliveryDateInOrderLine = "BEGIN TRANSACTION; UPDATE order_line SET OL_DELIVERY_D = ? " +
                "WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ? AND OL_NUMBER = ?; END TRANSACTION;";

    public final static String getCustomerBalance = "BEGIN TRANSACTION; SELECT C_BALANCE FROM customer WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?; END TRANSACTION;";

    public final static String getOrderTotalPrice = "BEGIN TRANSACTION; SELECT SUM(OL_AMOUNT) as total_price FROM order_line " +
                "WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?; END TRANSACTION;";

    public final static String getOrderLineInOrder = "BEGIN TRANSACTION; SELECT OL_AMOUNT, OL_C_ID, OL_NUMBER FROM order_line " +
            "WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?; END TRANSACTION;";

    public final static String updateCustomerBalanceAndDcount = "BEGIN TRANSACTION; UPDATE customer SET C_BALANCE = ? , C_DELIVERY_CNT = C_DELIVERY_CNT + 1 "
                    + "WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?; END TRANSACTION;";
    public final static String getCustomerFullNameAndBalance = "BEGIN TRANSACTION; SELECT C_FIRST, C_MIDDLE, C_LAST, C_BALANCE FROM customer" +
            " WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?; END TRANSACTION;";

    public final static String getCustomerLastOrderInfo = "BEGIN TRANSACTION; SELECT O_ID, O_CARRIER_ID, O_ENTRY_D FROM \"order\"" +
            "WHERE O_W_ID = ? AND O_D_ID = ? AND O_C_ID = ? ORDER BY O_ID DESC LIMIT 1;";

    public final static String getCustomerLastOrderItemsInfo = "BEGIN TRANSACTION; SELECT OL_I_ID, OL_SUPPLY_W_ID, OL_QUANTITY, OL_AMOUNT, OL_DELIVERY_D " +
            "FROM order_line WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?;";

    public final static String getNextAvailableOrderNumber = "BEGIN TRANSACTION; SELECT D_NEXT_O_ID FROM District WHERE D_W_ID = ? AND D_ID = ?; END TRANSACTION;";

    public final static String getLastOrdersFromOrderLine = "BEGIN TRANSACTION; SELECT OL_I_ID FROM Order_Line " +
            "WHERE OL_O_ID >= ? AND OL_O_ID < ? AND OL_W_ID = ? AND OL_D_ID = ?; END TRANSACTION;";

    public final static String getLastOrdersFromOrder = "BEGIN TRANSACTION; SELECT O_ID, O_W_ID, O_D_ID, O_C_ID, O_ENTRY_D FROM \"order\" " +
            "WHERE O_ID >= ? AND O_ID < ? AND O_W_ID = ? AND O_D_ID = ?; END TRANSACTION;";

    public final static String getItemStock = "BEGIN TRANSACTION; SELECT S_QUANTITY FROM STOCK WHERE S_W_ID = ? AND S_I_ID = ANY (?); END TRANSACTION;";

    public final static String getPopularItemsFromOrder = "BEGIN TRANSACTION; SELECT OL_I_ID, OL_QUANTITY FROM Order_Line " +
            "WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ? ORDER BY OL_QUANTITY; END TRANSACTION;";

    public final static String getOrderedItemsByCustomerStmt = "BEGIN TRANSACTION; SELECT OL_I_ID, OL_O_ID FROM order_line " +
            "WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_C_ID = ?; END TRANSACTION;";

    public final static String getPossibleCustomerStmt = "BEGIN TRANSACTION; SELECT OL_W_ID, OL_D_ID, OL_C_ID, OL_O_ID, OL_I_ID " +
            "FROM order_line WHERE OL_W_ID <> ?; END TRANSACTION;";
}
