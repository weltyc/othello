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

package com.welty.othello.assign;

import com.orbanova.common.feed.Feed;
import com.orbanova.common.feed.Feeds;
import com.orbanova.common.feed.Mapper;
import com.orbanova.common.misc.Utils;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 */
public class Assigner {
    /**
     * Print out a new assignment for a subsection of the draw tree.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        final String squares = "F5 F6 E6 F4 E3 C5 C6 D3 F3 E2 C4";
        final int assignDepth = 14;
        final String drawFileName = "c:/dev/oth/draws.txt";

        final GgfMoves opening = new GgfMoves(squares, true);
        System.out.println(opening);

                // find openings
        final Feed<String> feed = Feeds.ofLines(new File(drawFileName))
                .grep("transposition", true)
                .grep(squares)
                .map(new Mapper<String, String>() {
                    @NotNull @Override public String y(String x) {
                        final String continuationMoveList = x.substring(squares.length() + 1, assignDepth * 3);
                        return new GgfMoves(continuationMoveList, opening.blackToMove).text;
                    }
                });
        final Feed<Feed.Count<String>> counts = feed.uniqueCount();
        for (Feed.Count<String> continuation : counts) {
            System.out.println(continuation + ": ---");
        }
    }


    private static class GgfMoves {
        /**
         * Text of moves in GGF format "B[F5]W[D6]..."
         */
        final String text;
        /**
         * Player to move AFTER the text
         */
        final boolean blackToMove;

        /**
         * Constructor
         *
         * @param moveList    squares to parse in MoveList format "F5 D6 ..."
         * @param blackToMove is it black's move before these moves start?
         */
        GgfMoves(String moveList, boolean blackToMove) {
            StringBuilder sb = new StringBuilder();
            for (String square : moveList.split("\\s+")) {
                sb.append(blackToMove ? "B[" : "W[").append(square).append("]");
                blackToMove = !blackToMove;
            }
            this.blackToMove = blackToMove;
            text = sb.toString();
        }

        @Override public String toString() {
            return text;
        }
    }
}
