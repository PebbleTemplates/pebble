package com.mitchellbosecke.pebble.utils;

public class StringUtils {

    public static String ltrim(String input) {
        int i = 0;
        while (i < input.length() && Character.isWhitespace(input.charAt(i))) {
            i++;
        }
        return input.substring(i);
    }

    public static String rtrim(String input) {
        int i = input.length() - 1;
        while (i >= 0 && Character.isWhitespace(input.charAt(i))) {
            i--;
        }
        return input.substring(0, i + 1);
    }
}
