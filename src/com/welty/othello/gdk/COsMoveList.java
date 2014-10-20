/*
 * Copyright (c) 2014 Chris Welty.
 *
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * For the license, see <http://www.gnu.org/licenses/gpl.html>.
 */

package com.welty.othello.gdk;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;

/**
 * A list of GGF-format moves. Passes are included.
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

    /**
     * @return A string representation of the move list. For example "F5 D6 C3/0.01/1.0".
     */
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
