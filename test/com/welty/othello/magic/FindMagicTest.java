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

package com.welty.othello.magic;

import junit.framework.TestCase;

/**
 */
public class FindMagicTest extends TestCase {
    final long mask = FindMagic.A1H8;
    final FindMagic.Magic magic = new FindMagic.Magic(1, 1, mask, 0);

    public void testCalcC() {
        assertEquals(0, magic.calcC(0));
    }

    public void testNext() {
        final long[] vs = {0x01, 0x0200, 0x0201, 0x040000, 0x040001, 0x040200, 0x040201, 0x08000000};
        assertEquals(0, magic.next(mask));
        for (int i = 0; i<vs.length-1; i++) {
            assertEquals(vs[i+1], magic.next(vs[i]));
        }
    }
}
