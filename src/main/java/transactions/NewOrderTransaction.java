package transactions;

import java.util.ArrayList;
import java.util.List;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import utils.PreparedQueries;
import utils.TimeFormatter;


import java.sql.*;


public class NewOrderTransaction extends AbstractTransaction {

    private final int customerId;
    private final int warehouseId;
    private final int districtId;
    private final int nOrderLines;

    private static final int STOCK_REFILL_THRESHOLD = 10;
    private static final int STOCK_REFILL_QTY = 100;

    public NewOrderTransaction(Connection connection, int cid, int wid, int did, int n) {
        super(connection);
        customerId = cid;
        warehouseId = wid;
        districtId = did;
        nOrderLines = n;
    }

    public String distIdStr(int distId) {
        assert(distId >= 1 && distId <= 10);

        if(distId < 10) {
            return "0" + Integer.toString(distId);
        } else {
            return Integer.toString(distId);
        }
    }

    public void execute(List<Integer> itemIds,List<Integer> supplyWarehouseIds, List<Integer> quantities) throws SQLException {
         /*
          1. N denotes the next available order number D_NEXT_O_ID for district (W_ID, D_ID)
          Update district (W_ID, D_ID) by incrementing D_NEXT_O_ID by 1.
         */

        ResultSet res;
        String formattedNextOrderIdAndTax = this.stringFormatter(PreparedQueries.getDistrictNextOrderIdAndTax, warehouseId, districtId);
        res = this.executeQuery(formattedNextOrderIdAndTax);
        ResultSet districtInfo = res;

        int orderId = res.getInt("D_NEXT_O_ID");

        String formattedIncrementDistrictNextOrderId = this.stringFormatter(PreparedQueries.incrementDistrictNextOrderId, warehouseId, districtId);

        this.executeQuery(formattedIncrementDistrictNextOrderId);



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
        String orderDateTime = TimeFormatter.getCurrentTimestamp();
        String formattedNewOrder = this.stringFormatter(PreparedQueries.createNewOrder,orderId, districtId, warehouseId, customerId, orderDateTime, nOrderLines, isAllLocal);
        this.executeQuery(formattedNewOrder);


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
              3.1. S_QUANTITY = stock quantity of itemIds[i] and supplyWarehouseIds[i]
              ADJUST_QTY = S_QUANTITY - quantities[i]
              if ADJUST_QTY < 10, then ADJUST_QTY += 100
             */

            String formattedGetStockQty = this.stringFormatter(PreparedQueries.getStockQty, supplyWarehouseId, itemId);
            res = this.executeQuery(formattedGetStockQty);
            int stockQty = res.getInt(0);

            int adjustQty = stockQty - quantity;
            if (adjustQty < STOCK_REFILL_THRESHOLD) {
                adjustQty += STOCK_REFILL_QTY;
            }
            adjustQuantities.add(adjustQty);

            /*
            3.2. Update stock for (itemIds[i], supplyWarehouseIds[i]):
              - update S_QUANTITY to ADJUST_QUANTITY
              - increment S_YTD by quantities[i]
              - increment S_ORDER_CNT by 1
              - Increment S_REMOTE_CNT by 1 if supplyWarehouseIds[i] != warehouseID
             */
            if(supplyWarehouseId != warehouseId) {
                String formattedUpdateStockQtyIncrRemoteCnt = this.stringFormatter(PreparedQueries.updateStockQtyIncrRemoteCnt, adjustQty, quantity, supplyWarehouseId, itemId);
                this.executeQuery(formattedUpdateStockQtyIncrRemoteCnt);
            } else {
                String formattedUpdateStockQty = this.stringFormatter(PreparedQueries.updateStockQty, adjustQty, quantity, supplyWarehouseId, itemId);
                this.executeQuery(formattedUpdateStockQty);
            }
            /*
              3.3. ITEM_AMOUNT = quantities[i] * I_PRICE, where I_PRICE is price of itemIds[i]
              TOTAL_AMOUNT += ITEM_AMOUNT
             */
            String formattedGetItemPriceAndName = this.stringFormatter(PreparedQueries.getItemPriceAndName, itemId);
            ResultSet itemInfo = this.executeQuery(formattedGetItemPriceAndName);

            double price = itemInfo.getDouble("I_PRICE");
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
              - OL_I_ID = itemIds[i]
              - OL_SUPPLY_W_ID = supplyWarehouseIds[i]
              - OL_AMOUNT = ITEM_AMOUNT
              - OL_DELIVERY_D = null
              - OL_DIST_INFO = S_DIST_xx where xx=D_ID
             */
            String distIdStr = distIdStr(districtId);
            String formattedGetStockDistInfo = this.stringFormatter(PreparedQueries.getStockDistInfo, distIdStr, warehouseId, itemId);

            res = this.executeQuery(formattedGetStockDistInfo);
            String distInfo = res.getString(0);

            String formattedCreateNewOrderLine = this.stringFormatter(PreparedQueries.createNewOrderLine, districtId, warehouseId, i, itemId, supplyWarehouseId, itemAmount, distInfo);
            this.executeQuery(formattedCreateNewOrderLine);
        }

        /*
          4. TOTAL_AMOUNT = TOTAL_AMOUNT × (1+D_TAX +W_TAX) × (1−C_DISCOUNT),
          where W_TAX is the tax rate for warehouse W_ID,
          D_TAX is the tax rate for district (W_ID, D_ID),
          and C_DISCOUNT is the discount for customer C_ID.
         */
        double dTax = districtInfo.getBigDecimal("D_TAX").doubleValue();
        String formattedGetWarehouseTax = this.stringFormatter(PreparedQueries.getWarehouseTax, warehouseId);
        res = this.executeQuery(formattedGetWarehouseTax);

        double wTax = res.getDouble(0);
        String formattedGetCustomerLastAndCreditAndDiscount = this.stringFormatter(PreparedQueries.getCustomerLastAndCreditAndDiscount, warehouseId, districtId, customerId);
        res = this.executeQuery(formattedGetCustomerLastAndCreditAndDiscount);
        ResultSet cInfo = res;
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

        System.out.println("NewOrderTransaction Summary:");
        System.out.printf(
                "1. (%d, %d, %d), C_LAST:%s, C_CREDIT:%s, C_DISCOUNT:%.2f\n",
                warehouseId, districtId, customerId, cLast, cCredit, cDiscount);
        System.out.printf("2. W_TAX:%.2f, D_TAX:%.2f\n", wTax, dTax);
        System.out.printf("3. O_ID:%d, O_ENTRY_D:%s\n", orderId, orderDateTime);
        System.out.printf("4. NUM_ITEMS:%d, TOTAL_AMOUNT:%.2f\n", nOrderLines, totalAmount);
        System.out.println("5. Item Info:");
        for (int i = 0; i < nOrderLines; ++i) {
            System.out.printf(
                    "\t ITEM_NUMBER: %d, I_NAME: %s, SUPPLIER_WAREHOUSE: %d, QUANTITY: %d, OL_AMOUNT: %.2f, S_QUANTITY: %d\n",
                    itemIds.get(i), itemNames.get(i), supplyWarehouseIds.get(i), quantities.get(i), itemAmounts.get(i), adjustQuantities.get(i));
        }

    }

}
