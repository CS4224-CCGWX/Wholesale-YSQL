package transactions;

import utils.PreparedQueries;

import java.sql.*;
import java.util.HashMap;

public class TopBalanceTransaction extends AbstractTransaction{
    public TopBalanceTransaction(Connection connection) {
        super(connection);
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
//        try {
//            ResultSet customers = this.executeQuery(PreparedQueries.getCustomerWithTopBalance);
//            ResultSetMetaData rsmd = customers.getMetaData();
//            int columnsNumber = rsmd.getColumnCount();
//            while (customers.next()) {
//                int districtID = customers.getInt("C_D_ID");
//                int warehouseID = customers.getInt("C_W_ID");
//                if (!districts.containsKey(districtID)) {
//                    districts.put(districtID, "");
//                }
//                if (!warehouses.containsKey(warehouseID)) {
//                    warehouses.put(warehouseID, "");
//                }
//
//                for (int i = 1; i <= columnsNumber; i++) {
//                    if (i > 1) System.out.print(",  ");
//                    String columnValue = customers.getString(i);
//                    System.out.print(columnValue + " " + rsmd.getColumnName(i));
//                }
//                System.out.println("");
//            }
//
//
//            Thread getDistrictThread = new Thread(() -> {
//                try {
//                    PreparedStatement getDistrictStmt = connection.prepareStatement(PreparedQueries.getDistrictWithIDs);
//                    Array districtsArray = getDistrictStmt.getConnection().createArrayOf("INTEGER", districts.keySet().toArray());
//                    getDistrictStmt.setArray(1, districtsArray);
//                    System.out.println(getDistrictStmt.toString());
//                    ResultSet districtRecords = this.executeQuery(getDistrictStmt);
//
//                    while (districtRecords.next()) {
//                        districts.put(districtRecords.getInt("D_ID"), districtRecords.getString("D_NAME"));
//                    }
//                } catch (SQLException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//            getDistrictThread.start();
//
//            Thread getWareHouseThread = new Thread(() -> {
//                try {
//                    PreparedStatement getWarehouseStmt = connection.prepareStatement(PreparedQueries.getWarehouseWithIDs);
//                    Array warehousesArray = getWarehouseStmt.getConnection().createArrayOf("INTEGER", warehouses.keySet().toArray());
//                    getWarehouseStmt.setArray(1, warehousesArray);
//                    ResultSet warehouseRecords = this.executeQuery(getWarehouseStmt);
//
//                    while (warehouseRecords.next()) {
//                        warehouses.put(warehouseRecords.getInt("W_ID"), warehouseRecords.getString("W_NAME"));
//                    }
//                } catch (SQLException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//            getWareHouseThread.start();
//
//            getDistrictThread.join();
//            getWareHouseThread.join();
//
//            StringBuilder sb = new StringBuilder();
//            customers.beforeFirst();
//            while (customers.next()) {
//                String districtName = districts.get(customers.getInt("C_D_ID"));
//                String warehouseName = warehouses.get(customers.getInt("C_W_ID"));
//                sb.append(String.format("%s, %s, %s, %s, %s",
//                        customers.getString("C_FIRST"),
//                        customers.getString("C_MIDDLE"),
//                        customers.getString("C_LAST"),
//                        customers.getString("C_BALANCE"),
//                        districtName,
//                        warehouseName
//                ));
//                sb.append("\n");
//            }
//            System.out.println(sb.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }
}
