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

import com.welty.othello.core.OperatingSystem;
import com.welty.othello.timer.progress.ProgressCombiner;
import com.welty.othello.timer.progress.ProgressTask;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

/**
 * <PRE>
 * User: Chris
 * Date: Jul 23, 2009
 * Time: 8:10:07 PM
 * </PRE>
 */
public class OthelloTimer {
    public static final boolean isMac = OperatingSystem.os == OperatingSystem.MACINTOSH;
    private static final String COMMAND = isMac ? "./ntest t" : "o.exe t";

    public static void main(String[] args) {
        final boolean useGui = args.length < 3;
        try {
            final Inputs inputs;
            if (useGui) {
                inputs = InputsDialog.getInputs();
            } else {
                inputs = new Inputs(getNCopies(args), getMachineName(args), getProcessorType(args));
            }
            run(inputs);
        } catch (Exception e) {
            if (useGui) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                System.err.println(e.getMessage());
            }
        }
    }

    private static void run(Inputs inputs) throws IOException, InterruptedException {
        System.out.println("Starting othello endgame timing test for " + inputs.machineName + " with " + inputs.nCopies + " copies");
        final ArrayList<TimerProcess> processes = new ArrayList<>();
        for (int i = 0; i < inputs.nCopies; i++) {
            final TimerProcess process = TimerProcess.execGrep(COMMAND);
            processes.add(process);
        }

        ProgressWindow progressWindow = createProgressWindow(processes);

        double endgameMn = 0;
        double midgameMn = 0;

        for (TimerProcess process : processes) {
            process.process.waitFor();
            final NtestInputStreamGobbler gobbler = process.inputGobbler;
            endgameMn += gobbler.getEndgameResult();
            midgameMn += gobbler.getMidgameResult();
        }

        progressWindow.setEndgameResult(endgameMn);
        progressWindow.setMidgameResult(midgameMn);
        outputResults(inputs, endgameMn, midgameMn);
    }

    private static void outputResults(Inputs inputs, double endgameMn, double midgameMn) {
        final File file = new File("speeds.csv");
        final boolean printHeaderRow = !file.exists();
        try {
            final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
            if (printHeaderRow) {
                out.println("processor,manufacturer,# copies,midgame speed, endgame speed");
            }
            out.format("%s,%s,%d,%.1f,%.1f%n", inputs.processorType, inputs.machineName, inputs.nCopies, midgameMn, endgameMn);
            out.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Unable to write score to file : " + e.getMessage());
        }
    }

    private static ProgressWindow createProgressWindow(ArrayList<TimerProcess> processes) {
        ProgressCombiner endgameCombiner = createCombiner(processes, true);
        ProgressCombiner midgameCombiner = createCombiner(processes, false);
        return new ProgressWindow(endgameCombiner, midgameCombiner);
    }

    private static ProgressCombiner createCombiner(ArrayList<TimerProcess> processes, boolean isEndgame) {
        final int nCopies = processes.size();
        final ProgressTask[] tasks = new ProgressTask[nCopies];
        for (int i = 0; i < nCopies; i++) {
            final NtestErrorStreamGobbler eg = processes.get(i).errorGobbler;
            tasks[i] = isEndgame ? eg.getEndgameTask() : eg.getMidgameTask();
        }
        return new ProgressCombiner(tasks);
    }

    private static String getMachineName(String[] args) {
        if (isBad(args[1])) {
            throw new IllegalArgumentException("second argument (machine name) must not contain spaces or special characters, was: '" + args[1] + "'");
        }
        return args[1];
    }

    private static String getProcessorType(String[] args) {
        if (isBad(args[2])) {
            throw new IllegalArgumentException("third argument (processor type) must not contain spaces or special characters, was: '" + args[1] + "'");
        }
        return args[2];
    }

    static boolean isBad(String machineName) {
        return machineName.matches(".*[ *.?\\\\/].*") || machineName.isEmpty();
    }

    private static int getNCopies(String[] args) {
        try {
            final int nCopies = args.length > 0 ? Integer.parseInt(args[0]) : 1;
            if (nCopies > 0) {
                return nCopies;
            }
        } catch (NumberFormatException e) {
            // throw NCopiesException below
        }
        throw new IllegalArgumentException("first argument (number of copies) must be an integer >= 0, was: '" + args[0] + "'");
    }
}
