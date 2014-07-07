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
