package com.welty.othello.database;

import junit.framework.TestCase;

/**
 */
@SuppressWarnings("JavaDoc")
public class OthelloGameTest extends TestCase {
    public void testType() throws Exception {
       final String innerText = "GM[Othello]PC[GGS/os]DT[2009.07.01_02:52:45.MDT]PB[edax]PW[Roxane]RB[2691.38]RW[2693.68]TI[05:00//02:00]TY[s8r18]RE[-10.000]BO[8 --**O--- --**---- --*OO--- ****OO-- ---O*--- --O-*--- -------- -------- *]B[f1/-6.00/26.00]W[C5/8.00/25.36]B[d6/-8.00/9.00]W[B6/8.00/15.61]B[f5/-8.00/26.00]W[D7/8.00/23.46]B[b5/-8.00/29.00]W[F3/8.00/20.17]B[e2/-10.00/31.00]W[A5/10.00/0.01]B[e7/-10.00/34.00]W[C7/10.00/0.05]B[f6/-10.00/36.00]W[A3/10.00/15.05]B[d8/-10.00/26.00]W[G5/10.00/75.49]B[g6/-10.00/34.00]W[F8/10.00]B[f7/-10.00]W[H6/10.00/4.31]B[a6/-10.00]W[A7/10.00]B[b3/-10.00]W[G4/10.00]B[f2/-10.00]W[C8/10.00]B[h3/-10.00]W[H4/10.00]B[b8/-10.00]W[A2/10.00]B[g3/-10.00]W[G8/10.00]B[b2/-10.00]W[A1/10.00]B[b1/-10.00]W[G2/10.00]B[h5/-10.00]W[G1/10.00]B[h1/-10.00]W[H2/10.00]B[e8/-10.00]W[A8/10.00]B[b7/-10.00]W[G7]B[h7/-10.00]W[H8]";
        final OthelloGame game = new OthelloGame(innerText);
        assertEquals("s8r18", game.type());
        assertEquals(2691.38, game.blackRating(), 1e-10);
        assertEquals(2693.68, game.whiteRating(), 1e-10);
        assertEquals(-10, game.score(), 1e-10);
        assertEquals("edax", game.blackName());
        assertEquals("Roxane", game.whiteName());
    }

    public void testParseResult() {
        testParseResult("-10.000", -10, null);
        testParseResult("+64.000:t", 64, "t");
    }

    private static void testParseResult(String resultText, int score, String comment) {
        final OthelloGame.Result result = new OthelloGame.Result(resultText);
        assertEquals(score, result.score, 1e-10);
        assertEquals(comment, result.comment);
    }

}
