package com.welty.othello.gdk;

import junit.framework.TestCase;

/**
 * Test COsMoveListItem behaviour
 */
public class COsMoveListItemTest extends TestCase {
    public void testReadWrite() {
        final String test = "F5/1.23/2.1";
        final COsMoveListItem mli = new COsMoveListItem(test);
        assertEquals(test, mli.toString());
    }

    public void testHasEval() {
        testHasEval(true, "F5/1.00");
        testHasEval(false, "F5");
        testHasEval(true, "F5/0.00");
        testHasEval(false, "F5//1.0");
        testHasEval(true, "F5/0.00/1.0");
    }

    private void testHasEval(boolean expected, String text) {
        final COsMoveListItem mli = new COsMoveListItem(text);
        assertEquals(text, expected, mli.hasEval());
        assertEquals(text, text, mli.toString());
    }

    public void testMoveOnlyConstructor() {
        final COsMove move = new COsMove("F5");
        final COsMoveListItem mli = new COsMoveListItem(move);
        assertEquals(move, mli.mv);
        assertEquals(false, mli.hasEval());
        assertEquals(0., mli.tElapsed);

        // check defensive copy of move
        move.Set(1, 1);
        assertFalse(move.equals(mli.mv));
    }
}
