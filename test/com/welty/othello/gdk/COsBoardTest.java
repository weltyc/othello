package com.welty.othello.gdk;

import com.welty.othello.c.CReader;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: Jun 19, 2009
 * Time: 9:25:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class COsBoardTest extends TestCase {
    public void testEquals() {
        final String sBoard = "OOOOOOOO ******** OOOOOOOO ********* OOOOOOOO ******** OOOOOOOO *********";
        final String sBoard2 = "*OOOOOOO ******** OOOOOOOO ********* OOOOOOOO ******** OOOOOOOO *********";
        final COsBoard a = new COsBoard(new COsBoardType("8"), sBoard, false);
        final COsBoard b = new COsBoard(new COsBoardType("8"), sBoard, false);
        final COsBoard c = new COsBoard(new COsBoardType("6"), sBoard, false);
        final COsBoard d = new COsBoard(new COsBoardType("8"), sBoard2, false);
        final COsBoard e = new COsBoard(new COsBoardType("8"), sBoard, true);
        assertEquals(a, a);
        assertEquals(a, b);
        assertFalse(a.equals(c));
        assertFalse(a.equals(d));
        assertFalse(a.equals(e));
    }

    public void testIn() {
        final CReader in = new CReader("8 " +
                "--------\n" +
                "--------\n" +
                "--*O----\n" +
                "--***O--\n" +
                "---OO*--\n" +
                "---O----\n" +
                "--------\n" +
                "--------\n" +
                "O");
        final COsBoard board = new COsBoard(in);
        assertFalse(board.blackMove());
        assertEquals(5, board.getPieceCounts().nBlack);
    }
}
