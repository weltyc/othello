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
