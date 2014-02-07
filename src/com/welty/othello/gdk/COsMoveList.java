package com.welty.othello.gdk;

import java.util.ArrayList;

/**
 * A list of GGF-format moves. Passes are included.
 * <PRE>
 * User: Chris
 * Date: May 2, 2009
 * Time: 3:45:23 PM
 * </PRE>
 */
public class COsMoveList extends ArrayList<OsMoveListItem> {
    /**
     * Create a copy of a move list
     *
     * @param ml     source for copy
     * @param nMoves number of moves to retain; in the range 0..ml.size()
     */
    public COsMoveList(COsMoveList ml, int nMoves) {
        super(ml);
        removeRange(nMoves, size());
    }

    public COsMoveList() {
    }

    public String toMoveListString() {
        final StringBuilder sb = new StringBuilder();
        for (OsMoveListItem mli : this) {
            sb.append(mli.move.toString());
        }
        return sb.toString();
    }

    /**
     * Strip all evals and times from the moves
     */
    public void stripEvalsAndTimes() {
        for (int i = 0; i < size(); i++) {
            set(i, new OsMoveListItem(get(i).move));
        }
    }
}
