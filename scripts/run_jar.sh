ip=$1
tx=$3
port=$2


cd ..
if [[ ! -e log ]]; then
    mkdir log
fi

for ((c=0; c<4; c++))
do
	client=`expr $c \* 5  + $tx`
	output_path=./log/$client.out
	err_path=./log/$client.err
	
	echo "Run transaction file at node: $ip"
	tmux new-session -d -s client$client "java -jar target/yugabyte-simple-java-app-1.0-SNAPSHOT.jar $ip $port $client run > $output_path 2>$err_path"
done
exit 0
