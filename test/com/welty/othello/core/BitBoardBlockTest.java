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

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: May 1, 2009
 * Time: 7:23:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitBoardBlockTest extends TestCase {
    public void testFlipHorizontal() {
        assertHex(0x8040201001040208L, BitBoardBlock.flipHorizontal(0x0102040880204010L));
        assertHex(0x4000000000000000L, BitBoardBlock.flipHorizontal(0x0200000000000000L));
    }

    public void testFlipVertical() {
        assertHex(0x1040208008040201L, BitBoardBlock.flipVertical(0x0102040880204010L));
    }

    public void testFlipDiagonal() {
        assertHex(0x4100000000000001L, BitBoardBlock.flipDiagonal(0x0080000000000081L));
    }

    public void testSymmetry() {
        assertHex(0x02, BitBoardBlock.symmetry(0x02, 0));
        assertHex(0x0200000000000000L, BitBoardBlock.symmetry(0x02, 1));
        assertHex(0x40, BitBoardBlock.symmetry(0x02, 2));
        assertHex(0x4000000000000000L, BitBoardBlock.symmetry(0x02, 3));
        assertHex(0x0100, BitBoardBlock.symmetry(0x02, 4));
        assertHex(0x8000L, BitBoardBlock.symmetry(0x02, 5));
        assertHex(0x0001000000000000L, BitBoardBlock.symmetry(0x02, 6));
        assertHex(0x0080000000000000L, BitBoardBlock.symmetry(0x02, 7));
    }

    private void assertHex(long expected, long actual) {
        assertEquals(Long.toHexString(expected), Long.toHexString(actual));
    }

    public void testGetRow() {
        for (int row = 0; row < 8; row++) {
            assertEquals(row, BitBoardBlock.getRow(0x0706050403020100L, row));
        }
    }

    public void testOrRow() {
        assertHex(0, BitBoardBlock.orRow(0, 0, (byte) 0));
        assertHex(0, BitBoardBlock.orRow(0, 7, (byte) 0));
        assertHex(0x801, BitBoardBlock.orRow(0x801, 0, (byte) 0));
        assertHex(0x801, BitBoardBlock.orRow(0x801, 0, (byte) 1));
        assertHex(0x803, BitBoardBlock.orRow(0x801, 0, (byte) 2));

        assertHex(0x80, BitBoardBlock.orRow(0, 0, (byte) 0x80));
        assertHex(0x8000000000000000L, BitBoardBlock.orRow(0, 7, (byte) 0x80));
    }
}
