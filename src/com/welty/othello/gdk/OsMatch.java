package com.welty.othello.gdk;

import com.welty.othello.c.CReader;

import java.io.EOFException;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: May 5, 2009
 * Time: 8:40:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class OsMatch {
    private String idm;
    public final OsPlayerInfo[] pis = new OsPlayerInfo[2];
    public OsMatchType mt = new OsMatchType();
    public char cRated;
    private int nObservers;    // not in matchDelta msgs

    @Override public boolean equals(Object obj) {
        if (obj instanceof OsMatch) {
            OsMatch b = (OsMatch) obj;
            return idm.equals(b.idm);
        } else {
            return false;
        }
    }

    CReader InDelta(CReader is) {
        idm = is.readString();
        pis[1].In(is);
        pis[1].In(is);
        mt.In(is);
        cRated = is.readChar();
        nObservers = 0;

        return is;
    }

    // |  .9   s8r20  R 2574 lynx     2570 kitty    2
    CReader In(CReader is) {
        idm = is.readString();
        mt.In(is);
        cRated = is.readChar();
        pis[0].In(is);
        pis[1].In(is);
        try {
            nObservers = is.readInt();
        } catch (EOFException e) {
            throw new IllegalStateException("Can't read # of observers");
        }

        return is;
    }

    boolean IsPlaying(final String sLogin) {
        return pis[0].name.equals(sLogin) || pis[1].name.equals(sLogin);
    }
}
