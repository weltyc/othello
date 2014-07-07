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

package com.welty.othello.scraper;

import junit.framework.TestCase;

/**
 * Test results
 */
@SuppressWarnings({"JavaDoc"})
public class StatusTest extends TestCase {
    private static final String forcedMove = "=== A7      ? 0 100%  0.00    0 n/0.000s =    0kn/s; **.** us/n  4e:  ===";
    private static final String bookMove = "=== D2  +4.00 0  30 , book 0.00    0 n/0.001s =    0kn/s; **.** us/n 47e:  ===";
    private static final String searchMove = "=== C7  -4.00 0 100%W 2924.87   34Gn/2924.874s = 11882kn/s;  0.08 us/n 32e:  ===";

    public void testCreate() {
        assertNull(Status.create("not a score line"));
        testCreate("D2", 4, 0, 47, true, bookMove);
        testCreate("C7", -4, 2924.87, 32, false, searchMove);
        testCreate("A7", Double.NaN, 0, 4, false, forcedMove);
    }

    private static void testCreate(String square, double score, double seconds, int depth, boolean isBook, String text) {
        Status status = Status.create(text);
        assertNotNull(status);
        assertEquals(square, status.square);
        if (Double.isNaN(score)) {
            assertTrue(Double.isNaN(status.score));
        }
        else {
            assertEquals(score, status.score, 1e-10);
        }
        assertEquals(seconds, status.seconds, 1e-10);
        assertEquals(depth, status.nEmpty, 1e-10);
        assertEquals(isBook, status.isBook);
    }

    public void testParseEngineering() {
        testParseEngineering(12, "12 ");
        testParseEngineering(1.2, "1.2 ");
        testParseEngineering(2000, "2k");
        testParseEngineering(2000, "2 k");
        testParseEngineering(2000, "2. k");
        testParseEngineering(2000, "2.0 k");
        testParseEngineering(2.3e-6, "2.3 u");
        testParseEngineering(2.3e-6, "2.3u");
        testParseEngineering(1234000000, "1234M");
    }

    private static void testParseEngineering(double expected, String text) {
        assertEquals(expected, Status.parseEngineering(text), 1e-1);
    }

    public void testEngineering() {
        testEngineering("120. ", 120.);
        testEngineering("12.0 ", 12.);
        testEngineering("1.20 ", 1.2);
        testEngineering("120.m", .12);
        testEngineering("120.k", 120000);
        testEngineering("0.00 ", 0);
        testEngineering("1.00k", 1000);
    }

    private void testEngineering(String expected, double value) {
        assertEquals(expected, Status.engineering(value));
    }

    public void testSameResult() {
        final Status forced = Status.create(forcedMove);
        final Status book = Status.create(bookMove);

        assertNotNull(forced);
        assertNotNull(book);
        assertTrue(forced.sameResult(forced));
        assertTrue(book.sameResult(book));
        assertFalse(forced.sameResult(book));
        assertFalse(book.sameResult(forced));
    }

    public void testIsConsistentWith() {
        final Status forced = Status.create(forcedMove);
        final Status book = Status.create(bookMove);
        final Status search = Status.create("=== C7  -4.00 0 100%W 2924.87   34Gn/2924.874s = 11882kn/s;  0.08 us/n 32e:  ===");
        final Status search2 = Status.create("=== C7  4.00 0 100%W 2924.87   34Gn/2924.874s = 11882kn/s;  0.08 us/n 32e:  ===");

        assertNotNull(forced);
        assertNotNull(book);
        assertNotNull(search);
        assertTrue(forced.isConsistentWith(forced));
        assertTrue(book.isConsistentWith(book) );
        assertTrue(search.isConsistentWith(search));
        assertFalse(search.isConsistentWith(search2));
     }
}
