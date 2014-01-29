package com.welty.othello.core;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: May 2, 2009
 * Time: 9:07:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class Mobility {
    private static final long notWestEdge = 0xFEFEFEFEFEFEFEFEL;
    private static final long notEastEdge = 0x7F7F7F7F7F7F7F7FL;

    public static long calcMoves(long mover, long empty) {
        //	a direction bit is set if we have seen a mover followed by an unbroken string of enemy squares


        long south = (mover << 8);
        long north = (mover >>> 8);

        final long moverE = mover & notEastEdge;
        final long moverW = mover & notWestEdge;

        long east = (moverE << 1);
        long west = (moverW >>> 1);

        long northeast = (moverE >>> 7);
        long northwest = (moverW >>> 9);
        long southeast = (moverE << 9);
        long southwest = (moverW << 7);

        final long enemy = ~(mover | empty);
        final long enemyE = enemy & notEastEdge;
        final long enemyW = enemy & notWestEdge;

        long moves = 0;

        for (int i = 0; i < 6; i++) {
            south = (south & enemy) << 8;
            north = (north & enemy) >>> 8;
            east = ((east & enemyE) << 1);
            west = ((west & enemyW) >>> 1);

            northeast = ((northeast & enemyE) >>> 7);
            northwest = ((northwest & enemyW) >>> 9);
            southeast = ((southeast & enemyE) << 9);
            southwest = ((southwest & enemyW) << 7);
            moves |= ((north | south | east | west | northeast | northwest | southeast | southwest) & empty);
        }
        return moves;
    }
}
