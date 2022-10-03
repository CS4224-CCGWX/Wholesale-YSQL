package transactions;

import java.sql.*;

public abstract class AbstractTransaction {
    protected Connection connection;

    AbstractTransaction(Connection connection) {
        this.connection = connection;
    }

    protected ResultSet executeQuery(String query) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(query);
    }

    protected ResultSet executeQuery(PreparedStatement stmt) throws SQLException {
        return stmt.executeQuery();
    }
}
