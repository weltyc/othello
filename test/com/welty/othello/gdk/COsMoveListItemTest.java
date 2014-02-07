package com.welty.othello.gdk;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: Jun 21, 2009
 * Time: 10:16:46 PM
 * To change this template use File | Settings | File Templates.
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
}
