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
    public final OsMove move;

    /**
     * Eval, in disks, or Double.NaN if no eval exists
     */
    private final double eval;

    /**
     * Elapsed time, in seconds
     */
    private final double tElapsed;

    /**
     * Create a MoveListItem
     *
     * @param move     move
     * @param eval     move evaluation in disks, or Double.NaN if no eval was provided
     * @param tElapsed elapsed time, in seconds
     */
    public OsMoveListItem(OsMove move, double eval, double tElapsed) {
        this.move = move;
        this.eval = eval;
        this.tElapsed = tElapsed;
    }

    public OsMoveListItem(String text) {
        this(new CReader(text));
    }

    public OsMoveListItem(CReader in) {

        // move
        move = new OsMove(in);
        in.ignoreAlphaNumeric();     // sometimes have extra chars after the move

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
     * @param move move made
     */
    public OsMoveListItem(OsMove move) {
        this.move = move;
        eval = Double.NaN;
        tElapsed = 0;
    }

    @Override public boolean equals(Object obj) {
        if (obj instanceof OsMoveListItem) {
            OsMoveListItem b = (OsMoveListItem) obj;
            // use Double.compare() because of possibility of NaNs.
            final boolean evalsOk = Double.compare(eval, b.eval) == 0;
            return move.equals(b.move) && evalsOk && tElapsed == b.tElapsed;
        } else {
            return false;
        }
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(move);
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
     * @return evaluation, in disks, or Double.NaN if there is no eval.
     */
    public double getEval() {
        return eval;
    }

    public double getElapsedTime() {
        return tElapsed;
    }

    /**
     * Reflect this moveListItem
     * <p/>
     * MoveListItem is immutable, so this returns a new object.
     * <p/>
     * Note: no guarantee this reflection index matches BitBoardUtils.reflect().
     *
     * @param iReflection reflection index
     * @return reflected moveListItem.
     */
    public OsMoveListItem reflect(int iReflection) {
        return new OsMoveListItem(move.reflect(iReflection), eval, tElapsed);
    }
}
