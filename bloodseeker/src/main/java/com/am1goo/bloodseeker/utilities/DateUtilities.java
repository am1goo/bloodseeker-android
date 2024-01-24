package com.am1goo.bloodseeker.utilities;

import java.time.Duration;
import java.util.Date;

public class DateUtilities {

    public static Duration getDuration(Date from, Date to) {
        long deltaTime = to.getTime() - from.getTime();
        return Duration.ofMillis(deltaTime);
    }
}
