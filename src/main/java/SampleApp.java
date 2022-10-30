import com.yugabyte.ysql.YBClusterAwareDataSource;
import parser.DataLoader;
import parser.TransactionParser;
import transactions.AbstractTransaction;
import utils.OutputFormatter;
import utils.PerformanceReportGenerator;
import utils.QueryUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class SampleApp {
    private static Connection conn;

    private static DataLoader dataLoader;
    private static QueryUtils utils;

    /*
    args[0] - ip     args[1] - port
     */
    public static void main(String[] args) throws SQLException {
        if (args.length < 3) {
            System.err.println("error parameters");
            return;
        }

        String ip = args[0], port = args[1], dbUser = "yugabyte", action = args[2];
        try {
            conn = connectToDB(ip, port);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        dataLoader = new DataLoader(conn, ip, port, dbUser);
        utils = new QueryUtils(conn);

        if (action.equals("load")) {
            dataLoader.loadAll();
        } else if (action.equals("run")) {
            run(conn);
        } else {
            System.err.printf("Action: %s not specified", action);
        }
    }

    private static Connection connectToDB(String ip, String port) throws Exception {
        YBClusterAwareDataSource ds = new YBClusterAwareDataSource();
        ds.setUrl(String.format("jdbc:yugabytedb://%s:%s/yugabyte", ip, port));
        ds.setUser("yugabyte");
        return ds.getConnection();
    }

    private static void run(Connection conn) throws SQLException {
        TransactionParser transactionParser = new TransactionParser(conn, utils);
        OutputFormatter outputFormatter = new OutputFormatter();

        List<Long> latencyList = new ArrayList<>();
        long fileStart, fileEnd, txStart, txEnd, elapsedTime;

        fileStart = System.nanoTime();
        AbstractTransaction transaction;
        while (transactionParser.hasNext()) {
            try {
                transaction = transactionParser.parseNextTransaction();
            } catch (SQLException e) {
                e.printStackTrace();
                continue;
            }

            System.out.println(OutputFormatter.linebreak);
            System.out.println(outputFormatter.formatTransactionID(latencyList.size()));

            txStart = System.nanoTime();
            try {
                transaction.execute();
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("**************************************");
                System.err.println(transaction.toString());
            }

            txEnd = System.nanoTime();
            System.out.println(OutputFormatter.linebreak);

            elapsedTime = txEnd - txStart;
            latencyList.add(elapsedTime);
        }
        fileEnd = System.nanoTime();

        long totalElapsedTime = TimeUnit.SECONDS.convert(fileEnd - fileStart, TimeUnit.NANOSECONDS);
        PerformanceReportGenerator.generatePerformanceReport(latencyList, totalElapsedTime);

        transactionParser.close();
    }
}
