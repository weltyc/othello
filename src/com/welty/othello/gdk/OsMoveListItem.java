package com.welty.othello.gdk;

import com.welty.othello.c.CReader;

/**
 * An Othello move, including optional evaluation and elapsed time
 */
public class OsMoveListItem {
    /**
     * A pass with no evaluation or elapsed time
     */
    public static final OsMoveListItem PASS = new OsMoveListItem(OsMove.PASS);

    /**
     * The move that was made
     */
    public final OsMove mv;

    /**
     * Eval, or Double.NaN if no eval exists
     */
    private final double eval;

    /**
     * Elapsed time, in seconds
     */
    private final double tElapsed;

    public OsMoveListItem(OsMove mv, double eval, double tElapsed) {
        this.mv = new OsMove(mv);
        this.eval = eval;
        this.tElapsed = tElapsed;
    }

    public OsMoveListItem(OsMoveListItem mli) {
        mv = new OsMove(mli.mv);
        eval = mli.eval;
        tElapsed = mli.tElapsed;
    }

    public OsMoveListItem(String text) {
        this(new CReader(text));
    }

    public OsMoveListItem(CReader in) {

        // move
        mv = new OsMove(in);
        in.ignoreAlnum();     // sometimes have extra chars after the move

        eval = parseEval(in);
        tElapsed = parseElapsedTime(in);
    }

    private static double parseElapsedTime(CReader in) {
        double tElapsed = 0;
        if (in.peek() == '/') {
            in.ignore(1);
            tElapsed = in.readDoubleNoExponent();
        }
        return tElapsed;
    }

    private static double parseEval(CReader in) {
        double dEval = Double.NaN;
        if (in.peek() == '/') {
            in.ignore(1);
            try {
                dEval = in.readDoubleNoExponent();
            } catch (NumberFormatException e) {
                // if blank, do nothing - it's already set to Double.NaN
            }
        }
        return dEval;
    }

    /**
     * Construct a move with elapsed time of 0 and no evaluation
     *
     * @param mv move made
     */
    public OsMoveListItem(OsMove mv) {
        this.mv = new OsMove(mv);
        eval = Double.NaN;
        tElapsed = 0;
    }

    @Override public boolean equals(Object obj) {
        if (obj instanceof OsMoveListItem) {
            OsMoveListItem b = (OsMoveListItem) obj;
            // use Double.compare() because of possibility of NaNs.
            final boolean evalsOk = Double.compare(eval, b.eval) == 0;
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
                sb.append(String.format("%3.2f", eval));
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
        return !Double.isNaN(eval);
    }

    /**
     * @return evaluation, or Double.NaN if there is no eval.
     */
    public double getEval() {
        return eval;
    }

    public double getElapsedTime() {
        return tElapsed;
    }
}
