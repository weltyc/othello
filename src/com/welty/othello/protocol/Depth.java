package com.welty.othello.protocol;

import com.welty.othello.c.CReader;
import lombok.EqualsAndHashCode;

import java.io.EOFException;

@EqualsAndHashCode
public class Depth {
    public int depth;
    public String suffix;


    public Depth(String s) {
        final CReader cReader = new CReader(s);
        try {
            depth = cReader.readInt();
        } catch (EOFException e) {
            throw new IllegalArgumentException("Depth must start with an integer, had " + s);
        }
        suffix = cReader.readLine();
    }

    public Depth(int depth) {
        this.depth = depth;
        this.suffix = "";
    }

    /**
     * @return true if this depth represents a proven exact solve
     */
    public boolean isExact() {
        return depth == 100 && suffix.equals("%");
    }

    /**
     * @return true if this depth represents a proven win/loss/draw solve
     */
    public boolean isWldProven() {
        return depth == 100 && suffix.equals("%W");
    }

    /**
     * @return true if this depth represents either a probable or proven solve (w/l/d or exact).
     */
    public boolean isProbableSolve() {
        return suffix.contains("%");
    }

    @Override public String toString() {
        return depth + suffix;
    }
}
