package com.welty.othello.timer.progress;

/**
 * <PRE>
 * User: Chris
 * Date: Jul 25, 2009
 * Time: 8:16:51 AM
 * </PRE>
 */
public abstract class AbstractProgressTask implements ProgressTask {
    private ProgressListener listener;

    /**
     * Notify the listener that progress has occurred
     */
    public void sendProgress() {
        if (listener!=null) {
            listener.handleProgress();
        }
    }

    public void addListener(ProgressListener listener) {
        if (this.listener==null) {
            this.listener = listener;
        }
        else {
            throw new IllegalStateException("multiple progress listeners");
        }
    }
}
