package transactions;

import utils.IO;
import utils.PreparedQueries;
import utils.TimeFormatter;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewOrderTransaction extends AbstractTransaction {

    private final int customerId;
    private final int warehouseId;
    private final int districtId;
    private final int nOrderLines;

    private final List<Integer> itemIds;
    private final List<Integer> quantities;
    private final List<Integer> supplyWarehouseIds;

    private static final int STOCK_REFILL_THRESHOLD = 10;
    private static final int STOCK_REFILL_QTY = 100;


    public NewOrderTransaction(Connection connection, IO io, int cid, int wid, int did, int n,
                               List<Integer> itemIds, List<Integer> quantities, List<Integer> supplyWarehouseIds) throws SQLException {
        super(connection, io);
        customerId = cid;
        warehouseId = wid;
        districtId = did;
        nOrderLines = n;
        this.itemIds = itemIds;
        this.quantities = quantities;
        this.supplyWarehouseIds = supplyWarehouseIds;
    }

    public String distIdStr(int distId) {
        assert(distId >= 1 && distId <= 10);

        if(distId < 10) {
            return "S_DIST_" + "0" + Integer.toString(distId);
        } else {
            return "S_DIST_" + Integer.toString(distId);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("New **** New Order Transaction Information ****\n");
        sb.append(String.format("CID:%d, WID:%d, DID:%d, num order-lines:%d\n", customerId, warehouseId, districtId, nOrderLines));
        sb.append("Item IDs are:");
        for (int id : itemIds) {
            sb.append(",").append(id);
        }
        sb.append("\n");
        sb.append("Quantities:");
        for (int qty : quantities) {
            sb.append(",").append(qty);
        }
        sb.append("\n");
        sb.append("Supply warehouse IDs:");
        for (int id : supplyWarehouseIds) {
            sb.append(",").append(id);
        }
        sb.append("\n");

        return sb.toString();
    }

    public void execute() throws SQLException {

        try {
            connection.setReadOnly(false);
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

         /*
          1. N denotes the next available order number D_NEXT_O_ID for district (W_ID, D_ID)
          Update district (W_ID, D_ID) by incrementing D_NEXT_O_ID by 1.
         */

//        this.executeQuery("BEGIN TRANSACTION;");
            ResultSet res;

            PreparedQueries.getDistrictNextOrderIdAndTax.setInt(1, warehouseId);
            PreparedQueries.getDistrictNextOrderIdAndTax.setInt(2, districtId);
            res = this.executeQuery(PreparedQueries.getDistrictNextOrderIdAndTax);

            ResultSet districtInfo = res;
            if (!res.next()) {
                error("formattedNextOrderIdAndTax");
                throw new SQLException();
            }
            int orderId = res.getInt("D_NEXT_O_ID");


            PreparedQueries.incrementDistrictNextOrderId.setInt(1, warehouseId);
            PreparedQueries.incrementDistrictNextOrderId.setInt(2, districtId);

            this.executeUpdate(PreparedQueries.incrementDistrictNextOrderId);



        /*
          2. Create new order with:
          O_ID = N
          O_D_ID = D_ID
          O_W_ID = W_ID
          O_C_ID = C_ID
          O_ENTRY_D = Current date and time
          O_CARRIER_ID = null
          O_OL_CNT = NUM_ITEMS
          O_ALL_LOCAL = 0 if exists i in [1, NUM_ITEMS] such that SUPPLIER_WAREHOUSE[i] != W_ID;
                          otherwise O_ALL_LOCAL = 1
         */
            int isAllLocal = 1;
            for (int supplyWarehouseId : supplyWarehouseIds) {
                if (supplyWarehouseId != warehouseId) {
                    isAllLocal = 0;
                    break;
                }
            }


            Date orderDateTime = TimeFormatter.getCurrentDate();
            PreparedQueries.createNewOrder.setInt(1, orderId);
            PreparedQueries.createNewOrder.setInt(2, districtId);
            PreparedQueries.createNewOrder.setInt(3, warehouseId);
            PreparedQueries.createNewOrder.setInt(4, customerId);
            PreparedQueries.createNewOrder.setTimestamp(5, Timestamp.from(new Date().toInstant()));
            PreparedQueries.createNewOrder.setInt(6, nOrderLines);
            PreparedQueries.createNewOrder.setInt(7, isAllLocal);

            this.executeUpdate(PreparedQueries.createNewOrder);


        /*
          3. Initialize TOTAL_AMOUNT = 0
          For i = [1...NUM_ITEMS],
         */
            double totalAmount = 0;
            List<Integer> adjustQuantities = new ArrayList<>();
            List<Double> itemAmounts = new ArrayList<>();
            List<String> itemNames = new ArrayList<>();
            for (int i = 0; i < nOrderLines; ++i) {
                int itemId = itemIds.get(i);
                int supplyWarehouseId = supplyWarehouseIds.get(i);
                int quantity = quantities.get(i);

            /*
              3.1. S_QUANTITY = stock quantity of itemNumber[i] and supplyWarehouseIds[i]
              ADJUST_QTY = S_QUANTITY - quantities[i]
              if ADJUST_QTY < 10, then ADJUST_QTY += 100
             */

                PreparedQueries.getStockQty.setInt(1, supplyWarehouseId);
                PreparedQueries.getStockQty.setInt(2, itemId);

                ResultSet qtyInfo = this.executeQuery(PreparedQueries.getStockQty);

                if (!qtyInfo.next()) {
                    error("getStockQty");
//                    System.err.println("SupplyWarehouseId: " + supplyWarehouseId);
//                    System.err.println("ItemId: " + itemId);
//                    System.err.println("ToString info: " + this.toString());
//                    System.err.println("\n");
                    throw new SQLException();
                }

                int stockQty = qtyInfo.getBigDecimal("S_QUANTITY").intValue();
                int stockYtd = qtyInfo.getBigDecimal("S_YTD").intValue();

                stockYtd += quantity;

                int adjustQty = stockQty - quantity;
                if (adjustQty < STOCK_REFILL_THRESHOLD) {
                    adjustQty += STOCK_REFILL_QTY;
                }
                adjustQuantities.add(adjustQty);

            /*
            3.2. Update stock for (itemNumber[i], supplyWarehouseIds[i]):
              - update S_QUANTITY to ADJUST_QUANTITY
              - increment S_YTD by quantities[i]
              - increment S_ORDER_CNT by 1
              - Increment S_REMOTE_CNT by 1 if supplyWarehouseIds[i] != warehouseID
             */
                if (supplyWarehouseId != warehouseId) {
                    PreparedQueries.updateStockQtyIncrRemoteCnt.setBigDecimal(1, BigDecimal.valueOf(adjustQty));
                    PreparedQueries.updateStockQtyIncrRemoteCnt.setBigDecimal(2, BigDecimal.valueOf(stockYtd));
                    PreparedQueries.updateStockQtyIncrRemoteCnt.setInt(3, supplyWarehouseId);
                    PreparedQueries.updateStockQtyIncrRemoteCnt.setInt(4, itemId);
                    this.executeUpdate(PreparedQueries.updateStockQtyIncrRemoteCnt);

                } else {
                    PreparedQueries.updateStockQty.setBigDecimal(1, BigDecimal.valueOf(adjustQty));
                    PreparedQueries.updateStockQty.setBigDecimal(2, BigDecimal.valueOf(stockYtd));
                    PreparedQueries.updateStockQty.setInt(3, supplyWarehouseId);
                    PreparedQueries.updateStockQty.setInt(4, itemId);
                    this.executeUpdate(PreparedQueries.updateStockQty);
                }
            /*
              3.3. ITEM_AMOUNT = quantities[i] * I_PRICE, where I_PRICE is price of itemNumber[i]
              TOTAL_AMOUNT += ITEM_AMOUNT
             */
                PreparedQueries.getItemPriceAndName.setInt(1, itemId);
                ResultSet itemInfo = this.executeQuery(PreparedQueries.getItemPriceAndName);

                if (!itemInfo.next()) {
                    error("formattedGetItemPriceAndName");
                    throw new SQLException();
                }
                double price = itemInfo.getBigDecimal("I_PRICE").doubleValue();
                double itemAmount = quantity * price;
                itemAmounts.add(itemAmount);
                itemNames.add(itemInfo.getString("I_NAME"));
                totalAmount += itemAmount;

            /*
              3.4. Create a new order line
              - OL_O_ID = N
              - OL_D_ID = D_ID
              - OL_W_ID = W_ID
              - OL_NUMBER = i
              - OL_I_ID = itemNumber[i]
              - OL_SUPPLY_W_ID = supplyWarehouseIds[i]
              - OL_AMOUNT = ITEM_AMOUNT
              - OL_DELIVERY_D = null
              - OL_DIST_INFO = S_DIST_xx where xx=D_ID
             */
                String distIdStr = distIdStr(districtId);
                PreparedQueries.getStockDistInfo.setString(1, distIdStr);
                PreparedQueries.getStockDistInfo.setInt(2, warehouseId);
                PreparedQueries.getStockDistInfo.setInt(3, itemId);
                res = this.executeQuery(PreparedQueries.getStockDistInfo);

                if (!res.next()) {
                    error("formattedGetStockDistInfo");
                    throw new SQLException();
                }

                String distInfo = res.getString(1);

                PreparedQueries.createNewOrderLine.setInt(1, orderId);
                PreparedQueries.createNewOrderLine.setInt(2, districtId);
                PreparedQueries.createNewOrderLine.setInt(3, warehouseId);
                PreparedQueries.createNewOrderLine.setInt(4, customerId);

                PreparedQueries.createNewOrderLine.setInt(5, i);
                PreparedQueries.createNewOrderLine.setInt(6, itemId);
                PreparedQueries.createNewOrderLine.setInt(7, supplyWarehouseId);
                PreparedQueries.createNewOrderLine.setBigDecimal(8, BigDecimal.valueOf(quantity));
                PreparedQueries.createNewOrderLine.setBigDecimal(9, BigDecimal.valueOf(itemAmount));
                PreparedQueries.createNewOrderLine.setString(10, distInfo);

                this.executeUpdate(PreparedQueries.createNewOrderLine);
            }

        /*
          4. TOTAL_AMOUNT = TOTAL_AMOUNT × (1+D_TAX +W_TAX) × (1−C_DISCOUNT),
          where W_TAX is the tax rate for warehouse W_ID,
          D_TAX is the tax rate for district (W_ID, D_ID),
          and C_DISCOUNT is the discount for customer C_ID.
         */
            double dTax = districtInfo.getBigDecimal("D_TAX").doubleValue();

            PreparedQueries.getWarehouseTax.setInt(1, warehouseId);
            res = this.executeQuery(PreparedQueries.getWarehouseTax);

            if (!res.next()) {

                error("formattedGetWarehouseTax");
                throw new SQLException();
            }

            double wTax = res.getBigDecimal("W_TAX").doubleValue();

            PreparedQueries.getCustomerLastAndCreditAndDiscount.setInt(1, warehouseId);
            PreparedQueries.getCustomerLastAndCreditAndDiscount.setInt(2, districtId);
            PreparedQueries.getCustomerLastAndCreditAndDiscount.setInt(3, customerId);
            res = this.executeQuery(PreparedQueries.getCustomerLastAndCreditAndDiscount);


            ResultSet cInfo = res;

            if (!cInfo.next()) {
                error("formattedGetCustomerLastAndCreditAndDiscount");
                throw new SQLException();
            }

            double cDiscount = cInfo.getBigDecimal("C_DISCOUNT").doubleValue();

            totalAmount = totalAmount * (1 + dTax + wTax) * (1 - cDiscount);


        /*
        Output following info:
        1. Customer identifier (W ID, D ID, C ID), lastname C LAST, credit C CREDIT, discount C DISCOUNT
        2. Warehouse tax rate W TAX, District tax rate D TAX
        3. Order number O ID, entry date O ENTRY D
        4. Number of items NUM ITEMS, Total amount for order TOTAL AMOUNT
        5. For each ordered item ITEM NUMBER[i], i ∈ [1,NUM ITEMS]
        (a) ITEM NUMBER[i] (b) I NAME
        (c) SUPPLIER WAREHOUSE[i] (d) QUANTITY[i]
        (e) OL AMOUNT (f) S QUANTITY
         */
            String cLast = cInfo.getString("C_LAST");
            String cCredit = cInfo.getString("C_CREDIT");

            io.println("*** New Order Transaction Summary ***");
            io.println(
                    String.format("Customer ID: (%d, %d, %d), Last name:%s, Credit:%s, C_DISCOUNT:%.4f\n",
                            warehouseId, districtId, customerId, cLast, cCredit, cDiscount));
            io.println(String.format("Warehouse tax:%.4f, District tax:%.4f\n", wTax, dTax));
            io.println(String.format("Order ID:%d, Order entry date:%s\n", orderId, TimeFormatter.formatTime(orderDateTime)));
            io.println(String.format("#items:%d, Total amount:%.2f\n", nOrderLines, totalAmount));
            io.println("Items information:");
            for (int i = 0; i < nOrderLines; ++i) {
                io.println(
                        String.format("\t Item number: %d, name: %s, Supplier warehouse: %d, quantity: %d, Order-line amount: %.2f, Adjusted quantity: %d\n",
                                itemIds.get(i), itemNames.get(i), supplyWarehouseIds.get(i), quantities.get(i), itemAmounts.get(i), adjustQuantities.get(i)));
            }
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[Error]:  New Order Abort " + this.toString());
            System.err.println("[Error]: New Order Abort " + this.toString());
            connection.rollback();
        }

    }

    public void error(String s) {
        System.err.println("[Error]: NewOrder " + s + " are missing");
    }

}
