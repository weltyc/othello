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

import java.io.ByteArrayInputStream;

/**
 * <PRE>
 * User: Chris
 * Date: Jul 25, 2009
 * Time: 8:43:07 AM
 * </PRE>
 */
public class NtestErrorStreamGobblerTest extends TestCase {
    public void testProgress() throws InterruptedException {
        final ByteArrayInputStream is = new ByteArrayInputStream("Setup\nssssssssssss\nssssssssssss\n".getBytes());
        final NtestErrorStreamGobbler gobbler = new NtestErrorStreamGobbler(is);
        gobbler.run();
        assertEquals(1., gobbler.getEndgameTask().getProgress(), 1e-10);
        assertEquals(1., gobbler.getMidgameTask().getProgress(), 1e-10);
    }

    // Actual data from ntest does not have a newline after the final midgame 's'
    public void testProgress2() throws InterruptedException {
        final ByteArrayInputStream is = new ByteArrayInputStream("Setup\nssssssssssss\nssssssssssss".getBytes());
        final NtestErrorStreamGobbler gobbler = new NtestErrorStreamGobbler(is);
        gobbler.run();
        assertEquals(1., gobbler.getEndgameTask().getProgress(), 1e-10);
        assertEquals(1., gobbler.getMidgameTask().getProgress(), 1e-10);
    }
}
