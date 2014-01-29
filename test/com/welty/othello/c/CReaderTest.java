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
        }
        catch (IllegalArgumentException success) {
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

    public void testIgnoreTo() {
        assertTrue(new CReader("foo").ignoreTo('o'));
        assertFalse(new CReader("foo").ignoreTo('b'));
    }
}
