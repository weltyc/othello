package com.welty.othello.gdk;

/**
 * Created by IntelliJ IDEA.
* User: HP_Administrator
* Date: Jun 19, 2009
* Time: 9:32:47 PM
* To change this template use File | Settings | File Templates.
*/
public class PieceCounts {
    public final int nBlack, nWhite, nEmpty;

    public PieceCounts(int nBlack, int nWhite, int nEmpty) {
        this.nBlack = nBlack;
        this.nWhite = nWhite;
        this.nEmpty = nEmpty;
    }

    public int result(boolean fAnti) {
        int nNet = nBlack - nWhite;
        if (nNet < 0)
            nNet -= nEmpty;
        else if (nNet > 0)
            nNet += nEmpty;

        return fAnti ? nNet : -nNet;
    }

    public int netBlackSquares() {
        return nBlack - nWhite;
    }

    @Override public boolean equals(Object obj) {
        if (obj instanceof PieceCounts) {
            PieceCounts b = (PieceCounts) obj;
            return nBlack == b.nBlack && nWhite == b.nWhite && nEmpty == b.nEmpty;
        } else {
            return false;
        }
    }
}
