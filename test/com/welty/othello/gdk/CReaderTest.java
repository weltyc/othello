package com.welty.othello.gdk;

import com.welty.othello.c.CReader;
import junit.framework.TestCase;

import java.io.EOFException;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: May 2, 2009
 * Time: 12:09:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class CReaderTest extends TestCase {
    public void testReadInt() throws EOFException {
        CReader in = new CReader("102 93  42a");
        assertEquals(102, in.readInt());
        assertEquals(93, in.readInt());
        assertEquals(42, in.readInt());
        assertEquals('a', in.read());

        in = new CReader(" 144");
        assertEquals(144, in.readInt());
    }

    public void testReadDoubleNoExponent() {
        CReader in = new CReader("102 -9 +13.1  .45  13.2  -13.3a");
        assertEquals(102, in.readDoubleNoExponent(), 1e-10);
        assertEquals(-9, in.readDoubleNoExponent(), 1e-10);
        assertEquals(13.1, in.readDoubleNoExponent(), 1e-10);
        assertEquals(.45, in.readDoubleNoExponent(), 1e-10);
        assertEquals(13.2, in.readDoubleNoExponent(), 1e-10);
        assertEquals(-13.3, in.readDoubleNoExponent(), 1e-10);
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

    public void testIgnoreTo() {
        final String s = "no end of line";
        final CReader in = new CReader(s);
        assertTrue(in.ignoreTo(' '));
        assertEquals('e', in.read());
        assertFalse(in.ignoreTo(')'));
    }
}
