package transactions;

import utils.QueryUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RelatedCustomerTransaction extends AbstractTransaction{
    Connection conn;
    int warehouseID, districtID, customerID;
    QueryUtils queryUtils;

    public RelatedCustomerTransaction(Connection conn, QueryUtils utils, int warehouseID, int districtID, int customerID) {
        super(conn);
        this.warehouseID = warehouseID;
        this.districtID = districtID;
        this.customerID = customerID;
        this.queryUtils = utils;
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
