package com.welty.othello.c;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: May 2, 2009
 * Time: 12:06:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class CReader {
    private final PushbackInputStream in;

    public CReader(File file) throws FileNotFoundException {
        this(new BufferedInputStream(new FileInputStream(file)));
    }

    public CReader(InputStream is) {
        in = new PushbackInputStream(is);
    }

    /**
     * Create a CReader whose data is the given string
     */
    public CReader(String s) {
        this(new ByteArrayInputStream(s.getBytes()));
    }

    public int readInt() throws EOFException {
        ignoreWhitespace();
        StringBuilder sb = new StringBuilder();
        char c = read();
        if (Character.isDigit(c) || c == '-' || c == '+') {
            if (c != '+') {
                // java doesn't like integers that start with '+'
                sb.append(c);
            }
            readDigits(sb);
            return Integer.parseInt(sb.toString());
        } else if (c == -1) {
            throw new EOFException("End of file reached");
        } else {
            throw new IllegalArgumentException("Can't parse integer starting char '" + c + "'");
        }
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
        }
        catch (EOFException e) {
            return def;
        }
    }

    private String readNumberText() {
        StringBuilder sb = new StringBuilder();

        ignoreWhitespace();

        readDigits(sb);
        return sb.toString();
    }

    private void readDigits(StringBuilder sb) {
        char c;
        while (Character.isDigit(c = read())) {
            sb.append(c);
        }
        unread(c);
    }

    /**
     * Ignores whitespace, then returns the long created from the next digits in the stream
     * Positive only for now
     *
     * @return long value
     */
    public long readLong() {
        final String text = readNumberText();
        return Long.parseLong(text);
    }

    /**
     * Strip whitespace characters from the stream
     */
    public void ignoreWhitespace() {
        char c;
        while (Character.isWhitespace(c = read())) {
        }
        unread(c);
    }

    /**
     * @return the next character (does not strip whitespace characters, returns them instead)
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
    public void ignoreAlnum() {
        char c;
        while (Character.isLetterOrDigit(c = read())) {
        }
        unread(c);
    }

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
     * reads the line up to the terminal character. The terminal character is removed from the stream but is not added to the return value.
     * Whitespace at the beginning of the line is returned, not ignored
     *
     * @param terminal character that ends the line
     * @return line up to, but not including, the terminal character
     */
    public String readLine(char terminal) {
        StringBuilder sb = new StringBuilder();
        char c;
        while (terminal != (c = read()) && c != (char) -1) {
            sb.append(c);
        }
        return sb.toString();
    }

    public String readLine() {
        return readLine('\n');
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
     * Strip of all chars in the stream up to 'c'
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
