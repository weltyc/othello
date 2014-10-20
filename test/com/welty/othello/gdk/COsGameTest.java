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
 * Test COsGame class
 */
public class COsGameTest extends TestCase {

    private String EMPTY_GAME = "(;GM[Othello]PC[]PB[]PW[]RE[?]TI[0]TY[8]BO[8 ---------------------------O*------*O--------------------------- *];)";

    public void testOutIn() {
        final String text = "(;GM[Othello]PC[]PB[HP_Administrator]PW[HP_Administrator]RE[?]TI[0]TY[8]BO[8 ---------------------------O*------*O--------------------------- *]B[F5]W[F6]B[E6]W[F4]B[G5]W[E7]B[E3]W[G6]B[D6]W[C4]B[C5]W[F3];)";
        final COsGame game = new COsGame(new CReader(text));
        assertEquals(text, game.toString());
    }

    public void testDefaultConstruction() {
        final String text = EMPTY_GAME;
        final COsGame game = new COsGame();
        game.setToDefaultStartPosition(OsClock.DEFAULT, OsClock.DEFAULT);
        assertEquals(text, game.toString());
    }

    public void testReflection() {
        final String text = "(;GM[Othello]PC[]PB[]PW[]RE[?]TI[0//0]TY[8]BO[8 ---------------------------O*------*O--------------------------- *]B[F5/1/2];)";
        final COsGame game = new COsGame(text);
        for (int i = 0; i < 8; i++) {
            final COsGame gamer = new COsGame(game);
            gamer.reflect(i);
            assertEquals(game.getMli(0).move.reflect(i), gamer.getMli(0).move);
            validate(game);
        }
    }

    private static void validate(COsGame game) {
        // check that game.getPos() is up to date
        final String pos = game.getPos().toString();
        game.CalcCurrentPos();
        assertEquals(pos, game.getPos().toString());
    }

    public void testCopyConstructor() {
        final String text = "(;GM[Othello]PC[]PB[foo]PW[bar]RE[?]TI[0//0]TY[8]BO[8 ---------------------------O*------*O--------------------------- *]B[F5/1/2];)";
        final COsGame game = new COsGame(text);
        final COsGame game1 = new COsGame(game, 1);
        assertEquals(game.toString(), game1.toString());
        validate(game1);

        final COsGame game0 = new COsGame(game, 0);
        assertEquals("(;GM[Othello]PC[]PB[foo]PW[bar]RE[?]TI[0//0]TY[8]BO[8 ---------------------------O*------*O--------------------------- *];)", game0.toString());
        validate(game0);
    }

    public void testOfPgn() {
          final String text = "[Event \"?\"]\n" +
                  "[Site \"kurnik\"]\n" +
                  "[Date \"2014.10.19\"]\n" +
                  "[Round \"-\"]\n" +
                  "[Black \"t7m4o1k9a\"]\n" +
                  "[White \"ntwo\"]\n" +
                  "[Result \"37-27\"]\n" +
                  "[Time \"07:31:22\"]\n" +
                  "[TimeControl \"420\"]\n" +
                  "[BlackElo \"1159\"]\n" +
                  "[WhiteElo \"1246\"]\n" +
                  "\n" +
                  "1. f5 d6 2. c3 d3 3. c4 f4 4. f6 f3 5. e6 e7 6. d7 g6 7. f8 f7 8. h6 c5 9. b6\n" +
                  "e8 10. d8 c6 11. c7 b5 12. e3 c8 13. b8 f2 14. a5 d2 15. g8 b4 16. a3 a4 17. b3\n" +
                  "c2 18. g4 g5 19. h5 h3 20. b2 a7 21. a6 a1 22. h4 h7 23. g3 a2 24. d1 c1 25. e1\n" +
                  "f1 26. e2 h2 27. b1 g2 28. a8 b7 29. h1 g1 30. h8 g7 37-27\n";
        final COsGame game = COsGame.ofPgn(text);
        assertEquals(new OsPlayerInfo("t7m4o1k9a", 1159), game.getBlackPlayer());
        assertEquals(OsMatchType.STANDARD, game.getMatchType());
        final String expectedMoveList = "f5 d6 c3 d3 c4 f4 f6 f3 e6 e7 d7 g6 f8 f7 h6 c5 b6 e8 d8 c6 c7 b5 e3 c8 b8 f2 a5 d2 g8 b4 a3 a4 b3 c2 g4 g5 h5 h3 b2 a7 a6 a1 h4 h7 g3 a2 d1 c1 e1 f1 e2 h2 b1 g2 a8 b7 h1 g1 h8 g7".replace(" ", "").toUpperCase();
        assertEquals(expectedMoveList, game.getMoveList().toMoveListString());
        assertEquals(new OsPlayerInfo("ntwo", 1246), game.getWhitePlayer());
        assertEquals("2014.10.19 07:31:22", game.sDateTime);
        assertEquals("kurnik", game.sPlace);
        assertTrue(game.isOver());
        assertTrue(game.is8x8());
        assertFalse(game.getMatchType().anti);
        assertEquals(new OsResult(OsResult.TStatus.kNormalEnd, 10), game.Result());
    }

    public void testOfPgnWithPass() {
        final String text = "[Event \"?\"]\n" +
                "[Site \"kurnik\"]\n" +
                "[Date \"2014.10.19\"]\n" +
                "[Round \"-\"]\n" +
                "[Black \"ntwo\"]\n" +
                "[White \"cosmostan\"]\n" +
                "[Result \"37-27\"]\n" +
                "[Time \"07:43:20\"]\n" +
                "[TimeControl \"300\"]\n" +
                "[BlackElo \"1226\"]\n" +
                "[WhiteElo \"1228\"]\n" +
                "\n" +
                "1. f5 d6 2. c3 d3 3. c4 f4 4. c5 b3 5. c2 e3 6. d2 b4 7. e2 e6 8. e7 f3 9. g4\n" +
                "g3 10. g5 f2 11. f1 c1 12. c7 f6 13. f7 d7 14. c6 b5 15. c8 h5 16. h6 h4 17. b6\n" +
                "h7 18. a5 g6 19. d1 f8 20. e8 d8 21. h2 b8 22. a2 a3 23. a4 a7 24. b1 e1 25. b2\n" +
                "b7 26. g2 h1 27. h3 g1 28. h8 a1 29. a6 -- 30. a8 -- 31. g8 g7 37-27\n" +
                "\n";
        final COsGame game = COsGame.ofPgn(text);
        assertEquals(OsMove.PASS, game.getMoveList().get(57).move);
    }

    public void testEquals() {
        final String text = "(;GM[Othello]PC[]PB[foo]PW[bar]RE[?]TI[0//0]TY[8]BO[8 ---------------------------O*------*O--------------------------- *]B[F5/1/2];)";
        final COsGame game = new COsGame(text);
        final COsGame game1 = new COsGame(game, 1);
        assertEquals(game, game1);
    }

    public void testOfLogbook() {
        final COsGame game = COsGame.ofLogbook("+d3-c3+c4-e3+c2-b3+d2-e1+d1-c1+f4-d6+e6-g4+b2-f6+d7-c8+e7-d8+f3-f5+g5-h4+h6-f2+f7-h5+g6-c6+c5-c7+b6-b5+b4-e8+h3-g3+h2-e2+h7-g2+b1-a1+h1-a2+a3-g7+f8-h8+g8-a4+g1-f1+a6-a5+a7-a8+b8-b7: -04 10");
        final COsBoard startBoard = new COsBoard();
        startBoard.initialize(new OsBoardType("8"));
        assertEquals(startBoard, game.getStartPosition().board);
    }

    public void testSetMoveList() {
        final COsGame game = new COsGame(EMPTY_GAME);
        game.setMoveList("E6");
        assertEquals("E6", game.getMoveList().toMoveListString());

        // test multiple moves, and deletion of existing moves
        game.setMoveList("F5 D6");
        assertEquals("F5D6", game.getMoveList().toMoveListString());

        // test with no spaces
        game.setMoveList("F5D6C3");
        assertEquals("F5D6C3", game.getMoveList().toMoveListString());

        // test lower case
        game.setMoveList("f5f6");
        assertEquals("F5F6", game.getMoveList().toMoveListString());
    }

    public void testSetMoveListErrors() {
        final COsGame game = new COsGame(EMPTY_GAME);
        // test error handling.
        // Need a reasonable error message because it is displayed directly to the client.
        try {
            final String ml = "F€5";
            for (char c : ml.toCharArray()) {
                System.out.println(0+c);
            }
            game.setMoveList(ml);
        } catch (IllegalArgumentException e) {
            final String message = e.getMessage();
            assertTrue(message.contains("€"));
        }
        // test error handling.
        // Need a reasonable error message because it is displayed directly to the client.
        try {
            game.setMoveList("F");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("ends"));
        }
    }

}
