# Wholesale-YSQL

## Cluster setup
### Pre-requisite
- `yugabyte-2.14.1.0` installed under `/temp`, if not installed, run `./scripts/install_yugabyte.sh`.
- Make sure `/mnt/ramdisk` has enough space (>12G).
- Make sure the default ports used by Yugabyte masters and tservers are available. You can find the default ports [here](https://docs.yugabyte.com/preview/reference/configuration/default-ports/).

### Setup steps
Assume the current working directory is the root directory of this repo (Wholesale-YCQL)
#### Start cluster on `xcnd20 - xcnd24`
- On `xcnd20`, run `./scripts/start_cluster.sh`.
- The script will start YB-Masters on `xcnd20 - xcnd22` and start YB-TServers on `xcnd20 - xcnd24`
- Check if server is up by run `ps aux | grep -E 'master|tserver'` on all cluster nodes. Make sure the 3 masters and 5 tservers are up.
#### Start cluster on alternative cluster nodes
- If setting up cluster on alternative nodes, please modify the node names and IP addresses in `run_master.sh`, `run_tserver.sh`, and `start_cluster.sh`.
- The other scripts involving specifying IPs and node names are also to be modified.

## Run YSQL Java application benchmark
### Pre-requisite
- `JDK 11`
- `Maven`
- `Python 3.7` with `pandas` package (neccessary for data preprocessing).

### Build and package Java application
```
mvn compile
mvn package
```
Make sure the project jar file is generated at `./target/yugabyte-simple-java-app-1.0-SNAPSHOT.jar`.

### Run benchmark
- On `xcnd20`, run `./scripts/run_benchmark.sh`.
- Check `./log` for each transaction files' output and error.
- CSV files containing summary statistics can be found under `./backup`.
