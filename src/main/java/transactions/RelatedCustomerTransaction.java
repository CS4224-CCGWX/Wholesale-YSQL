package transactions;

import java.sql.Connection;

public class RelatedCustomerTransaction extends AbstractTransaction{
    Connection conn;
    int warehouseID, districtID, customerID;

    public RelatedCustomerTransaction(Connection conn, int warehouseID, int districtID, int customerID) {
        super(conn);
        this.warehouseID = warehouseID;
        this.districtID = districtID;
        this.customerID = customerID;
    }

    @Override
    public void execute() {

    }
}
