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
