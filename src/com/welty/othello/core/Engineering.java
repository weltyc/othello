package com.welty.othello.core;

/**
 * Format numbers in Engineering format, for example "123k" for 123000.
 */
public class Engineering {
    private static char[] prefixes = " kMGTPE".toCharArray();
    private static char[] negPrefixes = " m\u03BCnpa".toCharArray();

    // Engineering format - double

    public static String engineeringDouble(double x) {
        return engineeringDouble(x, calcPrefix(x));
    }

    public static int calcPrefix(double x) {
        x = Math.abs(x);
        double prefix = 1;
        int prefixIndex = 0;
        final double limit = 999.995;
        if (x!=0) {
            while (x/prefix < limit/1000) {
                prefix /= 1000;
                prefixIndex--;
            }
        }
        while (x /prefix>= limit) {
            prefix*=1000;
            prefixIndex++;
        }
        return prefixIndex;
    }

    public static String engineeringDouble(double x, int prefixIndex) {
        x *= Math.pow(1000, -prefixIndex);
        return String.format("%7.2f %c", x, prefixIndex >0 ? prefixes[prefixIndex] : negPrefixes[-prefixIndex]);
    }

    // Engineering format - long

    public static String engineeringLong(long x) {
        return engineeringLong(x, calcPrefix(x));
    }

    public static String engineeringLong(long nFlips, int prefixIndex) {
        for (int i=0; i<prefixIndex; i++) {
            nFlips /=1000;
        }
        return String.format("%,6d %c", nFlips, prefixes[prefixIndex]);
    }

    static int calcPrefix(long x) {
        x = Math.abs(x);
        int prefix = 1;
        int prefixIndex = 0;
        final long limit = 100000;
        while (x /prefix>= limit) {
            prefix*=1000;
            prefixIndex++;
        }
        return prefixIndex;
    }


}
