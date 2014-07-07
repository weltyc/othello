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

package com.welty.othello.database;

import junit.framework.TestCase;

import java.io.EOFException;

/**
 */
@SuppressWarnings("JavaDoc")
public class OthelloMatchTest extends TestCase {
    public void testParsing() throws EOFException {
        final String line = "2 (;GM[Othello]PC[GGS/os]DT[2009.07.01_02:52:45.MDT]PB[edax]PW[Roxane]RB[2691.38]RW[2693.68]TI[05:00//02:00]TY[s8r18]RE[-10.000]BO[8 --**O--- --**---- --*OO--- ****OO-- ---O*--- --O-*--- -------- -------- *]B[f1/-6.00/26.00]W[C5/8.00/25.36]B[d6/-8.00/9.00]W[B6/8.00/15.61]B[f5/-8.00/26.00]W[D7/8.00/23.46]B[b5/-8.00/29.00]W[F3/8.00/20.17]B[e2/-10.00/31.00]W[A5/10.00/0.01]B[e7/-10.00/34.00]W[C7/10.00/0.05]B[f6/-10.00/36.00]W[A3/10.00/15.05]B[d8/-10.00/26.00]W[G5/10.00/75.49]B[g6/-10.00/34.00]W[F8/10.00]B[f7/-10.00]W[H6/10.00/4.31]B[a6/-10.00]W[A7/10.00]B[b3/-10.00]W[G4/10.00]B[f2/-10.00]W[C8/10.00]B[h3/-10.00]W[H4/10.00]B[b8/-10.00]W[A2/10.00]B[g3/-10.00]W[G8/10.00]B[b2/-10.00]W[A1/10.00]B[b1/-10.00]W[G2/10.00]B[h5/-10.00]W[G1/10.00]B[h1/-10.00]W[H2/10.00]B[e8/-10.00]W[A8/10.00]B[b7/-10.00]W[G7]B[h7/-10.00]W[H8];)(;GM[Othello]PC[GGS/os]DT[2009.07.01_02:52:45.MDT]PB[Roxane]PW[edax]RB[2693.68]RW[2691.38]TI[05:00//02:00]TY[s8r18]RE[-10.000]BO[8 --**O--- --**---- --*OO--- ****OO-- ---O*--- --O-*--- -------- -------- *]B[F1/-8.00/26.32]W[C5/6.00/13.00]B[D6/-8.00/26.69]W[B6/8.00/28.00]B[F5/-8.00/22.41]W[D7/8.00/31.00]B[G3/-8.00/43.46]W[F6/8.00/27.00]B[B5/-8.00/53.10]W[G5/8.00/30.00]B[G4/-9.00/54.71]W[F3/10.00/32.00]B[A7/-10.00/0.02]W[B2/10.00/9.00]B[B3/-10.00/0.09]W[A5/10.00/29.00]B[A6/-10.00/27.62]W[A3/10.00]B[F2/-10.00]W[B1/10.00]B[E2/-10.00]W[H4/10.00]B[F7/-10.00]W[E7/10.00]B[C8/-10.00]W[A8/10.00]B[C7/-10.00]W[H3/10.00]B[G6/-10.00]W[B8/10.00]B[B7/-10.00]W[D8/10.00]B[H5/-10.00]W[G1/10.00]B[E8/-10.00]W[G2/10.00]B[A1/-10.00]W[A2/10.00]B[H1/-10.00]W[H2/10.00]B[PA]W[F8/10.00]B[G7/-10.00]W[H8/10.00]B[H7/-10.00]W[H6/10.00]B[G8];)";
        final OthelloMatch match = new OthelloMatch(line);
        assertEquals(2, match.games.size());

        testParsingThrows("game count of 1 but 2 games", line.replaceFirst("2", "1"));
        testParsingThrows("game count of 3 but 2 games", line.replaceFirst("2", "3"));
    }

    private static void testParsingThrows(String msg, String line1) throws EOFException {
        try {
            new OthelloMatch(line1);
            fail("should throw : " + msg);
        }
        catch(IllegalArgumentException e) {
            // expected
        }
    }
}
