package transactions;

import java.sql.*;
import java.util.Formatter;

public abstract class AbstractTransaction {
    protected Connection connection;

    AbstractTransaction(Connection connection) {
        this.connection = connection;
    }

    protected ResultSet executeQuery(String query) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(query);
    }

    protected String stringFormatter(String format, Object... args) {
        return new Formatter().format(format, args).toString();
    }

    protected ResultSet executeQuery(PreparedStatement stmt) throws SQLException {
        return stmt.executeQuery();
    }
}
