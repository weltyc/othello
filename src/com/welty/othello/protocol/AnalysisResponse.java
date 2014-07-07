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
import lombok.ToString;

import java.io.EOFException;

@ToString @EqualsAndHashCode
public class AnalysisResponse implements NBoardResponse {
    public final int pong;
    /**
     * Number of moves of the game that have been played when this value is calculated.
     * <p/>
     * Thus, the moveNumber is 0 when producing an evaluation for the start position of the game.
     * Passes count as moves, so moveNumber can go above 60.
     */
    public final int moveNumber;

    /**
     * Evaluation of the position. Positions favouring the player-to-move are positive.
     */
    public final double eval;

    public AnalysisResponse(int pong, int moveNumber, double eval) {
        this.pong = pong;
        this.moveNumber = moveNumber;
        this.eval = eval;
    }

    /**
     * Construct an AnalysisResponse from an NBoard protocol remainder.
     * <p/>
     * The "remainder" is the rest of the line after the "analysis" command.
     *
     * @param pong most recent pong sent by engine.
     * @param in   CReader containing the line remainder
     * @return a new NodeStatsResponse corresponding to the line
     * @throws NumberFormatException if the remainder can't be parsed
     * @throws EOFException          if the remainder can't be parsed
     */
    public static AnalysisResponse of(int pong, CReader in) throws EOFException {
        final int moveNumber = in.readInt();
        final double eval = in.readDoubleNoExponent();
        return new AnalysisResponse(pong, moveNumber, eval);
    }
}
