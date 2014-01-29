package com.welty.othello.lp;

import java.io.PrintStream;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: Jun 27, 2009
 * Time: 2:34:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class PrintStreamLinePrinter implements LinePrinter {
    private final PrintStream out;

    public PrintStreamLinePrinter(PrintStream out) {
        this.out = out;
    }

    public void println(Object msg) {
        out.println(msg);
    }
}
