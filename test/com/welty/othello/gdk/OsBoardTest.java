package com.welty.othello.gdk;

import com.welty.othello.c.CReader;
import junit.framework.TestCase;

/**
 * Test OsBoard
 */
public class OsBoardTest extends TestCase {
    public static final String sBoard = "8 OOOOOOOO ******** OOOOOOOO ******** OOOOOOOO ******** OOOOOOOO ******** O";
    private static final OsBoard boardWtm = board(sBoard);
    private static final OsBoard board6 = board("6 OOOOOO ****** OOOOOO ****** OOOOOO ****** O");
    private static final OsBoard boardBtm = board("8 OOOOOOOO ******** OOOOOOOO ******** OOOOOOOO ******** OOOOOOOO ******** *");
    private static final OsBoard board2 = board("8 *OOOOOOO ******** OOOOOOOO ******** OOOOOOOO ******** OOOOOOOO ******** O");

    public void testInitialization() {
        assertFalse(board(sBoard).blackMove());
        assertTrue(boardBtm.blackMove());
        testInitializationThrows("too short", "4 OOOO OOOO OOOO OOOO ");
        testInitializationThrows("too long", "4 OOOO OOOO OOOO OOOO *O");
    }

    private void testInitializationThrows(String message, String sBoard) {
        try {
            board(sBoard);
            fail("Should throw: " + message);
        }
        catch(IllegalArgumentException e) {
            // expected
        }
    }

    public void testEquals() {
        assertEquals(boardWtm, boardWtm);
        assertEquals(boardWtm, board(sBoard));
        assertFalse(boardWtm.equals(board6));
        assertFalse(boardWtm.equals(boardBtm));
        assertFalse(boardWtm.equals(board2));
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
        final OsBoard board = new OsBoard(in);
        assertFalse(board.blackMove());
        assertEquals(5, board.getPieceCounts().nBlack);
    }

    public void testIsMoveLegal() {
        final COsMove pass = new COsMove("pass");
        assertFalse(boardWtm.IsMoveLegal(pass));
    }

    public static OsBoard board(String boardText) {
        return new OsBoard(new CReader(boardText));
    }

    public void testSquareCounts() {
        final OsBoard osBoard = new OsBoard();
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
}
