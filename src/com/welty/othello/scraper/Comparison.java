package com.welty.othello.scraper;

import com.orbanova.common.feed.Feed;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 */
public class Comparison {

    /**
     * Compare two log files and determines which positions appear in both (not in book), then prints out
     * the status row for each file
     *
     * @param args ignored
     * @throws IOException if files can't be read
     */
    public static void main(String[] args) throws IOException {
        final String file1;
        final String file2;
        file1 = "//Cow/dev/oth4/log.txt";
        file2 = "//Tiger1/dev/oth4/log.txt";
        Map<Board, Status> stats1 = loadStats(file1);
        Map<Board, Status> stats2 = loadStats(file2);
        CumulativeStats s1 = new CumulativeStats(), s2 = new CumulativeStats();

        for (Board pos : stats1.keySet()) {
            final Status result2 = stats2.get(pos);
            if (result2 != null) {
                final Status result1 = stats1.get(pos);
//                if (!result1.sameResult(result2)) {
                if (!result1.isConsistentWith(result2)) {
                    System.out.print(pos);
                    System.out.println(result1);
                    System.out.println(result2);
                    System.out.println();
                }
                s1.add(result1);
                s2.add(result2);
            }
        }
        System.out.println("Total times:");
        System.out.println("File 1: " + s1);
        System.out.println("File 2: " + s2);
        System.out.format("Speedup: %.2fx%n", s1.time / s2.time);
    }

    private static class CumulativeStats {
        double time = 0;
        double nodes = 0;
        int n32e = 0;
        int nPositions = 0;
        int nSolved = 0;

        void add(Status status) {
            time += status.seconds;
            nodes += status.nodes;
            if (status.nEmpty == 32) {
                n32e++;
            }
            nPositions++;
            if (status.isSolved()) {
                nSolved++;
            }
        }

        @Override public String toString() {
            return Status.engineering(nodes) + " nodes / " + Status.engineering(time) + "s = " + Status.engineering(nodes / time) + "n/s. "
                    + n32e + " searches from 32 empty; " + nPositions + " total positions; " + nSolved + " solvedPositions";
        }
    }

    private static Map<Board, Status> loadStats(String fileName) throws IOException {
        Map<Board, Status> result = new LinkedHashMap<Board, Status>();

        final Feed<Move> moves = Move.fileFeed(fileName);
        Move move;
        while (null != (move = moves.next())) {
            // don't allow book moves from later games to overwrite non-book moves from earlier games
            if (!move.status.isBook) {
                result.put(move.board, move.status);
            }
        }
        return result;
    }
}
