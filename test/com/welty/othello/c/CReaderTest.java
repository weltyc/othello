package com.welty.othello.c;

import junit.framework.TestCase;

import java.io.EOFException;

/**
 * Test of CReader, a class designed to ease porting C programs to Java.
 * <p/>
 * The class roughly mimics scanf() or "<<" by automatically stripping whitespace when parsing.
 */
public class CReaderTest extends TestCase {
    public void testReadInt() throws EOFException {
        CReader reader = new CReader("100 -100 +4");
        assertEquals(100, reader.readInt());
        assertEquals(-100, reader.readInt());
        assertEquals(4, reader.readInt());
        try {
            new CReader("--1").readInt();
            fail("not an integer, should throw");
        } catch (IllegalArgumentException success) {
            // expected
        }
    }

    public void testReadInt2() throws EOFException {
        CReader in = new CReader("102 93  42a");
        assertEquals(102, in.readInt());
        assertEquals(93, in.readInt());
        assertEquals(42, in.readInt());
        assertEquals('a', in.read());

        in = new CReader(" 144");
        assertEquals(144, in.readInt());
    }

    public void testReadIntEof() {
        CReader in = new CReader("");
        try {
            in.readInt();
            fail("Should throw EOFException");
        } catch (EOFException e) {
            // expected
        }
    }

    public void testReadDoubleNoExponent() {
        CReader reader = new CReader("1 2.3 -3.4 +5.");
        assertEquals(1., reader.readDoubleNoExponent(), 1e-15);
        assertEquals(2.3, reader.readDoubleNoExponent(), 1e-15);
        assertEquals(-3.4, reader.readDoubleNoExponent(), 1e-15);
        assertEquals(5, reader.readDoubleNoExponent(), 1e-15);

        reader = new CReader("1.23/");
        assertEquals(1.23, reader.readDoubleNoExponent(), 1e-15);
    }

    public void testReadDoubleNoExponent2() {
        CReader in = new CReader("102 -9 +13.1  .45  13.2  -13.3a");
        assertEquals(102, in.readDoubleNoExponent(), 1e-10);
        assertEquals(-9, in.readDoubleNoExponent(), 1e-10);
        assertEquals(13.1, in.readDoubleNoExponent(), 1e-10);
        assertEquals(.45, in.readDoubleNoExponent(), 1e-10);
        assertEquals(13.2, in.readDoubleNoExponent(), 1e-10);
        assertEquals(-13.3, in.readDoubleNoExponent(), 1e-10);
    }

    public void testIgnoreTo() {
        assertTrue(new CReader("foo").ignoreTo('o'));
        assertFalse(new CReader("foo").ignoreTo('b'));
    }

    public void testIgnoreTo2() {
        final String s = "no end of line";
        final CReader in = new CReader(s);
        assertTrue(in.ignoreTo(' '));
        assertEquals('e', in.read());
        assertFalse(in.ignoreTo(')'));
    }

    public void testReadString() {
        CReader in = new CReader("102 -9 \t booYah!");
        assertEquals("102", in.readString());
        assertEquals("-9", in.readString());
        assertEquals("booYah!", in.readString());
    }

    public void testPeekAtEof() throws EOFException {
        CReader in = new CReader("");
        assertEquals((char) -1, in.peek());

        // make sure readInt() does the right thing too
        in = new CReader("102");
        in.readInt();
        assertEquals((char) -1, in.peek());
    }

    public void testReadLine() {
        final String s = "no end of line";
        CReader in = new CReader(s);
        assertEquals(s, in.readLine());
    }

    public void testReadLong() throws EOFException {
        testReadLong("1", 1, "");
        testReadLong(" 1", 1, "");
        testReadLong("+1", 1, "");
        testReadLong("-1", -1, "");
        testReadLong("123", 123, "");
        testReadLong("-123", -123, "");
        testReadLong("123456789011", 123456789011L, "");
        testReadLong("123 foo", 123, " foo");

        testReadLongThrowsEofException("");
        testReadLongThrowsEofException(" ");

        testReadLongThrowsIllegalArgumentException("foo");
        testReadLongThrowsIllegalArgumentException("  foo");
    }

    private void testReadLong(String input, long expected, String remainder) throws EOFException {
        final CReader in = new CReader(input);
        assertEquals(expected, in.readLong());
        assertEquals(remainder, in.readLine());
    }

    private void testReadLongThrowsEofException(String input) {
        try {
            new CReader(input).readLong();
            fail("should throw");
        } catch (EOFException e) {
            // expected
        }
    }

    private void testReadLongThrowsIllegalArgumentException(String input) {
        try {
            new CReader(input).readLong();
            fail("should throw");
        } catch (IllegalArgumentException e) {
            // expected
        } catch (EOFException e) {
            fail("should throw IllegalArgumentException");
        }
    }
}
