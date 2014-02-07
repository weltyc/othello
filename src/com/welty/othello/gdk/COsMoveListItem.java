package com.welty.othello.gdk;

import com.welty.othello.c.CReader;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: May 2, 2009
 * Time: 3:18:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class COsMoveListItem {
    public COsMove mv;
    private double dEval;
    public double tElapsed;
    private boolean hasEval;

    public COsMoveListItem() {
    }

    public COsMoveListItem(COsMove mv, double dEval, double tElapsed) {
        this.mv = new COsMove(mv);
        this.dEval = dEval;
        this.tElapsed = tElapsed;
    }

    public COsMoveListItem(COsMoveListItem mli) {
        mv = new COsMove(mli.mv);
        dEval = mli.dEval;
        tElapsed = mli.tElapsed;
    }

    public COsMoveListItem(String text) {
        this(new CReader(text));
    }

    public COsMoveListItem(CReader cReader) {
        In(cReader);
    }

    public void In(CReader is) {

        // move
        mv = new COsMove();
        mv.in(is);
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
