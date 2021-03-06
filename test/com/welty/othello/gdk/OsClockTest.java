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
import junit.framework.TestCase;

/**
 * Test of OsClock object
 */
public class OsClockTest extends TestCase {
    public void testRead() {
        assertEquals(23, OsClock.ReadTime(new CReader("23")), 1e-10);
        assertEquals(83, OsClock.ReadTime(new CReader("1:23")), 1e-10);
        assertEquals(12 * 3600 + 83, OsClock.ReadTime(new CReader("12:01:23")), 1e-10);
        assertEquals(36 * 3600 + 83, OsClock.ReadTime(new CReader("1.12:01:23")), 1e-10);
    }

    public void testWrite() {
        assertEquals("23", writeTime(23));
        assertEquals("1:23", writeTime(83));
        assertEquals("12:01:23", writeTime(12 * 3600 + 83));
        assertEquals("0.005", writeTime(0.005));
        assertEquals("1:00.005", writeTime(60.005));
    }

    private String writeTime(double t) {
        StringBuilder sb = new StringBuilder();
        OsClock.writeTime(sb, t);
        return sb.toString();
    }

    public void testIn() {
        testIn("23", 23, 0, 120);
        testIn("23/1/14:00", 23, 1, 14 * 60);
        testIn("23//0", 23, 0, 0);
        testIn("23/1:00", 23, 60, 120);
        testIn("23/1:01", 23, 61, 120);
        testIn("5:00//2:00", 300, 0, 120);
    }

    public void testOut() {
        assertEquals("5:00", new OsClock(5 * 60, 0, 120).toString());
        assertEquals("5:00//0", new OsClock(5 * 60, 0, 0).toString());
        assertEquals("1:00//1:00", new OsClock(60, 0, 60).toString());
        assertEquals("1:00:00//1:00:00", new OsClock(60*60, 0, 60*60).toString());
        assertEquals("0.005", new OsClock(0.005).toString());
    }

    public void testToDisplayString() {
        assertEquals("0:02", new OsClock(2, 0, 120).toDisplayString());
        assertEquals("14:02", new OsClock(14 * 60 + 2, 0, 120).toDisplayString());
        assertEquals("1:00:02", new OsClock(3600 + 2, 0, 120).toDisplayString());
        assertEquals("0:02 (overtime)", new OsClock(2, 0, 120, 1).toDisplayString());
        assertEquals("0:00 (timeout)", new OsClock(0, 0, 120, 2).toDisplayString());
    }

    private void testIn(String text, int current, int tIncrement, int grace) {
        OsClock expected = new OsClock(current, tIncrement, grace);
        OsClock clock = new OsClock(new CReader(text));
        assertEquals(expected, clock);
    }
}
