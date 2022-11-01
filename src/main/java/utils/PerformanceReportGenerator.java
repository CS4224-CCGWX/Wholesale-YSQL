package utils;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.*;

public class PerformanceReportGenerator {
    private static String reportFilePath;
    private static String performanceFormat = "Transaction files: %d, Total Count: %s," +
            "Time Taken: %s, Throughput: %s, Average: %s, Median: %s, 95: %s, 99: %s\n";

    private static String individualTransactionPerformance = "Transaction name: %s, Total time: %d, Total count: %d \n";

    static FileWriter fw;

    public int client;

    public static void setFilePath(String path, int client) {
        reportFilePath = String.format(path, client);
    }

//    public static void setFilePath(String path) {
//        reportFilePath = path;
//    }
    public static void generatePerformanceReport(List<Long> latencyList, long totalTime, int client,
                                                 Map<String, Long> individual_time, Map<String, Long> individual_count) throws IOException {
        Collections.sort(latencyList);
        int count = latencyList.size();
        long sum = latencyList.stream().mapToLong(Long::longValue).sum();
        OutputFormatter outputFormatter = new OutputFormatter();

        fw = new FileWriter(reportFilePath, true);
        fw.write(String.format(performanceFormat,client, outputFormatter.formatTotalTransactions(count),
                outputFormatter.formatTotalElapsedTime(totalTime),
                outputFormatter.formatThroughput((double) count / totalTime),
                outputFormatter.formatAverage((double) convertToMs(sum) / count),
                outputFormatter.formatMedian(convertToMs(getMedian(latencyList))),
                outputFormatter.formatPercentile(convertToMs(getByPercentile(latencyList, 95))),
                outputFormatter.formatPercentile(convertToMs(getByPercentile(latencyList, 99)))
                )
        );
        for (Map.Entry<String, Long> set :
                individual_time.entrySet()) {
            String curTrans = set.getKey();
            long tTime = individual_time.get(curTrans);
            long transCount = individual_count.get(curTrans);
            fw.write(String.format(individualTransactionPerformance, curTrans, tTime, transCount));
            fw.write("\n");
        }
        System.out.println(individual_time.size());
        fw.close();
    }

    private static long convertToMs(long nano) {
        return TimeUnit.MILLISECONDS.convert(nano, TimeUnit.NANOSECONDS);
    }

    private static long getMedian(List<Long> list) {
        long mid1 = list.get(list.size() / 2);
        if (list.size() % 2 != 0) {
            return mid1;
        } else {
            long mid2 = list.get(list.size() / 2 - 1);
            return (mid1 + mid2) / 2;
        }
    }

    private static long getByPercentile(List<Long> list, int percentile) {
        int i = list.size() * percentile / 100;
        return list.get(i);
    }
}
