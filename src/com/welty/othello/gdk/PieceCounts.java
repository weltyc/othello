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

    /**
     * Compute black's winning margin, including empty disks.
     *
     * if fAnti is false, this is (black disks - white disks), with winner getting empties.
     *
     * if fAnti is true, this is (white disks - black disks), with the loser getting empties.
     *
     * @param fAnti is this an 'anti' game?
     * @return black winning margin.
     */
    public int result(boolean fAnti) {
        int nNet = nBlack - nWhite;
        if (nNet < 0)
            nNet -= nEmpty;
        else if (nNet > 0)
            nNet += nEmpty;

        return fAnti ? -nNet : +nNet;
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
