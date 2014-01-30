package com.welty.othello.core;

import junit.framework.TestCase;

public class EngineeringTest extends TestCase {

    public void testEngineering() {
        testEngineeringLong("     0  ", (long) 0);
        testEngineeringLong("     3  ", (long) 3);
        testEngineeringLong("    12  ", (long) 12);
        testEngineeringLong("   123  ", (long) 123);
        testEngineeringLong(" 1,234  ", (long) 1234);
        testEngineeringLong("12,345  ", (long) 12345);
        testEngineeringLong("99,999  ", (long) 99999);
        testEngineeringLong("   100 k", (long) 100000);
        testEngineeringLong("   123 k", (long) 123456);
        testEngineeringLong(" 1,234 k", (long) 1234568);
        testEngineeringLong("12,345 k", (long) 12345678);
        testEngineeringLong("   123 M", (long) 123456789);
    }


    private static void testEngineeringLong(String expected, long input) {
        assertEquals(expected, Engineering.engineeringLong(input));
    }

    public void testEngineeringDouble() {
        testEngineeringDouble("   0.00  ", 0);
        testEngineeringDouble("   3.00  ",  3);
        testEngineeringDouble("  12.00  ",  12);
        testEngineeringDouble(" 123.00  ",  123);
        testEngineeringDouble("   1.23 k",  1234);
        testEngineeringDouble("  12.34 k",  12344);
        testEngineeringDouble("  99.99 k",  99990);
        testEngineeringDouble(" 100.00 k",  100000);
        testEngineeringDouble(" 123.45 k",  123450);
        testEngineeringDouble(" 999.99 k",  999994);
        testEngineeringDouble("   1.00 M",  999995);
        testEngineeringDouble("   1.23 M",  1230000);
        testEngineeringDouble("  12.34 M",  12340000);
        testEngineeringDouble(" 123.45 M",  123450000);

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
