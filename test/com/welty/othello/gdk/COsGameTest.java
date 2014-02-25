package com.welty.othello.gdk;

import com.welty.othello.c.CReader;
import junit.framework.TestCase;

/**
 * Test COsGame class
 */
public class COsGameTest extends TestCase {
    public void testOutIn() {
        final String text = "(;GM[Othello]PC[]PB[HP_Administrator]PW[HP_Administrator]RE[?]TI[0]TY[8]BO[8 ---------------------------O*------*O--------------------------- *]B[F5]W[F6]B[E6]W[F4]B[G5]W[E7]B[E3]W[G6]B[D6]W[C4]B[C5]W[F3];)";
        final COsGame game = new COsGame(new CReader(text));
        assertEquals(text, game.toString());
    }

    public void testDefaultConstruction() {
        final String text = "(;GM[Othello]PC[]PB[]PW[]RE[?]TI[0]TY[8]BO[8 ---------------------------O*------*O--------------------------- *];)";
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

    public void testEquals() {
        final String text = "(;GM[Othello]PC[]PB[foo]PW[bar]RE[?]TI[0//0]TY[8]BO[8 ---------------------------O*------*O--------------------------- *]B[F5/1/2];)";
        final COsGame game = new COsGame(text);
        final COsGame game1 = new COsGame(game, 1);
        assertEquals(game, game1);
    }
}
