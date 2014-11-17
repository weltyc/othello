package com.welty.othello.thor;

import com.orbanova.common.misc.Engineering;

public class PrintingProgressTracker implements IndeterminateProgressTracker {
    private final String suffix;
    private long n;
    private long nextPrintTime;

    public PrintingProgressTracker(String suffix) {
        this.suffix = suffix;
    }

    @Override synchronized public void increment() {
        n++;
        if (System.currentTimeMillis() >= nextPrintTime) {
            print();
        }
    }

    private void print() {
        System.out.println("PrintingMonitor: " + Engineering.compactFormat(n) + " " + suffix);
        nextPrintTime = System.currentTimeMillis() + 1000;
    }

    @Override synchronized public void update() {
        print();
    }

    @Override synchronized public void close() {

    }
}
