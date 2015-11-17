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

import com.welty.othello.c.CReader;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;

/**
 * An Othello board type
 */
@EqualsAndHashCode
public class OsBoardType {
    public static final OsBoardType BT_8x8 = new OsBoardType("8");

    public final int n;
    public final boolean octo;

    public OsBoardType(@NotNull String s) {
        this(new CReader(s));
    }

    public OsBoardType(@NotNull CReader is) {
        try {
            int n = is.readInt();
            switch (n) {
                case 4:
                case 6:
                case 8:
                case 10:
                case 12:
                case 14:
                    this.n = n;
                    octo = false;
                    break;
                case 88:
                    this.n = 10;
                    octo = true;
                    break;
                default:
                    throw new IllegalArgumentException("unknown board type : " + n);
            }
        } catch (EOFException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override public String toString() {
        if (octo) {
            return "88";
        } else {
            return "" + n;
        }
    }

    /**
     * @return the maximum number of rand disks allowed by GGS
     */
    int maxRandDisks() {
        if (octo)
            return 44;
        else
            return n * n - 16;
    }

    /**
     * @return the minimum number of rand disks allowed by GGS
     */
    int minRandDisks() {
        return 4;
    }

    /**
     * @return The number of squares on the board that can have a disk in them
     */
    int nPlayableSquares() {
        if (octo)
            return 88;
        else
            return n * n;
    }

    /**
     * @return The total number of squares used to represent the position, including
     *         a border row of dummy squares
     */
    int nTotalSquares() {
        return (n + 2) * (n + 2);
    }

    /**
     * @param row row of square, 0..n-1
     * @param col col of square, 0..n-1
     * @return true if the given square is a 'dummy' square, i.e. can't contain a disk.
     */
    boolean dummyCorner(int row, int col) {
        if (octo) {
            int rowMirror = n - 1 - row;
            int colMirror = n - 1 - col;

            if (rowMirror < row)
                row = rowMirror;
            if (colMirror < col)
                col = colMirror;

            return (row + col) < 2;
        } else
            return false;
    }

    final String description() {
        if (octo)
            return "Octagon";
        else switch (n) {
            case 4:
                return "4x4";
            case 6:
                return "6x6";
            case 8:
                return "8x8";
            case 10:
                return "10x10";
            case 12:
                return "12x12";
            case 14:
                return "14x14";
            default:
                return "Unknown board";
        }
    }

    /**
     * Determine whether the row/column is a legal square on the board.
     *
     * 'legal' means in the board and, in the case of octagonal boards, not in the unused corner.
     *
     * @param row 0-based row of square
     * @param col  0-based col of square
     * @return true if the square is a legal board square.
     */
    public boolean isLegalSquare(int row, int col) {
        return row >= 0 && col >= 0 && row < n && col < n && !dummyCorner(row, col);
    }
}
