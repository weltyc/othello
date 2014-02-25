package com.welty.othello.gdk;

import com.welty.othello.c.CReader;
import lombok.EqualsAndHashCode;

/**
 * Information about a player in a GGF format game
 */
@EqualsAndHashCode
public class OsPlayerInfo {
    public String sName;
    public double dRating;

    public OsPlayerInfo(OsPlayerInfo pi) {
        sName = pi.sName;
        dRating = pi.dRating;
    }

    public OsPlayerInfo() {
    }

    void In(CReader is) {
        dRating = is.readDoubleNoExponent();
        sName = is.readString();
    }

    void Clear() {
        dRating = 0;
        sName = "";
    }


}
