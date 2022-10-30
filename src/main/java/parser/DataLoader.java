package parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.Properties;
public class DataLoader {
    private final String DELIM = ",";
    private final String WAREHOUSE_FILE = "warehouse.csv";
    private final String CUSTOMER_FILE = "customer.csv";
    private final String DISTRICT_FILE = "district.csv";
    private final String ITEM_FILE = "item.csv";
    private final String ORDER_FILE = "order.csv";
    private final String ORDER_LINE_FILE = "order-line.csv";
    private final String STOCK_FILE = "stock.csv";

    private final String YSQLSH_PATH = "/home/stuproj/cs4224i/yugabyte-2.14.2.0/bin/ysqlsh";

    private final String schemaPath = "/home/stuproj/cs4224i/project_files/data_files/schema.sql";

    private final String dataDir = "/home/stuproj/cs4224i/project_files/data_files";

    Connection session;

    Properties settings;


    public DataLoader(Connection session, Properties settings) {
        this.session = session;
        this.settings = settings;
    }

    public void loadAll() {
        defSchema();
        loadWarehouse();
        loadDistrict();
        loadCustomer();
        loadOrder();
        loadItem();
        loadOrderLine();
        loadStock();
    }

    private void runInShell(String command) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(command);

        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String line = in.readLine();
        while (line != null) {
            System.out.println(line);
            line = in.readLine();
        }
        line = err.readLine();
        while(line != null) {
            System.err.println(line);
            line = err.readLine();
        }
        process.waitFor();

        in.close();
        err.close();
    }

    private void defSchema() {
        System.out.println("Load Schema: \n");
        try {
            System.out.println(String.format("YSQL Path: %s \n", YSQLSH_PATH));
            System.out.println(String.format("Schema Path: %s \n", schemaPath));
            Runtime.getRuntime().exec(String.format("%s -f %s -h %s -p %s -U %s", YSQLSH_PATH, schemaPath, settings.getProperty("host"), settings.getProperty("port"), settings.getProperty("dbUser")));
            System.out.println("successfully load: \n");
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    private void loadWarehouse() {
        System.out.println("loadWarehouse: \n");
        try {
            String path = dataDir + "/" + WAREHOUSE_FILE;
            this.runInShell(String.format(
                    "%s -c \"\\COPY warehouse FROM %s WITH DELIMITER '%s';\"", YSQLSH_PATH, path, DELIM));
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    private void loadDistrict() {
        System.out.println("loadDistrict: \n");
        try {
            String path = dataDir + "/" + DISTRICT_FILE;
            this.runInShell(String.format(
                    "%s -c \"\\COPY district FROM %s DELIMITER '%s';\"", YSQLSH_PATH, path, DELIM));
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    private void loadCustomer() {
        try {
            String path = dataDir + "/" + CUSTOMER_FILE;
            this.runInShell(String.format(
                    "%s -c \"\\COPY customer FROM %s DELIMITER '%s';\"", YSQLSH_PATH, path, DELIM));
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    private void loadOrder() {
        try {
            String path = dataDir + "/" + ORDER_FILE;
            this.runInShell(String.format(
                    "%s -c \"\\COPY \"order\" FROM %s DELIMITER '%s';\"", YSQLSH_PATH, path, DELIM));
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    private void loadItem() {
        try {
            String path = dataDir + "/" + ITEM_FILE;
            this.runInShell(String.format(
                    "%s -c \"\\COPY item FROM %s DELIMITER '%s';\"", YSQLSH_PATH, path, DELIM));
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    private void loadOrderLine() {
        try {
            String path = dataDir + "/" + ORDER_LINE_FILE;
            this.runInShell(String.format(
                    "%s -c \"\\COPY order_line FROM %s DELIMITER '%s';\"", YSQLSH_PATH, path, DELIM));
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    private void loadStock() {
        try {
            String path = dataDir + "/" + STOCK_FILE;
            this.runInShell(String.format(
                    "%s -c \"\\COPY stock FROM %s DELIMITER '%s';\"", YSQLSH_PATH, path, DELIM));
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

}
