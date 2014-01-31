package com.welty.othello.core;

import junit.framework.TestCase;

public class EngineeringTest extends TestCase {
    public void testNDigits() {
        assertEquals(3, Engineering.calculateNDigits(999));
        assertEquals(4, Engineering.calculateNDigits(1000));
        assertEquals(1, Engineering.calculateNDigits(1));
        assertEquals(1, Engineering.calculateNDigits(0));
    }
    public void testEngineeringWithLsp() {
        testFormatLong(" 0 ", 0, 0, 0, 2);
        testFormatLong("  0 ", 0, 0, 0, 3);
        testFormatLong("  1 ", 1, 0, 0, 3);
        testFormatLong(" -1 ", -1, 0, 0, 3);
        testFormatLong("-1 ", -1, 0, 0, 1);
        testFormatLong(" 10 ", 10, 0, 0, 3);
        testFormatLong("0.01 k", 10, 1, 2, 1);
        testFormatLong("0.01 k", 14, 1, 2, 1);
        testFormatLong("0.02 k", 16, 1, 2, 1);
        testFormatLong("-0.02 k", -16, 1, 2, 2);
        testFormatLong(" 0.02 k", 16, 1, 2, 2);
        testFormatLong("-0.02 k", -16, 1, 2, 1);
        testFormatLong("12.3 k", 12345, 1, 1, 2);
    }

    private void testFormatLong(String expected, long value, int prefix, int nPost, int nPre) {
        assertEquals(value+", prefix="+prefix+", nPost="+nPost + ", nPre=" + nPre, expected, Engineering.formatLong(value, prefix, nPost, nPre));
    }

    public void testEngineering() {
        testEngineeringLong("0 ", (long) 0);
        testEngineeringLong("3 ", (long) 3);
        testEngineeringLong("-3 ", (long) -3);
        testEngineeringLong("12 ", (long) 12);
        testEngineeringLong("123 ", (long) 123);
        testEngineeringLong("1.23 k", (long) 1234);
        testEngineeringLong("12.3 k", (long) 12345);
        testEngineeringLong("99.9 k", (long) 99949);
        // behaviour of 99500 intentionally unspecified - can round either way, we don't care.
        testEngineeringLong("100 k", (long) 99951);
        testEngineeringLong("100 k", (long) 99999);
        testEngineeringLong("100 k", (long) 100000);
        testEngineeringLong("123 k", (long) 123456);
        testEngineeringLong("1.23 M", (long) 1234568);
        testEngineeringLong("12.3 M", (long) 12345678);
        testEngineeringLong("123 M", (long) 123456789);

        // Rounding
        testEngineeringLong("99.9 M", (long) 99949999);
        // behaviour of 99500000 intentionally unspecified - can round either way, we don't care.
        testEngineeringLong("100 M", (long) 99950001);
    }


    private static void testEngineeringLong(String expected, long input) {
        assertEquals(""+input, expected, Engineering.formatLong(input));
    }

    public void testEngineeringDouble() {
        testEngineeringDouble("   0.00  ", 0);
        testEngineeringDouble("   3.00  ", 3);
        testEngineeringDouble("  12.00  ", 12);
        testEngineeringDouble(" 123.00  ", 123);
        testEngineeringDouble("   1.23 k", 1234);
        testEngineeringDouble("  12.34 k", 12344);
        testEngineeringDouble("  99.99 k", 99990);
        testEngineeringDouble(" 100.00 k", 100000);
        testEngineeringDouble(" 123.45 k", 123450);
        testEngineeringDouble(" 999.99 k", 999994);
        testEngineeringDouble("   1.00 M", 999995);
        testEngineeringDouble("   1.23 M", 1230000);
        testEngineeringDouble("  12.34 M", 12340000);
        testEngineeringDouble(" 123.45 M", 123450000);

        testEngineeringDouble(" 999.99 m", 0.99999);
        testEngineeringDouble(" 100.00 m", 0.1);
        testEngineeringDouble(" 100.00 m", 0.099995);
        testEngineeringDouble("  99.99 m", 0.099994);
        testEngineeringDouble("  10.00 m", 0.01);
        testEngineeringDouble("  10.00 m", 0.009995);
        testEngineeringDouble("   9.99 m", 0.009994);
        testEngineeringDouble("   1.00 m", 0.001);
        testEngineeringDouble("   1.00 m", 0.000999995);
        testEngineeringDouble(" 999.99 μ", 0.000999994);
        testEngineeringDouble("   1.00 μ", 0.000001);

        testEngineeringDouble("-999.99 m", -0.99999);

    }


    private static void testEngineeringDouble(String expected, double input) {
        assertEquals(expected, Engineering.engineeringDouble(input));
    }

}
