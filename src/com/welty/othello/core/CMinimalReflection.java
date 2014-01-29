package com.welty.othello.core;

/**
 * This is just a bitboard that's guaranteed to be a minimal reflection.
 *
 * The class won't completely guarantee that you haven't messed up, since you can alter the data
 * elements to make it a nonminimal bitboard. Don't do this.
 *
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: May 9, 2009
 * Time: 12:08:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class CMinimalReflection extends CBitBoard {
	public CMinimalReflection(final CBitBoard bb) {
        super(bb.MinimalReflection());
    }

    public CMinimalReflection() {
        super(0,0);
    }
}
