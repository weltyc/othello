package com.welty.othello.timer.timer;

import com.welty.othello.timer.progress.AbstractProgressTask;

/**
 * <PRE>
 * User: Chris
 * Date: Jul 25, 2009
 * Time: 10:32:02 AM
 * </PRE>
 */
abstract public class ProgressWindowTest {
    public static void main(String[] args) {
        final MockProgressTask endgameTask = new MockProgressTask();
        final MockProgressTask midgameTask = new MockProgressTask();
        final ProgressWindow window = new ProgressWindow(endgameTask, midgameTask);
        for (int i=0; i<13; i++) {
            endgameTask.setProgress(i/12.);
            sleep(100);
        }
        for (int i=0; i<13; i++) {
            midgameTask.setProgress(i/12.);
            sleep(100);
        }
        window.setEndgameResult(5.672);
        window.setMidgameResult(1.338);
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class MockProgressTask extends AbstractProgressTask {
        private double progress;

        public void setProgress(double progress) {
            this.progress = progress;
            sendProgress();
        }

        public double getProgress() {
            return progress;
        }
    }
}
