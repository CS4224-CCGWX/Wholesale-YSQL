schema="/home/stuproj/cs4224i/Wholesale-YSQL/src/main/resources/schema.sql"
DELIM=","
YSQLSH="/temp/yugabyte-2.14.1.0/bin/ysqlsh"
dataDir="/home/stuproj/cs4224i/Wholesale-YSQL/project_files/data_files"
base_node=20

#create csv file for measurement
cd ..
if [[ -e report.csv ]]; then
    rm -f report.csv
fi

ip=$1
port=$2


echo "***** Start dump data *****"
echo "Defining schema"
$YSQLSH -f $schema -h $ip -p $port


# DateTime format reference: https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
echo "***** Load warehouse table *****"
$YSQLSH  -c "\COPY warehouse FROM '$dataDir/warehouse.csv' WITH (FORMAT CSV, NULL 'null');" -h $ip -p $port

echo "***** Load district table *****"
$YSQLSH -c "\COPY district FROM '$dataDir/district.csv' WITH (FORMAT CSV, NULL 'null');" -h $ip -p $port

echo "***** Load customer table *****"
$YSQLSH -c "\COPY customer FROM '$dataDir/customer.csv' WITH (FORMAT CSV, NULL 'null');" -h $ip -p $port

echo "***** Load order table *****"
$YSQLSH -c "\COPY \"order\" FROM '$dataDir/order.csv' WITH (FORMAT CSV, NULL '');" -h $ip -p $port

echo "***** Load item table *****"
$YSQLSH -c "\COPY item FROM '$dataDir/item.csv' WITH (FORMAT CSV, NULL 'null');" -h $ip -p $port

echo "***** Load order_line table *****"
$YSQLSH -c "\COPY order_line FROM '$dataDir/order_line.csv' WITH (FORMAT CSV, NULL '');" -h $ip -p $port

echo "***** Load stock table *****"
$YSQLSH -c "\COPY stock FROM '$dataDir/stock.csv' WITH (FORMAT CSV, NULL 'null');" -h $ip -p $port
