import parser.DataLoader;
import parser.TransactionParser;
import transactions.AbstractTransaction;
import utils.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class SampleApp {
    private static Connection conn;

    private static DataLoader dataLoader;
    private static QueryUtils utils;

    private static IO io;

    private static Map<String, Long> hm = new HashMap<>();

    private static Map<String, Long> hm_count = new HashMap<>();
    private static Map<String, Long> hm_retry = new HashMap<>();

    private static int retryTimes = 2;

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
        Properties settings = new Properties();
        try {
            settings.load(SampleApp.class.getResourceAsStream("app.properties"));
            io = new IO(client);
            io.setFilePath(settings.getProperty("inputFilePath"));
            conn = connectToDB(ip, port);
            PreparedQueries.init(conn);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("initialize error");
        }

        PerformanceReportGenerator.setFilePath(settings.getProperty("reportFilePath"), client);
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
        String url = String.format("jdbc:postgresql://%s:%s/yugabyte", ip, port);
        return DriverManager.getConnection(url, "yugabyte", "");
    }

    private static void run(Connection conn, int client) throws SQLException, IOException {
        TransactionParser transactionParser = new TransactionParser(conn, utils, io);
        OutputFormatter outputFormatter = new OutputFormatter();
        long totalNumberOfRetry = 0;
        List<Long> latencyList = new ArrayList<>();
        long fileStart, fileEnd, txStart, txEnd, elapsedTime, txIndividualStart, txIndividualEnd, elapsedTimeForIndividual;

        fileStart = System.nanoTime();
        AbstractTransaction transaction;
        System.err.println("******** Current Client: " + client + " **********");
        int totalTransaction = 0;
        while (transactionParser.hasNext()) {
            totalTransaction += 1;
            try {
                transaction = transactionParser.parseNextTransaction();
            } catch (SQLException e) {
                e.printStackTrace();
                continue;
            }

            io.println(OutputFormatter.linebreak);
            io.println(outputFormatter.formatTransactionID(latencyList.size()));

            txStart = System.nanoTime();
            int i = 0;
            for (; i <= retryTimes; i++) {
                try {
                    transaction.execute();
                    txEnd = System.nanoTime();
                    elapsedTime = TimeUnit.SECONDS.convert(txEnd - txStart, TimeUnit.NANOSECONDS);
                    String[] s = transaction.toString().split(" ");
                    String curS = s[0];

                    long defaultValue = 0;
                    hm.put(curS, hm.getOrDefault(curS, defaultValue) + elapsedTime);
                    hm_count.put(curS, hm_count.getOrDefault(curS, defaultValue) + 1);
                    latencyList.add(elapsedTime);
                    System.out.println("Time used: " + elapsedTime);
                    break;
                } catch (Exception e) {
                    String[] s = transaction.toString().split(" ");
                    String curS = s[0];
                    long defaultValue = 0;
                    hm_retry.put(curS, hm_retry.getOrDefault(curS, defaultValue) + 1);

                    totalNumberOfRetry = totalNumberOfRetry + 1;
                    System.out.println("[out]retry counter: " + i);
                    System.err.println("[err]retry counter: " + i);
                    System.out.println(transaction.toString());
                    System.err.println(transaction.toString());
                    io.println("[io]retry counter: " + i);
                    e.printStackTrace();
                    io.println("**************************************");
                    System.out.println("**************************************");
                    System.err.println("**************************************");
                }
            }
            if (i > retryTimes) {
                System.out.println("transaction " + transaction.toString() + "fails after 3 attempts");
                io.println("transaction " + transaction.toString() + "fails after 3 attempts");
                System.err.println("transaction " + transaction.toString() + "fails after 3 attempts");
            }

            io.println(OutputFormatter.linebreak);
        }
        io.close();
        fileEnd = System.nanoTime();

        long totalElapsedTime = TimeUnit.SECONDS.convert(fileEnd - fileStart, TimeUnit.NANOSECONDS);
        PerformanceReportGenerator.generatePerformanceReport(latencyList, totalElapsedTime, client, hm, hm_count, hm_retry, totalTransaction, totalNumberOfRetry);

    }
}
