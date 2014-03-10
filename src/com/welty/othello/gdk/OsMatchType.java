package com.welty.othello.gdk;

import com.welty.othello.c.CReader;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;

/**
 * Match Type, as used in GGF format games
 */
@EqualsAndHashCode
public class OsMatchType {
    /**
     * Method of determining which player plays which color.
     */
    public enum Color {
        STANDARD, BLACK, WHITE, SYNCH, KOMI
    }

    public @NotNull Color color;

    OsBoardType bt = new OsBoardType("8");
    public boolean rand;
    public boolean anti;
    private int nRandDiscs;

    public OsMatchType() {

    }

    public OsMatchType(OsMatchType b) {
        bt = b.bt;
        color = b.color;
        rand = b.rand;
        anti = b.anti;
        nRandDiscs = b.nRandDiscs;
    }


    public void Initialize(String sBoardType) {
        In(new CReader(sBoardType));
    }

    void In(CReader is) {
        Clear();

        final StringBuilder colorChars = new StringBuilder();

        is.ignoreWhitespace();
        while (Character.isLetterOrDigit(is.peek())) {
            char c;
            c = is.peek();
            if (Character.isDigit(c)) {
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
                            nRandDiscs = is.readInt();
                        } catch (EOFException e) {
                            throw new IllegalStateException("unable to calculate # of rand discs");
                        }
                        break;

                    default:
                        throw new IllegalArgumentException("unknown match type character : " + c);
                }
            }
        }
        if (colorChars.length() > 1) {
            throw new IllegalArgumentException("illegal color scheme: " + colorChars);
        }
        if (colorChars.length() == 0) {
            color = Color.STANDARD;
        }
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

    void Clear() {
        color = Color.STANDARD;
        rand = anti = false;
        bt = OsBoardType.BT_8x8;
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
