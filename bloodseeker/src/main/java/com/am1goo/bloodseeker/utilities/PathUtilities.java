package com.am1goo.bloodseeker.utilities;

import java.io.File;

public class PathUtilities {

    public static String join(String... elements) {
        return String.join(File.separator, elements);
    }

    public static String join(Iterable<? extends CharSequence> elements) {
        return String.join(File.separator, elements);
    }
}
