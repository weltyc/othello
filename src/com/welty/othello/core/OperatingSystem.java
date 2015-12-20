/*
 * Copyright (c) 2014 Chris Welty.
 *
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * For the license, see <http://www.gnu.org/licenses/gpl.html>.
 */

package com.welty.othello.core;

public enum OperatingSystem {
    WINDOWS, MACINTOSH, LINUX, UNKNOWN;

    public static final OperatingSystem os = detectOs();

    private static OperatingSystem detectOs() {
        String name = System.getProperty("os.name");
        if (name.startsWith("Mac OS")) {
            return MACINTOSH;
        } else if (name.equals("Linux")) {
            return LINUX;
        } else if (name.contains("Windows")) {
            return WINDOWS;
        }
        else {
            return UNKNOWN;
        }
    }

    public boolean isMacintosh() {
        return os == MACINTOSH;
    }
}
