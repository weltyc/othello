package com.welty.othello.timer.progress;

/**
 * Listener for progress updates. Listener is responsible for calculating the amount of progress.
 * <PRE>
 * User: Chris
 * Date: Jul 25, 2009
 * Time: 7:28:28 AM
 * </PRE>
 */
public interface ProgressListener {
    /**
     * Notify the Listener that progress has occurred.
     */
    void handleProgress();
}
