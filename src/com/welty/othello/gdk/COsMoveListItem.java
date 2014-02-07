package com.welty.othello.gdk;

import com.welty.othello.c.CReader;

/**
 * An Othello move, including optional evaluation and elapsed time
 */
public class COsMoveListItem {
    /**
     * A pass with no evaluation or elapsed time
     */
    public static final COsMoveListItem PASS = new COsMoveListItem(OsMove.PASS);

    public OsMove mv;
    private double dEval;
    public double tElapsed;
    private boolean hasEval;

    public COsMoveListItem() {
    }

    public COsMoveListItem(OsMove mv, double dEval, double tElapsed) {
        this.mv = new OsMove(mv);
        this.dEval = dEval;
        this.tElapsed = tElapsed;
    }

    public COsMoveListItem(COsMoveListItem mli) {
        mv = new OsMove(mli.mv);
        dEval = mli.dEval;
        tElapsed = mli.tElapsed;
        hasEval = mli.hasEval;
    }

    public COsMoveListItem(String text) {
        this(new CReader(text));
    }

    public COsMoveListItem(CReader cReader) {
        In(cReader);
    }

    /**
     * Construct a move with elapsed time of 0 and no evaluation
     *
     * @param mv move made
     */
    public COsMoveListItem(OsMove mv) {
        this.mv = new OsMove(mv);
        dEval = 0;
        hasEval = false;
        tElapsed = 0;
    }

    public void In(CReader is) {

        // move
        mv = new OsMove(is);
        is.ignoreAlnum();

        // eval
        dEval = 0;
        if (is.peek() == '/') {
            is.ignore(1);
            try {
                dEval = is.readDoubleNoExponent();
                hasEval = true;
            } catch (NumberFormatException e) {
                hasEval = false;
            }
        }

        tElapsed = 0;
        if (is.peek() == '/') {
            is.ignore(1);
            tElapsed = is.readDoubleNoExponent();
        }
    }

    @Override public boolean equals(Object obj) {
        if (obj instanceof COsMoveListItem) {
            COsMoveListItem b = (COsMoveListItem) obj;
            return mv.equals(b.mv) && dEval == b.dEval && tElapsed == b.tElapsed;
        } else {
            return false;
        }
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(mv);
        if (hasEval || tElapsed != 0) {
            sb.append('/');
            if (hasEval) {
                sb.append(String.format("%3.2f", dEval));
            }
            if (tElapsed != 0) {
                sb.append('/').append(tElapsed);
            }
        }
        return sb.toString();
    }

    /**
     * @return true if an eval was given to this move list item
     */
    public boolean hasEval() {
        return hasEval;
    }

    public double getEval() {
        return dEval;
    }
}
