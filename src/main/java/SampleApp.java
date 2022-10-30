import com.yugabyte.ysql.YBClusterAwareDataSource;
import parser.DataLoader;
import parser.TransactionParser;
import transactions.AbstractTransaction;
import utils.IO;
import utils.OutputFormatter;
import utils.PerformanceReportGenerator;
import utils.QueryUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
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

    private static IO io;

    /*
    args[0] - ip
    args[1] - port
    args[2] - client number
    args[3] - action
     */
    public static void main(String[] args) throws SQLException, IOException {
        if (args.length < 4) {
            System.err.println("error parameters");
            return;
        }

        String ip = args[0], port = args[1], dbUser = "yugabyte", action = args[3];
        int client = Integer.parseInt(args[2]);
        try {
            io = new IO(client);
        } catch (FileNotFoundException e) {
            System.err.println("transaction file not found");
        }


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
            run(conn, client);
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

    private static void run(Connection conn, int client) throws SQLException, IOException {
        TransactionParser transactionParser = new TransactionParser(conn, utils, io);
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

            io.println(OutputFormatter.linebreak);
            io.println(outputFormatter.formatTransactionID(latencyList.size()));

            txStart = System.nanoTime();
            try {
                transaction.execute();
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("**************************************");
                System.err.println(transaction.toString());
            }

            txEnd = System.nanoTime();
            io.println(OutputFormatter.linebreak);

            elapsedTime = txEnd - txStart;
            latencyList.add(elapsedTime);
        }
        io.close();
        fileEnd = System.nanoTime();

        long totalElapsedTime = TimeUnit.SECONDS.convert(fileEnd - fileStart, TimeUnit.NANOSECONDS);
        PerformanceReportGenerator.generatePerformanceReport(latencyList, totalElapsedTime, client);

    }
}
