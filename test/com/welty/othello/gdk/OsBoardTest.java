/*
 * Copyright (c) 2014 Chris Welty.
 *
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * For the license, see <http://www.gnu.org/licenses/gpl.html>.
 */

package com.welty.othello.gdk;

import com.welty.othello.c.CReader;
import junit.framework.TestCase;

/**
 * Test COsBoard
 */
public class OsBoardTest extends TestCase {
    public static final String sBoard = "8 OOOOOOOO ******** OOOOOOOO ******** OOOOOOOO ******** OOOOOOOO ******** O";
    private static final COsBoard boardWtm = board(sBoard);
    private static final COsBoard board6 = board("6 OOOOOO ****** OOOOOO ****** OOOOOO ****** O");
    private static final COsBoard boardBtm = board("8 OOOOOOOO ******** OOOOOOOO ******** OOOOOOOO ******** OOOOOOOO ******** *");
    private static final COsBoard board2 = board("8 *OOOOOOO ******** OOOOOOOO ******** OOOOOOOO ******** OOOOOOOO ******** O");
    private static final String TEXT =
            "--------\n" +
            "--------\n" +
            "--*O----\n" +
            "--***O--\n" +
            "---OO*--\n" +
            "---O----\n" +
            "--------\n" +
            "--------\n" +
            "O";
    private static final String README = "8 " + TEXT;

    public void testInitialization() {
        assertFalse(board(sBoard).isBlackMove());
        assertTrue(boardBtm.isBlackMove());
        testInitializationThrows("too short", "4 OOOO OOOO OOOO OOOO ");
        testInitializationThrows("too long", "4 OOOO OOOO OOOO OOOO *O");
    }

    private void testInitializationThrows(String message, String sBoard) {
        try {
            board(sBoard);
            fail("Should throw: " + message);
        } catch (IllegalArgumentException e) {
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
        final CReader in = new CReader(README);
        final COsBoard board = new COsBoard(in);
        assertFalse(board.isBlackMove());
        assertEquals(5, board.getPieceCounts().nBlack);
    }

    public void testToText() {
        final CReader in = new CReader(README);
        final COsBoard board = new COsBoard(in);
        assertEquals(TEXT, board.toText());
    }

    public void testIsMoveLegal() {
        final OsMove pass = new OsMove("pass");
        assertFalse(boardWtm.isMoveLegal(pass));
    }

    public static COsBoard board(String boardText) {
        return new COsBoard(new CReader(boardText));
    }

    public void testSquareCounts() {
        final COsBoard osBoard = new COsBoard();
        final OsBoardType bt = new OsBoardType("8");
        osBoard.initialize(bt);
        osBoard.setText("--------OOOOOOOO********----------------OOOOOOOO********--------");
        assertEquals(new PieceCounts(16, 16, 32), osBoard.getPieceCounts());
        assertEquals(0, osBoard.netBlackSquares());
        assertTrue(osBoard.isBlackMove());
        assertFalse(osBoard.isGameOver());
        assertFalse(osBoard.isMoveLegal(new OsMove("a2")));
        assertTrue(osBoard.isMoveLegal(new OsMove("a1")));
        assertEquals(16, osBoard.getMoves(true).size());
    }

    public void testThrowsIfBadMove() {
        final COsBoard board = board("4 OOOO OOOO OOOO *OO. O");
        board.update(new OsMove("pass"));
        try {
            board.update(new OsMove("pass"));
            fail("can't pass, black has a legal move");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
}
