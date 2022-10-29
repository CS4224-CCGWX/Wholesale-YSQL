import com.yugabyte.ysql.YBClusterAwareDataSource;
import transactions.AbstractTransaction;
import transactions.NewOrderTransaction;
import parser.*;

import utils.*;


import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class SampleApp {

        static Connection conn = getCloudSession();

    public static void main(String[] args) throws SQLException {
        String action = args[0];
        switch(action) {
            case "load_data": {
                loadData(args);
                break;
            }
            case "run": {
                run(args);
                break;
            }
//            case "summary": {
//                summary(args);
//                break;
//            }
            default: {
                System.err.printf("Action: %s not specified", action);
            }
        }
    }

    private static void loadData(String[] args) throws SQLException {
        // Load partial data on the cloud
        System.out.println("Loading partial data to cloud");
        defSchema(conn);
//        insertSomeData(conn);
        System.out.println("Finished");
    }

    private static void runTransaction() throws SQLException {
        runNewOrderTransaction();
    }

    private static void runNewOrderTransaction() throws SQLException {
        TransactionParser tp =  new TransactionParser(conn);
        String[] newOrderInput = new String[5];
        NewOrderTransaction not = tp.parseNewOrderTransaction(newOrderInput);
        not.execute();
    }

//    private static void summary(String[] args) {
//        String ip = args[1];
//        CqlSession session = getSessionByIp(ip);
//
//        AbstractTransaction summaryTransaction = new SummaryTransaction(session);
//        summaryTransaction.execute();
//
//        session.close();
//    }

//    private static CqlSession getSessionByIp(String ip) {
//        return CqlSession
//                .builder()
//                .addContactPoint(new InetSocketAddress(ip, 9042))
//                .withLocalDatacenter("datacenter1")
//                .withKeyspace(CqlIdentifier.fromCql("wholesale"))
//                .build();
//    }

    private static void run(String[] args) {
        String ip = args[1];
        String consistencyLevel = "";

//        CqlSession session = getSessionByIp(ip);


        TransactionParser transactionParser = new TransactionParser(conn);
        OutputFormatter outputFormatter = new OutputFormatter();

        List<Long> latencyList = new ArrayList<>();
        long fileStart, fileEnd, txStart, txEnd, elapsedTime;

        fileStart = System.nanoTime();
        while (transactionParser.hasNext()) {
            AbstractTransaction transaction = transactionParser.parseNextTransaction();
            System.out.println(OutputFormatter.linebreak);
            System.out.println(outputFormatter.formatTransactionID(latencyList.size()));
            if (args.length >= 2) {
                consistencyLevel = args[2];
                transaction.setDefaultConsistencyLevel(consistencyLevel);
            }
            txStart = System.nanoTime();
            try {
                transaction.execute();
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("**************************************");
                System.err.println(transaction.toString());
//                exit(-1);
            }

            txEnd = System.nanoTime();
            System.out.println(OutputFormatter.linebreak);

            elapsedTime = txEnd - txStart;
            latencyList.add(elapsedTime);
        }
        fileEnd = System.nanoTime();

        long totalElapsedTime = TimeUnit.SECONDS.convert(fileEnd - fileStart, TimeUnit.NANOSECONDS);
        PerformanceReportGenerator.generatePerformanceReport(latencyList, totalElapsedTime);

//        conn.close();
        transactionParser.close();
    }

    private static Connection getCloudSession() {

        Properties settings = new Properties();
        try {
            settings.load(SampleApp.class.getResourceAsStream("app.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        YBClusterAwareDataSource ds = new YBClusterAwareDataSource();

        ds.setUrl("jdbc:yugabytedb://" + settings.getProperty("host") + ":"
                + settings.getProperty("port") + "/yugabyte");
        ds.setUser(settings.getProperty("dbUser"));
        ds.setPassword(settings.getProperty("dbPassword"));

        try {
            System.out.println(">>>> before connected to YugabyteDB!");
            conn = ds.getConnection();
            System.out.println(">>>> Successfully connected to YugabyteDB!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return conn;
    }
    private static void defSchema(Connection conn) throws SQLException {
        // def schema
        Statement session = conn.createStatement();
        session.execute("DROP TABLE IF EXISTS warehouse;");
        session.execute("CREATE TABLE IF NOT EXISTS warehouse (\n" +
                "    W_ID int,\n" +
                "    W_NAME varchar,\n" +
                "    W_STREET_1 varchar,\n" +
                "    W_STREET_2 varchar,\n" +
                "    W_CITY varchar,\n" +
                "    W_STATE text,\n" +
                "    W_ZIP text,\n" +
                "    W_TAX decimal,\n" +
                "    W_YTD decimal,\n" +
                "    PRIMARY KEY (W_ID)\n" +
                ");");
        session.execute("DROP TABLE IF EXISTS district;");
        session.execute("CREATE TABLE IF NOT EXISTS district (\n" +
                "    D_W_ID int,\n" +
                "    D_ID int,\n" +
                "    D_NAME varchar,\n" +
                "    D_STREET_1 varchar,\n" +
                "    D_STREET_2 varchar,\n" +
                "    D_CITY varchar,\n" +
                "    D_STATE text,\n" +
                "    D_ZIP text,\n" +
                "    D_TAX decimal,\n" +
                "    D_YTD decimal,\n" +
                "    D_NEXT_O_ID int,\n" +
                "    D_NEXT_DELIVER_O_ID int,\n" +
                "    PRIMARY KEY ((D_W_ID, D_ID))\n" +
                ");");
        session.execute("DROP TABLE IF EXISTS customer;");
        session.execute("CREATE TABLE IF NOT EXISTS customer (\n" +
                "    C_W_ID int,\n" +
                "    C_D_ID int,\n" +
                "    C_ID int,\n" +
                "    C_FIRST varchar,\n" +
                "    C_MIDDLE text,\n" +
                "    C_LAST varchar,\n" +
                "    C_STREET_1 varchar,\n" +
                "    C_STREET_2 varchar,\n" +
                "    C_CITY varchar,\n" +
                "    C_STATE text,\n" +
                "    C_ZIP text,\n" +
                "    C_PHONE text,\n" +
                "    C_SINCE timestamp,\n" +
                "    C_CREDIT text,\n" +
                "    C_CREDIT_LIM decimal,\n" +
                "    C_DISCOUNT decimal,\n" +
                "    C_BALANCE decimal,\n" +
                "    C_YTD_PAYMENT float,\n" +
                "    C_PAYMENT_CNT int,\n" +
                "    C_DELIVERY_CNT int,\n" +
                "    C_DATA varchar,\n" +
                "    PRIMARY KEY ((C_W_ID, C_D_ID, C_ID))\n" +
                ");");
        session.execute("DROP TABLE IF EXISTS \"order\";");
        session.execute("CREATE TABLE IF NOT EXISTS \"order\" (\n" +
                "    O_W_ID int,\n" +
                "    O_D_ID int,\n" +
                "    O_ID int,\n" +
                "    O_C_ID int,\n" +
                "    O_CARRIER_ID int,\n" +
                "    O_OL_CNT int,\n" +
                "    O_ALL_LOCAL int,\n" +
                "    O_ENTRY_D timestamp,\n" +
                "    PRIMARY KEY ((O_W_ID, O_D_ID), O_ID)\n" +
                ");");
        session.execute("DROP TABLE IF EXISTS item;");
        session.execute("CREATE TABLE IF NOT EXISTS item (\n" +
                "    I_ID int,\n" +
                "    I_NAME varchar,\n" +
                "    I_PRICE decimal,\n" +
                "    I_IM_ID int,\n" +
                "    I_DATA varchar,\n" +
                "    PRIMARY KEY (I_ID)\n" +
                ");");
        session.execute("DROP TABLE IF EXISTS order_line;");
        session.execute("CREATE TABLE IF NOT EXISTS order_line (\n" +
                "    OL_W_ID int,\n" +
                "    OL_D_ID int,\n" +
                "    OL_O_ID int,\n" +
                "    OL_NUMBER int,\n" +
                "    OL_I_ID int,\n" +
                "    OL_DELIVERY_D timestamp,\n" +
                "    OL_AMOUNT decimal,\n" +
                "    OL_SUPPLY_W_ID int,\n" +
                "    OL_QUANTITY decimal,\n" +
                "    OL_DIST_INFO varchar,\n" +
                "    OL_C_ID int,\n" +
                "    PRIMARY KEY ((OL_W_ID, OL_D_ID), OL_O_ID, OL_NUMBER)\n" +
                ");");
        session.execute("DROP TABLE IF EXISTS stock;");
        session.execute("CREATE TABLE IF NOT EXISTS stock (\n" +
                "    S_W_ID int,\n" +
                "    S_I_ID int,\n" +
                "    S_QUANTITY decimal,\n" +
                "    S_YTD decimal,\n" +
                "    S_ORDER_CNT int,\n" +
                "    S_REMOTE_CNT int,\n" +
                "    S_DIST_01 text,\n" +
                "    S_DIST_02 text,\n" +
                "    S_DIST_03 text,\n" +
                "    S_DIST_04 text,\n" +
                "    S_DIST_05 text,\n" +
                "    S_DIST_06 text,\n" +
                "    S_DIST_07 text,\n" +
                "    S_DIST_08 text,\n" +
                "    S_DIST_09 text,\n" +
                "    S_DIST_10 text,\n" +
                "    S_DATA varchar,\n" +
                "    PRIMARY KEY ((S_W_ID, S_I_ID))\n" +
                ");");
    }

    private static void insertSomeData(Connection conn) throws SQLException {
        Statement session = conn.createStatement();
        session.execute("INSERT INTO warehouse (W_ID, W_NAME, W_STREET_1, W_STREET_2, W_CITY, W_STATE, W_ZIP, W_TAX, W_YTD) VALUES (1,'sxvnjhpd','dxvcrastvybcwvmgnyk','xvzxkgxtspsjdgylue','qflaqlocfljbepowfn','OM',123456789,0.0384,300000.0);");
        session.execute("INSERT INTO district (D_W_ID, D_ID, D_NAME, D_STREET_1, D_STREET_2, D_CITY, D_STATE, D_ZIP, D_TAX, D_YTD, D_NEXT_O_ID) VALUES (1,1,'byiavt','tbbvflmyew','fpezdooohykpmx','oelrbuwtpmf','JV',123456789,0.1687,30000.0,3001);");
    }
}
