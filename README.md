# Wholesale-YSQL

## Cluster setup
Refer to the cluster setup section of our [YCQL repository](https://github.com/CS4224-CCGWX/Wholesale-YCQL).

### Setup steps
Assume the current working directory is the root directory of this repo (Wholesale-YSQL)


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
