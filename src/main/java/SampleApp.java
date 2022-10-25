

import com.yugabyte.ysql.YBClusterAwareDataSource;
import transactions.AbstractTransaction;
import transactions.TopBalanceTransaction;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import parser.DataLoader;

public class SampleApp {
    private static final String TABLE_NAME = "wholesale";

    public static void main(String[] args) throws SQLException {
        String dataFileDirectory = "/Users/bytedance/Desktop/CS4224/Group_project/Wholesale-YSQL/src/main/project_source/data_files";
        String schemaDirectory = "/Users/bytedance/Desktop/CS4224/Group_project/Wholesale-YSQL/src/main/resources/";
        String ysqlPath = "/Users/bytedance/Desktop/CS4224/Group_project/yugabyte/yugabyte-2.15.2.0/bin/ysqlsh";

        Properties settings = new Properties();
        try {
            settings.load(SampleApp.class.getResourceAsStream("app.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        YBClusterAwareDataSource ds = new YBClusterAwareDataSource();

//        ds.setUrl("jdbc:yugabytedb://" + settings.getProperty("host") + ":"
//                + settings.getProperty("port") + "/cs4224");
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

        Connection conn = null;
        try {
            System.out.println(">>>> before connected to YugabyteDB!");
            conn = ds.getConnection();
            System.out.println(">>>> Successfully connected to YugabyteDB!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

//        String fileName = "";
        String schemaName = "schema_v1.ysql";
        String schemaPath = schemaDirectory + schemaName;
        DataLoader dataLoader = new DataLoader(conn, schemaPath, dataFileDirectory, ysqlPath, settings);
        dataLoader.loadAll();

//
//        AbstractTransaction txn = new TopBalanceTransaction(conn);
//
//        txn.execute();
//        createDatabase(conn, );
    }

    private static void createDatabase(Connection conn, String TABLE_NAME, String filePath) throws SQLException {
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

        stmt.execute("COPY " + TABLE_NAME + " FROM " + filePath +
                " WITH (FORMAT CSV DELIMITER ',', HEADER, DISABLE_FK_CHECK) ");

        System.out.println(">>>> Successfully created " + TABLE_NAME + " table.");
    }
}
