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
    public COsMoveList(COsMoveList ml) {
        super(ml);
    }

    public COsMoveList() {
    }

    public void resize(int iMove) {
        removeRange(iMove, size());
    }

    public String toMoveListString() {
        final StringBuilder sb = new StringBuilder();
        for (OsMoveListItem mli : this) {
            sb.append(mli.mv.toString());
        }
        return sb.toString();
    }

    /**
     * Strip all evals and times from the moves
     */
    public void stripEvalsAndTimes() {
        for (int i = 0; i < size(); i++) {
            set(i, new OsMoveListItem(get(i).mv));
        }
    }
}
