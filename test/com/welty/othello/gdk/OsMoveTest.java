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
 * Test OsMove
 */
public class OsMoveTest extends TestCase {
    public void testConstructor() {
        final OsMove move = new OsMove(4, 3);
        assertEquals(4, move.row());
        assertEquals(3, move.col());
        assertFalse(move.equals(new Object()));
        assertFalse(move.equals(new OsMove(5, 3)));
        assertFalse(move.equals(new OsMove(4, 4)));
        assertEquals(move, new OsMove(4, 3));
        assertFalse(move.isPass());
    }

    public void testToString() {
        final OsMove pass = OsMove.PASS;
        assertEquals("PA", pass.toString());

        final OsMove move = new OsMove(4, 3);
        assertEquals("D5", move.toString());
    }

    public void testTextConstructor() {
        assertEquals("A3", new OsMove("A3").toString());
        assertEquals("A3", new OsMove("a3").toString());
        assertEquals("PA", new OsMove("PA").toString());
        assertEquals("PA", new OsMove("pAsS").toString());
    }

    public void testOfIos() throws Exception {
        assertEquals("A1", OsMove.ofIos(11).toString());
        assertEquals("A8", OsMove.ofIos(18).toString());
        assertEquals("negative numbers also work", "A1", OsMove.ofIos(-11).toString());
    }

    public void testReflect() {
        final OsMove move = new OsMove("F5");
        assertEquals("F5", move.reflect(0).toString());
        assertEquals("F4", move.reflect(1).toString());
        assertEquals("C5", move.reflect(2).toString());
        assertEquals("C4", move.reflect(3).toString());
        assertEquals("E6", move.reflect(4).toString());
        assertEquals("E3", move.reflect(5).toString());
        assertEquals("D6", move.reflect(6).toString());
        assertEquals("D3", move.reflect(7).toString());

        for (int i = 0; i < 8; i++) {
            assertEquals(OsMove.PASS, OsMove.PASS.reflect(i));
        }
    }

    public void testErrorHandling() {
        try {
            new OsMove("F");
            fail("should throw");
        } catch (IllegalArgumentException e) {
            assertTrue("need readable error message", e.getMessage().contains("ends"));
        }

        final OsBoardType bt = new OsBoardType("8");
        try {
            new OsMove(new CReader("F"), bt);
            fail("should throw");
        } catch (IllegalArgumentException e) {
            assertTrue("need readable error message", e.getMessage().contains("ends"));
        }
    }
}
