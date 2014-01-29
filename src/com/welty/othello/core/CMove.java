package com.welty.othello.core;

import com.orbanova.common.misc.Require;
import com.welty.othello.gdk.COsMove;

/**
 * A Mutable Othello move on an 8x8 board
 */
public class CMove implements Comparable<CMove> {
    private byte square; // -1 = pass, -2 = unconstructed

    public CMove() {
        square = -2;
    }

    /**
     * @param row, in the range 0-7
     * @param col, in the range 0-7
     */
    public CMove(int row, int col) {
        Require.inRange("row must be in range", row, "row", 0, 7);
        Require.inRange("col must be in range", col, "col", 0, 7);
        square = (byte) calcSquare(row, col);
    }

    public CMove(COsMove osMove) {
        if (osMove.Pass()) {
            square = -1;
        } else {
            square = (byte) calcSquare(osMove.Row(), osMove.Col());
        }
    }

    public CMove(byte square) {
        this.square = square;
    }

    public CMove(CMove move_) {
        this(move_.square);
    }

    public COsMove toOsMove() {
        if (IsPass()) {
            final COsMove osMove = new COsMove();
            osMove.SetPass();
            return osMove;
        } else {
            return new COsMove(Row(), Col());
        }
    }

    public static int calcSquare(int row, int col) {
        return (row << 3) + col;
    }

    /**
     * @param s string, e.g. "A8" or "pass"
     */
    public CMove(String s) {
        s = s.toUpperCase();
        if (s.startsWith("PA")) {
            square = -1;
        } else {
            Require.eq(s.length(), "string length", 2);
            final int row = s.charAt(1) - '1';
            final int col = s.charAt(0) - 'A';
            Require.inRange("row must be in range", row, "row", 0, 7);
            Require.inRange("col must be in range", col, "col", 0, 7);
            square = (byte) calcSquare(row, col);
        }
    }

    public int Row() {
        requireOnBoard();
        return square >> 3;
    }

    public int Col() {
        requireOnBoard();
        return square & 7;
    }

    @Override public String toString() {
//        requireValid();
        if (square < 0) {
            if (square == -2) {
                return "invalid";
            }
            return "pa";
        } else {
            return ((char) ('A' + Col())) + "" + ((char) ('1' + Row()));
        }
    }

    public boolean IsPass() {
        requireValid();
        return square == -1;
    }

    public int Square() {
        return square;
    }

    public long mask() {
        requireOnBoard();
        return 1L << Square();
    }

    void requireOnBoard() {
        if (square < 0 || square >= 64) {
            if (square == 1) {
                throw new IllegalStateException("pass not allowed");
            } else {
                throw new IllegalStateException("invalid square number : " + square);
            }
        }
    }

    void requireValid() {
        if (square < -1 || square >= 64) {
            throw new IllegalStateException("invalid square number : " + square);
        }
    }

    public void Set(int square) {
        this.square = (byte) square;
        requireValid();
    }

    /**
     * Set the move to be a pass
     */
    public void SetPass() {
        square = -1;
    }

    @Override public boolean equals(Object obj) {
        if (obj instanceof CMove) {
            CMove b = (CMove) obj;
            return square == b.square;
        } else {
            return false;
        }
    }

    public CMove Symmetry(int sym) {
        int sq = Square();

        if ((sym & 1) != 0) {
//            result = flipVertical(result);
            //noinspection OctalInteger
            sq ^= 070;
        }
        if ((sym & 2) != 0) {
//            result = flipHorizontal(result);
            sq ^= 7;
        }
        if ((sym & 4) != 0) {
            final int col = sq & 7;
            final int row = sq >> 3;
            sq = calcSquare(col, row);
        }

        return new CMove((byte) sq);
    }

    public void Initialize(CMove newBestMove) {
        square = newBestMove.square;
    }

    public void Initialize(byte square) {
        this.square = square;
    }

    public boolean Valid() {
        return square < Utils.NN && square >= 0;
    }

    public void MakeInvalid() {
        square = (byte) -2;
    }

    public int compareTo(CMove o) {
        return square == o.square ? 0 : square < o.square ? -1 : 1;
    }
}
