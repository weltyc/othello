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
public class Depth {
    public final int depth;
    public final String suffix;


    public Depth(String s) {
        final CReader cReader = new CReader(s);
        try {
            depth = cReader.readInt();
        } catch (EOFException e) {
            throw new IllegalArgumentException("Depth must start with an integer, had " + s);
        }
        this.suffix = cReader.readLineNoThrow();
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

    public String humanString() {
        return depth + (suffix.isEmpty() ? " ply" : suffix);
    }
}
