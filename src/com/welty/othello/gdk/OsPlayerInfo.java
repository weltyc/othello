package com.welty.othello.gdk;

import com.welty.othello.c.CReader;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: May 2, 2009
 * Time: 4:41:34 PM
 * To change this template use File | Settings | File Templates.
 */
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
