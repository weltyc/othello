package com.welty.othello.gdk;

import com.orbanova.common.misc.Require;
import com.welty.othello.c.CReader;

import java.io.ByteArrayInputStream;

/**
 * A GGF-format move
 */
public class COsMove {
    public static final COsMove PASS = new COsMove(-1, -1, true);

    private boolean fPass;
    private int row, col;

    // Creation
    public COsMove() {
    }

    public COsMove(int row, int col) {
        this(row, col, false);
    }

    private COsMove(int row, int col, boolean pass) {
        this.row = row;
        this.col = col;
        this.fPass = pass;
    }

    public COsMove(final String text) {
        final ByteArrayInputStream is = new ByteArrayInputStream(text.getBytes());
        in(new CReader(is));
    }

    public COsMove(COsMove mv) {
        fPass = mv.fPass;
        row = mv.row;
        col = mv.col;
    }

    public COsMove(CReader in) {
        in(in);
    }

    /**
     * Create a move from an IOS move code
     *
     * @param iosMove integer from 11-88; positive if a black move, negative if a white move
     * @return the move
     */
    public static COsMove ofIos(int iosMove) {
        if (iosMove < 0) {
            iosMove = -iosMove;
        }
        final int row = (iosMove % 10) - 1;
        final int col = (iosMove / 10) - 1;
        return new COsMove(row, col);
    }

    // Modification
    public void Set(int row, int col) {
        this.row = row;
        this.col = col;
        fPass = false;
    }

    // I/O
    void in(CReader is) {
        final char cCol = Character.toUpperCase(is.read());
        fPass = cCol == 'P';
        if (fPass) {
            while (true) {
                final char c = is.read();
                if (!Character.isLetter(c)) {
                    is.unread(c);
                    break;
                }
            }
        } else {
            col = cCol - 'A';
            row = is.read() - '1';
        }
    }

    /**
     * @return row number, from 0-7
     */
    public int Row() {
        requireOnBoard();
        return row;
    }

    /**
     * @return col number, from 0-7
     */
    public int Col() {
        requireOnBoard();
        return col;
    }

    private void requireOnBoard() {
        if (fPass) {
            throw new IllegalStateException("must be on the board");
        }
        Require.inRange("Row must be 0-7", row, "row", 0, 7);
        Require.inRange("Col must be 0-7", col, "col", 0, 7);
    }

    /**
     * @return true if the move is a pass
     */
    public boolean Pass() {
        return fPass;
    }

    @Override public String toString() {
        if (fPass)
            return "PA";
        else
            return "" + (char) (col + 'A') + (row + 1);
    }

    @Override public boolean equals(Object obj) {
        if (obj instanceof COsMove) {
            COsMove b = (COsMove) obj;

            if (fPass || b.fPass)
                return fPass && b.fPass;
            else
                return row == b.row && col == b.col;
        }
        return false;
    }
}
