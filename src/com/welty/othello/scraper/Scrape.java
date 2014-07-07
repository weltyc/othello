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

import com.orbanova.common.feed.Feed;
import com.orbanova.common.feed.Feeds;

import java.io.*;

/**
 * <PRE>
 * User: chris
 * Date: 3/7/11
 * Time: 11:01 AM
 * </PRE>
 */
public class Scrape {
    public static void main(String[] args) throws IOException {
        final int depth = 6;
        final String dir = "c:/dev/oth";
        final File outFile = new File("c:/dev/n64/src/solver" + depth + "Positions.txt");
        final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));

        int nResults = 0;
        for (int i = 1; i <= 5; i++) {
            final File file = new File(dir + i + "/log.txt");
            final Feed<Move> moves = Move.fileFeed(file);
            for (Move move; null!=(move=moves.next()); ) {
                if (move.status.nEmpty == 18) {
                    System.out.println(move.board.toScrZebra());
                    nResults++;
                }
            }
        }
        out.close();
        System.out.println("# of positions : " + nResults);
    }

}
