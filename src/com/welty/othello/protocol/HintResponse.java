package com.welty.othello.protocol;

import com.welty.othello.c.CReader;
import com.welty.othello.gdk.OsMove;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;

@EqualsAndHashCode
public class HintResponse implements NBoardResponse {
    public final int pong;
    public final boolean book;
    public final String pv;
    public final String eval;
    public final int nGames;
    public final String depth;
    public final String freeformText;
    public final OsMove move;
    public final int depthInt;

    public HintResponse(int pong, boolean isBook, String pv, String eval, int nGames, String depth, String freeformText) {
        if (pv.contains(" ")) {
            throw new IllegalArgumentException("pv can't contain spaces");
        }
        if (eval.contains(" ")) {
            throw new IllegalArgumentException("eval can't contain spaces");
        }
        if (depth.contains(" ")) {
            throw new IllegalArgumentException("depth can't contain spaces");
        }
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
    @NotNull static HintResponse of(int pong, boolean isBook, CReader in) throws EOFException {
        final String pv = in.readString();
        final String eval = in.readString();
        final int nGames = in.readInt();
        final String depth = in.readString();
        final String freeformText = in.readLine();
        return new HintResponse(pong, isBook, pv, eval, nGames, depth, freeformText);
    }

    @Override public String toString() {
        return String.format("%s %s %s %d %s %s", book?"book":"search", pv, eval, nGames, depth, freeformText);
    }
}
