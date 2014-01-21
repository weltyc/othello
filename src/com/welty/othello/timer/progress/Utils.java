package com.welty.othello.timer.progress;

import javax.swing.*;
import java.awt.*;

/**
 * <PRE>
 * User: Chris
 * Date: Jul 25, 2009
 * Time: 10:57:31 AM
 * </PRE>
 */
class Utils {
    public static JLabel createLabel(String text, int width) {
        final JLabel label = new JLabel(text);
        label.setPreferredSize(new Dimension(width,20));
        label.setVerticalAlignment(SwingConstants.TOP);
        return label;
    }
}
