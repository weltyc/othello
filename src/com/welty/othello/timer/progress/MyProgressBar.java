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

import javax.swing.*;
import java.awt.*;

/**
 * <PRE>
 * User: Chris
 * Date: Jan 1, 2006
 * Time: 1:05:35 AM
 * </PRE>
 */
class MyProgressBar extends JProgressBar {
    private final ColorSet colorSet;

    MyProgressBar(ColorSet colorSet) {
        this(0, 100, colorSet);
    }

    private MyProgressBar(int min, int max, ColorSet colorSet) {
        super(min, max);
        setBackground(Color.gray);
        setStringPainted(true);
        setForeground(colorSet.dark);
        this.colorSet = colorSet;
    }

    public void paint(Graphics g) {
        final Graphics2D g2 = (Graphics2D) g;
        g2.setFont(getFont());
        final int width = getWidth();
        final int height = getHeight();
        final double fractionComplete = getPercentComplete();
        final int x = (int) (width * fractionComplete);
        final int y = (int) (height * 0.30);

        // draw background
        final Color background = getBackground();
        Paint bgPaint = new GradientPaint(0, y + y - height, background, 0, y, Color.lightGray, true);
        g2.setPaint(bgPaint);
        g.fillRect(x, 0, width - x, height);

        // draw foreground
        drawForeground(g2, height, x, y);

        // draw text
        final Color foreground = getForeground();
        final int pct = (int) (fractionComplete * 100 + 0.5);
        final String text = pct + "%";
        final FontMetrics fm = g2.getFontMetrics();
        final int stringWidth = fm.stringWidth(text);
        final int xPos = (width - stringWidth) >> 1;
        final int yPos = (height + fm.getAscent() - 4) >> 1;
        g2.setColor(foreground);
        g2.drawString(text, xPos, yPos);
    }

    /**
     * draw foreground and return foreground color
     *
     * @param g2     graphics to draw on
     * @param height height of entire bar
     * @param x      x-value of right of bar
     * @param y      y-value where highlight is maximized, in pixels
     */
    void drawForeground(Graphics2D g2, int height, int x, int y) {
        final Color foreground = getForeground();
        Paint fgPaint = new GradientPaint(0, y + y - height, foreground, 0, y, colorSet.light, true);
        g2.setPaint(fgPaint);
        g2.fillRect(0, 0, x, height);
    }

    ColorSet getColorSet() {
        return colorSet;
    }
}
