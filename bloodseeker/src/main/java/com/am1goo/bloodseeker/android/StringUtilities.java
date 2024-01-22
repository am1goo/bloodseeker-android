package com.am1goo.bloodseeker.android;

public class StringUtilities {

    public static String trimAtStart(String str, Character c) {
        if (str == null)
            return null;

        int lastIndex = -1;
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            if (chars[i] != c)
                break;

            lastIndex = i;
        }

        if (lastIndex < 0)
            return str;

        int fromIndex = lastIndex + 1;
        if (fromIndex < chars.length) {
            return str.substring(fromIndex);
        }
        else {
            return "";
        }
    }

    public static String trimAtEnd(String str, Character c) {
        if (str == null)
            return null;

        int lastIndex = -1;
        char[] chars = str.toCharArray();
        for (int i = chars.length - 1; i >= 0; --i) {
            if (chars[i] != c)
                break;

            lastIndex = i;
        }

        if (lastIndex < 0)
            return str;

        int fromIndex = 0;
        int toIndex = lastIndex;
        return str.substring(fromIndex, toIndex);
    }

    public static String trim(String str, Character c) {
        return trimAtStart(trimAtEnd(str, c), c);
    }
}
