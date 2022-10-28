export ybb="/home/stuproj/cs4224i/yugabyte-2.14.2.0/bin"

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
    echo "Unknown node name: $curr_node"
    exit -1
fi
echo "Launching tserver on $ip ($curr_node)"

# List of ports to modify: https://docs.yugabyte.com/preview/deploy/manual-deployment/verify-deployment/#default-ports-reference
# yb-tserver reference: https://docs.yugabyte.com/preview/reference/configuration/yb-tserver/#ysql-flags
master1="192.168.48.239"
master2="192.168.48.240"
master3="192.168.48.241"
rpc_port="11453"
web_port="11454"
ycql_addr="127.0.0.1:2333"
ycql_web_port="2334"
ysql_addr="127.0.0.1:6666"
ysql_web_port="6667"

diskDir="/mnt/ramdisk"
if [[ ! -d $diskDir/yugabyte-data ]]; then
    mkdir $diskDir/yugabyte-data/
fi

$ybb/yb-tserver \
--tserver_master_addrs $master1:$rpc_port,$master2:$rpc_port,$master3:$rpc_port \
--rpc_bind_addresses $ip:$rpc_port \
--webserver_port $web_port \
--cql_proxy_bind_address $ycql_addr \
--cql_proxy_webserver_port $ycql_web_port \
--pgsql_proxy_bind_address $ysql_addr \
--pgsql_proxy_webserver_port $ysql_web_port \
--fs_data_dirs "$diskDir/yugabyte-data" >& $diskDir/yugabyte-data/yb-tserver.out &
# --fs_data_dirs "/export/data/ybdisk1,/export/data/ybdisk2"
