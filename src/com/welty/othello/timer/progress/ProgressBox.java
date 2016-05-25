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

import com.orbanova.common.jsb.JSwingBuilder;

import javax.swing.*;
import java.awt.*;

/**
 * A ProgressBox is a thread-safe alternative to Java's Progress Bar.
 * <p>
 * Create one like this:
 * <pre>
 *     ProgressBox.create("Endgame", endgameTask, ColorSet.green);
 * </pre>
 * To display a window showing the progress box,
 * <pre>
 *     ProgressBox.create("Endgame", endgameTask, ColorSet.green).displayWindow();
 * </pre>
 */
public class ProgressBox extends JPanel implements ProgressListener {
    private final MyProgressBar progressBar;
    private final ProgressTask task;
    private final JLabel resultLabel;

    public static ProgressBox create(String text, ProgressTask task, ColorSet colorSet) {
        final ProgressBox box = new ProgressBox(text, task, colorSet);
        task.addListener(box);
        return box;
    }

    /**
     * Construct using create() so this is added as a listener to task
     */
    private ProgressBox(String text, ProgressTask task, ColorSet colorSet) {
        this.task = task;
        progressBar = new MyMatteProgressBar(colorSet);
        final Box box = Box.createHorizontalBox();
        box.add(createLabel(text, 80));
        box.add(progressBar);
        resultLabel = createLabel("", 80);
        resultLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        box.add(resultLabel);
        add(box);
    }

    private static JLabel createLabel(String text, int width) {
        final JLabel label = new JLabel(text);
        label.setPreferredSize(new Dimension(width,20));
        label.setVerticalAlignment(SwingConstants.TOP);
        return label;
    }

    public void handleProgress() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                progressBar.setValue((int) (task.getProgress() * 100));
            }
        });
    }

    public void setResult(final double result) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                resultLabel.setText(String.format("%.1f Mn/s", result));
            }
        });
    }

    /**
     * Create a window and display this progress box in it.
     * <p>
     * The Window is created as Dispose on close.
     *
     * @return The window.
     */
    @SuppressWarnings("unused")
    public JFrame displayWindow() {
        return JSwingBuilder.frame("", WindowConstants.DISPOSE_ON_CLOSE, this);
    }
}
