package transactions;

import java.sql.*;

public abstract class AbstractTransaction {
    protected Connection connection;

    AbstractTransaction(Connection connection) {
        this.connection = connection;
    }

    public abstract void execute();
}
