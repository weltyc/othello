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

import com.welty.othello.timer.gobbler.CharBufferedStreamGobbler;
import com.welty.othello.timer.progress.AbstractProgressTask;
import com.welty.othello.timer.progress.ProgressTask;

import java.io.InputStream;

/**
 * <PRE>
 * User: Chris
 * Date: Jul 24, 2009
 * Time: 11:04:50 PM
 * </PRE>
 */
class NtestErrorStreamGobbler extends CharBufferedStreamGobbler {
    private boolean mayProgress = true; // true if every char since the last newline has been an 's'
    private MyProgressTask currentTask;
    private final MyProgressTask endgameTask;
    private final MyProgressTask midgameTask;

    public NtestErrorStreamGobbler(InputStream is) {
        super(is);
        endgameTask = new MyProgressTask();
        midgameTask = new MyProgressTask();
        currentTask = endgameTask;
    }

    public void close() {
        // make sure we know we're done
        handleChar('\n');
    }

    protected void handleChar(char c) {
        System.err.print(c);
        if (c=='\n') {
            if (currentTask.progress ==12) {
                currentTask.updateProgress();
                currentTask = midgameTask;
            }
            mayProgress = currentTask.progress ==0;
        }
        else if (c=='s') {
            if (mayProgress) {
                currentTask.updateProgress();
            }
        }
        else {
            mayProgress = false;
        }
    }

    public ProgressTask getEndgameTask() {
        return endgameTask;
    }

    public ProgressTask getMidgameTask() {
        return midgameTask;
    }

    private class MyProgressTask extends AbstractProgressTask {
        int progress;

        private void updateProgress() {
            progress++;
            sendProgress();
        }

        public double getProgress() {
           return progress/13.;
        }
    }
}
