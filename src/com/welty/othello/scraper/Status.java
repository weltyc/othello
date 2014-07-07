/*
 * Copyright (c) 2014 Chris Welty.
 *
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * For the license, see <http://www.gnu.org/licenses/gpl.html>.
 */

package com.welty.othello.scraper;

import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Status of an othello search, parsed for easy access
 */
class Status implements Comparable<Status> {
    // === B4  -6.00 0 100%  117.86 1052Mn/117.863s = 8925kn/s;  0.11 us/n 28e:  ===
    private static final String squarePattern = "(..)";
    private static final String scorePattern = "\\+?(-?\\S+)";
    private static final String ws = "\\s+";
    private static final String depthPattern = "(\\S+)\\s+(?:, book )?";
    private static final String emptiesPattern = "(\\d+)e:";
    private static final String secondsPattern = "(\\S+)";
    private static final String nodesPattern = "(\\d+\\D)n/";
    private static final Pattern resultPattern = Pattern.compile("=== " + squarePattern + ws + scorePattern
            + ws + "\\S+" + ws + depthPattern + secondsPattern +ws + nodesPattern + ".* " + emptiesPattern + ".*");
    private static final String engineeringExponents = "pnum kMGTPE";

    final String square;
    final double score;   // or NaN if we didn't compute a score because the move was forced
    final int nEmpty;
    final double seconds;
    final boolean isBook;
    final double nodes;
    private final String depth;
    private final String text;

    private Status(Matcher matcher) {
        square = matcher.group(1);
        final String scoreText = matcher.group(2);
        score = scoreText.equals("?") ? Double.NaN : Double.parseDouble(scoreText);
        depth = isForced() ? "0" : matcher.group(3);   // new version reports a depth for forced moves; old version doesn't
        seconds = Double.parseDouble(matcher.group(4));
        nodes = parseEngineering(matcher.group(5));
        nEmpty = Integer.parseInt(matcher.group(6));
        isBook = matcher.group(0).contains("book");
        text = matcher.group(0);
    }

    /**
     * Convert engineering notation ("12M") into double representation (12000000)
     * @param text text - must be (parse-able as double) (optional whitespace) (engineering exponent or space)
     * @return  double that it parses to
     */
    public static double parseEngineering(String text) {
        final int exponentChar = text.charAt(text.length() - 1);
        final int exponent = 3*(engineeringExponents.indexOf(exponentChar)-4);
        final String mantissaText = text.substring(0, text.length()-1).trim();
        final double mantissa = Double.parseDouble(mantissaText);
        return Math.pow(10, exponent)*mantissa;
    }

    /**
     * Create a Status object for a score line
     *
     * @param line a line of text
     * @return score, or null if the line is not a score line
     */
    public static @Nullable Status create(String line) {
        final Matcher matcher = resultPattern.matcher(line);
        if (matcher.matches()) {
            try {
                return new Status(matcher);
            } catch (NumberFormatException e) {
                throw new RuntimeException("For string '" + line + "'", e);
            }
        } else {
            return null;
        }
    }

    public boolean isSolved() {
        return depth.equals("100%W") || depth.equals("100%");
    }

    @Override public String toString() {
        return text;
    }

    /**
     * Convert a double to engineering notation (for instance, 12e6 turns into "12M").
     * <p>
     * Exponents are base 10, not binary, so "k" means 10<sup>3</sup> not 2<sup>10</sup>.
     * <p/>
     * If there is no exponent (for instance, the number 12), a space is appended instead. This allows
     * printed values to line up.
     * <p/>
     * This function always displays 3 significant figures. 0 is converted as "0.00 ".
     *
     * @param value value to convert
     * @return String containing engineering notation
     */
    public static String engineering(double value) {
        if (value==0) {
            return "0.00 ";
        }
        int exp = 0;
        while (value < 1) {
            value*=1000;
            exp--;
        }
        while (value >= 1000) {
            value*=0.001;
            exp++;
        }
        final String mantissa;
        if (value < 10) {
            mantissa = String.format("%4.2f", value);
        }
        else if (value < 100) {
            mantissa = String.format("%4.1f", value);
        }
        else {
            mantissa = String.format("%#4.0f", value);
        }
        return mantissa + engineeringExponents.charAt(exp+4);
    }

    private static boolean equalsOrBothNan(double a, double b) {
        if (Double.isNaN(a)) {
            return Double.isNaN(b);
        }
        else {
            return a==b;
        }
    }

    public boolean sameResult(Status result2) {
        return square.equals(result2.square) && equalsOrBothNan(score,result2.score);
    }

    /**
     * @param result2  score to compare
     * @return false if the results show an error in the program - a 100%WLD search with a different WLD score, or a
     * 100% score with a different value.
     */
    public boolean isConsistentWith(Status result2) {
        if (!depth.equals(result2.depth)) {
            return false;
        }
        if (isForced()) {
            return result2.isForced();
        }
        else if (result2.isForced()) {
            return false;
        }

        if (depth.equals("100%W")) {
            return Math.signum(score)==Math.signum(result2.score);
        }
        else if (depth.equals("100%")) {
            return score==result2.score;
        }
        else {
            return true;
        }
    }

    private boolean isForced() {
        return Double.isNaN(score);
    }

    public int compareTo(Status o) {
        return Double.compare(score, o.score);
    }
}
