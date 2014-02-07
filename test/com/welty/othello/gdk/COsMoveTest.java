package com.welty.othello.gdk;

import junit.framework.TestCase;

public class COsMoveTest extends TestCase {
    public void testOfIos() throws Exception {
        assertEquals("A1", COsMove.ofIos(11).toString());
        assertEquals("A8", COsMove.ofIos(18).toString());
        assertEquals("negative numbers also work", "A1", COsMove.ofIos(-11).toString());
    }
}
