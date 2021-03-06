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

import junit.framework.TestCase;

/**
 * Test OsMoveListItem behaviour
 */
public class OsMoveListItemTest extends TestCase {
    public void testReadWrite() {
        testReadWrite("F5/1.23/2.1");
        testReadWrite("H8//1.03");
    }

    private static void testReadWrite(String text) {
        final OsMoveListItem mli = new OsMoveListItem(text);
        assertEquals(text, mli.toString());
    }

    public void testHasEval() {
        testHasEval(true, "F5/1.00");
        testHasEval(false, "F5");
        testHasEval(true, "F5/0.00");
        testHasEval(false, "F5//1.0");
        testHasEval(true, "F5/0.00/1.0");
    }

    private void testHasEval(boolean expected, String text) {
        final OsMoveListItem mli = new OsMoveListItem(text);
        assertEquals(text, expected, mli.hasEval());
        assertEquals(text, text, mli.toString());
    }

    public void testMoveOnlyConstructor() {
        final OsMove move = new OsMove("F5");
        final OsMoveListItem mli = new OsMoveListItem(move);
        assertEquals(move, mli.move);
        assertEquals(false, mli.hasEval());
        assertEquals(0., mli.getElapsedTime());
    }

    public void testEquals() {
        testEquals(true, "F5", "F5");
        testEquals(false, "F5", "D5");
        testEquals(true, "F5/1.0/2.0", "F5/1.0/2.0");
        testEquals(false, "F5/1.0/5.0", "F5/1.0/2.0");
        testEquals(false, "F5/3.0/2.0", "F5/1.0/2.0");
    }

    private void testEquals(boolean expected, String a, String b) {
        final OsMoveListItem ma = new OsMoveListItem(a);
        final OsMoveListItem mb = new OsMoveListItem(b);
        assertEquals(a + " == " + b, expected, ma.equals(mb));
    }

    public void testReflection() {
        final OsMoveListItem mli = new OsMoveListItem("F5/2/3");
        for (int i=0; i<8; i++) {
            final OsMoveListItem mlir = mli.reflect(i);
            assertEquals(mli.move.reflect(i), mlir.move);
            assertEquals(mli.getEval(), mlir.getEval());
            assertEquals(mli.getElapsedTime(), mlir.getElapsedTime());
        }
    }
}
