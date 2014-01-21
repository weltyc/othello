package com.welty.othello.scraper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Calculate the average node speed of solves from 32 empty for an othello program
 */
public class NodeSpeedCalc {
    /**
     * Calculate the average node speed of solves from 32 empty
     *
     * @param args ignored
     * @throws IOException on error reading file
     */
    public static void main(String[] args) throws IOException {
        for (int i = 1; i <= 5; i++) {
            final String fileName = "c:/dev/oth" + i + "/log.txt";
            processFile(fileName);
        }
        processFile("c:/dev/n64/log.txt");
    }

    private static void processFile(String fileName) throws IOException {
        ArrayList<Double> speeds = new ArrayList<Double>();
        double totalNodes = 0, totalTime = 0;

        final BufferedReader in = new BufferedReader(new FileReader(fileName));
        String line;
        while (null != (line = in.readLine())) {
            Status status = Status.create(line);
            if (status != null && !status.isBook && status.nEmpty == 32 && status.nodes > 1e6) {
                final double nps = status.nodes / status.seconds;
                speeds.add(nps);
                totalNodes += status.nodes;
                totalTime += status.seconds;
            }
        }
        Collections.sort(speeds);
        final int n = speeds.size();
        final String low = Status.engineering(speeds.get(0));
        final String high = Status.engineering(speeds.get(n - 1));
        final String median = Status.engineering(speeds.get(n / 2));
        final String mean = Status.engineering(totalNodes / totalTime);
        System.out.println(low + " " + median + " " + high + " " + mean + " -- " + fileName);
    }

}
