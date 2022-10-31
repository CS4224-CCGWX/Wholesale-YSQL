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

    PreparedStatement formattedNextOrderIdAndTax, formattedIncrementDistrictNextOrderId,
            formattedNewOrder, formattedGetStockQty, formattedUpdateStockQtyIncrRemoteCnt,
            formattedGetItemPriceAndName, formattedGetCustomerLastAndCreditAndDiscount,
            formattedGetWarehouseTax, formattedUpdateStockQty, formattedGetStockDistInfo,
            formattedCreateNewOrderLine;

    public NewOrderTransaction(Connection connection, IO io, int cid, int wid, int did, int n) throws SQLException {
        super(connection, io);
        customerId = cid;
        warehouseId = wid;
        districtId = did;
        nOrderLines = n;
        itemIds = new ArrayList<>();
        quantities = new ArrayList<>();
        supplyWarehouseIds = new ArrayList<>();

        formattedNextOrderIdAndTax = connection.prepareStatement(PreparedQueries.getDistrictNextOrderIdAndTax);
        formattedIncrementDistrictNextOrderId = connection.prepareStatement(PreparedQueries.incrementDistrictNextOrderId);
        formattedNewOrder = connection.prepareStatement(PreparedQueries.createNewOrder);
        formattedGetStockQty = connection.prepareStatement(PreparedQueries.getStockQty);
        formattedUpdateStockQtyIncrRemoteCnt = connection.prepareStatement(PreparedQueries.updateStockQtyIncrRemoteCnt);
        formattedGetItemPriceAndName = connection.prepareStatement(PreparedQueries.getItemPriceAndName);
        formattedGetCustomerLastAndCreditAndDiscount = connection.prepareStatement(PreparedQueries.getCustomerLastAndCreditAndDiscount);
        formattedGetWarehouseTax = connection.prepareStatement(PreparedQueries.getWarehouseTax);
        formattedUpdateStockQty = connection.prepareStatement(PreparedQueries.updateStockQty);
        formattedGetStockDistInfo = connection.prepareStatement(PreparedQueries.getStockDistInfo);
        formattedCreateNewOrderLine = connection.prepareStatement(PreparedQueries.createNewOrderLine);
    }

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


        formattedNextOrderIdAndTax = connection.prepareStatement(PreparedQueries.getDistrictNextOrderIdAndTax);
        formattedIncrementDistrictNextOrderId = connection.prepareStatement(PreparedQueries.incrementDistrictNextOrderId);
        formattedNewOrder = connection.prepareStatement(PreparedQueries.createNewOrder);
        formattedGetStockQty = connection.prepareStatement(PreparedQueries.getStockQty);
        formattedUpdateStockQtyIncrRemoteCnt = connection.prepareStatement(PreparedQueries.updateStockQtyIncrRemoteCnt);
        formattedGetItemPriceAndName = connection.prepareStatement(PreparedQueries.getItemPriceAndName);
        formattedGetCustomerLastAndCreditAndDiscount = connection.prepareStatement(PreparedQueries.getCustomerLastAndCreditAndDiscount);
        formattedGetWarehouseTax = connection.prepareStatement(PreparedQueries.getWarehouseTax);
        formattedUpdateStockQty = connection.prepareStatement(PreparedQueries.updateStockQty);
        formattedGetStockDistInfo = connection.prepareStatement(PreparedQueries.getStockDistInfo);
        formattedCreateNewOrderLine = connection.prepareStatement(PreparedQueries.createNewOrderLine);
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
        sb.append("**** New Order Transaction Information ****\n");
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
         /*
          1. N denotes the next available order number D_NEXT_O_ID for district (W_ID, D_ID)
          Update district (W_ID, D_ID) by incrementing D_NEXT_O_ID by 1.
         */

        ResultSet res;

        formattedNextOrderIdAndTax.setInt(1, warehouseId);
        formattedNextOrderIdAndTax.setInt(2, districtId);
        res = this.executeQuery(formattedNextOrderIdAndTax);

        ResultSet districtInfo = res;
        if (!res.next()) {
            error("formattedNextOrderIdAndTax");
            throw new SQLException();
        }
        int orderId = res.getInt("D_NEXT_O_ID");


        formattedIncrementDistrictNextOrderId.setInt(1, warehouseId);
        formattedIncrementDistrictNextOrderId.setInt(2, districtId);

        this.executeUpdate(formattedIncrementDistrictNextOrderId);



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
        for(int supplyWarehouseId : supplyWarehouseIds) {
            if(supplyWarehouseId != warehouseId) {
                isAllLocal = 0;
                break;
            }
        }


        Date orderDateTime = TimeFormatter.getCurrentDate();
        formattedNewOrder.setInt(1, orderId);
        formattedNewOrder.setInt(2, districtId);
        formattedNewOrder.setInt(3, warehouseId);
        formattedNewOrder.setInt(4, customerId);
        formattedNewOrder.setTimestamp(5, Timestamp.from(new Date().toInstant()));
        formattedNewOrder.setInt(6, nOrderLines);
        formattedNewOrder.setInt(7, isAllLocal);

        this.executeUpdate(formattedNewOrder);


        /*
          3. Initialize TOTAL_AMOUNT = 0
          For i = [1...NUM_ITEMS],
         */
        double totalAmount = 0;
        List<Integer> adjustQuantities = new ArrayList<>();
        List<Double> itemAmounts = new ArrayList<>();
        List<String> itemNames = new ArrayList<>();
        for(int i=0; i < nOrderLines; ++i) {
            int itemId = itemIds.get(i);
            int supplyWarehouseId = supplyWarehouseIds.get(i);
            int quantity = quantities.get(i);

            /*
              3.1. S_QUANTITY = stock quantity of itemNumber[i] and supplyWarehouseIds[i]
              ADJUST_QTY = S_QUANTITY - quantities[i]
              if ADJUST_QTY < 10, then ADJUST_QTY += 100
             */

            formattedGetStockQty.setInt(1, supplyWarehouseId);
            formattedGetStockQty.setInt(2, itemId);

            ResultSet qtyInfo = this.executeQuery(formattedGetStockQty);

            if (!qtyInfo.next()) {
                error("formattedGetStockQty");
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
            if(supplyWarehouseId != warehouseId) {
                formattedUpdateStockQtyIncrRemoteCnt.setBigDecimal(1, BigDecimal.valueOf(adjustQty));
                formattedUpdateStockQtyIncrRemoteCnt.setBigDecimal(2, BigDecimal.valueOf(stockYtd));
                formattedUpdateStockQtyIncrRemoteCnt.setInt(3, supplyWarehouseId);
                formattedUpdateStockQtyIncrRemoteCnt.setInt(4, itemId);
                this.executeUpdate(formattedUpdateStockQtyIncrRemoteCnt);

            } else {
                formattedUpdateStockQty.setBigDecimal(1, BigDecimal.valueOf(adjustQty));
                formattedUpdateStockQty.setBigDecimal(2, BigDecimal.valueOf(stockYtd));
                formattedUpdateStockQty.setInt(3, supplyWarehouseId);
                formattedUpdateStockQty.setInt(4, itemId);
                this.executeUpdate(formattedUpdateStockQty);
            }
            /*
              3.3. ITEM_AMOUNT = quantities[i] * I_PRICE, where I_PRICE is price of itemNumber[i]
              TOTAL_AMOUNT += ITEM_AMOUNT
             */
            formattedGetItemPriceAndName.setInt(1, itemId);
            ResultSet itemInfo = this.executeQuery(formattedGetItemPriceAndName);

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
            formattedGetStockDistInfo.setString(1, distIdStr);
            formattedGetStockDistInfo.setInt(2, warehouseId);
            formattedGetStockDistInfo.setInt(3, itemId);
            res = this.executeQuery(formattedGetStockDistInfo);

            if (!res.next()) {
                error("formattedGetStockDistInfo");
                throw new SQLException();
            }

            String distInfo = res.getString(1);

            formattedCreateNewOrderLine.setInt(1, orderId);
            formattedCreateNewOrderLine.setInt(2, districtId);
            formattedCreateNewOrderLine.setInt(3, warehouseId);
            formattedCreateNewOrderLine.setInt(4, customerId);

            formattedCreateNewOrderLine.setInt(5, i);
            formattedCreateNewOrderLine.setInt(6, itemId);
            formattedCreateNewOrderLine.setInt(7, supplyWarehouseId);
            formattedCreateNewOrderLine.setBigDecimal(8, BigDecimal.valueOf(quantity));
            formattedCreateNewOrderLine.setBigDecimal(9, BigDecimal.valueOf(itemAmount));
            formattedCreateNewOrderLine.setString(10, distInfo);

            this.executeUpdate(formattedCreateNewOrderLine);
        }

        /*
          4. TOTAL_AMOUNT = TOTAL_AMOUNT × (1+D_TAX +W_TAX) × (1−C_DISCOUNT),
          where W_TAX is the tax rate for warehouse W_ID,
          D_TAX is the tax rate for district (W_ID, D_ID),
          and C_DISCOUNT is the discount for customer C_ID.
         */
        double dTax = districtInfo.getBigDecimal("D_TAX").doubleValue();

        formattedGetWarehouseTax.setInt(1, warehouseId);
        res = this.executeQuery(formattedGetWarehouseTax);

        if (!res.next()) {

            error("formattedGetWarehouseTax");
            throw new SQLException();
        }

        double wTax = res.getBigDecimal("W_TAX").doubleValue();

        formattedGetCustomerLastAndCreditAndDiscount.setInt(1, warehouseId);
        formattedGetCustomerLastAndCreditAndDiscount.setInt(2, districtId);
        formattedGetCustomerLastAndCreditAndDiscount.setInt(3, customerId);
        res = this.executeQuery(formattedGetCustomerLastAndCreditAndDiscount);

        ResultSet cInfo = res;

        if (!cInfo.next()) {
            error("formattedGetCustomerLastAndCreditAndDiscount");
            throw new SQLException();
        }

        double cDiscount = cInfo.getBigDecimal("C_DISCOUNT").doubleValue();

        totalAmount = totalAmount*(1 + dTax + wTax) * (1 - cDiscount);


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
        io.printf(
                "Customer ID: (%d, %d, %d), Last name:%s, Credit:%s, C_DISCOUNT:%.4f\n",
                warehouseId, districtId, customerId, cLast, cCredit, cDiscount);
        io.printf("Warehouse tax:%.4f, District tax:%.4f\n", wTax, dTax);
        io.printf("Order ID:%d, Order entry date:%s\n", orderId, TimeFormatter.formatTime(orderDateTime));
        io.printf("#items:%d, Total amount:%.2f\n", nOrderLines, totalAmount);
        io.println("Items information:");
        for (int i = 0; i < nOrderLines; ++i) {
            io.printf(
                    "\t Item number: %d, name: %s, Supplier warehouse: %d, quantity: %d, Order-line amount: %.2f, Adjusted quantity: %d\n",
                    itemIds.get(i), itemNames.get(i), supplyWarehouseIds.get(i), quantities.get(i), itemAmounts.get(i), adjustQuantities.get(i));
        }

    }

    public void error(String s) {
        System.err.println("[Error]: NewOrder " + s + " are missing");
    }

}
