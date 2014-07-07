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

package com.welty.othello.database;

import java.io.EOFException;

/**
*/
class Parser {
    int loc = 0;
    final String string;

    Parser(String string) {
        this.string = string;
    }

    /**
     * Parses the next digits in the string, stopping when the next character is not a digit.
     *
     * @return Int representation of the digits.
     * @throws java.io.EOFException          if starting at the end of the string
     * @throws IllegalStateException if the next character is not a digit.
     */
    public int readPositiveInt() throws EOFException {
        int start = loc;
        if (loc == string.length()) {
            throw new EOFException();
        }
        //noinspection StatementWithEmptyBody
        for (; notEof() && Character.isDigit(string.charAt(loc)); loc++) ;
        if (loc == start) {
            throw new IllegalStateException("required digit, was " + string.charAt(loc));
        }
        return Integer.parseInt(string.substring(start, loc));
    }

    /**
     * Skip past characters that are whitespace.
     * <p/>
     * Stops at non-whitespace char or EOF.
     */
    public void skipWs() {
        while (notEof() && Character.isSpaceChar(peek())) {
            loc++;
        }
    }

    public boolean notEof() {
        return loc < string.length();
    }

    public char peek() {
        return string.charAt(loc);
    }

    /**
     * Check that the next few characters match the string, and skip them.
     *
     * @param expected expected next characters
     * @throws java.io.EOFException          if there are not enough characters to match the string.
     * @throws IllegalStateException if the next characters don't match.
     */
    public void expect(String expected) throws EOFException {
        if (loc + expected.length() > string.length()) {
            throw new EOFException();
        }
        final String actual = string.substring(loc, loc + expected.length());
        if (!expected.equals(actual)) {
            throw new IllegalStateException("Expected <" + expected + ">, was <" + actual + ">");
        }
        loc+=expected.length();
    }

    /**
     * Read until the character 'c' occurs.
     *
     * Leaves the character 'c' on the stream.
     *
     * @param c string terminator
     * @return the string from the current location to immediately preceding the character c.
     * @throws java.io.EOFException if c doesn't occur before EOF
     */
    public String readUntil(char c) throws EOFException {
        final int start = loc;
        for (; notEof(); loc++) {
            if (peek()==c) {
                return string.substring(start, loc);
            }
        }
        throw new EOFException();
    }

    public int getLoc() {
        return loc;
    }

    public String remaining() {
        return string.substring(loc);
    }
}
