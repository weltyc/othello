package com.welty.othello.protocol;

import com.welty.othello.c.CReader;
import com.welty.othello.gdk.OsMove;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;

@EqualsAndHashCode @ToString
public class HintResponse implements NBoardResponse {
    public final int pong;
    public final boolean book;
    public final String pv;
    public final float eval;
    public final long nGames;
    public final String depth;
    public final String freeformText;
    public final OsMove move;
    public final int depthInt;

    public HintResponse(int pong, boolean isBook, String pv, float eval, long nGames, String depth, String freeformText) {
        this.pong = pong;
        book = isBook;
        this.pv = pv;
        this.eval = eval;
        this.nGames = nGames;
        this.depth = depth;
        this.freeformText = freeformText;
        move = new OsMove(pv);
        try {
            depthInt = new CReader(depth).readInt();
        } catch (EOFException e) {
            throw new IllegalArgumentException("depth string must start with an integer");
        }
    }

    /**
     * Create a hint response from the rest of the command after the initial "search" or "book"
     *
     * @param isBook true if this is a "book" response, false if it's a "search" response
     * @param in     CReader containing the rest of the line
     */
    @NotNull static HintResponse of(int pong, boolean isBook, CReader in) {
        final String pv = in.readString();
        final float eval = in.readFloatNoExponent();
        final long nGames = in.readLong();
        final String depth = in.readString();
        final String freeformText = in.readLine();
        return new HintResponse(pong, isBook, pv, eval, nGames, depth, freeformText);
    }
}
