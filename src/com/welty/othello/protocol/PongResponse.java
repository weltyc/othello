package com.welty.othello.protocol;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString @EqualsAndHashCode
public class PongResponse implements NBoardResponse {
    public int pong;

    public PongResponse(int pong) {
        this.pong = pong;
    }
}
