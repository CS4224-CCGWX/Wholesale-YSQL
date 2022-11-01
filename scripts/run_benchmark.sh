num_nodes=5
base_node=20  # base node is xcnd20
port_id=5433
root_dir=/home/stuproj/cs4224i/Wholesale-YSQL

submit_job() {
  job_id=$1
  echo "submit job: $job_id"
  
  node_id="xcnd$(($base_node + (($job_id % $num_nodes))))"
  
  echo "the job will be submitted to node: {$node_id}"

  ssh "cs4224i@$node_id.comp.nus.edu.sg" cd "Wholesale-YSQL && ./scripts/run_jar.sh $node_id $port_id $job_id"
}

load_data() {
    ssh "cs4224i@xcnd20.comp.nus.edu.sg" cd "Wholesale-YSQL && ./scripts/dump_data.sh $node_id $port_id"
}


load_data
for ((c=0; c<20; c++))
do
  submit_job $c &
done
wait


exit 0