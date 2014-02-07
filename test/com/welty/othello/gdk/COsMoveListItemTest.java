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
        final OsMove move = new OsMove("F5");
        final COsMoveListItem mli = new COsMoveListItem(move);
        assertEquals(move, mli.mv);
        assertEquals(false, mli.hasEval());
        assertEquals(0., mli.getElapsedTime());
    }

    public void testEquals() {
        testEquals(true, "F5", "F5");
        testEquals(false, "F5", "D5");
        testEquals(true, "F5/1.0/2.0", "F5/1.0/2.0");
        testEquals(false, "F5/1.0/5.0", "F5/1.0/2.0");
        testEquals(false, "F5/3.0/2.0", "F5/1.0/2.0");
    }

    private void testEquals(boolean expected, String a, String b) {
        final COsMoveListItem ma = new COsMoveListItem(a);
        final COsMoveListItem mb = new COsMoveListItem(b);
        assertEquals(a + " == " + b, expected, ma.equals(mb));
    }
}
