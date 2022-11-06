package transactions;

import utils.IO;

import java.sql.*;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTransaction {
    protected Connection connection;

    protected IO io;

    private static Map<String, PreparedStatement> preparedStatementHashMap = new HashMap<>();

    AbstractTransaction(Connection connection, IO io) throws SQLException {
        this.connection = connection;
        this.io = io;
    }

    protected ResultSet executeQuery(String query) throws SQLException {
        Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        return stmt.executeQuery(query);
    }

    protected String stringFormatter(String format, Object... args) {
        return String.format(format, args);
    }

    protected ResultSet executeQuery(PreparedStatement stmt) throws SQLException {
        return stmt.executeQuery();
    }

    protected int executeUpdate(PreparedStatement stmt) throws SQLException {
        return stmt.executeUpdate();
    }

    public void execute() throws SQLException {
        return;
    }
}
