package utils;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class PerformanceReportGenerator {
    private static String reportFilePath;
    private static String performanceFormat = "%d,%s,%s,%s,%s,%s,%s,%s\n";

    static FileWriter fw;

    public int client;

    public static void setFilePath(String path, int client) {
        reportFilePath = String.format(path, client);
    }

//    public static void setFilePath(String path) {
//        reportFilePath = path;
//    }
    public static void generatePerformanceReport(List<Long> latencyList, long totalTime, int client) throws IOException {
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
