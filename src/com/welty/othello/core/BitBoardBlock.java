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

package com.welty.othello.core;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: May 1, 2009
 * Time: 7:25:49 PM
 * To change this template use File | Settings | File Templates.
 */
class BitBoardBlock {
    private static final long hflipmask4 = 0x0F0F0F0F0F0F0F0FL;
    private static final long hflipmask2 = 0x3333333333333333L;
    private static final long hflipmask1 = 0x5555555555555555L;

    public static long flipHorizontal(long rows) {
        long templ, tempr;

        templ = rows & hflipmask4;
        tempr = rows & ~hflipmask4;
        rows = (templ << 4) | (tempr >>> 4);

        templ = rows & hflipmask2;
        tempr = rows & ~hflipmask2;
        rows = (templ << 2) | (tempr >>> 2);

        templ = rows & hflipmask1;
        tempr = rows & ~hflipmask1;
        rows = (templ << 1) | (tempr >>> 1);

        return rows;
    }

    private static final long vflipmask4 = 0x00000000FFFFFFFFL;
    private static final long vflipmask2 = 0x0000FFFF0000FFFFL;
    private static final long vflipmask1 = 0x00FF00FF00FF00FFL;

    public static long flipVertical(long rows) {
        long templ, tempr;

        templ = rows & vflipmask4;
        tempr = rows & ~vflipmask4;
        rows = (templ << 32) | (tempr >>> 32);

        templ = rows & vflipmask2;
        tempr = rows & ~vflipmask2;
        rows = (templ << 16) | (tempr >>> 16);

        templ = rows & vflipmask1;
        tempr = rows & ~vflipmask1;
        rows = (templ << 8) | (tempr >>> 8);

        return rows;
    }

    public static long flipDiagonal(long rows) {
        rows = flipDiagonalBlock(rows, 0x5500550055005500L, 0x00AA00AA00AA00AAL, 7);         // flip 1x1 blocks within 2x2 blocks
        rows = flipDiagonalBlock(rows, 0x3333000033330000L, 0x0000CCCC0000CCCCL, 14);         // flip 2x2 blocks within 4x4 blocks
        rows = flipDiagonalBlock(rows, 0x0F0F0F0F00000000L, 0x00000000F0F0F0F0L, 28);         // flip 4x4 blocks within 8x8 blocks
        return rows;
    }

    private static long flipDiagonalBlock(long rows, long maskR, long maskL, int move) {
        final long templ = maskL & rows;
        final long tempr = maskR & rows;
        rows ^= (templ | tempr);
        rows |= (templ << move) | (tempr >>> move);

        return rows;
    }

    public static long symmetry(long bitBoard, int sym) {
        long result = bitBoard;

        if ((sym & 1) != 0)
            result = flipVertical(result);
        if ((sym & 2) != 0)
            result = flipHorizontal(result);
        if ((sym & 4) != 0)
            result = flipDiagonal(result);

        return result;
    }

    public static int getRow(long bitBoard, int row) {
        return 0xFF & (int) (bitBoard >>> rowShift(row));
    }

    private static int rowShift(int row) {
        return (row << 3);
    }

    static long orRow(long bitBoard, int row, byte b) {
        bitBoard |= ((long) b & 0xFF) << rowShift(row);
        return bitBoard;
    }
}
