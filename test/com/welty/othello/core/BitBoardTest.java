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

package com.welty.othello.core;

import junit.framework.TestCase;

/**
 * Test CBitBoard class
 */

public class BitBoardTest extends TestCase {
    private static final String INITIAL_STRING = "---------------------------O*------*O---------------------------";

    private static void testCalcMobility(String board, boolean fBlackMove, int nMoverMoves, int nEnemyMoves, int pass) {
        CBitBoard bb = new CBitBoard(board, fBlackMove);

        final CBitBoard.MobilityResult result = bb.CalcMobility();
        assertEquals(nMoverMoves, result.nMoverMoves);
        assertEquals(nEnemyMoves, result.nEnemyMoves);
        assertEquals(pass, result.pass);
    }

    public static void testCalcMobility() {
        testCalcMobility("------------------*O------***O-----OOOO----O-*------------------", true, 11, 9, 0);
        testCalcMobility("..........................*OO*.....OOO..........................", false, 5, 3, 0);
        testCalcMobility("*O..............................................................", true, 1, 0, 0);
        testCalcMobility("................................................................", true, 0, 0, 2);
        testCalcMobility("*O..............................................................", false, 0, 1, 1);
    }

    public void testCompare(long aMover, long aEmpty, long bMover, long bEmpty) {
        CBitBoard a = new CBitBoard(aMover, aEmpty);
        CBitBoard b = new CBitBoard(bMover, bEmpty);
        assertEquals(-1, a.compareTo(b));
        assertEquals(1, b.compareTo(a));
        assertEquals(0, a.compareTo(a));
    }

    public void testCompare() {
        testCompare(0xFFFFFFFFL, 0xFFFFFFFF00000000L, 0xFFFFFFFF00000000L, 0xFFFFFFFFL);
        testCompare(0x7FFFFFFFL, 0x7FFFFFFF00000000L, 0x7FFFFFFF00000000L, 0x7FFFFFFFL);

        // make empty the same, then must compare movers
        testCompare(0xFFFFFFFFL, 0, 0xFFFFFFFF00000000L, 0);
        testCompare(0x7FFFFFFFL, 0, 0x7FFFFFFF00000000L, 0);

        // make mover the same, must compare empties
        testCompare(0, 0xFFFFFFFFL, 0, 0xFFFFFFFF00000000L);
        testCompare(0, 0x7FFFFFFFL, 0, 0x7FFFFFFF00000000L);
    }

    public void testMinimalReflection() {
        final CBitBoard bb = new CBitBoard(0, 0);
        bb.Initialize();
        final CBitBoard mr = new CBitBoard(0x810000000L, 0xFFFFFFE7E7FFFFFFL);
        assertEquals(mr, bb.MinimalReflection());
    }

    public void testGetSBoard() {
        final CBitBoard bb = new CBitBoard(0, 0);
        bb.Initialize();
        assertEquals(INITIAL_STRING, bb.GetSBoard(true));
        bb.Initialize(INITIAL_STRING, true);
        assertEquals(INITIAL_STRING, bb.GetSBoard(true));
        bb.Initialize(INITIAL_STRING, false);
        assertEquals(INITIAL_STRING, bb.GetSBoard(false));
    }
}
