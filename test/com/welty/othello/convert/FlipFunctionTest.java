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

package com.welty.othello.convert;

import junit.framework.TestCase;

import java.util.regex.Matcher;

/**
 * <PRE>
 * User: chris
 * Date: 3/9/11
 * Time: 5:10 PM
 * </PRE>
 */
public class FlipFunctionTest extends TestCase {
    public void testPattern() {
        final Matcher matcher = FlipFunction.pattern.matcher("\tbb.mover.u4s[0]^=0x1;");
        assertTrue(matcher.matches());
        assertEquals("mover", matcher.group(1));
        assertEquals("0", matcher.group(2));
        assertEquals("1", matcher.group(3));
    }
}
