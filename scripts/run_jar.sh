curr_node=$1
tx=$2
port=$3

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
    echo "Unknown node name: $curr_node"
    exit -1
fi

cd ..
mvn install


if [[ ! -d log ]]; then
    mkdir log
fi
output_path="./log/"$tx".out"
err_path="./log/"{$tx}".err"

echo "Run transaction file at node: $curr_node"
java -jar target/yugabyte-simple-java-app-1.0-SNAPSHOT.jar $curr_node $port $tx run > $output_path 2>$err_path
exit 0