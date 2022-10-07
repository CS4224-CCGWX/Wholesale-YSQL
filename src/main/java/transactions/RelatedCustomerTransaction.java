package transactions;

import utils.QueryUtils;
import java.util.*;
import java.sql.Connection;
import java.sql.SQLException;

public class RelatedCustomerTransaction extends AbstractTransaction{
    Connection conn;
    int warehouseID, districtID, customerID;
    QueryUtils queryUtils;

    public RelatedCustomerTransaction(Connection conn, int warehouseID, int districtID, int customerID) {
        super(conn);
        this.warehouseID = warehouseID;
        this.districtID = districtID;
        this.customerID = customerID;
        this.queryUtils = new QueryUtils(connection);
    }

    @Override
    public void execute() {
        try {
            List<Integer> result = queryUtils.getRelatedCustomers(warehouseID, districtID, customerID);
            System.out.println(result);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
