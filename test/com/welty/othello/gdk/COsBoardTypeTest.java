package com.welty.othello.gdk;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: Jun 19, 2009
 * Time: 9:27:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class COsBoardTypeTest extends TestCase {
    public void testEquals() {
        final COsBoardType a = new COsBoardType("10");
        final COsBoardType b = new COsBoardType("10");
        final COsBoardType c = new COsBoardType("8");
        final COsBoardType d = new COsBoardType("8");

        assertEquals(a, a);
        assertEquals(a, b);
        assertFalse(a.equals(c));
        assertFalse(a.equals(d));
    }
}
