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
import lombok.EqualsAndHashCode;

import java.io.EOFException;

@EqualsAndHashCode
public class NodeStatsResponse implements NBoardResponse {
    public final int pong;
    public final long nNodes;
    public final double tElapsed;

    public NodeStatsResponse(int pong, long nNodes, double tElapsed) {
        this.pong = pong;
        this.nNodes = nNodes;
        this.tElapsed = tElapsed;
    }

    /**
     * Construct a NodeStatsResponse from an NBoard protocol remainder.
     * <p/>
     * The "remainder" is the rest of the line after the "nodestats" command.
     *
     * @param pong most recent pong sent by engine.
     * @param in   CReader containing the line remainder
     * @return a new NodeStatsResponse corresponding to the line
     * @throws NumberFormatException if the remainder can't be parsed
     */
    public static NBoardResponse of(int pong, CReader in) throws EOFException {
        final long nNodes = in.readLong();
        final double tElapsed = in.readFloatNoExponent();
        return new NodeStatsResponse(pong, nNodes, tElapsed);
    }

    @Override public String toString() {
        return "(pong=" + pong + ") nodestats " + nNodes + " " + String.format("%.3f", tElapsed);
    }
}
