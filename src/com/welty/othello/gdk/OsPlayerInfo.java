package com.welty.othello.gdk;

import com.welty.othello.c.CReader;
import lombok.EqualsAndHashCode;

/**
 * Information about a player in a GGF format game
 */
@EqualsAndHashCode
public class OsPlayerInfo {
    public String name;
    public double rating;

    public OsPlayerInfo(OsPlayerInfo pi) {
        name = pi.name;
        rating = pi.rating;
    }

    public OsPlayerInfo() {
    }

    void In(CReader is) {
        rating = is.readDoubleNoExponent();
        name = is.readString();
    }

    void Clear() {
        rating = 0;
        name = "";
    }


}
