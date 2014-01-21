package com.welty.othello.timer.timer;

import junit.framework.TestCase;

/**
 * <PRE>
 * User: Chris
 * Date: Jul 23, 2009
 * Time: 9:32:44 PM
 * </PRE>
 */
public class OthelloTimerTest extends TestCase {
    public void testIsBad() {
        assertTrue(OthelloTimer.isBad(" "));
        assertTrue(OthelloTimer.isBad("Two Words"));
        assertTrue(OthelloTimer.isBad("Start "));
        assertTrue(OthelloTimer.isBad(" End"));
        assertTrue(OthelloTimer.isBad("*"));
        assertTrue(OthelloTimer.isBad("\\"));
        assertTrue(OthelloTimer.isBad("/"));
        assertTrue(OthelloTimer.isBad("?"));
        assertTrue(OthelloTimer.isBad("."));
        assertTrue(OthelloTimer.isBad("Foo.Bar"));
        assertTrue(OthelloTimer.isBad(""));
        assertFalse(OthelloTimer.isBad("FooBar"));
    }
}
