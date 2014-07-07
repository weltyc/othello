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
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: Jun 26, 2009
 * Time: 9:57:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class COsMoveListTest extends TestCase {
    public void testToMoveListString() {
        COsMoveList ml = new COsMoveList();
        ml.add(new OsMoveListItem("F5/3.2/1"));
        ml.add(new OsMoveListItem("D6"));
        ml.add(new OsMoveListItem("C3/.01"));
        assertEquals("F5D6C3", ml.toMoveListString());
    }

    public void testTruncation() {
        COsMoveList ml = new COsMoveList();
        ml.add(new OsMoveListItem("F5/3.2/1"));
        ml.add(new OsMoveListItem("D6"));
        ml.add(new OsMoveListItem("C3/.01"));

        assertEquals(0, new COsMoveList(ml, 0).size());
        assertEquals(1, new COsMoveList(ml, 1).size());
        assertEquals(2, new COsMoveList(ml, 2).size());
        assertEquals(3, new COsMoveList(ml, 3).size());
    }
}
