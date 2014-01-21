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
