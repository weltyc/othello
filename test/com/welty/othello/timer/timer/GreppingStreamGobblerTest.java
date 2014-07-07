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
