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

package com.welty.othello.protocol;

import com.welty.othello.c.CReader;
import com.welty.othello.gdk.OsMove;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;

@EqualsAndHashCode
public class HintResponse implements NBoardResponse {
    public final int pong;
    public final boolean book;
    public final String pv;
    public final Value eval;
    public final int nGames;
    public final Depth depth;
    public final String freeformText;
    public final OsMove move;

    public HintResponse(int pong, boolean isBook, String pv, Value eval, int nGames, Depth depth, String freeformText) {
        if (pv.contains(" ")) {
            throw new IllegalArgumentException("pv can't contain spaces");
        }
        this.pong = pong;
        book = isBook;
        this.pv = pv;
        this.eval = eval;
        this.nGames = nGames;
        this.depth = depth;
        this.freeformText = freeformText;
        move = new OsMove(pv);
    }

    /**
     * Create a hint response from the rest of the command after the initial "search" or "book"
     *
     * @param isBook true if this is a "book" response, false if it's a "search" response
     * @param in     CReader containing the rest of the line
     */
    @NotNull static HintResponse of(int pong, boolean isBook, CReader in) throws EOFException {
        final String pv = in.readString();
        final Value eval = new Value(in.readString());
        final int nGames = in.readInt();
        final Depth depth = new Depth(in.readString());
        final String freeformText = in.readLine();
        return new HintResponse(pong, isBook, pv, eval, nGames, depth, freeformText);
    }

    @Override public String toString() {
        return String.format("%s %s %s %d %s %s", book ? "book" : "search", pv, eval, nGames, depth, freeformText);
    }
}
