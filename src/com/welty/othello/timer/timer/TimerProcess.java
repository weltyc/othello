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

import java.io.IOException;

/**
 * <PRE>
 * User: Chris
 * Date: Jul 23, 2009
 * Time: 8:10:25 PM
 * </PRE>
 */
class TimerProcess {
    final Process process;
    final NtestInputStreamGobbler inputGobbler;
    final NtestErrorStreamGobbler errorGobbler;

    private TimerProcess(Process process, NtestInputStreamGobbler inputGobbler, NtestErrorStreamGobbler errorGobbler) {
        this.process = process;
        this.inputGobbler = inputGobbler;
        this.errorGobbler = errorGobbler;
    }

    public static TimerProcess execGrep(String commandName) throws IOException {
        final Process process = Runtime.getRuntime().exec(commandName);
        final NtestInputStreamGobbler inputGobbler = new NtestInputStreamGobbler(process.getInputStream());
        final NtestErrorStreamGobbler errorGobbler = new NtestErrorStreamGobbler(process.getErrorStream());
        inputGobbler.start();
        errorGobbler.start();
        return new TimerProcess(process, inputGobbler, errorGobbler);
    }
}
