/*
 * Copyright (c) 2014 Chris Welty.
 *
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * For the license, see <http://www.gnu.org/licenses/gpl.html>.
 */

package com.welty.othello.scraper;

import com.orbanova.common.feed.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Prints out a list of positions and how many times they've been played
 */
public class InitialPositionAnalysis implements Handler<Move> {
    private Map<Board, Data> counts = new LinkedHashMap<>();

    /**
     * Print out a list of positions and how many times they've been played
     * @param args ignored
     */
    public static void main(String[] args) {
        process("c:/dev/oth1/log.txt");
        process("c:/dev/oth2/log.txt");
        process("c:/dev/oth3/log.txt");
        process("c:/dev/oth4/log.txt");
        process("c:/dev/oth5/log.txt");
    }

    private static void process(String fileName) {
        System.out.println("=== " + fileName + " ===");
        final InitialPositionAnalysis a = new InitialPositionAnalysis();
        Move.fileFeed(fileName).each(a);
        a.dump();
    }

    public void handle(@NotNull Move move) {
        if (move.isInitialPosition) {
            Data data = counts.get(move.board);
            if (data==null) {
                data = new Data();
                counts.put(move.board, data);
            }
            data.count++;
            data.mostRecentStatus = move.status;
        }
    }

    void dump() {
        for (Board b : counts.keySet()) {
            System.out.println(b);
            System.out.println(counts.get(b));
            System.out.println("---------------");

        }
    }

    private static class Data {
        int count;
        Status mostRecentStatus;

        @Override public String toString() {
            return count + " times. Last search: " + mostRecentStatus;
        }
    }

}
