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
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: May 2, 2009
 * Time: 7:32:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class OsMatchTypeTest extends TestCase {
    public void testCreate() {
        final CReader cReader = new CReader("8");
        final OsMatchType type = new OsMatchType(cReader);
        assertEquals("8", type.bt.toString());
        assertEquals(OsMatchType.Color.STANDARD, type.getColor());
    }
}
