package parser;

import transactions.*;
import utils.IO;
import utils.QueryUtils;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class TransactionParser {
    private IO io;
    Connection session;

    QueryUtils utils;
    final String SEPARATOR = ",";

    public TransactionParser(Connection session, QueryUtils utils, IO io) {
        this.session = session;
        this.utils = utils;
        this.io = io;
    }

    public boolean hasNext() {
        return this.io.hasMoreTokens();
    }

    public void close() {
        this.io.close();
    }

    public AbstractTransaction parseNextTransaction() throws SQLException {
        if (!io.hasMoreTokens()) return null;


        String line = io.getLine();
        String[] inputs = line.split(SEPARATOR);
        String txType = inputs[0];

        if (txType.equals("N")) {
            return parseNewOrderTransaction(inputs);
        } else if (txType.equals("P")) {
            return parsePaymentTransaction(inputs);
        } else if (txType.equals("D")) {
            return parseDeliveryTransaction(inputs);
        } else if (txType.equals("O")) {
            return parseOrderStatusTransaction(inputs);
        } else if (txType.equals("S")) {
            return parseStockLevelTransaction(inputs);
        } else if (txType.equals("I")) {
            return parsePopularItemTransaction(inputs);
        } else if (txType.equals("T")) {
            return parseTopBalanceTransaction();
        } else if (txType.equals("R")) {
            return parseRelatedCustomerTransaction(inputs);
        } else {
            throw new RuntimeException("Invalid type of transaction");
        }
    }

    public NewOrderTransaction parseNewOrderTransaction(String[] inputs) {
        int index = 1;
        int c_id = Integer.parseInt(inputs[index++]);
        int w_id = Integer.parseInt(inputs[index++]);
        int d_id = Integer.parseInt(inputs[index++]);

        int m = Integer.parseInt(inputs[index++]);
        ArrayList<Integer> i_ids = new ArrayList<>();
        ArrayList<Integer> w_ids = new ArrayList<>();
        ArrayList<Integer> quantities = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            String line = io.getLine();
            inputs = line.split(SEPARATOR);
            i_ids.add(Integer.parseInt(inputs[0]));
            w_ids.add(Integer.parseInt(inputs[1]));
            quantities.add(Integer.parseInt(inputs[2]));
        }

        return new NewOrderTransaction(session, io, c_id, w_id, d_id, m, i_ids, w_ids, quantities);
    }

    public PaymentTransaction parsePaymentTransaction(String[] inputs) throws SQLException {
        int index = 1;
        int w_id = Integer.parseInt(inputs[index++]);
        int d_id = Integer.parseInt(inputs[index++]);
        int c_id = Integer.parseInt(inputs[index++]);
        double payment = Double.parseDouble(inputs[index++]);

        return new PaymentTransaction(session, io, w_id, d_id, c_id, payment);
    }

    public DeliveryTransaction parseDeliveryTransaction(String[] inputs) throws SQLException {
        int index = 1;
        int w_id = Integer.parseInt(inputs[index++]);
        int carrier_id = Integer.parseInt(inputs[index++]);
        return new DeliveryTransaction(session, io, w_id, carrier_id);
    }

    public OrderStatusTransaction parseOrderStatusTransaction(String[] inputs) throws SQLException {
        int index = 1;
        int w_id = Integer.parseInt(inputs[index++]);
        int d_id = Integer.parseInt(inputs[index++]);
        int c_id = Integer.parseInt(inputs[index++]);
        return new OrderStatusTransaction(session, io, w_id, d_id, c_id);
    }

    private StockLevelTransaction parseStockLevelTransaction(String[] inputs) throws SQLException {
        int index = 1;
        int w_id = Integer.parseInt(inputs[index++]);
        int d_id = Integer.parseInt(inputs[index++]);
        int t = Integer.parseInt(inputs[index++]);
        int l = Integer.parseInt(inputs[index++]);

        return new StockLevelTransaction(session, io, utils, w_id, d_id, t, l);
    }

    private PopularItemTransaction parsePopularItemTransaction(String[] inputs) {
        int index = 1;
        int w_id = Integer.parseInt(inputs[index++]);
        int d_id = Integer.parseInt(inputs[index++]);
        int l = Integer.parseInt(inputs[index++]);
        return new PopularItemTransaction(session, io, utils, w_id, d_id, l);
    }

    private TopBalanceTransaction parseTopBalanceTransaction() throws SQLException {
        return new TopBalanceTransaction(session, io, utils);
    }

    private RelatedCustomerTransaction parseRelatedCustomerTransaction(String[] inputs) throws SQLException {
        int index = 1;
        int w_id = Integer.parseInt(inputs[index++]);
        int d_id = Integer.parseInt(inputs[index++]);
        int c_id = Integer.parseInt(inputs[index++]);
        return new RelatedCustomerTransaction(session, io, utils, w_id, d_id, c_id);
    }
}

