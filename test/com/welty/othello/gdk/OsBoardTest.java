package com.welty.othello.gdk;

import com.welty.othello.c.CReader;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: May 2, 2009
 * Time: 1:20:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class OsBoardTest extends TestCase {
    public void testSquareCounts() {
        final COsBoard osBoard = new COsBoard();
        final COsBoardType bt = new COsBoardType("8");
        osBoard.initialize(bt);
        osBoard.setText("--------OOOOOOOO********----------------OOOOOOOO********--------");
        assertEquals(new PieceCounts(16, 16, 32), osBoard.getPieceCounts());
        assertEquals(0, osBoard.netBlackSquares());
        assertTrue(osBoard.IsBlackMove());
        assertFalse(osBoard.GameOver());
        assertFalse(osBoard.IsMoveLegal(new COsMove("a2")));
        assertTrue(osBoard.IsMoveLegal(new COsMove("a1")));
        assertEquals(16, osBoard.GetMoves(true).size());
    }

    public void testIn() {
        final COsBoard osBoard = new COsBoard();
        final COsBoardType bt = new COsBoardType("8");
        osBoard.initialize(bt);
        osBoard.In(new CReader("8 --------OOOOOOOO********----------------OOOOOOOO********--------"));
    }
}
