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

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: May 2, 2009
 * Time: 7:17:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class OsGameTest extends TestCase {
    private final String gameText = "1 (;GM[Othello]PC[GGS/os]DT[2003.12.15_12:33:18.MST]PB[Saio1200]PW[Saio3000]RB[2196.37]RW[2200.35]TI[05:00//02:00]TY[8]R" +
            "E[+0.000]BO[8 -------- -------- -------- ---O*--- ---*O--- -------- -------- -------- *]B[d3//0.01]W[c5//0.01]B[f6//0.01" +
            "]W[f5//0.01]B[e6//0.01]W[e3//0.01]B[c3//0.01]W[d2/2.39/0.01]B[c4//0.01]W[b5/2.37/0.01]B[f4//0.01]W[d6/2.35/0.01]B[f3//0." +
            "01]W[b4/2.33/0.01]B[c7//0.01]W[d7/2.25/20.97]B[c6//0.01]W[e7/2.25/28.64]B[b6//0.01]W[f7/2.23/28.47]B[a5//0.01]W[a4/2.25/" +
            "20.49]B[e8//0.01]W[f8/2.25/14.31]B[d8//0.01]W[e2/2.19/16.20]B[a3//0.01]W[c8/0.25/16.36]B[b3//0.01]W[a6//0.01]B[c1//0.01]" +
            "W[a2//0.01]B[f1//0.01]W[g6//0.01]B[g5//97.63]W[h5//0.01]B[g4//0.01]W[h3//0.01]B[h4//0.01]W[c2//0.01]B[b7//0.01]W[d1//0.0" +
            "1]B[h6//0.01]W[g3//0.01]B[e1//0.01]W[f2//0.01]B[g2//0.01]W[h7//0.01]B[b1//0.01]W[h2//0.01]B[g1//0.01]W[b8//0.01]B[a8//0." +
            "01]W[a7//0.01]B[a1//0.01]W[b2//0.01]B[g8//0.01]W[h1//0.01]B[g7//0.01]W[h8//0.01];)";

    public void testConstructor() {
        final COsGame game = new COsGame(new CReader(gameText));
        assertEquals(true, game.isOver());
        assertEquals(60, game.nMoves());
        assertEquals("8", game.getMatchType().toString());
        assertEquals("Saio1200", game.getBlackPlayer().name);
        assertEquals(2200.35, game.getWhitePlayer().rating, 1e-10);
        assertEquals("5:00", game.getStartPosition().getBlackClock().toString());
    }

    public void testConstructorWithMoves() {
        final COsGame game = new COsGame();
        try {
            new COsGame(game, 1);
            fail("should throw, can't copy game to move 1 when there are no moves");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testUpdate() {
        final COsMoveList expected = expectedMoveList();

        final COsGame game = new COsGame();
        game.setToDefaultStartPosition(OsClock.DEFAULT, OsClock.DEFAULT);
        final OsMoveListItem mli = new OsMoveListItem(new OsMove("F5"));
        game.append(mli);
        assertEquals(expected, game.getMoveList());
    }

    private static COsMoveList expectedMoveList() {
        COsMoveList expected = new COsMoveList();
        OsMoveListItem expectedItem = new OsMoveListItem(new OsMove("F5"));
        expected.add(expectedItem);
        return expected;
    }

    public void testEquals() {
        final COsBoard boardBtm = OsBoardTest.board("8 OOOOOOOO ******** OOOOOOOO ******** OOOOOOOO ******** OOOOOOOO ******** *");
        final COsBoard boardWtm = OsBoardTest.board("8 OOOOOOOO ******** OOOOOOOO ******** OOOOOOOO ******** OOOOOOOO ******** O");

        final COsPosition a = new COsPosition(boardBtm, new OsClock(1, 2), new OsClock(2, 3));
        final COsPosition b = new COsPosition(boardBtm, new OsClock(1, 2), new OsClock(2, 3));
        assertEquals(a, a);
        assertEquals(a, b);
        final COsPosition c = new COsPosition(boardWtm, new OsClock(1, 2), new OsClock(2, 3));
        final COsPosition d = new COsPosition(boardBtm, new OsClock(1, 3), new OsClock(2, 3));
        final COsPosition e = new COsPosition(boardBtm, new OsClock(1, 2), new OsClock(2, 4));
        assertFalse(a.equals(c));
        assertFalse(a.equals(d));
        assertFalse(a.equals(e));

    }

    public void testCalcPosition() {
        COsGame game = new COsGame();
        game.setToDefaultStartPosition(OsClock.DEFAULT, OsClock.DEFAULT);
        final COsPosition pos = game.calcPosition(new ArrayList<OsMoveListItem>());
        COsPosition expected = new COsPosition();
        expected.board.initialize(new OsBoardType("8"));

        assertEquals(expected, pos);
    }

    public void testClockUpdating() {
        final COsGame game = new COsGame();
        final OsClock startClock = new OsClock(15 * 60, 0, 2 * 60, 0);
        game.setToDefaultStartPosition(startClock, startClock);

        game.append(new OsMoveListItem("F5/1/1"));
        OsClock expected = new OsClock(15 * 60 - 1, 0, 2 * 60, 0);
        assertEquals(expected, game.getPos().getBlackClock());
        assertEquals(startClock, game.getPos().getWhiteClock());
    }
}
