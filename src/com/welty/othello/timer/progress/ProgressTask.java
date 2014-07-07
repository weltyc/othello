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
 * A Task that notifies a ProgressListener when it has made progress towards completion
 *
 * <PRE>
 * User: Chris
 * Date: Jul 25, 2009
 * Time: 7:37:18 AM
 * </PRE>
 */
public interface ProgressTask {
    /**
     * Add a listener to be notified when progress occurs
     */
    void addListener(ProgressListener listener);

    /**
     * @return a fraction between 0 and 1 representing the fraction of the task that is complete
     */
    double getProgress();
}
