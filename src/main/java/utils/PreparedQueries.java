package utils;

public class PreparedQueries {
    public final static String getItemById = """
            SELECT I_NAME, I_ID
            FROM Item
            WHERE I_ID = ?
            """;
    public final static String getDistrictWithIDs = """
            SELECT D_W_ID, D_ID, D_NAME FROM District
            WHERE D_ID = ANY (?)
            """;

    public final static String getWarehouseWithIDs = """
            SELECT W_ID, W_NAME FROM Warehouse
            WHERE W_ID = ANY (?)
            """;

    public final static String getCustomerNameByID = """
            SELECT C_ID, C_FIRST, C_MIDDLE, C_LAST
            FROM Customer
            WHERE C_ID = ?
            """;

    public final static String getCustomerWithTopBalance = """
           SELECT C_W_ID, C_D_ID, C_FIRST, C_MIDDLE, C_LAST, C_BALANCE
           FROM CUSTOMER
           ORDER BY C_BALANCE DESC
           LIMIT 10
           """;

    public final static String getNextAvailableOrderNumber = """
            SELECT D_NEXT_O_ID
            FROM District
            WHERE D_W_ID = ? AND D_ID = ?
            """;

    public final static String getLastOrdersFromOrderLine = """
            SELECT OL_I_ID
            FROM Order_Line
            WHERE OL_O_ID >= ? AND OL_O_ID < ? AND OL_W_ID = ? AND OL_D_ID = ?
            """;

    public final static String getLastOrdersFromOrder = """
            SELECT O_ID, O_W_ID, O_D_ID, O_C_ID, O_ENTRY_D
            FROM customer_order
            WHERE O_ID >= ? AND O_ID < ? AND O_W_ID = ? AND O_D_ID = ?
            """;

    public final static String getItemStock = """
            SELECT S_QUANTITY
            FROM STOCK
            WHERE S_W_ID = ? AND S_I_ID = ANY (?)
            """;

    public final static String getPopularItemsFromOrder = """
            SELECT OL_I_ID, OL_QUANTITY
            FROM Order_Line
            WHERE OL_W_ID = ? AND OL_D_ID = ? AND OL_O_ID = ?
            ORDER BY OL_QUANTITY
            """;

    public final static String getRelatedCustomers = """
            SELECT t2.OL_C_ID as customerID
            FROM order-line as t1 INNER JOIN order-line as t2
            ON t1.OL_I_ID = t2.OL_I_ID
            WHERE t1.OL_W_ID = ? AND t1.OL_D_ID = ? AND t1.OL_C_ID = ? AND t1.OL_D_ID <> t2.OL_D_ID
            GROUP BY t2.OL_C_ID
            HAVING COUNT(*) >= 2
            """;
}
