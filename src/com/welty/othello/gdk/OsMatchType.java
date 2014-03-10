package com.welty.othello.gdk;

import com.welty.othello.c.CReader;
import lombok.EqualsAndHashCode;

import java.io.EOFException;

/**
 * Match Type, as used in GGF format games
 */
@EqualsAndHashCode
public class OsMatchType {
    COsBoardType bt = new COsBoardType("8");
    public boolean synch;
    public boolean rand;
    public boolean komi;
    public boolean anti;
    private boolean fBlack;
    private boolean fWhite;
    private int nRandDiscs;

    private static final int kErrRandAndColor = 0x8500;
    private static final int kErrSynchAndColor = 0x8501;
    private static final int kErrKomiAndColor = 0x8502;
    private static final int kErrBlackAndWhite = 0x8503;     // color errors
    private static final int kErrSynchAndKomi = 0x8504;
    private static final int kErrRandUnbalanced = 0x8505;    // nonsynch games
    private static final int kErrTooFewRandDiscs = 0x8506;
    private static final int kErrTooManyRandDiscs = 0x8507;

    public OsMatchType() {

    }

    public OsMatchType(OsMatchType b) {
        bt = new COsBoardType(b.bt);
        synch = b.synch;
        rand = b.rand;
        komi = b.komi;
        anti = b.anti;
        fBlack = b.fBlack;
        fWhite = b.fWhite;
        nRandDiscs = b.nRandDiscs;
    }


    public void Initialize(String sBoardType) {
        In(new CReader(sBoardType));
    }

    void In(CReader is) {
        Clear();

        is.ignoreWhitespace();
        while (Character.isLetterOrDigit(is.peek())) {
            char c;
            c = is.peek();
            if (Character.isDigit(c)) {
                bt = new COsBoardType(is);
            } else {
                c = is.read();
                switch (Character.toLowerCase(c)) {
                    case 's':
                        synch = true;
                        break;
                    case 'a':
                        anti = true;
                        break;
                    case 'k':
                        komi = true;
                        break;
                    case 'r':
                        rand = true;
                        try {
                            nRandDiscs = is.readInt();
                        } catch (EOFException e) {
                            throw new IllegalStateException("unable to calculate # of rand discs");
                        }
                        break;
                    case 'b':
                        fBlack = true;
                        break;
                    case 'w':
                        fWhite = true;
                        break;
                    default:
                        throw new IllegalArgumentException("unknown match type character : " + c);
                }
            }
        }
        int nColors = 0;
        if (synch) {
            nColors++;
        }
        if (komi) {
            nColors++;
        }
        if (fBlack) {
            nColors++;
        }
        if (fWhite) {
            nColors++;
        }
        if (nColors > 1) {
            throw new IllegalArgumentException("illegal color scheme");
        }
    }

    void Out(StringBuilder sb) {
        if (synch)
            sb.append('s');
        sb.append(bt.toString());
        if (komi)
            sb.append('k');
        if (anti)
            sb.append('a');
        if (rand)
            sb.append('r').append(nRandDiscs);
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        Out(sb);
        return sb.toString();
    }

    void Clear() {
        synch = rand = komi = anti = fBlack = fWhite = false;
        bt.Clear();
    }

    //@return the color of the match, i.e.
    //	S: synchro
    //  K: komi
    //  B: black
    //  W: white
    //  ?: random
    char GetColor() {
        if (synch) {
            return 'S';
        } else if (komi) {
            return 'K';
        } else if (fBlack) {
            return 'B';
        } else if (fWhite) {
            return 'W';
        } else {
            return '?';
        }
    }

    int Validate() {
        // color errors, may not specify a color with anything else
        if (fBlack && fWhite)
            return kErrBlackAndWhite;
        if (fBlack || fWhite) {
            if (rand)
                return kErrRandAndColor;
            if (synch)
                return kErrSynchAndColor;
            if (komi)
                return kErrKomiAndColor;
        }

        // misc errors
        if (synch && komi)
            return kErrSynchAndKomi;

        // rand errors
        if (rand) {
            if (!synch && !komi)
                return kErrRandUnbalanced;
            if (nRandDiscs > bt.NRandDiscsMax())
                return kErrTooManyRandDiscs;
            else if (nRandDiscs < bt.NRandDiscsMin())
                return kErrTooFewRandDiscs;
        }

        return 0;
    }
}
