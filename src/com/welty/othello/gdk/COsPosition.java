package com.welty.othello.gdk;

import com.orbanova.common.misc.Require;

import java.util.Arrays;

/**
 * Board position and clocks from a GGF game
 * <PRE>
 * User: Chris
 * Date: May 2, 2009
 * Time: 3:46:38 PM
 * </PRE>
 */
public class COsPosition {
    public final COsBoard board;
    public final OsClock[] cks;

    public COsPosition() {
        board = new COsBoard();
        cks = new OsClock[]{new OsClock(), new OsClock()};
    }

    public COsPosition(COsBoard board, OsClock whiteClock, OsClock blackClock) {
        this.board = new COsBoard(board);
        cks = new OsClock[]{new OsClock(whiteClock), new OsClock(blackClock)};
    }

    /**
     * Copy constructor. Deep copy.
     */
    public COsPosition(COsPosition posStart) {
        board = new COsBoard(posStart.board);
        cks = new OsClock[]{new OsClock(posStart.cks[0]), new OsClock(posStart.cks[1])};
    }

    void Update(final COsMoveList ml) {
        Update(ml, 100000);
    }

    void Update(final COsMoveListItem mli) {
        cks[board.blackMove() ? 1 : 0].Update(mli.tElapsed);
        board.Update(mli.mv);
    }

    void Update(final COsMoveList ml, int nMoves) {
        int i;
        if (nMoves > ml.size())
            nMoves = ml.size();
        for (i = 0; i < nMoves; i++)
            Update(ml.get(i));
    }

    /**
     * UpdateKomiSet() is called in a komi game to set the first move
     * choices of both players.
     * It updates the clock of the nonmover. The mover's clock
     * is updated by a call to Update().
     */
    void UpdateKomiSet(final COsMoveListItem[] mlis) {
        boolean fBlackOpponent = !board.blackMove();
        final int enemyIndex = fBlackOpponent ? 1 : 0;
        cks[enemyIndex].Update(mlis[enemyIndex].tElapsed);
    }

    void Calculate(final COsGame game) {
        Calculate(game, 100000);
    }

    public void Calculate(final COsGame game, int nMoves) {
        final COsPosition posStart = game.GetPosStart();
        board.copy(posStart.board);
        cks[0] = new OsClock(posStart.cks[0]);
        cks[1] = new OsClock(posStart.cks[1]);

        if (nMoves != 0 && game.mt.fKomi) {
            Require.isTrue(!game.NeedsKomi(), "Needs komi as first move");

            UpdateKomiSet(game.mlisKomi);
        }
        Update(game.ml, nMoves);
    }

    void Clear() {
        board.Clear();
        cks[0].Clear();
        cks[1].Clear();
    }

    @Override public boolean equals(Object obj) {
        if (obj instanceof COsPosition) {
            COsPosition pos = (COsPosition) obj;
            return board.equals(pos.board) && Arrays.equals(cks, pos.cks);
        } else {
            return false;
        }
    }

    @Override public String toString() {
        return board.toString() + " : " + Arrays.toString(cks);
    }
}
