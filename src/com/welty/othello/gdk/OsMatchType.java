package com.welty.othello.gdk;

import com.welty.othello.c.CReader;

import java.io.EOFException;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: May 2, 2009
 * Time: 4:50:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class OsMatchType {
    COsBoardType bt = new COsBoardType("8");
    private boolean fSynch;
    public boolean fRand;
    boolean fKomi;
    boolean fAnti;
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
        fSynch = b.fSynch;
        fRand = b.fRand;
        fKomi = b.fKomi;
        fAnti = b.fAnti;
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
                        fSynch = true;
                        break;
                    case 'a':
                        fAnti = true;
                        break;
                    case 'k':
                        fKomi = true;
                        break;
                    case 'r':
                        fRand = true;
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
        if (fSynch) {
            nColors++;
        }
        if (fKomi) {
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
        if (fSynch)
            sb.append('s');
        sb.append(bt.toString());
        if (fKomi)
            sb.append('k');
        if (fAnti)
            sb.append('a');
        if (fRand)
            sb.append('r').append(nRandDiscs);
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        Out(sb);
        return sb.toString();
    }

    void Clear() {
        fSynch = fRand = fKomi = fAnti = fBlack = fWhite = false;
        bt.Clear();
    }

    //@return the color of the match, i.e.
    //	S: synchro
    //  K: komi
    //  B: black
    //  W: white
    //  ?: random
    char GetColor() {
        if (fSynch) {
            return 'S';
        } else if (fKomi) {
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
            if (fRand)
                return kErrRandAndColor;
            if (fSynch)
                return kErrSynchAndColor;
            if (fKomi)
                return kErrKomiAndColor;
        }

        // misc errors
        if (fSynch && fKomi)
            return kErrSynchAndKomi;

        // rand errors
        if (fRand) {
            if (!fSynch && !fKomi)
                return kErrRandUnbalanced;
            if (nRandDiscs > bt.NRandDiscsMax())
                return kErrTooManyRandDiscs;
            else if (nRandDiscs < bt.NRandDiscsMin())
                return kErrTooFewRandDiscs;
        }

        return 0;
    }
}
