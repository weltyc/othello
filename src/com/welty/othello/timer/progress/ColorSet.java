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

package com.welty.othello.timer.progress;

import java.awt.*;

/**
 * <PRE>
* User: Chris
* Date: Jul 26, 2009
* Time: 1:31:47 PM
* </PRE>
*/
public class ColorSet {
    final Color dark;
    final Color light;
    final Color midrange;

    private ColorSet(Color dark, Color light) {
        this(dark, light, 3./7);
    }
    private ColorSet(Color dark, Color light, double darkFraction) {
        this(dark, light, mix(dark, light, darkFraction));
    }

    private ColorSet(Color dark, Color light, Color midrange) {
        this.dark = dark;
        this.light = light;
        this.midrange = midrange;
    }

    private static Color mix(Color a, Color b, double fractionA) {
        final int r = mix(a.getRed(), b.getRed(), fractionA);
        final int g = mix(a.getGreen(), b.getGreen(), fractionA);
        final int blue = mix(a.getBlue(), b.getBlue(), fractionA);
        return new Color(r,g,blue);
    }

    private static int mix(int a, int b, double fractionA) {
        return (int)(.5+b + fractionA*(a-b));
    }

    public static ColorSet cyan = new ColorSet(new Color(0, 0x80, 0x90), new Color(0xA0, 0xFF, 0xFF));
    public static ColorSet gold = new ColorSet(new Color(0x80, 0x70, 0), new Color(0xFF, 0xFF, 0x80));    
    public static ColorSet green = new ColorSet(new Color(0,0x80,0), new Color(0x60, 0xFF, 0x60), new Color(0x40, 0xD0, 0x40));
    public static ColorSet blue = new ColorSet(new Color(0,0, 0x80), new Color(0x80,0x80,0xFF), new Color(0,0,0xFF));
}
