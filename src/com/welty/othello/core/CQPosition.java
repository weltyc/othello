package com.welty.othello.core;

import com.orbanova.common.misc.Require;
import com.welty.othello.gdk.COsGame;
import com.welty.othello.gdk.COsPosition;
import com.welty.othello.gdk.OsBoard;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * A low-overhead position class. The position does not do all the incremental updates needed for
 * fast evaluation of a position, and as a result it is quicker to set up and easier to use.
 * <p/>
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: May 2, 2009
 * Time: 11:47:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class CQPosition {
    private final CBitBoard m_bb = new CBitBoard(0, 0);
    private int nEmpty, nMover;
    private boolean fBlackMove;

    /**
     * Construct with start position
     */
    public CQPosition() {
        Initialize();
    }

    public CQPosition(final CBitBoard aBoard, boolean blackMove) {
        Initialize(aBoard, blackMove);
    }

    public CQPosition(final String cstrBoard, boolean blackMove) {
        Initialize(cstrBoard, blackMove);
    }

    public CQPosition(final COsGame game, int iMove) {
        this(game.GetPosStart());
        for (int i = 0; i < iMove && i < game.ml.size(); i++) {
            CMove mv = new CMove(game.ml.get(i).move);
            MakeMove(mv);
        }
    }

    private CQPosition(final COsPosition position) {
        final OsBoard.GetTextResult textResult = position.board.getText();
        Initialize(textResult.getText(), textResult.isBlackMove());
    }

    public CQPosition(final OsBoard osboard) {
        final OsBoard.GetTextResult result = osboard.getText();
        Initialize(result.getText(), result.isBlackMove());
    }

    public CQPosition(CQPosition qPosition) {
        m_bb.Initialize(qPosition.m_bb);
        nEmpty = qPosition.nEmpty;
        nMover = qPosition.nMover;
        fBlackMove = qPosition.fBlackMove;

    }


    public boolean BlackMove() {
        return fBlackMove;
    }

    public int NEmpty() {
        return nEmpty;
    }

    public final CBitBoard BitBoard() {
        return m_bb;
    }

    public boolean CalcMoves(CMoves moves) {
        return m_bb.CalcMoves(moves);
    }

    @Override public boolean equals(Object obj) {
        if (obj instanceof CQPosition) {
            CQPosition b = (CQPosition) obj;
            return m_bb.equals(b.m_bb) && fBlackMove == b.fBlackMove && nEmpty == b.nEmpty && nMover == b.nMover;
        } else {
            return false;
        }
    }

    /**
     * Set the position to the othello start position
     */
    public void Initialize() {
        m_bb.Initialize();
        nEmpty = 60;
        nMover = 2;
        fBlackMove = true;
    }

    public void Initialize(CQPosition pos) {
        m_bb.Initialize(pos.m_bb);
        nEmpty = pos.nEmpty;
        nMover = pos.nMover;
        fBlackMove = pos.fBlackMove;
    }

    public void Initialize(final String cstrBoard, boolean blackMove) {
        m_bb.Initialize(cstrBoard, blackMove);
        fBlackMove = blackMove;
        nEmpty = m_bb.NEmpty();
        nMover = m_bb.NMover();
    }

    // Initialize a position with a bitboard.
    public void Initialize(final CBitBoard bb, boolean blackMove) {
        m_bb.Initialize(bb);
        fBlackMove = blackMove;
        nEmpty = m_bb.NEmpty();
        nMover = m_bb.NMover();
    }

    // return the output m_bb string
    public String GetSBoard() {
        return m_bb.GetSBoard(fBlackMove);
    }

    /////////////////////////////////////////
// Moving routines
/////////////////////////////////////////

    private static boolean ValidSquare(int col, int row) {
        return (col & 7) == col && (row & 7) == row;
    }

    boolean NonmoverSquare(int col, int row) {
        return ValidSquare(col, row) && m_bb.isEnemySquare(Utils.Square(row, col));
    }

    void FlipInDirection(int square, int dCol, int dRow) {
        int col = Utils.Col(square);
        int row = Utils.Row(square);

        for (col += dCol, row += dRow; NonmoverSquare(col, row); col += dCol, row += dRow) {
        }
        if (ValidSquare(col, row) && m_bb.isMoverSquare(Utils.Square(row, col))) {
            for (col -= dCol, row -= dRow; !m_bb.isEmptySquare(Utils.Square(row, col)); col -= dCol, row -= dRow) {
                m_bb.flipMoverBit(Utils.Square(row, col));
                nMover++;
            }
        }
    }

    // Set down a piece and flip opposing pieces. Change m_bb.blacks.
    public void MakeMove(CMove move) {
        if (!move.IsPass()) {
            Require.eq(~m_bb.getEmpty() & move.mask(), "move location is empty", 0);
            final int sq = move.Square();

            FlipInDirection(sq, 0, 1);
            FlipInDirection(sq, 1, 1);
            FlipInDirection(sq, 1, 0);
            FlipInDirection(sq, 1, -1);
            FlipInDirection(sq, 0, -1);
            FlipInDirection(sq, -1, -1);
            FlipInDirection(sq, -1, 0);
            FlipInDirection(sq, -1, 1);

            // set down the piece
            m_bb.flipMoverBit(sq);
            nMover++;
            m_bb.flipEmptyBit(sq);
            nEmpty--;
        }

        // switch player to move
        Pass();
    }

// Values of interest

    // value at end of game

    public short TerminalValue() {
        int nWhite = 64 - nMover - nEmpty;
        int net = nMover - nWhite;
        if (net < 0)
            net -= nEmpty;
        else if (net > 0)
            net += nEmpty;

        return (short) (net * Utils.kStoneValue);
    }

    /**
     * @param playerToMove if true, calc mover moves; if false, calc enemy moves
     * @return number of available moves
     */
    public int Mobility(boolean playerToMove) {
        CMoves moves = new CMoves();

        if (playerToMove) {
            CalcMoves(moves);
        } else {
            CQPosition next = new CQPosition(this);
            next.Pass();
            next.CalcMoves(moves);
        }
        return moves.NMoves();
    }

    /**
     * Switch mover and nonmover, update nMover and fBlackMove for a pass.
     * This routine also is called after making a move on the m_bb.
     */
    public void Pass() {
        m_bb.InvertColors();
        nMover = 64 - nMover - nEmpty;
        fBlackMove = !fBlackMove;
    }

    /**
     * CalcMovesAndPass - calc moves. decide if the mover needs to pass, and pass if he does
     *
     * @param moves - the moves available
     * @return 0 if the next player can move
     *         1 if the next player must pass but game is not over
     *         2 if both players must pass and game is over
     *         If pass>0 the position is left with one move passed
     *         (and you must call UndoPass before UndoMove)
     */
    public int CalcMovesAndPass(CMoves moves) {
        if (CalcMoves(moves))
            return 0;
        else {
            Pass();
            if (CalcMoves(moves))
                return 1;
            else
                return 2;
        }
    }

    /**
     * Make a move, then pass if the opponent must pass. Return pass code.
     *
     * @param move move to make
     * @return 0 = no pass, 1 = opponent must pass, 2 = both must pass (game is over)
     */
    public int MakeMoveAndPass(CMove move) {
        MakeMove(move);
        CMoves moves = new CMoves();
        return CalcMovesAndPass(moves);
    }

    public void Print() {
        FPrint(System.out);
    }

    void FPrint(PrintStream fp) {
        BitBoard().FPrint(fp, fBlackMove);
        fp.format("%s to move\n", fBlackMove ? "Black" : "White");
    }

    public boolean IsSuccessor(final CQPosition posSuccessor) {
        CMoves moves = new CMoves();
        CMove move = new CMove();
        CQPosition pos2 = new CQPosition(this);
        pos2.CalcMoves(moves);
        while (moves.GetNext(move)) {
            pos2 = this;
            pos2.MakeMove(move);
            if (pos2 == posSuccessor) {
                return true;
            }
        }
        return false;
    }

    @Override public String toString() {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FPrint(new PrintStream(baos));
        return baos.toString();
    }

    public CQPosition Symmetry(int i) {
        return new CQPosition(BitBoard().Symmetry(i), fBlackMove);
    }
}
