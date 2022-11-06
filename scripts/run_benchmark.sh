num_nodes=5
base_node=20  # base node is xcnd20
port_id=5433

submit_job() {
  job_id=$1
  echo "submit job: $job_id"

  node_id="xcnd$(($base_node + (($job_id % $num_nodes))))"

  echo "the job will be submitted to node: $node_id"

  if [ $node_id == "xcnd20" ]; then
    ip="192.168.48.239"
  elif [ $node_id == "xcnd21" ]; then
    ip="192.168.48.240"
  elif [ $node_id == "xcnd22" ]; then
    ip="192.168.48.241"
  elif [ $node_id == "xcnd23" ]; then
    ip="192.168.48.242"
  elif [ $node_id == "xcnd24" ]; then
    ip="192.168.48.243"
  else
    echo "Using default node xcnd20"
    ip="192.168.48.239"
  fi

  ssh "cs4224i@$node_id.comp.nus.edu.sg"  "./Wholesale-YSQL/scripts/run_jar.sh $ip $port_id $job_id"
}

load_data() {
    ssh "cs4224i@xcnd20.comp.nus.edu.sg"  "./Wholesale-YSQL/scripts/dump_data.sh 192.168.48.239 $port_id"
}


# load_data
for ((c=0; c<5; c++))
do
  submit_job $c &
done
wait


exit 0
