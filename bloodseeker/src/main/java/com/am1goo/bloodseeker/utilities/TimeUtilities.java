package com.am1goo.bloodseeker.utilities;

public class TimeUtilities {

    public static long getDays(long millis) {
        long hours = getHours(millis);
        return hours / 24;
    }

    public static long getHours(long millis) {
        long minutes = getMinutes(millis);
        return minutes / 60;
    }

    public static long getMinutes(long millis) {
        long seconds = getSeconds(millis);
        return seconds / 60;
    }

    public static long getSeconds(long millis) {
        return millis / 1000;
    }
}
