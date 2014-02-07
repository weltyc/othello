package com.welty.othello.gdk;

import junit.framework.TestCase;

/**
 * Test OsMove
 */
public class OsMoveTest extends TestCase {
    public void testConstructor() {
        final OsMove move = new OsMove(4, 3);
        assertEquals(4, move.row());
        assertEquals(3, move.col());
        assertFalse(move.equals(new Object()));
        assertFalse(move.equals(new OsMove(5, 3)));
        assertFalse(move.equals(new OsMove(4, 4)));
        assertEquals(move, new OsMove(4, 3));
        assertFalse(move.isPass());
    }

    public void testToString() {
        final OsMove pass = OsMove.PASS;
        assertEquals("PA", pass.toString());

        final OsMove move = new OsMove(4, 3);
        assertEquals("D5", move.toString());
    }

    public void testTextConstructor() {
        assertEquals("A3", new OsMove("A3").toString());
        assertEquals("A3", new OsMove("a3").toString());
        assertEquals("PA", new OsMove("PA").toString());
        assertEquals("PA", new OsMove("pAsS").toString());
    }

    public void testOfIos() throws Exception {
        assertEquals("A1", OsMove.ofIos(11).toString());
        assertEquals("A8", OsMove.ofIos(18).toString());
        assertEquals("negative numbers also work", "A1", OsMove.ofIos(-11).toString());
    }
}
