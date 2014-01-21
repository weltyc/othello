package com.welty.othello.optimizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <PRE>
 * User: chris
 * Date: 3/16/11
 * Time: 1:47 PM
 * </PRE>
 */
public class Fitter {
    /**
     * Create a function to predict the expected number of solver nodes for an othello position.
     *
     * @throws IOException if trouble reading file
     */
    public static void main(String[] args) throws IOException {
        final int nEmpty = 12;
        final ArrayList<Position> positions = loadPositions(nEmpty);

        final HashFunction gs = new HashFunction();
        final HashFunction hes = new HashFunction();
        final HashFunction je = new HashFunction();
        final HashFunction hms = new HashFunction();
        final HashFunction jm = new HashFunction();

        NodeFunction one = new NodeFunction() {
            public double y(Position position) {
                return 1;
            }
        };

        for (int i = 0; i <= 9; i++) {
            je.put(i, i);
            jm.put(i,i);
        }

        final NodeFunction predictor = new NodeFunction() {
            public double y(Position position) {
                final int e = position.enemyMobs;
                final int s = position.getStaticInt();
                final int m = position.moverMobs;
                final double g = gs.get(s);
                final double h = hes.get(s);
                final double j = je.get(e);
                final double h2 = hms.get(s);
                final double j2 = jm.get(m);

                return g + h * j + h2*j2;
            }
        };

        final NodeFunction error = new NodeFunction() {
            public double y(Position position) {
                return position.nNodes - predictor.y(position);
            }
        };

        final NodeFunction jeFunction = new NodeFunction() {
            public double y(Position position) {
                return je.get(position.enemyMobs);
            }
        };

        final NodeFunction hesFunction = new NodeFunction() {
            public double y(Position position) {
                return hes.get(position.getStaticInt());
            }
        };

         final NodeFunction jmFunction = new NodeFunction() {
            public double y(Position position) {
                return jm.get(position.moverMobs);
            }
        };

        final NodeFunction hmsFunction = new NodeFunction() {
            public double y(Position position) {
                return hms.get(position.getStaticInt());
            }
        };

        for (int i = 0; i < 10; i++) {
            System.out.println("=== gs ===");
            gs.add(sFunctionDelta(one, error, positions));
            gs.dump();
            System.out.format("SSE is %3.1f%n", sse(positions, predictor) / 1e9);

            System.out.println();
            System.out.println("=== hes ===");
            hes.add(sFunctionDelta(jeFunction, error, positions));
            hes.dump();
            System.out.format("SSE is %3.1f%n", sse(positions, predictor) / 1e9);

            System.out.println();
            System.out.println("=== je ===");
            je.add(eFunctionDelta(hesFunction, error, positions));
            je.dump();
            System.out.format("SSE is %3.1f%n", sse(positions, predictor) / 1e9);

            System.out.println();
            System.out.println("=== hms ===");
            hms.add(sFunctionDelta(jmFunction, error, positions));
            hms.dump();
            System.out.format("SSE is %3.1f%n", sse(positions, predictor) / 1e9);

            System.out.println();
            System.out.println("=== jm ===");
            jm.add(mFunctionDelta(hmsFunction, error, positions));
            jm.dump();
            System.out.format("SSE is %3.1f%n", sse(positions, predictor) / 1e9);
        }

        gs.dumpAsArray("gs", 65);
        hes.dumpAsArray("hes", 65);
        hms.dumpAsArray("hms", 65);
        je.dumpAsArray("je", 10);
        jm.dumpAsArray("jm", 10);
    }

    private static double sse(ArrayList<Position> positions, NodeFunction predictor) {
        double sse = 0;
        for (Position position : positions) {
            final double err = predictor.y(position) - position.nNodes;
            sse += err * err;
        }
        return sse;
    }

    static ArrayList<Position> loadPositions(int nEmpty) throws IOException {
        final ArrayList<Position> positions = new ArrayList<Position>();

        final File file = new File("c:/dev/n64/ps"+nEmpty+".txt");
        final BufferedReader in = new BufferedReader(new FileReader(file));
        String header = "";

        while (!header.startsWith("mover mobs")) {
            header = in.readLine();
        }
        String line;
        while (null != (line = in.readLine())) {
            if (!line.isEmpty() && Character.isDigit(line.charAt(0))) {
                positions.add(new Position(line));
                printProgress(positions.size());
            } else {
                break;
            }
        }
        System.out.println(positions.size() + " positions loaded");

        return positions;
    }

    private static void printProgress(int size) {
        if ((size & 0xFFFFF) == 0) {
            System.err.println((size >> 20) + "M positions read");
        }
    }

    private static HashFunction sFunctionDelta(NodeFunction weight, NodeFunction error, List<Position> positions) {
        final HashFunction top = new HashFunction();
        final HashFunction bottom = new HashFunction();

        for (Position position : positions) {
            final int s = position.getStaticInt();

            final double e = error.y(position);
            final double w = weight.y(position);
            top.add(s, e * w);
            bottom.add(s, w * w);
        }

        return top.dividedBy(bottom);
    }

    private static HashFunction eFunctionDelta(NodeFunction weight, NodeFunction error, List<Position> positions) {
        final HashFunction top = new HashFunction();
        final HashFunction bottom = new HashFunction();

        for (Position position : positions) {
            final int e = position.enemyMobs;

            final double err = error.y(position);
            final double w = weight.y(position);
            top.add(e, err * w);
            bottom.add(e, w * w);
        }

        return top.dividedBy(bottom);
    }

    private static HashFunction mFunctionDelta(NodeFunction weight, NodeFunction error, List<Position> positions) {
        final HashFunction top = new HashFunction();
        final HashFunction bottom = new HashFunction();

        for (Position position : positions) {
            final int m = position.moverMobs;

            final double err = error.y(position);
            final double w = weight.y(position);
            top.add(m, err * w);
            bottom.add(m, w * w);
        }

        return top.dividedBy(bottom);
    }

}
