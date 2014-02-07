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

    public final OsMove mv;

    /**
     * Eval, or Double.NaN if no eval exists
     */
    private double dEval;

    private double tElapsed;

    public COsMoveListItem(OsMove mv, double dEval, double tElapsed) {
        this.mv = new OsMove(mv);
        this.dEval = dEval;
        this.tElapsed = tElapsed;
    }

    public COsMoveListItem(COsMoveListItem mli) {
        mv = new OsMove(mli.mv);
        dEval = mli.dEval;
        tElapsed = mli.tElapsed;
    }

    public COsMoveListItem(String text) {
        this(new CReader(text));
    }

    public COsMoveListItem(CReader in) {

        // move
        mv = new OsMove(in);
        in.ignoreAlnum();

        // eval
        dEval = Double.NaN;
        if (in.peek() == '/') {
            in.ignore(1);
            try {
                dEval = in.readDoubleNoExponent();
            } catch (NumberFormatException e) {
                // if blank, do nothing - it's already set to Double.NaN
            }
        }

        tElapsed = 0;
        if (in.peek() == '/') {
            in.ignore(1);
            tElapsed = in.readDoubleNoExponent();
        }
    }

    /**
     * Construct a move with elapsed time of 0 and no evaluation
     *
     * @param mv move made
     */
    public COsMoveListItem(OsMove mv) {
        this.mv = new OsMove(mv);
        dEval = Double.NaN;
        tElapsed = 0;
    }

    @Override public boolean equals(Object obj) {
        if (obj instanceof COsMoveListItem) {
            COsMoveListItem b = (COsMoveListItem) obj;
            // use Double.compare() because of possibility of NaNs.
            final boolean evalsOk = Double.compare(dEval, b.dEval) == 0;
            return mv.equals(b.mv) && evalsOk && tElapsed == b.tElapsed;
        } else {
            return false;
        }
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(mv);
        if (hasEval() || tElapsed != 0) {
            sb.append('/');
            if (hasEval()) {
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
        return !Double.isNaN(dEval);
    }

    /**
     * @return evaluation, or Double.NaN if there is no eval.
     */
    public double getEval() {
        return dEval;
    }

    public double getElapsedTime() {
        return tElapsed;
    }
}
