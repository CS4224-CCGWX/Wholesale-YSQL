curr_node=$1
tx=$3
port=$2

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

cd /home/stuproj/cs4224i/Wholesale-YSQL
# mvn install


# if [[ -e log ]]; then
#     rm -rf log
# fi
# mkdir log
for ((c=0; c<4; c++))
do
	client=`expr $c \* 5  + $tx`
	output_path=/home/stuproj/cs4224i/Wholesale-YSQL/log/$client.out
	err_path=/home/stuproj/cs4224i/Wholesale-YSQL/log/$client.err

	echo "Run transaction file $client at node: $curr_node"
	tmux new-session -d -s client$client "java -jar target/yugabyte-simple-java-app-1.0-SNAPSHOT.jar $ip $port $client run > $output_path 2>$err_path"
done
exit 0
