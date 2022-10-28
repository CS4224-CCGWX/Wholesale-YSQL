# to change crlf to lf 
# https://stackoverflow.com/questions/11616835/r-command-not-found-bashrc-bash-profile



schema="/root/Wholesale-YCQL/src/main/resources/schema0.ycql"
DELIM=","
YCQLSH="/home/yugabyte/bin/ycqlsh"
dataDir="/root/Wholesale-YCQL/project_files/data_files"
bsz=500

echo "***** Remove null in dataset *****"
python preprocess/precompute.py

echo "***** Start dump data *****"
echo "Defining schema"
$YCQLSH -f $schema --request-timeout=3600

echo "Load warehouse table"
$YCQLSH -e "USE wholesale; COPY warehouse (W_ID, W_NAME, W_STREET_1, W_STREET_2, W_CITY, W_STATE, W_ZIP, W_TAX, W_YTD) FROM '$dataDir/warehouse.csv' WITH DELIMITER='$DELIM' AND MAXBATCHSIZE=$bsz;"

# echo "Load district table"
# $YCQLSH -e "USE wholesale; COPY district (D_W_ID, D_ID, D_NAME, D_STREET_1, D_STREET_2, D_CITY, D_STATE, D_ZIP, D_TAX, D_YTD, D_NEXT_O_ID, D_NEXT_DELIVER_O_ID) FROM '$dataDir/district-with-delivery.csv' WITH DELIMITER='$DELIM' AND MAXBATCHSIZE=$bsz;"

# Load large tables with Cassadra Loader
C_LOADER="/root/Wholesale-YCQL/cassandra-loader"
badDir="/root/Wholesale-YCQL/cassandra-loader-bad-dir"
customer_bsz=1000
order_bsz=1000
item_bsz=5000
order_line_bsz=1000
stock_bsz=10000

curr_node=$1
if [[ $curr_node == "xcnd20" ]]; then
    ip="192.168.48.239"
elif [[ $curr_node == "xcnd21" ]]; then
    ip="192.168.48.240"
elif [[ $curr_node == "xcnd22" ]]; then
    ip="192.168.48.241"
elif [[ $curr_node == "xcnd23" ]]; then
    ip="192.168.48.242"
elif [[ $curr_node == "xcnd24" ]]; then
    ip="192.168.48.243"
else
    echo "Using default node xcnd20"
    ip="192.168.48.239"
fi

ip="localhost"
echo "***** Using Cassandra Loader with host: $ip *****"


# Cassandra-loader reference: https://github.com/yugabyte/cassandra-loader#options
# DateTime format reference: https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
echo "Load district table"
$C_LOADER \
    -f $dataDir/district-with-delivery.csv \
    -schema "wholesale.district(D_W_ID, D_ID, D_NAME, D_STREET_1, D_STREET_2, D_CITY, D_STATE, D_ZIP, D_TAX, D_YTD, D_NEXT_O_ID, D_NEXT_DELIVER_O_ID)" \
    -batchSize $customer_bsz \
    -dateFormat 'yyyy-MM-dd HH:mm:ss.SSS' \
    -delim $DELIM \
    -host $ip \
    -badDir $badDir
# $YCQLSH -e "USE wholesale; COPY customer (C_W_ID, C_D_ID, C_ID, C_FIRST, C_MIDDLE, C_LAST, C_STREET_1, C_STREET_2, C_CITY, C_STATE, C_ZIP, C_PHONE, C_SINCE, C_CREDIT, C_CREDIT_LIM, C_DISCOUNT, C_BALANCE, C_YTD_PAYMENT, C_PAYMENT_CNT, C_DELIVERY_CNT, C_DATA) FROM '$dataDir/customer.csv' WITH DELIMITER='$DELIM' AND MAXBATCHSIZE=$bsz;"



echo "Load customer table"
$C_LOADER \
    -f $dataDir/customer.csv \
    -schema "wholesale.customer(C_W_ID, C_D_ID, C_ID, C_FIRST, C_MIDDLE, C_LAST, C_STREET_1, C_STREET_2, C_CITY, C_STATE, C_ZIP, C_PHONE, C_SINCE, C_CREDIT, C_CREDIT_LIM, C_DISCOUNT, C_BALANCE, C_YTD_PAYMENT, C_PAYMENT_CNT, C_DELIVERY_CNT, C_DATA)" \
    -batchSize $customer_bsz \
    -dateFormat 'yyyy-MM-dd HH:mm:ss.SSS' \
    -delim $DELIM \
    -host $ip \
    -badDir $badDir
# $YCQLSH -e "USE wholesale; COPY customer (C_W_ID, C_D_ID, C_ID, C_FIRST, C_MIDDLE, C_LAST, C_STREET_1, C_STREET_2, C_CITY, C_STATE, C_ZIP, C_PHONE, C_SINCE, C_CREDIT, C_CREDIT_LIM, C_DISCOUNT, C_BALANCE, C_YTD_PAYMENT, C_PAYMENT_CNT, C_DELIVERY_CNT, C_DATA) FROM '$dataDir/customer.csv' WITH DELIMITER='$DELIM' AND MAXBATCHSIZE=$bsz;"

echo "Load order table"
$C_LOADER \
    -f $dataDir/order.csv \
    -schema "wholesale.\"order\"(O_W_ID, O_D_ID, O_ID, O_C_ID, O_CARRIER_ID, O_OL_CNT, O_ALL_LOCAL, O_ENTRY_D)" \
    -batchSize $order_bsz \
    -dateFormat 'yyyy-MM-dd HH:mm:ss.SSS' \
    -delim $DELIM \
    -host $ip \
    -badDir $badDir
# $YCQLSH -e "USE wholesale; COPY \"order\" (O_W_ID, O_D_ID, O_ID, O_C_ID, O_CARRIER_ID, O_OL_CNT, O_ALL_LOCAL, O_ENTRY_D) FROM '$dataDir/order.csv' WITH DELIMITER='$DELIM' AND MAXBATCHSIZE=$bsz;"

echo "Load item table"
$C_LOADER \
    -f $dataDir/item.csv \
    -schema "wholesale.item(I_ID, I_NAME, I_PRICE, I_IM_ID, I_DATA)" \
    -batchSize $item_bsz \
    -delim $DELIM \
    -host $ip \
    -badDir $badDir
# $YCQLSH -e "USE wholesale; COPY item (I_ID, I_NAME, I_PRICE, I_IM_ID, I_DATA) FROM '$dataDir/item.csv' WITH DELIMITER='$DELIM' AND MAXBATCHSIZE=$bsz;"

echo "Load order_line table"
$C_LOADER \
    -f $dataDir/order-line-with-cid.csv \
    -schema "wholesale.order_line(OL_W_ID, OL_D_ID, OL_O_ID, OL_NUMBER, OL_I_ID, OL_DELIVERY_D, OL_AMOUNT, OL_SUPPLY_W_ID, OL_QUANTITY, OL_DIST_INFO, OL_C_ID)" \
    -batchSize $order_line_bsz \
    -dateFormat 'yyyy-MM-dd HH:mm:ss.SSS' \
    -delim $DELIM \
    -host $ip \
    -badDir $badDir
# $YCQLSH -e "USE wholesale; COPY order_line (OL_W_ID, OL_D_ID, OL_O_ID, OL_NUMBER, OL_I_ID, OL_DELIVERY_D, OL_AMOUNT, OL_SUPPLY_W_ID, OL_QUANTITY, OL_DIST_INFO) FROM '$dataDir/order-line.csv' WITH DELIMITER='$DELIM' AND MAXBATCHSIZE=$bsz;"

echo "Load stock table"
$C_LOADER \
    -f $dataDir/stock.csv \
    -schema "wholesale.stock(S_W_ID, S_I_ID, S_QUANTITY, S_YTD, S_ORDER_CNT, S_REMOTE_CNT, S_DIST_01, S_DIST_02, S_DIST_03, S_DIST_04, S_DIST_05, S_DIST_06, S_DIST_07, S_DIST_08, S_DIST_09, S_DIST_10, S_DATA)" \
    -batchSize $stock_bsz \
    -delim $DELIM \
    -host $ip \
    -badDir $badDir
# $YCQLSH -e "USE wholesale; COPY stock (S_W_ID, S_I_ID, S_QUANTITY, S_YTD, S_ORDER_CNT, S_REMOTE_CNT, S_DIST_01, S_DIST_02, S_DIST_03, S_DIST_04, S_DIST_05, S_DIST_06, S_DIST_07, S_DIST_08, S_DIST_09, S_DIST_10, S_DATA) FROM '$dataDir/stock.csv' WITH DELIMITER='$DELIM' AND MAXBATCHSIZE=$bsz;"

echo "***** Loaded all tables *****"