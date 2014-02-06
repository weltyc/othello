package com.welty.othello.timer.timer;

import com.welty.othello.timer.progress.ColorSet;
import com.welty.othello.timer.progress.ProgressBox;
import com.welty.othello.timer.progress.ProgressTask;

import javax.swing.*;
import java.util.Properties;

/**
 * <PRE>
 * User: Chris
 * Date: Jul 25, 2009
 * Time: 7:35:42 AM
 * </PRE>
 */
class ProgressWindow extends JFrame {
    private final ProgressBox endgameBox;
    private final ProgressBox midgameBox;

    public ProgressWindow(ProgressTask endgameTask, ProgressTask midgameTask) {
        super("Ntest Speed Test");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final Box box = Box.createVerticalBox();
        final Properties props = System.getProperties();
        box.add(systemLabel(props));
        endgameBox = ProgressBox.create("Endgame", endgameTask, ColorSet.green);
        box.add(endgameBox);
        midgameBox = ProgressBox.create("Midgame", midgameTask, ColorSet.gold);
        box.add(midgameBox);
        add(box);
        pack();
        setVisible(true);
    }

    private static Box systemLabel(Properties props) {
        final Box box = Box.createHorizontalBox();
        box.add(Box.createHorizontalGlue());
        final JLabel label = new JLabel(props.get("os.name") + "," + props.get("os.arch") + "," + props.get("os.version"));
        label.setHorizontalAlignment(SwingConstants.LEFT);
        box.add(label);
        box.add(Box.createHorizontalGlue());
        return box;
    }

    public void setEndgameResult(double result) {
        endgameBox.setResult(result);
    }

    public void setMidgameResult(double result) {
        midgameBox.setResult(result);
    }
}
