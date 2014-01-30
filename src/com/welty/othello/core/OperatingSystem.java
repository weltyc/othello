package com.welty.othello.core;

public enum OperatingSystem {
    WINDOWS, MACINTOSH, LINUX, UNKNOWN;

    public static final OperatingSystem os = detectOs();

    private static OperatingSystem detectOs() {
        if (System.getProperty("os.name").startsWith("Mac OS")) {
            return MACINTOSH;
        }
        else {
            return UNKNOWN;
        }
    }

    public boolean isMacintosh() {
        return os == MACINTOSH;
    }
}
