package com.welty.othello.protocol;

import com.welty.othello.gdk.OsMoveListItem;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString @EqualsAndHashCode
public class MoveResponse implements NBoardResponse {
    public final int pong;
    public final OsMoveListItem mli;

    public MoveResponse(int pong, OsMoveListItem mli) {
        this.pong = pong;
        this.mli = mli;
    }
}
