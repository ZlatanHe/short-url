package com.zlatan.shorturl.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Zlatan on 19/2/24.
 */
public class DateTimeUtils {

    private static final SimpleDateFormat HOUR_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH");

    public static String format2Hour(Date date) {
        return HOUR_FORMAT.format(date);
    }
}
