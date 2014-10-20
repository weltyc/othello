package com.welty.othello.gdk;

import junit.framework.TestCase;

public class PieceCountsTest extends TestCase {

    private PieceCounts c3727 = new PieceCounts(37, 27, 0);
    private PieceCounts c3232 = new PieceCounts(32, 32, 0);
    private PieceCounts c5310 = new PieceCounts(53, 10, 1);
    private PieceCounts c1053 = new PieceCounts(10, 53, 1);

    public void testResult() throws Exception {
        assertEquals(10, c3727.result(false));
        assertEquals(-10, c3727.result(true));

        assertEquals(0, c3232.result(false));
        assertEquals(0, c3232.result(true));

        assertEquals(44, c5310.result(false));
        assertEquals(-44, c5310.result(true));
    }

    public void testNetBlackSquares() throws Exception {
        assertEquals(10, c3727.netBlackSquares());
        assertEquals(0, c3232.netBlackSquares());
        assertEquals(43, c5310.netBlackSquares());
        assertEquals(-43, c1053.netBlackSquares());
    }
}