package utils;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimeFormatter {
    private static final Format dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private static final DateTimeFormatter instantFormatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            .withZone(ZoneId.systemDefault());

    public static Date getCurrentDate() {
        return new Date();
    }

    public static String formatTime(Instant dateTime) {
        return instantFormatter.format(dateTime);
    }
    public static String getCurrentTimestamp() {
        return dateFormatter.format(new Date());
    }
}
