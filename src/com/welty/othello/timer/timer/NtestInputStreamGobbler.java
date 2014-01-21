package com.welty.othello.timer.timer;

import com.welty.othello.timer.gobbler.LineBufferedStreamGobbler;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <PRE>
 * User: Chris
 * Date: Jul 23, 2009
 * Time: 9:03:05 PM
 * </PRE>
 */
final class NtestInputStreamGobbler extends LineBufferedStreamGobbler {
    private volatile int stage = 0;
    private volatile double endgameResult;
    private volatile double midgameResult;

    /**
     * Creates a GreppingStreamGobbler thread. The gobbler has not been started; the caller must call start() to start it.
     *
     * @param is inputStream from the Exec process
     */
    public NtestInputStreamGobbler(InputStream is) {
        super(is);
    }

    public void close() {
    }

    protected synchronized void handleLine(String line) {
        if (line.contains("Mn/s")) {
            final double result = mnFromLine(line);
            switch(stage) {
                case 0:
                    endgameResult = result;
                    break;
                case 1:
                    midgameResult = result;
                    break;
                default:
                    // ignore
            }
            stage++;
        }
    }

    public double getMidgameResult() {
        myJoin();
        return midgameResult;
    }

    private void myJoin() {
        try {
            this.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public double getEndgameResult() {
        myJoin();
        return endgameResult;
    }

    private static final Pattern mnPattern = Pattern.compile("([0-9.]+)Mn/s");

    static double mnFromLine(String line) {
        final Matcher matcher = mnPattern.matcher(line);
        matcher.find();
        return Double.parseDouble(matcher.group(1));
    }
}
