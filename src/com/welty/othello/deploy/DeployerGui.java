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

package com.welty.othello.deploy;


import com.orbanova.common.jsb.RadioButtonGrid;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.orbanova.common.jsb.JSwingBuilder.*;


/**
 * <PRE>
 * User: chris
 * Date: 10/10/11
 * </PRE>
 */
public final class DeployerGui {

    private final JTextField nCores = textField("" + Runtime.getRuntime().availableProcessors());
    private final JTextField othelloSourceDir = textField("U:\\dev\\n64");
    private final JTextField mergeBookSource = textField("\\\\Buffalo\\dev\\oth1\\src\\resource\\coefficients\\merge.book");
    private Runnable deployMethod;
    private final JFrame frame;


    DeployerGui() throws UnknownHostException {
        //////////////////////////////////
        // Deployment method buttons
        //////////////////////////////////

        final JRadioButton fullDeployButton = deployChoice("Full (will overwrite book)",
                "<html>Copies the entire source directory to c:/dev/oth{n}, where n goes from 1 to nCores.<p/>" +
                        " Also creates n startup scripts, so that each instance of " +
                        "NTest runs automatically at startup.</html>",
                true, true, new Runnable() {
            @Override public void run() {
                try {
                    if (Files.exists(Paths.get("c:\\dev\\oth1")) && !confirmFull()) {
                        return;
                    }
                    final File sourceDir = getOthelloSourceDir();
                    if (!sourceDir.exists()) {
                        throw new IllegalStateException("Unable to find source directory " + sourceDir);
                    }
                    Deployer.fullDeploy(getNCores(), sourceDir, getMergeBookSource());
                } catch (Exception e) {
                    error(e);
                }
            }
        });
        final JRadioButton deployExeButton = deployChoice("Executable only",
                "Copies the ntest executable file from {othelloSourceDirectory}/o1.exe to c:/dev/oth{n}/o{n}.exe, where n goes from 1 to nCores.",
                false, true, new Runnable() {
            @Override public void run() {
                try {
                    Deployer.deployExe(getNCores(), getOthelloSourceDir());
                } catch (Exception e) {
                    error(e);
                }
            }
        });
        final JRadioButton deployRunScriptButton = deployChoice("Run Script only",
                "<html>Creates a new run.bat for each ntest instance. <p>This is usually run after changing the code" +
                        " that creates run.bat.</html>", true, false, new Runnable() {
            @Override public void run() {
                try {
                    Deployer.deployRunScripts(getNCores(), getMergeBookSource());
                } catch (Exception e) {
                    error(e);
                }
            }
        });

        final JPanel deploymentMethodChoices = new RadioButtonGrid("Deployment Method", 99, fullDeployButton, deployExeButton, deployRunScriptButton);
        fullDeployButton.getAction().actionPerformed(null); // enable choices for first radio button item
        //////////////////////////////////////
        // Action Buttons
        //////////////////////////////////////

        Action deploy = new AbstractAction("Deploy") {
            public void actionPerformed(ActionEvent e) {
                try {
                    deployMethod.run();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, ex.toString(), "Deployer Error", JOptionPane.ERROR_MESSAGE);
                }
                frame.dispose();
            }
        };

        Action cancel = new AbstractAction("Cancel") {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        };

        /////////////////////////////////////
        // Overall layout
        /////////////////////////////////////

        frame = frame("Ntest Deployer", JFrame.EXIT_ON_CLOSE, true,
                vBox(
                        deploymentMethodChoices,
                        controlGrid(
                                control("# Cores", nCores),
                                control("Merge Book source", mergeBookSource),
                                control("Othello source dir", othelloSourceDir)
                        ),
                        buttonBar(true,
                                button(deploy),
                                button(cancel)
                        )
                ));
    }

    /**
     * Display a message box asking the user whether he truly wishes to overwrite an existing deployment.
     *
     * @return true if the user wishes to overwrite.
     */
    private boolean confirmFull() {
        final String warning = "NTest has already been deployed to this machine. This will erase all existing data. Overwrite existing deployment?";
        final int option = JOptionPane.showConfirmDialog(frame, warning, "Full deploy", JOptionPane.OK_CANCEL_OPTION);
        return option == JOptionPane.OK_OPTION;
    }

    private File getMergeBookSource() {
        return new File(mergeBookSource.getText());
    }

    private File getOthelloSourceDir() {
        return new File(othelloSourceDir.getText());
    }

    private int getNCores() {
        return Integer.parseInt(nCores.getText());
    }

    private void error(Exception e) {
        JOptionPane.showMessageDialog(frame, e.toString(), "Deployer Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * @param title          button title
     * @param needsMergeBook true if deployment requires the merge book location
     * @param needsSourceDir true if deployment requires the source directory
     * @param runnable       Runnable that will be executed when the user clicks the "Deploy" button
     * @return the button
     */
    private JRadioButton deployChoice(String title, final String toolTipText, final boolean needsMergeBook, final boolean needsSourceDir
            , final Runnable runnable) {
        final AbstractAction action = new AbstractAction(title) {
            @Override public void actionPerformed(ActionEvent e) {
                mergeBookSource.setEnabled(needsMergeBook);
                othelloSourceDir.setEnabled(needsSourceDir);
                deployMethod = runnable;
            }
        };
        action.putValue(Action.SHORT_DESCRIPTION, toolTipText);
        return radioButton(action);
    }

    /**
     * Display a GUI allowing the user to deploy Ntest
     *
     * @param args ignored
     */
    public static void main(String[] args) throws UnknownHostException {
        new DeployerGui();
    }
}
