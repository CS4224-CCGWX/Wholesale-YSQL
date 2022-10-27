package utils;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeFormatter {
    private static final Format dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static String getCurrentTimestamp() {
        return dateFormatter.format(new Date());
    }
}
