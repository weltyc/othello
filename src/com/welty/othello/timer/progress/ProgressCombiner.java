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

/**
 * A ProgressTask whose progress fraction is the average of the progress fractions of a number of subtasks.
 *
 * <PRE>
 * User: Chris
 * Date: Jul 25, 2009
 * Time: 7:39:19 AM
 * </PRE>
 */
public class ProgressCombiner extends AbstractProgressTask implements ProgressListener {
    private final ProgressTask[] tasks;

    /**
     * Construct a ProgressTask whose progress fraction is the average of the progress fractions of a number of subtasks.
     *
     * @param tasks the subtasks
     */
    public ProgressCombiner(ProgressTask... tasks) {
        this.tasks = tasks;
        for (ProgressTask task : tasks) {
            task.addListener(this);
        }
    }

    public double getProgress() {
        double progress = 0;
        for (ProgressTask task : tasks) {
            progress+=task.getProgress();
        }
        return progress/tasks.length;
    }

    public void handleProgress() {
        sendProgress();
    }
}
