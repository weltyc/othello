package com.welty.othello.core;

import java.io.*;

/**
 * Logs communications to and from a Process
 */
public class ProcessLogger {
    private final PrintWriter out;
    private final BufferedReader in;
    private final boolean debug;
    private boolean wasWriting = true;

    public ProcessLogger(Process process, boolean debug) {
        this.debug = debug;
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(process.getOutputStream())), true);
        in = new BufferedReader(new InputStreamReader(process.getInputStream()));

    }

    private void changeState(boolean writing) {
        if (debug && wasWriting != writing) {
            System.out.println();
            wasWriting = writing;
        }
    }

    public synchronized void println(String text) {
        out.println(text);
        out.flush();
        changeState(true);
        if (debug) {
            System.out.println("< " + text);
        }
    }

    public String readLine() throws IOException {
        final String line = in.readLine();
        if (line != null) {
            synchronized (this) {
                changeState(false);
                if (debug) {
                    System.out.println("> " + line);
                }
            }
        }
        return line;
    }
}
