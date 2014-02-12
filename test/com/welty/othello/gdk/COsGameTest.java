package com.welty.othello.gdk;

import com.welty.othello.c.CReader;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: Jun 21, 2009
 * Time: 7:39:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class COsGameTest extends TestCase {
    public void testOutIn() {
        final String text = "(;GM[Othello]PC[]PB[HP_Administrator]PW[HP_Administrator]RE[?]TI[0]TY[8]BO[8 ---------------------------O*------*O--------------------------- *]B[F5]W[F6]B[E6]W[F4]B[G5]W[E7]B[E3]W[G6]B[D6]W[C4]B[C5]W[F3];)";
        final COsGame game = new COsGame(new CReader(text));
        assertEquals(text, game.toString());
    }

    public void testDefaultConstruction() {
        final String text = "(;GM[Othello]PC[]PB[]PW[]RE[?]TI[0//0]TY[8]BO[8 ---------------------------O*------*O--------------------------- *];)";
        final COsGame game = new COsGame();
        game.SetDefaultStartPos();
        assertEquals(text, game.toString());
    }

    public void testReflection() {
        final String text =  "(;GM[Othello]PC[]PB[]PW[]RE[?]TI[0//0]TY[8]BO[8 ---------------------------O*------*O--------------------------- *]B[F5/1/2];)";
        final COsGame game = new COsGame(text);
        for (int i=0; i<8; i++) {
            final COsGame gamer = new COsGame(game);
            gamer.reflect(i);
            assertEquals(game.getMli(0).move.reflect(i), gamer.getMli(0).move);
        }
    }
}
