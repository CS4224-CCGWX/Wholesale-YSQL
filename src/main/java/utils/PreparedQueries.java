package utils;

public class PreparedQueries {
    public final static String getItemById = "SELECT I_NAME, I_ID FROM Item WHERE I_ID = ?;";
    public final static String getDistrictWithIDs = "SELECT D_W_ID, D_ID, D_NAME FROM District WHERE D_ID = ANY (?);";

    public final static String getWarehouseWithIDs = "SELECT W_ID, W_NAME FROM Warehouse WHERE W_ID = ANY (?);";

    public final static String getCustomerNameByID = "SELECT C_ID, C_FIRST, C_MIDDLE, C_LAST FROM Customer WHERE C_ID = ?;";

    public final static String getCustomerWithTopBalance = "SELECT C_W_ID, C_D_ID, C_FIRST, C_MIDDLE, C_LAST, C_BALANCE " +
            "FROM CUSTOMER ORDER BY C_BALANCE DESC LIMIT 10;";
    public final static String getDistrictNextOrderIdAndTax =  "SELECT D_NEXT_O_ID, D_TAX FROM district "
            + "WHERE D_W_ID = ? AND D_ID = ?;";

    public final static String incrementDistrictNextOrderId = "UPDATE district SET D_NEXT_O_ID = D_NEXT_O_ID + 1" +
            "WHERE D_W_ID = ? AND D_ID = ?;";

    public final static String createNewOrder = "INSERT INTO \"order\"(O_ID, O_D_ID, O_W_ID, O_C_ID, O_ENTRY_D, " +
            "O_OL_CNT, O_ALL_LOCAL) VALUES (?, ?, ?, ?, ?, ?, ?);";

    public final static String getStockQty = "SELECT S_QUANTITY, S_YTD FROM stock WHERE S_W_ID = ? AND S_I_ID = ?;";

    public final static String updateStockQtyIncrRemoteCnt = "UPDATE stock "
            + "SET S_QUANTITY = ?, S_YTD = ?, S_ORDER_CNT = S_ORDER_CNT + 1, S_REMOTE_CNT = S_REMOTE_CNT + 1 "
            + "WHERE S_W_ID = ? AND S_I_ID = ?;";

    // update stock qty that NOT increments remote count
    public final static String updateStockQty = "UPDATE stock "
            + "SET S_QUANTITY = ?, S_YTD = ?, S_ORDER_CNT = S_ORDER_CNT + 1 "
            + "WHERE S_W_ID = ? AND S_I_ID = ?;";

    public final static String getItemPriceAndName = "SELECT I_PRICE, I_NAME FROM item WHERE I_ID = ?;";

    public final static String getStockDistInfo = "SELECT ? FROM stock WHERE S_W_ID = ? AND S_I_ID = ?;";

    public final static String createNewOrderLine =  "INSERT INTO order_line "
            + "(OL_O_ID, OL_D_ID, OL_W_ID, OL_C_ID, OL_NUMBER, OL_I_ID, OL_SUPPLY_W_ID, OL_QUANTITY, OL_AMOUNT, OL_DIST_INFO) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    public final static String getWarehouseTax =  "SELECT W_TAX FROM warehouse WHERE W_ID = ?;";

    public final static String getCustomerLastAndCreditAndDiscount = "SELECT C_LAST, C_CREDIT, C_DISCOUNT FROM customer " +
            "WHERE C_W_ID = ?, C_D_ID = ?, C_ID = ?;";

    public final static String updateWarehouseYearToDateAmount = "UPDATE warehouse SET W_YTD = ? WHERE W_ID = ?;";

    public final static String getDistrictAddressAndYtd = "SELECT D_STREET_1, D_STREET_2, D_CITY, D_STATE, D_ZIP, D_YTD "
                    + "FROM district WHERE D_W_ID = ? AND D_ID = ?;";

    public final static String getWarehouseAddressAndYtd =
            "SELECT W_STREET_1, W_STREET_2, W_CITY, W_STATE, W_ZIP, W_YTD "
                    + "FROM warehouse WHERE W_ID = ?;";

    public final static String updateDistrictYearToDateAmount =  "UPDATE district SET D_YTD = ? "
            + "WHERE D_W_ID = ? AND D_ID = ?;";

    public final static String updateCustomerPaymentInfo =  "UPDATE customer "
            + "SET C_BALANCE = ?, C_YTD_PAYMENT = ?, C_PAYMENT_CNT = C_PAYMENT_CNT + 1 "
            + "WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?;";

    public final static String getFullCustomerInfo =
            "SELECT C_W_ID, C_D_ID, C_ID, C_FIRST, C_MIDDLE, C_LAST, C_STREET_1, C_STREET_2, "
                    + "C_CITY, C_STATE, C_ZIP, C_PHONE, C_SINCE, C_CREDIT, C_CREDIT_LIM, C_DISCOUNT, C_BALANCE, C_YTD_PAYMENT "
                    + "FROM customer WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?;";

    public final static String getWarehouseAddress = "SELECT W_STREET_1, W_STREET_2, W_CITY, W_STATE, W_ZIP " +
            "FROM warehouse WHERE W_ID = ?";

    public final static String getDistrictAddress = "SELECT D_STREET_1, D_STREET_2, D_CITY, D_STATE, D_ZIP" +
                "FROM district WHERE D_W_ID = ? AND D_ID = ?;";

    public final static String getOrderIdToDeliver = "SELECT D_NEXT_DELIVER_O_ID FROM district " +
                    "WHERE D_W_ID = ? AND D_ID = ?;";

    public final static String updateOrderIdToDeliver = "UPDATE district "
                    + "SET D_NEXT_DELIVER_O_ID = D_NEXT_DELIVER_O_ID + 1 "
                    + "WHERE D_W_ID = ? AND D_ID = ?;";
    public final static String updateCarrierIdInOrder = "UPDATE \"order\" SET O_CARRIER_ID = ?" +
                "WHERE O_W_ID = ? AND O_D_ID = ? AND O_ID = ?;";

    public final static String updateDeliveryDateInOrderLine = "UPDATE order_line SET OL_DELIVERY_D = ? " +
                "WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ? AND OL_NUMBER = ?;";

    public final static String getCustomerBalance = "SELECT C_BALANCE FROM customer WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?";

    public final static String getOrderTotalPrice = "SELECT SUM(OL_AMOUNT) as total_price FROM order_line " +
                "WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?;";

    public final static String getOrderLineInOrder = "SELECT OL_AMOUNT, OL_C_ID, OL_NUMBER FROM order_line " +
            "WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?";

    public final static String updateCustomerBalanceAndDcount = "UPDATE customer SET C_BALANCE = ? , C_DELIVERY_CNT = C_DELIVERY_CNT + 1 "
                    + "WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?";
    public final static String getCustomerFullNameAndBalance = "SELECT C_FIRST, C_MIDDLE, C_LAST, C_BALANCE FROM customer" +
            " WHERE C_W_ID = ? AND C_D_ID = ? AND C_ID = ?;";

    public final static String getCustomerLastOrderInfo = "SELECT O_ID, O_CARRIER_ID, O_ENTRY_D FROM \"order\"" +
            "WHERE O_W_ID = ? AND O_D_ID = ? AND O_C_ID = ? ORDER BY O_ID DESC LIMIT 1;";

    public final static String getCustomerLastOrderItemsInfo = "SELECT OL_I_ID, OL_SUPPLY_W_ID, OL_QUANTITY, OL_AMOUNT, OL_DELIVERY_D " +
            "FROM order_line WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?;";

    public final static String getNextAvailableOrderNumber = "SELECT D_NEXT_O_ID FROM District WHERE D_W_ID = ? AND D_ID = ?;";

    public final static String getLastOrdersFromOrderLine = "SELECT OL_I_ID FROM Order_Line " +
            "WHERE OL_O_ID >= ? AND OL_O_ID < ? AND OL_W_ID = ? AND OL_D_ID = ?;";

    public final static String getLastOrdersFromOrder = "SELECT O_ID, O_W_ID, O_D_ID, O_C_ID, O_ENTRY_D FROM \"order\" " +
            "WHERE O_ID >= ? AND O_ID < ? AND O_W_ID = ? AND O_D_ID = ?;";

    public final static String getItemStock = "SELECT S_QUANTITY FROM STOCK WHERE S_W_ID = ? AND S_I_ID = ANY (?);";

    public final static String getPopularItemsFromOrder = "SELECT OL_I_ID, OL_QUANTITY FROM Order_Line " +
            "WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ? ORDER BY OL_QUANTITY;";

    public final static String getOrderedItemsByCustomerStmt = "SELECT OL_I_ID, OL_O_ID FROM order_line " +
            "WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_C_ID = ?;";

    public final static String getPossibleCustomerStmt = "SELECT OL_W_ID, OL_D_ID, OL_C_ID, OL_O_ID, OL_I_ID " +
            "FROM order_line WHERE OL_W_ID <> ?;";
}
