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

package com.welty.othello.gdk;

import com.welty.othello.c.CReader;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.util.HashMap;

/**
 * Match Type, as used in GGF format games
 */
@EqualsAndHashCode
public class OsMatchType {
    public static final OsMatchType STANDARD = new OsMatchType(OsBoardType.BT_8x8);

    /**
     * Construct a match type with standard options (color = STANDARD, not rand or anti).
     *
     * @param bt board size
     */
    public OsMatchType(@NotNull OsBoardType bt) {
        this.bt = bt;
        this.color = Color.STANDARD;
        this.rand = false;
        this.nRandDiscs = 0;
        this.anti = false;
    }

    /**
     * Method of determining which player plays which color.
     */
    public enum Color {
        STANDARD, BLACK, WHITE, SYNCH, KOMI
    }

    public final @NotNull Color color;
    final @NotNull OsBoardType bt;
    public final boolean rand;
    public final boolean anti;
    private final int nRandDiscs;

    public OsMatchType(String sBoardType) {
        this(new CReader(sBoardType));
    }

    private static final HashMap<Character, Color> colorFromChar = new HashMap<>();

    static {
        colorFromChar.put('b', Color.BLACK);
        colorFromChar.put('w', Color.WHITE);
        colorFromChar.put('s', Color.SYNCH);
        colorFromChar.put('k', Color.KOMI);
    }

    OsMatchType(@NotNull CReader is) {
        is.ignoreWhitespace();

        final StringBuilder colorChars = new StringBuilder();
        OsBoardType bt = null;
        boolean anti = false;
        boolean rand = false;
        int nRandDisks = 0;

        while (Character.isLetterOrDigit(is.peek())) {
            char c;
            c = is.peek();
            if (Character.isDigit(c)) {
                if (bt != null) {
                    throw new IllegalArgumentException("Multiple board types? already had " + bt);
                }
                bt = new OsBoardType(is);
            } else {
                c = is.read();
                switch (Character.toLowerCase(c)) {
                    case 's':
                    case 'k':
                    case 'b':
                    case 'w':
                        colorChars.append(c);
                        break;

                    case 'a':
                        anti = true;
                        break;

                    case 'r':
                        rand = true;
                        try {
                            nRandDisks = is.readInt();
                        } catch (EOFException e) {
                            throw new IllegalStateException("Random match type is missing # of random disks");
                        }
                        break;

                    default:
                        throw new IllegalArgumentException("unknown match type character : " + c);
                }
            }
        }

        // determine color
        if (colorChars.length() > 1) {
            throw new IllegalArgumentException("illegal color scheme: " + colorChars);
        }
        if (colorChars.length() == 0) {
            color = Color.STANDARD;
        } else {
            color = colorFromChar.get(colorChars.charAt(0));
        }

        if (bt == null) {
            throw new IllegalArgumentException("missing board type");
        }
        this.bt = bt;
        this.anti = anti;
        this.rand = rand;
        this.nRandDiscs = nRandDisks;
    }

    void Out(StringBuilder sb) {
        if (color == Color.SYNCH) {
            sb.append('s');
        }
        sb.append(bt.toString());
        if (color == Color.KOMI) {
            sb.append('k');
        }
        if (anti) {
            sb.append('a');
        }
        if (rand) {
            sb.append('r').append(nRandDiscs);
        }
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        Out(sb);
        return sb.toString();
    }

    /**
     * Get the color method for the match.
     *
     * @return the color method
     */
    @NotNull Color getColor() {
        return color;
    }

    public boolean isSynch() {
        return color == Color.SYNCH;
    }

    public boolean isKomi() {
        return color == Color.KOMI;
    }
}
