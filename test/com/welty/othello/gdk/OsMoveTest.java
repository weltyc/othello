package com.welty.othello.gdk;

import junit.framework.TestCase;

/**
 * Test OsMove
 */
public class OsMoveTest extends TestCase {
    public void testConstructor() {
        final COsMove move = new COsMove(4, 3);
        assertEquals(4, move.Row());
        assertEquals(3, move.Col());
        assertFalse(move.equals(new Object()));
        assertFalse(move.equals(new COsMove(5, 3)));
        assertFalse(move.equals(new COsMove(4, 4)));
        assertEquals(move, new COsMove(4, 3));
        assertFalse(move.Pass());
    }

    public void testToString() {
        final COsMove pass = COsMove.PASS;
        assertEquals("PA", pass.toString());

        final COsMove move = new COsMove(4, 3);
        assertEquals("D5", move.toString());
    }

    public void testTextConstructor() {
        assertEquals("A3", new COsMove("A3").toString());
        assertEquals("A3", new COsMove("a3").toString());
        assertEquals("PA", new COsMove("PA").toString());
        assertEquals("PA", new COsMove("pAsS").toString());
    }
}
