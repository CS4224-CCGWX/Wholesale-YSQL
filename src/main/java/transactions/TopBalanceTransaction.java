package transactions;

import utils.IO;
import utils.PreparedQueries;
import utils.QueryUtils;

import java.sql.*;
import java.util.HashMap;

public class TopBalanceTransaction extends AbstractTransaction {
    QueryUtils queryUtils;

    public TopBalanceTransaction(Connection connection, IO io, QueryUtils utils) throws SQLException {
        super(connection, io);
        this.queryUtils = utils;
    }


    public String toString() {
        String s = "Top *** Top Balance transaction info*** \n";
        return s;
    }



    /**
     * This transaction finds the top-10 customers ranked in descending order of their outstanding balance payments.
     *
     * Output: customer name(first, middle, last), outstanding balance, warehouse name, district name
     *
     * Processing Steps:
     * (1) Sort the customer table based on the balance and select the top-10 persons.
     * (2) Select all the district_id and warehouse_id from the list
     * (3) Made queries to warehouse table and district table separately to get their names
     */
    public void execute() {
        HashMap<Integer, String> districts = new HashMap<>(), warehouses = new HashMap<>();
        try {
            ResultSet customers = PreparedQueries.getCustomerWithTopBalance.executeQuery();
            while (customers.next()) {
                int districtID = customers.getInt("C_D_ID");
                int warehouseID = customers.getInt("C_W_ID");
                if (!districts.containsKey(districtID)) {
                    districts.put(districtID, "");
                }
                if (!warehouses.containsKey(warehouseID)) {
                    warehouses.put(warehouseID, "");
                }
            }

            Thread getDistrictThread = new Thread(() -> {
                try {
                    Array districtsArray = PreparedQueries.getDistrictWithIDs.getConnection().createArrayOf("INTEGER", districts.keySet().toArray());
                    PreparedQueries.getDistrictWithIDs.setArray(1, districtsArray);
                    ResultSet districtRecords = PreparedQueries.getDistrictWithIDs.executeQuery();

                    while (districtRecords.next()) {
                        districts.put(districtRecords.getInt("D_ID"), districtRecords.getString("D_NAME"));
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            getDistrictThread.start();

            Thread getWareHouseThread = new Thread(() -> {
                try {
                    Array warehousesArray = PreparedQueries.getWarehouseWithIDs.getConnection().createArrayOf("INTEGER", warehouses.keySet().toArray());
                    PreparedQueries.getWarehouseWithIDs.setArray(1, warehousesArray);
                    ResultSet warehouseRecords = PreparedQueries.getWarehouseWithIDs.executeQuery();

                    while (warehouseRecords.next()) {
                        warehouses.put(warehouseRecords.getInt("W_ID"), warehouseRecords.getString("W_NAME"));
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            getWareHouseThread.start();

            getDistrictThread.join();
            getWareHouseThread.join();

            StringBuilder sb = new StringBuilder();
            customers.beforeFirst();
            while (customers.next()) {
                String districtName = districts.get(customers.getInt("C_D_ID"));
                String warehouseName = warehouses.get(customers.getInt("C_W_ID"));
                sb.append(String.format("%s, %s, %s, %s, %s, %s",
                        customers.getString("C_FIRST"),
                        customers.getString("C_MIDDLE"),
                        customers.getString("C_LAST"),
                        customers.getString("C_BALANCE"),
                        districtName,
                        warehouseName
                ));
                sb.append("\n");
            }
            io.println(sb);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
