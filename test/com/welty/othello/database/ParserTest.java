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

package com.welty.othello.database;

import junit.framework.TestCase;

import java.io.EOFException;

/**
 */
@SuppressWarnings("JavaDoc")
public class ParserTest extends TestCase {
    public void testReadPositiveInt() throws Exception {
        testReadPositiveInt("1", 1, 1);
        testReadPositiveInt("1 ", 1, 1);
        testReadPositiveInt("1ss", 1, 1);
        testReadPositiveInt("12ss", 12, 2);
        testReadPositiveIntThrows(" 12");
        testReadPositiveIntThrows("");
    }

    private void testReadPositiveIntThrows(String s) {
        final Parser parser = new Parser(s);
        try {
            parser.readPositiveInt();
            fail("should throw : " + s);
        }
        catch(Exception e) {
            // expected
        }
    }

    private void testReadPositiveInt(String s, int expected, int loc) throws EOFException {
        final Parser parser = new Parser(s);
        final int actual = parser.readPositiveInt();
        assertEquals("int for " + s, expected, actual);
        assertEquals("terminal location for " + s, loc, parser.getLoc());
    }

    public void testSkipWs() throws Exception {
        testSkipWs("  ", 2);
        testSkipWs("foo",0);
        testSkipWs("", 0);
        testSkipWs(" f", 1);
    }

    private void testSkipWs(String s, int expectedLoc) {
        final Parser parser = new Parser(s);
        parser.skipWs();
        assertEquals(expectedLoc, parser.getLoc());
    }

    public void testNotEof() throws Exception {
        final Parser parser = new Parser("");
        assertFalse(parser.notEof());

        final Parser p2 = new Parser(" ");
        assertTrue(p2.notEof());
    }

    public void testPeek() throws Exception {
        final Parser p = new Parser("foo");
        assertEquals('f', p.peek());
        assertEquals(0, p.getLoc());
    }

    public void testExpect() throws Exception {
        final Parser p = new Parser(";foo");
        p.expect(";");
        assertEquals(1, p.getLoc());
        p.expect("f");
        assertEquals(2, p.getLoc());
        try {
            p.expect("gobstopper");
            fail("should throw, unexpected text");
        }   catch(EOFException e) {
            // expected
        }
        assertEquals(2, p.getLoc());
        try {
            p.expect("g");
            fail("should throw, unexpected text");
        }   catch(IllegalStateException e) {
            // expected
        }
        assertEquals(2, p.getLoc());


        final Parser foo = new Parser("foo");
        foo.expect("foo");
        assertEquals(3, foo.getLoc());
    }

    public void testReadUntil() throws Exception {
        final Parser foobar = new Parser("foobar");
        assertEquals("foo", foobar.readUntil('b'));
        assertEquals("ba", foobar.readUntil('r'));
        try {
            foobar.readUntil('z');
            fail("should throw, no 'z'");
        }
        catch(EOFException e) {
            // expected
        }
    }
}
