package com.welty.othello.timer.progress;

import java.awt.*;

/**
 * <PRE>
 * User: Chris
 * Date: Jul 26, 2009
 * Time: 1:37:18 PM
 * </PRE>
 */
class MyMatteProgressBar extends MyProgressBar {

    public MyMatteProgressBar(ColorSet colorSet) {
        super(colorSet);
    }

    @Override protected void drawForeground(Graphics2D g2, int height, int x, int y) {
        final ColorSet cs = getColorSet();
        Paint fgPaint = new GradientPaint(0, 0, cs.midrange, 0, y, cs.light, true);
        g2.setPaint(fgPaint);
        g2.fillRect(0, 0, x, y+y);

        final Color foreground = getForeground();
        Paint darkPaint = new GradientPaint(0, y+y, cs.midrange, 0, height, foreground);
        g2.setPaint(darkPaint);
        g2.fillRect(0, y+y, x, height-(y+y));
    }
}
