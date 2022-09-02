

import com.yugabyte.ysql.YBClusterAwareDataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class SampleApp {
    private static final String TABLE_NAME = "wholesale";

    public static void main(String[] args) {

        Properties settings = new Properties();
        try {
            settings.load(SampleApp.class.getResourceAsStream("app.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        YBClusterAwareDataSource ds = new YBClusterAwareDataSource();

        ds.setUrl("jdbc:yugabytedb://" + settings.getProperty("host") + ":"
                + settings.getProperty("port") + "/yugabyte");
        ds.setUser(settings.getProperty("dbUser"));
        ds.setPassword(settings.getProperty("dbPassword"));

        String sslMode = settings.getProperty("sslMode");
        if (!sslMode.isEmpty() && !sslMode.equalsIgnoreCase("disable")) {
            ds.setSsl(true);
            ds.setSslMode(sslMode);

            if (!settings.getProperty("sslRootCert").isEmpty())
                ds.setSslRootCert(settings.getProperty("sslRootCert"));
        }

        try {
            Connection conn = ds.getConnection();
            System.out.println(">>>> Successfully connected to YugabyteDB!");

            createDatabase(conn, "warehouse");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createDatabase(Connection conn, String TABLE_NAME) throws SQLException {
        Statement stmt = conn.createStatement();

        stmt.execute("DROP TABLE IF EXISTS " + TABLE_NAME);

        stmt.execute("CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                "(" +
                "W_ID int," +
                "W_NAME varchar(10)," +
                "W_STREET_1 varchar(20)," +
                "W_STREET_2 varchar(20)," +
                "W_CITY varchar(20)," +
                "W_STATE char(2)," +
                "W_ZIP char(9)," +
                "W_TAX decimal(4,4)," +
                "W_YTD decimal(12,2), " +
                "PRIMARY KEY (W_ID)" +
                ")");

//        stmt.execute("INSERT INTO " + TABLE_NAME + " VALUES" +
//                "(1, 'Jessica', 28, 'USA', 10000)," +
//                "(2, 'John', 28, 'Canada', 9000)");

        System.out.println(">>>> Successfully created " + TABLE_NAME + " table.");
    }
}
