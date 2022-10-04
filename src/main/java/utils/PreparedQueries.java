package utils;

public class PreparedQueries {
    public final static String getDistrictWithIDs = """
            SELECT W_ID, D_ID, D_NAME FROM District
            WHERE D_ID in (?)
            """;

    public final static String getWarehouseWithIDs = """
            SELECT W_ID, W_NAME FROM Warehouse
            WHERE W_ID in (?)
            """;

    public final static String getCustomerWithTopBalance = """
           SELECT C_W_ID, C_D_ID, C_FIRST, C_MIDDLE, C_LAST, C_BALANCE
           FROM CUSTOMER
           ORDER BY C_BALANCE DESC
           LIMIT 10
           """;

    public final static String getDistrictNextOrderIdAndTax = """
                SELECT D_NEXT_O_ID, D_TAX
                FROM district
                WHERE D_W_ID = ? AND D_ID = ?;
                """;

    public final static String incrementDistrictNextOrderId = """
                UPDATE district
                SET D_NEXT_O_ID=D_NEXT_O_ID+1
                WHERE D_W_ID = ? AND D_ID = ?;
                """;

    public final static String createNewOrder = """
                INSERT INTO "order"
                (O_ID, O_D_ID, O_W_ID, O_C_ID, O_ENTRY_D, O_OL_CNT, O_ALL_LOCAL)
                VALUES (?, ?, ?, ?, ?, ?, ?);
                """;

    public final static String getStockQty = """
                SELECT S_QUANTITY
                FROM stock
                WHERE S_W_ID = ?, S_I_ID = ?;
                """;

    public final static String updateStockQtyIncrRemoteCnt = """
                UPDATE stock
                SET S_QUANTITY = ?, S_YTD = S_YTD + ?, S_ORDER_CNT = S_ORDER_CNT + 1, S_REMOTE_CNT = S_REMOTE_CNT + 1
                WHERE S_W_ID = ?, S_I_ID = ?;
                """;

    // update stock qty that NOT increments remote count
    public final static String updateStockQty = """
                UPDATE stock
                SET S_QUANTITY = ?, S_YTD = S_YTD + ?, S_ORDER_CNT = S_ORDER_CNT + 1
                WHERE S_W_ID = ?, S_I_ID = ?;
                """;

    public final static String getItemPriceAndName = """
                SELECT I_PRICE, I_NAME
                FROM item
                WHERE I_ID = ?;
                """;

    public final static String getStockDistInfo = """
                SELECT S_DIST_?
                FROM stock
                WHERE S_W_ID = ?, S_I_ID = ?;
                """;

    public final static String createNewOrderLine = """
                INSERT INTO order_line
                (OL_O_ID, OL_W_ID, OL_NUMBER, OL_I_ID, OL_SUPPLY_W_ID, OL_AMOUNT, OL_DIST_INFO)
                VALUES (?, ?, ?, ?, ?, ?, ?);
                """;

    public final static String getWarehouseTax = """
                SELECT W_TAX
                FROM warehouse
                WHERE W_ID = ?;
                """;

    public final static String getCustomerLastAndCreditAndDiscount = """
                SELECT C_LAST, C_CREDIT, C_DISCOUNT
                FROM customer
                WHERE C_W_ID = ?, C_D_ID = ?, C_ID = ?;
                """;

}
