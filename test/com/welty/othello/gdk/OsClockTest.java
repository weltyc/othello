package com.welty.othello.gdk;

import com.welty.othello.c.CReader;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: May 2, 2009
 * Time: 4:17:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class OsClockTest extends TestCase {
    public void testRead() {
        assertEquals(23, OsClock.ReadTime(new CReader("23")), 1e-10);
        assertEquals(83, OsClock.ReadTime(new CReader("1:23")), 1e-10);
        assertEquals(12 * 3600 + 83, OsClock.ReadTime(new CReader("12:01:23")), 1e-10);
        assertEquals(36 * 3600 + 83, OsClock.ReadTime(new CReader("1.12:01:23")), 1e-10);
    }

    public void testWrite() {
        assertEquals("23", writeTime(23));
        assertEquals("1:23", writeTime(83));
        assertEquals("12:01:23", writeTime(12 * 3600 + 83));
    }

    private String writeTime(int t) {
        StringBuilder sb = new StringBuilder();
        OsClock.WriteTime(sb, t);
        return sb.toString();
    }

    public void testIn() {
        testIn("23", 23, 0, 120);
        testIn("23/1/14:00", 23, 1, 14 * 60);
        testIn("23//0", 23, 0, 0);
        testIn("23/1:00", 23, 60, 120);
        testIn("23/1:01", 23, 61, 120);
        testIn("5:00//2:00", 300, 0, 120);
    }

    public void testOut() {
        assertEquals("5:00", new OsClock(5 * 60, 0, 120).toString());
        assertEquals("5:00//0", new OsClock(5 * 60, 0, 0).toString());
    }

    private void testIn(String text, int current, int tIncrement, int grace) {
        OsClock clock = new OsClock();
        clock.In(new CReader(text));
        assertEquals(new OsClock(current, tIncrement, grace), clock);
    }
}
