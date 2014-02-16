package com.welty.othello.core;

import junit.framework.TestCase;

public class EngineeringTest extends TestCase {
    public void testNDigits() {
        assertEquals(3, Engineering.calculateNDigits(999));
        assertEquals(4, Engineering.calculateNDigits(1000));
        assertEquals(1, Engineering.calculateNDigits(1));
        assertEquals(1, Engineering.calculateNDigits(0));
    }
    public void testLongWithDetails() {
        testLongWithDetails(" 0 ", 0, 0, 0, 2);
        testLongWithDetails("  0 ", 0, 0, 0, 3);
        testLongWithDetails("  1 ", 1, 0, 0, 3);
        testLongWithDetails(" -1 ", -1, 0, 0, 3);
        testLongWithDetails("-1 ", -1, 0, 0, 1);
        testLongWithDetails(" 10 ", 10, 0, 0, 3);
        testLongWithDetails("0.01 k", 10, 1, 2, 1);
        testLongWithDetails("0.01 k", 14, 1, 2, 1);
        testLongWithDetails("0.02 k", 16, 1, 2, 1);
        testLongWithDetails("-0.02 k", -16, 1, 2, 2);
        testLongWithDetails(" 0.02 k", 16, 1, 2, 2);
        testLongWithDetails("-0.02 k", -16, 1, 2, 1);
        testLongWithDetails("12.3 k", 12345, 1, 1, 2);
    }

    private void testLongWithDetails(String expected, long value, int prefix, int nPost, int nPre) {
        assertEquals(value+", prefix="+prefix+", nPost="+nPost + ", nPre=" + nPre, expected, Engineering.formatLong(value, prefix, nPost, nPre));
    }

    public void testLongDefault() {
        testLongDefault("0 ", (long) 0);
        testLongDefault("3 ", (long) 3);
        testLongDefault("-3 ", (long) -3);
        testLongDefault("12 ", (long) 12);
        testLongDefault("123 ", (long) 123);
        testLongDefault("1.23 k", (long) 1234);
        testLongDefault("12.3 k", (long) 12345);
        testLongDefault("99.9 k", (long) 99949);
        // behaviour of 99500 intentionally unspecified - can round either way, we don't care.
        testLongDefault("100 k", (long) 99951);
        testLongDefault("100 k", (long) 99999);
        testLongDefault("100 k", (long) 100000);
        testLongDefault("123 k", (long) 123456);
        testLongDefault("1.23 M", (long) 1234568);
        testLongDefault("12.3 M", (long) 12345678);
        testLongDefault("123 M", (long) 123456789);

        // Rounding
        testLongDefault("99.9 M", (long) 99949999);
        // behaviour of 99500000 intentionally unspecified - can round either way, we don't care.
        testLongDefault("100 M", (long) 99950001);
    }


    private static void testLongDefault(String expected, long input) {
        assertEquals(""+input, expected, Engineering.compactFormat(input));
    }

    public void testDouble() {
        testDoubleDefault("0.00 ", 0);
        testDoubleDefault("3.00 ", 3);
        testDoubleDefault("12.0 ", 12);
        testDoubleDefault("123 ", 123);
        testDoubleDefault("1.23 k", 1234);
        testDoubleDefault("12.3 k", 12344);
        testDoubleDefault("99.9 k", 99900);
        testDoubleDefault("100 k", 100000);
        testDoubleDefault("123 k", 123450);
        testDoubleDefault("999 k", 999400);
        testDoubleDefault("1.00 M", 999500);
        testDoubleDefault("1.23 M", 1230000);
        testDoubleDefault("12.3 M", 12340000);
        testDoubleDefault("123 M", 123450000);

        testDoubleDefault("1.00 ", 0.99999);
        testDoubleDefault("100 m", 0.1);
        testDoubleDefault("100 m", 0.09996);
        testDoubleDefault("99.9 m", 0.09994);
        testDoubleDefault("10.0 m", 0.01);
        testDoubleDefault("10.0 m", 0.009995);
        testDoubleDefault("9.99 m", 0.009994);
        testDoubleDefault("1.00 m", 0.001);
        testDoubleDefault("1.00 m", 0.0009996);
        testDoubleDefault("999 μ", 0.0009994);
        testDoubleDefault("1.00 μ", 0.000001);

        testDoubleDefault("-999 m", -0.999);

    }


    private static void testDoubleDefault(String expected, double input) {
        assertEquals(expected, Engineering.compactFormat(input));
    }

    public void testDoubleWithDetails() {
        testDoubleWithDetails(" 0.00 ", 0., 0, 2, 2);
        testDoubleWithDetails(" 0 ", 0., 0, 0, 2);
        testDoubleWithDetails("  0 ", 0., 0, 0, 3);
        testDoubleWithDetails("1.23 m", .001234, -1, 2, 0);
        testDoubleWithDetails(" 1.00 ", 1., 0, 2, 2);
        testDoubleWithDetails("  1 ", 1., 0, 0, 3);
        testDoubleWithDetails(" -1 ", -1., 0, 0, 3);
        testDoubleWithDetails("-1 ", -1., 0, 0, 1);
        testDoubleWithDetails(" 10 ", 10., 0, 0, 3);
        testDoubleWithDetails("0.01 k", 10., 1, 2, 1);
        testDoubleWithDetails("0.01 k", 14., 1, 2, 1);
        testDoubleWithDetails("0.02 k", 16., 1, 2, 1);
        testDoubleWithDetails("-0.02 k", -16., 1, 2, 2);
        testDoubleWithDetails(" 0.02 k", 16., 1, 2, 2);
        testDoubleWithDetails("-0.02 k", -16., 1, 2, 1);
        testDoubleWithDetails("12.3 k", 12345., 1, 1, 2);
    }

    private void testDoubleWithDetails(String expected, double value, int prefix, int nPost, int nPre) {
        assertEquals(value + ", prefix=" + prefix + ", nPost=" + nPost + ", nPre=" + nPre, expected, Engineering.formatDouble(value, prefix, nPost, nPre));
    }
}
