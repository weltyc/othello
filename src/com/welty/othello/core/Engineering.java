package com.welty.othello.core;

import com.orbanova.common.misc.Require;

/**
 * Format numbers in Engineering format, for example "123k" for 123000.
 * <p/>
 * The converted value will have 1-3 digits to the left of the decimal place. It will be followed by
 * a space, then a single-character SI prefix denoting the power of 1000.
 * <p/>
 * There are three use cases:
 * <p/>
 * 1. Displaying a single long in a compact format. In this case, call {@link #formatLong(long)}
 * which will automatically choose an ldp and width.
 * <p/>
 * 2. Displaying a set of longs in a column, expecting them to line up with a common SI prefix and decimal place in
 * the same location (and therefore not expecting the smaller values to have as much precision, or necessarily any
 * precision at all). In this case, call {@link #formatLong(long, int, int, int)} with nPre, nPost, and prefix the same
 * for all values to be converted.
 * <p/>
 * 3. Displaying a set of longs in a column, expecting each to be displayed with the same number of decimal places
 * of precision (and therefore not expecting the suffixes to be the same or the decimal point to be in the same
 * place). In this case, call {@link #formatLong(long, int)}. In order to line things up, a missing SI prefix
 * is displayed as a space character in this case.
 */
public class Engineering {
    private static char[] prefixes = " kMGTPE".toCharArray();
    private static char[] negPrefixes = " m\u03BCnpa".toCharArray();

    ///////////////////////////////////////
    // Engineering format - double
    ///////////////////////////////////////

    public static String engineeringDouble(double x) {
        return engineeringDouble(x, calcPrefix(x));
    }

    public static int calcPrefix(double x) {
        x = Math.abs(x);
        double prefix = 1;
        int prefixIndex = 0;
        final double limit = 999.995;
        if (x != 0) {
            while (x / prefix < limit / 1000) {
                prefix /= 1000;
                prefixIndex--;
            }
        }
        while (x / prefix >= limit) {
            prefix *= 1000;
            prefixIndex++;
        }
        return prefixIndex;
    }

    public static String engineeringDouble(double x, int prefixIndex) {
        x *= Math.pow(1000, -prefixIndex);
        return String.format("%7.2f %c", x, prefixIndex > 0 ? prefixes[prefixIndex] : negPrefixes[-prefixIndex]);
    }

    ///////////////////////////////////////
    // Engineering format - long
    ///////////////////////////////////////

    /**
     * Format a number in engineering format with 3 significant figures and no left padding. If the number is displayed
     * with no SI prefix, a single space character will be appended; this makes it look nicer when the unit is appended.
     *
     * @param value number to format
     * @return formatted string
     */
    public static String formatLong(long value) {
        final AbscissaMantissa am = new AbscissaMantissa(value, 3);
        return am.toString();
    }

    /**
     * calculate nDigits, the number of digits in the number if displayed to infinite precision.
     * <p/>
     * 0 is a 1-digit number just like 1-9.
     */
    static int calculateNDigits(long value) {
        int nDigits = 1;
        while (value >= 10) {
            value /= 10;
            nDigits++;
        }
        return nDigits;
    }

    /**
     * Break down a long into an abscissa and exponent, rounded to n decimal places.
     * <p/>
     * long = abscissa * 10^exponent. The abscissa will have exactly n decimal places unless
     * exponent=0, in which case the abscissa will have no more than n decimal places.
     */
    static class AbscissaMantissa {
        final boolean isNegative;
        final long abscissa;
        final int exponent;
        /**
         * Number of digits in the abscissa
         */
        final int nAbscissaDigits;

        private AbscissaMantissa(long value, int nSigFigs) {
            isNegative = value < 0;
            if (isNegative) {
                value = -value;
            }

            int nDigits = calculateNDigits(value);

            // calculate the abscissa as it will be displayed, rounded.
            long v0 = value;
            int minSigDigit = Math.max(nDigits - nSigFigs, 0);
            if (minSigDigit > 0) {
                for (int i = 1; i < minSigDigit; i++) {
                    v0 /= 10;
                }
                v0 = (v0+5)/10;
            }

            // See if the number of digits in the abscissa was changed by rounding
            int nAbscissaDigits = calculateNDigits(v0);
            if (nAbscissaDigits > nSigFigs) {
                // rounding caused the number of digits in value to increase
                minSigDigit++;
                v0 = (v0 + 4) / 10;
                nAbscissaDigits--;
            }

            this.nAbscissaDigits = nAbscissaDigits;
            abscissa = v0;
            exponent = minSigDigit;
        }

        @Override public String toString() {
            // create it backwards
            final StringBuilder sb = new StringBuilder();

            final int prefix = (exponent + nAbscissaDigits - 1) / 3;
            if (prefix > 0) {
                sb.append(prefixes[prefix]);
            }
            sb.append(' ');

            final int nPost = prefix * 3 - exponent;
            long v = abscissa;
            for (int digit = 0; digit < nAbscissaDigits; digit++) {
                if (digit == nPost && nPost > 0) {
                    sb.append('.');
                }
                sb.append((char) ('0' + v % 10));
                v /= 10;
            }
            if (isNegative) {
                sb.append('-');
            }
            return sb.reverse().toString();
        }

    }

    public static String formatLong(long value, int prefixIndex) {
        for (int i = 0; i < prefixIndex; i++) {
            value /= 1000;
        }
        return String.format("%,6d %c", value, prefixes[prefixIndex]);
    }

    /**
     * Convert a long to a string in engineering notation.
     * <p/>
     * This is meant to line things up in columns, so the output will be padded with spaces on the left if it is too short.
     * <p/>
     * In order to make it look nice when units are appended, the returned String will have a space at the end if the SI
     * prefix is missing (prefix = 0) - for example "82 ". If the SI prefix exists, there will be a space between the
     * number and the prefix - for example "82 M".
     * <p/>
     * Bug: Doesn't format Long.MIN_VALUE correctly. No plans to fix it.
     *
     * @param value  value to be converted
     * @param prefix index of SI prefix to display. 0=no prefix, 1=k, 2=M, 3=G etc.
     * @param nPost  Number of digits after the decimal place to display. If this is 0, no decimal place will be displayed.
     * @param nPre   Number of characters before the decimal place to display. Output will be left-padded up to this limit.
     *               digits and '-' are characters to the left of the decimal place.
     * @return converted String
     */
    public static String formatLong(long value, int prefix, int nPost, int nPre) {
        Require.geq(prefix, "prefix", 0);
        Require.geq(nPost, "nPost", 0);
        Require.geq(nPre, "nPre", 0);
        Require.gt(value, "value", Long.MIN_VALUE);

        final boolean negative = value < 0;
        if (negative) {
            value = -value;
        }
        final boolean hasDecimalPlace = nPost > 0;

        // build it in reverse.
        final StringBuilder sb = new StringBuilder();
        if (prefix != 0) {
            sb.append(prefixes[prefix]);
        }
        sb.append(' ');
        int skip = prefix * 3 - nPost;
        if (skip < 0) {
            throw new IllegalArgumentException("Can't have " + nPost + " decimal places in a long with SI prefix " + prefixes[prefix]);
        }
        while (skip-- > 1) {
            value /= 10;
        }
        // If skip started out at 0 it's now -1 and we don't need to round. Otherwise it's now 0 and we do need to round.
        if (skip == 0) {
            // rounding
            if (value % 10 > 5) {
                value += 5;
            }
            value /= 10;
        }

        while (nPost-- > 0) {
            final char c = (char) ('0' + value % 10);
            value /= 10;
            sb.append(c);
        }
        if (hasDecimalPlace) {
            sb.append('.');
        }
        if (value == 0) {
            sb.append('0');
            nPre--;
        } else {
            while (value > 0) {
                final char c = (char) ('0' + value % 10);
                value /= 10;
                sb.append(c);
                nPre--;
            }
        }
        if (negative) {
            sb.append('-');
            nPre--;
        }
        while (nPre-- > 0) {
            sb.append(' ');
        }

        return sb.reverse().toString();
    }
}
