package utils;

public class PreparedQueries {
    public final static String getDistrictWithIDs = """
            SELECT D_W_ID, D_ID, D_NAME FROM District
            WHERE D_ID = ANY (?)
            """;

    public final static String getWarehouseWithIDs = """
            SELECT W_ID, W_NAME FROM Warehouse
            WHERE W_ID = ANY (?)
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

    public final static String getLastOrdersFromDistrict = """
            SELECT OL_I_ID
            FROM Order_Line
            WHERE OL_O_ID >= ? AND OL_O_ID < ?
            """;

    public final static String getItemStock = """
            SELECT S_QUANTITY
            FROM STOCK
            WHERE S_W_ID = ? AND S_I_ID = ANY (?)
            """;


}
