# to change crlf to lf 
# sed -i 's/\r$//' filename
# https://stackoverflow.com/questions/11616835/r-command-not-found-bashrc-bash-profile

schema="/home/stuproj/cs4224i/Wholesale-YSQL/src/main/resources/schema.sql"
DELIM=","
YSQLSH="/temp/yugabyte-2.14.1.0/bin/ysqlsh"
dataDir="/home/stuproj/cs4224i/Wholesale-YSQL/project_files/data_files"
bsz=500


# python preprocess/precompute.py

echo "***** Start dump data *****"
echo "Defining schema"
$YSQLSH -f $schema


curr_node=$1
#port=$2
if [ $curr_node == "xcnd20" ]; then
    ip="192.168.48.239"
elif [ $curr_node == "xcnd21" ]; then
    ip="192.168.48.240"
elif [ $curr_node == "xcnd22" ]; then
    ip="192.168.48.241"
elif [ $curr_node == "xcnd23" ]; then
    ip="192.168.48.242"
elif [ $curr_node == "xcnd24" ]; then
    ip="192.168.48.243"
else
    echo "Using default node xcnd20"
    ip="192.168.48.239"
fi

# DateTime format reference: https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
echo "***** Load warehouse table *****"
$YSQLSH  -c "\COPY warehouse FROM '$dataDir/warehouse.csv' WITH (FORMAT CSV, NULL 'null');" -h $ip

echo "***** Load district table *****"
$YSQLSH -c "\COPY district FROM '$dataDir/district.csv' WITH (FORMAT CSV, NULL 'null');" -h $ip

echo "***** Load customer table *****"
$YSQLSH -c "\COPY customer FROM '$dataDir/customer.csv' WITH (FORMAT CSV, NULL 'null');" -h $ip

echo "***** Load order table *****"
$YSQLSH -c "\COPY \"order\" FROM '$dataDir/order.csv' WITH (FORMAT CSV, NULL 'null');" -h $ip

echo "***** Load item table *****"
$YSQLSH -c "\COPY item FROM '$dataDir/item.csv' WITH (FORMAT CSV, NULL 'null');" -h $ip

echo "***** Load order_line table *****"
$YSQLSH -c "\COPY order_line FROM '$dataDir/order_line.csv' WITH (FORMAT CSV, NULL '');" -h $ip

echo "***** Load stock table *****"
$YSQLSH -c "\COPY stock FROM '$dataDir/stock.csv' WITH (FORMAT CSV, NULL 'null');" -h $ip
