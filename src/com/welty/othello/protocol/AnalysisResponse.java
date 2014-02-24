package com.welty.othello.protocol;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString @EqualsAndHashCode
public class AnalysisResponse implements NBoardResponse {
    public final int pong;
    /**
     * Number of moves of the game that have been played when this value is calculated.
     *
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
}
