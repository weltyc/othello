package com.welty.othello.gdk;

import com.welty.othello.c.CReader;
import lombok.EqualsAndHashCode;

import java.io.EOFException;

/**
 * An Othello board type
 */
public class COsBoardType {
    public static final COsBoardType BT_8x8 = new COsBoardType("8");

    public int n;
    public boolean fOcto;

    public COsBoardType(String s) {
        this(new CReader(s));
    }

    public COsBoardType(CReader is) {
        In(is);
    }

    public COsBoardType(COsBoardType bt) {
        n = bt.n;
        fOcto = bt.fOcto;
    }

    /**
     * Read in the board type from a stream.
     * example: "8" for an 8x8 game, "10" for a 10x10 game, "88" for an octo game.
     * If the board type is unknown, set failbit on the stream, assert(0), and set this to an 8x8 game.
     */
    void In(CReader is) {
        try {
            n = is.readInt();
        } catch (EOFException e) {
            throw new IllegalStateException(e);
        }
        switch (n) {
            case 4:
            case 6:
            case 8:
            case 10:
            case 12:
            case 14:
                fOcto = false;
                break;
            case 88:
                n = 10;
                fOcto = true;
                break;
            default:
                throw new IllegalArgumentException("unknown board type : " + n);
        }
    }

    @Override public String toString() {
        if (fOcto) {
            return "88";
        } else {
            return "" + n;
        }
    }

    void Clear() {
        n = 0;
        fOcto = false;
    }

    int NRandDiscsMax() {
        if (fOcto)
            return 44;
        else
            return n * n - 16;
    }

    int NRandDiscsMin() {
        return 4;
    }

    int NPlayableSquares() {
        if (fOcto)
            return 88;
        else
            return n * n;
    }

    int NTotalSquares() {
        return (n + 2) * (n + 2);
    }

    boolean DummyCorner(int row, int col) {
        if (fOcto) {
            int rowMirror = n - 1 - row;
            int colMirror = n - 1 - col;

            if (rowMirror < row)
                row = rowMirror;
            if (colMirror < col)
                col = colMirror;

            return (row + col) < 2;
        } else
            return false;
    }

    final String Description() {
        if (fOcto)
            return "Octagon";
        else switch (n) {
            case 4:
                return "4x4";
            case 6:
                return "6x6";
            case 8:
                return "8x8";
            case 10:
                return "10x10";
            case 12:
                return "12x12";
            case 14:
                return "14x14";
            default:
                return "Unknown board";
        }
    }

    @Override public boolean equals(Object obj) {
        if (obj instanceof COsBoardType) {
            COsBoardType b = (COsBoardType) obj;
            return n == b.n && fOcto == b.fOcto;
        } else {
            return false;
        }
    }
}
