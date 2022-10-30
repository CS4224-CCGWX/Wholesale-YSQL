package transactions;

import java.sql.*;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTransaction {
    protected Connection connection;

//    private ConsistencyLevel defaultConsistencyLevel;
    private final int defaultTimeout = 5;

    private static Map<String, PreparedStatement> preparedStatementHashMap = new HashMap<>();

    AbstractTransaction(Connection connection) {
        this.connection = connection;
    }

    protected ResultSet executeQueryWithTimeout(String query, int timeout, Object... values) throws SQLException {
        PreparedStatement preparedStatement;
        if (preparedStatementHashMap.containsKey(query)) {
            preparedStatement = preparedStatementHashMap.get(query);
        } else {
            preparedStatement = connection.prepareStatement(query);
            preparedStatementHashMap.put(query, preparedStatement);
        }
       preparedStatement.setQueryTimeout(Duration.ofMillis(timeout).toSecondsPart());
        ResultSet res = this.executeQuery(preparedStatement);
        return res;
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

    public void execute() throws SQLException {
        return;
    }

    ;

//    public void setDefaultConsistencyLevel(String s) {
//        ConsistencyLevel level;
//        switch (s) {
//            case "any": level = ConsistencyLevel.ANY;break;
//            case "one": level = ConsistencyLevel.ONE;break;
//            case "two": level = ConsistencyLevel.TWO;break;
//            case "three": level = ConsistencyLevel.THREE;break;
//            case "quorum": level = ConsistencyLevel.QUORUM;break;
//            case "all": level = ConsistencyLevel.ALL;break;
//            case "local_quorum": level = ConsistencyLevel.LOCAL_QUORUM;break;
//            case "each_quorum": level = ConsistencyLevel.EACH_QUORUM;break;
//            case "serial": level = ConsistencyLevel.SERIAL;break;
//            case "local_serial": level = ConsistencyLevel.LOCAL_SERIAL;break;
//            case "local_one": level = ConsistencyLevel.LOCAL_ONE;break;
//            default:level = ConsistencyLevel.ALL;break;
//        }
//        this.defaultConsistencyLevel = level;
//    }
}
