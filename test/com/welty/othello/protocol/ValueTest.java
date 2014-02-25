package com.welty.othello.protocol;

import junit.framework.TestCase;

/**
 */
public class ValueTest extends TestCase {
    public void testToString() {
        final Value value = new Value(3.999999f);
        assertEquals("4.00", value.toString());
    }
}
