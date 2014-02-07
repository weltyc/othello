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
}
