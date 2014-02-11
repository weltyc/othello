package com.welty.othello.protocol;

import com.welty.othello.c.CReader;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString @EqualsAndHashCode
public class NodeStatsResponse implements NBoardResponse {
    public final int pong;
    public final double nNodes;
    public final double tElapsed;

    public NodeStatsResponse(int pong, double nNodes, double tElapsed) {
        this.pong = pong;
        this.nNodes = nNodes;
        this.tElapsed = tElapsed;
    }

    public static NBoardResponse of(int pong, CReader in) {
        final double nNodes = in.readDoubleNoExponent();
        final double tElapsed = in.readFloatNoExponent();
        return new NodeStatsResponse(pong, nNodes, tElapsed);
    }
}