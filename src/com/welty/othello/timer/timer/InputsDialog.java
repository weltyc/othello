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

package com.welty.othello.timer.timer;

import javax.swing.*;
import java.util.prefs.Preferences;

/**
 * <PRE>
 * User: Chris
 * Date: Jul 25, 2009
 * Time: 10:53:27 AM
 * </PRE>
 */
class InputsDialog extends JFrame {
    static Inputs getInputs() {
        final String processorType = getInput("Processor type (e.g. 'i920')", "OthelloTimer/ProcessorType");
        final String machineName = getInput("Machine name (e.g. 'Dell Inspiron')", "OthelloTimer/MachineName");
        final int nProcesses = getNProcesses();
        return new Inputs(nProcesses, machineName, processorType);
    }

    private static String getInput(String message, String key) {
        final Preferences prefs = Preferences.userRoot();
        final String processorType = JOptionPane.showInputDialog(message, prefs.get(key,""));
        prefs.put(key,processorType);
        return processorType;
    }

    private static int getNProcesses() {
        final Preferences prefs = Preferences.userRoot();
        final String key = "OthelloTimer/NProcesses";
        final String def = prefs.get(key, "");
        while (true) {
            try  {
                final int i = Integer.parseInt(JOptionPane.showInputDialog("# processes (e.g. '1')", def));
                if (i>0) {
                    prefs.put(key,Integer.toString(i));
                    return i;
                }
            }
            catch(NumberFormatException e) {
                // fall through and display error message
            }
            JOptionPane.showMessageDialog(null, "Number of processes must be an integer greater than 0");
        }
    }
}
