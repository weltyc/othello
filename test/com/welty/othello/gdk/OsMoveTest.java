package com.welty.othello.gdk;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: May 2, 2009
 * Time: 7:33:06 AM
 * To change this template use File | Settings | File Templates.
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
        final COsMove pass = new COsMove();
        pass.SetPass();
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

    public void testSet() {
        final COsMove move = new COsMove();
        move.SetPass();
        assertTrue(move.Pass());
        move.Set(2, 3);
        assertEquals(new COsMove(2, 3), move);
    }
}