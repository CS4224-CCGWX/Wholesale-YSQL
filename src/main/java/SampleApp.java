import com.yugabyte.ysql.YBClusterAwareDataSource;
import parser.DataLoader;
import parser.TransactionParser;
import transactions.AbstractTransaction;
import utils.OutputFormatter;
import utils.PerformanceReportGenerator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class SampleApp {

    public static void main(String[] args) throws SQLException {
        Connection conn;
        Properties settings = new Properties();
        try {
            settings.load(SampleApp.class.getResourceAsStream("app.properties"));
            conn = connectToDB(settings);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        DataLoader dataLoader = new DataLoader(conn, settings);

        String action = args[0];
        switch (action) {
            case "load" -> {
                dataLoader.loadAll();
            }
            case "run" -> {
                run(args, conn);
            }

//            case "summary": {
//                summary(args);
//                break;
//            }
            default -> {
                System.err.printf("Action: %s not specified", action);
            }
        }
    }

    private static Connection connectToDB(Properties settings) throws Exception {
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

        return ds.getConnection();
    }

    private static void run(String[] args, Connection conn) {
        TransactionParser transactionParser = new TransactionParser(conn);
        OutputFormatter outputFormatter = new OutputFormatter();

        List<Long> latencyList = new ArrayList<>();
        long fileStart, fileEnd, txStart, txEnd, elapsedTime;

        fileStart = System.nanoTime();
        while (transactionParser.hasNext()) {
            AbstractTransaction transaction = transactionParser.parseNextTransaction();
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
