package com.welty.othello.c;

import junit.framework.TestCase;

import java.io.EOFException;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: Jun 15, 2009
 * Time: 5:38:33 AM
 * To change this template use File | Settings | File Templates.
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
}
