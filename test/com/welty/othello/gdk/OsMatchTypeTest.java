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
