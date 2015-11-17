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

package com.welty.othello.c;

import org.jetbrains.annotations.NotNull;

import java.io.*;

/**
 * A class designed to ease porting C programs to Java.
 * <p/>
 * The class roughly mimics scanf() or "<<" by automatically stripping whitespace when parsing.
 *
 * This class reads in bytes, which are then converted to characters using (char); no charset encoding
 * is performed.
 */
public class CReader {
    private final @NotNull PushbackInputStream in;

    public CReader(@NotNull File file) throws FileNotFoundException {
        this(new BufferedInputStream(new FileInputStream(file)));
    }

    public CReader(@NotNull InputStream is) {
        in = new PushbackInputStream(is);
    }

    /**
     * Create a CReader whose data is the given string
     */
    public CReader(@NotNull String s) {
        this(new ByteArrayInputStream(bytes(s)));
    }

    /**
     * Convert s to bytes.
     *
     * Characters are converted using (byte); no Charset is used. If the String contains any
     * characters outside the range 0-255 an exception is thrown.
     *
     * @param s string to convert
     * @return array of bytes
     * @throws IllegalArgumentException if s contains chars outside the range 0-255.
     */
    static private byte[] bytes(String s) {
        final byte[] bytes = new byte[s.length()];
        for (int i=0; i<s.length(); i++) {
            final char c = s.charAt(i);
            if (c > 255) {
                throw new IllegalArgumentException("Illegal character at location " + i + ": " + c);
            }
            bytes[i] = (byte)c;
        }
        return bytes;
    }

    /**
     * Read an integer from the stream.
     *
     * @param def default value
     * @return value read from stream, or default value if eof has been reached
     */
    public int readInt(int def) {
        try {
            return readInt();
        } catch (EOFException e) {
            return def;
        }
    }

    /**
     * Skip whitespace, then read an integer from the stream.
     *
     * @return the integer read
     * @throws EOFException             if the next non-space character is EOF.
     * @throws IllegalArgumentException if the next non-space character do not form an integer.
     */
    public int readInt() throws EOFException {
        return Integer.parseInt(readSignedDecimal());
    }

    /**
     * Skip whitespace, then read a long from the stream.
     *
     * @return long value
     * @throws EOFException             if the next non-space character is EOF.
     * @throws IllegalArgumentException if the next non-space character do not form an integer.
     */
    public long readLong() throws EOFException {
        return Long.parseLong(readSignedDecimal());
    }

    /**
     * Skip whitespace, then read [+-]?[0-9]+ from the stream.
     * <p/>
     * Initial '+' characters are not included in the returned String because java's
     * Integer.parseInt() and Long.parseLong() puke on an initial '+'.
     *
     * @return a String containing the non-whitespace characters
     * @throws EOFException             if the next non-space token is EOF.
     * @throws IllegalArgumentException if the next non-space tokens do not match the pattern.
     */
    private String readSignedDecimal() throws EOFException {
        ignoreWhitespace();
        StringBuilder sb = new StringBuilder();
        if (eof()) {
            throw new EOFException();
        }
        char c = peek();
        if (c == '-' || c == '+') {
            c = read();
            sb.append(c);
        } else if (!Character.isDigit(c)) {
            throw new IllegalArgumentException("expected +- or digit, had " + c);
        }
        copyDigits(sb);
        return sb.toString();
    }

    /**
     * Reads digits from this CReader and appends them to a StringBuilder.
     * <p/>
     * Reads a pattern matching "[0-9]*" from the CReader.
     *
     * @param sb destination for digits
     */
    private void copyDigits(StringBuilder sb) {
        char c;
        while (Character.isDigit(c = read())) {
            sb.append(c);
        }
        unread(c);
    }

    /**
     * Strip whitespace characters from the stream
     */
    public void ignoreWhitespace() {
        char c;
        //noinspection StatementWithEmptyBody
        while (Character.isWhitespace(c = read())) {
        }
        unread(c);
    }

    /**
     * Remove the next character from the stream.
     *
     * @return the next character (does not strip whitespace characters, returns them instead),
     *         or 65535 if at end of stream.
     */
    public char read() {
        try {
            return (char) in.read();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void unread(char c) {
        if (c != (char) -1) {
            try {
                in.unread(c);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    /**
     * strip from the stream any characters or digits, as determined by
     * Character.isLetterOrDigit()
     */
    public void ignoreAlphaNumeric() {
        char c;
        //noinspection StatementWithEmptyBody
        while (Character.isLetterOrDigit(c = read())) {
        }
        unread(c);
    }

    /**
     * Look at the next character from the stream without removing it.
     *
     * @return the next character (does not strip whitespace characters, returns them instead),
     *         or 65535 if at end of stream.
     */
    public char peek() {
        char c = read();
        unread(c);
        return c;
    }

    /**
     * discard the next n characters from the stream
     */
    public void ignore(int n) {
        for (int i = 0; i < n; i++) {
            read();
        }
    }

    /**
     * discard the next character from the stream
     */
    public void ignore() {
        ignore(1);
    }

    /**
     * Ignores whitespace and then returns the next double from the stream, ignoring any exponent data (exponent data is the 'E+10' at the end of some doubles)
     *
     * @return value of the double
     * @throws NumberFormatException if the next characters do not form a double
     */
    public double readDoubleNoExponent() {
        ignoreWhitespace();

        boolean hasSign = false; // have seen a sign char or a digit (which implicitly means a sign char)
        boolean hasDecimal = false;

        StringBuilder sb = new StringBuilder();
        char c;

        while (0 != (c = read())) {
            if (c == '+' | c == '-') {
                if (hasSign | hasDecimal) {
                    break;
                } else {
                    hasSign = true;
                }
            } else if (c == '.') {
                if (hasDecimal) {
                    break;
                } else {
                    hasDecimal = true;
                }
            } else if (Character.isDigit(c)) {
                hasSign = true;
            } else {
                break;
            }
            sb.append(c);
        }
        unread(c);

        return Double.parseDouble(sb.toString());
    }

    /**
     * ignore whitespace and then read in the next string (of non-whitespace characters)
     *
     * @return the next string, "" if at EOF
     */
    public String readString() {
        ignoreWhitespace();
        StringBuilder sb = new StringBuilder();
        char c;
        while (!Character.isWhitespace(c = read()) && c != (char) -1) {
            sb.append(c);
        }
        unread(c);
        return sb.toString();
    }

    /**
     * Reads the line up to the terminal character.
     *
     * The terminal character is removed from the stream but is not added to the return value.
     * Whitespace at the beginning of the line is returned, not ignored.
     *
     * If EOF is reached when the line is empty, an IllegalArgumentException is thrown. If EOF is reached
     * when the line is not empty, the line is returned.
     *
     * @param terminal character that ends the line
     * @return line up to, but not including, the terminal character
     * @throws IllegalArgumentException if EOF occurs with an empty line.
     */
    public String readLine(char terminal) throws EOFException {
        StringBuilder sb = new StringBuilder();
        char c;
        while (terminal != (c = read()) && c != (char) -1) {
            sb.append(c);
        }
        if (c==(char)-1 && sb.length()==0) {
            throw new EOFException("EOF");
        }
        return sb.toString();
    }

    /**
     * reads the line up to '\n'. The '\n' is removed from the stream but is not added to the return value.
     * Whitespace at the beginning of the line is returned, not ignored
     *
     * @return line up to, but not including, the '\n' character
     */
    public String readLine() throws EOFException {
        return readLine('\n');
    }

    /**
     * Reads the line up to the next newline (\n).
     *
     * The newline is removed from the stream but is not added to the return value.
     * Whitespace at the beginning of the line is returned, not ignored.
     *
     * The only difference between this method and {@link #readLine()} is that readLine() throws an exception
     * if this CReader is already at EOF, while this method returns an empty string.
     *
     * @return line up to, but not including, the newline
     */
    public String readLineNoThrow() {
        return readLineNoThrow('\n');
    }

    /**
     * Reads the line up to the terminal character.
     *
     * The terminal character is removed from the stream but is not added to the return value.
     * Whitespace at the beginning of the line is returned, not ignored.
     *
     * The only difference between this method and {@link #readLine(char)} is that readLine() throws an exception
     * if this CReader is already at EOF, while this method returns an empty string.
     *
     * @return line up to, but not including, the terminal character
     */
    public String readLineNoThrow(char terminal) {
        StringBuilder sb = new StringBuilder();
        char c;
        while (terminal != (c = read()) && c != (char) -1) {
            sb.append(c);
        }
        return sb.toString();
    }

    public boolean eof() {
        return peek() == (char) -1;
    }

    public short readShort() throws EOFException {
        return (short) readInt();
    }

    /**
     * Ignore whitespace before reading the next character via read()
     *
     * @return next non-whitespace character, or -1 if at EOF
     */
    public char readChar() {
        ignoreWhitespace();
        return read();
    }

    /**
     * strip whitespace and then return true if at eof
     *
     * @return true if at eof, else not
     */
    public boolean wsEof() {
        ignoreWhitespace();
        return eof();
    }

    public void close() {
        try {
            in.close();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public float readFloatNoExponent() {
        return (float) readDoubleNoExponent();
    }

    /**
     * Strip off all chars in the stream up to the first occurrence of a character.
     * <p/>
     * The first occurrence of the character will also be stripped off.
     *
     * @param c character to look for
     * @return true if the stream contains c, false if the stream doesn't contain c (in which case the stream will be at eof)
     */
    public boolean ignoreTo(char c) {
        while (true) {
            char r = read();
            if (r == (char) -1) {
                return false;
            }
            if (r == c) {
                return true;
            }
        }

    }
}
