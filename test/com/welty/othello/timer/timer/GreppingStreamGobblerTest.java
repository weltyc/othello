package com.welty.othello.timer.timer;

import junit.framework.TestCase;

/**
 * <PRE>
 * User: Chris
 * Date: Jul 24, 2009
 * Time: 10:47:48 PM
 * </PRE>
 */
public class GreppingStreamGobblerTest extends TestCase {
    public void testMnFromLine() {
        assertEquals(5.963, NtestInputStreamGobbler.mnFromLine("257,478,634   43.179s = 5.963Mn/s ; 244,560,089i, 12,918,545e => 0.053e/i"), 1e-10);
    }
}
