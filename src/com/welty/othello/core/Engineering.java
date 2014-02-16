package com.welty.othello.core;

import com.orbanova.common.misc.Require;

/**
 * Format longs or doubles in Engineering format, for example "123k" for 123000.
 * <p/>
 * There are three use cases:
 * <p/>
 * 1. Displaying a single long/double in a compact format. In this case, call {@link #compactFormat(long)}
 * or {@link #compactFormat(double)} which will automatically choose a prefix and display the number with 3 significant
 * figures (or fewer if it is a long between -99 and 99).
 * <p/>
 * 2. Displaying a set of longs/doubles in a column, expecting them to line up with a common SI prefix and decimal place in
 * the same location (and therefore not expecting the smaller values to have as much precision, or necessarily any
 * precision at all). In this case, call {@link #formatLong(long, int, int, int)} or
 * {@link #formatDouble(double, int, int, int)} with nPre, nPost, and prefix the same
 * for all values to be converted, or the simpler {@link #formatLong(long, int)} or {@link #formatDouble(double, int)}
 * which default 2 digits after the decimal and is padded to 4 characters before.
 * <p/>
 * 3. Displaying a set of longs in a column, expecting each to be displayed with the same number of decimal places
 * of precision (and therefore not expecting the suffixes to be the same or the decimal point to be in the same
 * place). This case is not implemented.
 */
public class Engineering {
    private static char[] prefixes = " kMGTPE".toCharArray();
    private static char[] negPrefixes = " m\u03BCnpa".toCharArray();

    ///////////////////////////////////////
    // Engineering format - double
    ///////////////////////////////////////

    /**
     * Format the double with exactly three decimal places of precision
     *
     * @param x value to format
     * @return formatted double
     */
    public static String compactFormat(double x) {
        final int nDecimalPlaces = 3;

        if (x == 0) {
            return "0.00 ";
        }
        final double absX = x < 0 ? -x : x;

        int pow = (int) Math.floor(Math.log10(absX));
        long abscissa = Math.round(absX * Math.pow(10, nDecimalPlaces - 1 - pow));
        if (abscissa >= Math.pow(10, nDecimalPlaces)) {
            abscissa /= 10;
            pow++;
        }

        final int prefix = pow >= 0 ? pow / 3 : (pow - 2) / 3;
        final int nPost = 2 - (pow - prefix * 3);
        final StringBuilder sb = new StringBuilder();
        if (prefix > 0) {
            sb.append(prefixes[prefix]);
        }
        if (prefix < 0) {
            sb.append(negPrefixes[-prefix]);
        }
        sb.append(' ');
        appendShiftedLong(abscissa, sb, x < 0, 0, nPost);

        return sb.reverse().toString();
    }

    public static String formatDouble(double x, int prefixIndex) {
        return formatDouble(x, prefixIndex, 2, 4);
    }

    public static int calcPrefix(double x) {
        if (x == 0) {
            return 0;
        }
        x = Math.abs(x);
        final int est = (int) Math.floor(Math.log10(x) / 3);
        x *= Math.pow(1000, -est);
        return (x >= 999.5) ? est + 1 : est;
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
     * @param value       value to be converted
     * @param prefixIndex index of SI prefix to display. -3=n, -2=u, -1=m, 0=no prefix, 1=k, 2=M, 3=G etc.
     * @param nPost       Number of digits after the decimal place to display. If this is 0, no decimal place will be displayed.
     * @param nPre        Minimum number of characters before the decimal place to display. Output will be left-padded up to this limit.
     *                    digits and '-' count as characters before the decimal place.
     * @return converted String
     */
    public static String formatDouble(double value, int prefixIndex, int nPost, int nPre) {
        Require.geq(nPost, "nPost", 0);
        Require.geq(nPre, "nPre", 0);
        Require.finite(value);

        final boolean negative = value < 0;
        if (negative) {
            value = -value;
        }
        // build it in reverse.
        final StringBuilder sb = new StringBuilder();
        if (prefixIndex > 0) {
            sb.append(prefixes[prefixIndex]);
        } else if (prefixIndex < 0) {
            sb.append(negPrefixes[-prefixIndex]);
        }

        sb.append(' ');
        int skip = prefixIndex * 3 - nPost;
        value *= Math.pow(10, -skip);
        long vRounded = Math.round(value);

        appendShiftedLong(vRounded, sb, negative, nPre, nPost);

        return sb.reverse().toString();
    }

    /**
     * Append a long to the StringBuilder, in reverse
     *
     * @param value    value to append
     * @param sb       destination
     * @param negative if true, insert a minus sign
     * @param nPre     minimum number of characters before the decimal places (spaces added if needed to get it up to this level)
     * @param nPost    number of decimals after the decimal place
     */
    private static void appendShiftedLong(long value, StringBuilder sb, boolean negative, int nPre, int nPost) {
        for (int i = 0; i < nPost; i++) {
            final char c = (char) ('0' + value % 10);
            value /= 10;
            sb.append(c);
        }
        if (nPost > 0) {
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
    public static String compactFormat(long value) {
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
                v0 = (v0 + 5) / 10;
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

    /**
     * Format a long as in {@link #formatLong(long, int, int, int)} with 2 digits after the decimal place and 4 characters before.
     *
     * @param value       value to format
     * @param prefixIndex SI prefix: 0 = units, 1=k, 2=M, 3=G etc.
     * @return formatted long
     */
    public static String formatLong(long value, int prefixIndex) {
        for (int i = 0; i < prefixIndex; i++) {
            value /= 1000;
        }
        return formatLong(value, prefixIndex, 2, 4);
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
     * @param value       value to be converted
     * @param prefixIndex index of SI prefix to display. 0=no prefix, 1=k, 2=M, 3=G etc.
     * @param nPost       Number of digits after the decimal place to display. If this is 0, no decimal place will be displayed.
     * @param nPre        Minimum number of characters before the decimal place to display. Output will be left-padded up to this limit.
     *                    digits and '-' count as characters before the decimal place.
     * @return converted String
     */
    public static String formatLong(long value, int prefixIndex, int nPost, int nPre) {
        Require.geq(prefixIndex, "prefix", 0);
        Require.geq(nPost, "nPost", 0);
        Require.geq(nPre, "nPre", 0);
        Require.gt(value, "value", Long.MIN_VALUE);

        final boolean negative = value < 0;
        if (negative) {
            value = -value;
        }

        // build it in reverse.
        final StringBuilder sb = new StringBuilder();
        if (prefixIndex != 0) {
            sb.append(prefixes[prefixIndex]);
        }
        sb.append(' ');
        int skip = prefixIndex * 3 - nPost;
        if (skip < 0) {
            throw new IllegalArgumentException("Can't have " + nPost + " digits after the decimal place in a long with SI prefix " + prefixes[prefixIndex]);
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

        appendShiftedLong(value, sb, negative, nPre, nPost);

        return sb.reverse().toString();
    }
}
