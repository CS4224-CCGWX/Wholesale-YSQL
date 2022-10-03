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

}
