package com.welty.othello.magic;

import gnu.trove.set.hash.TLongHashSet;

import java.util.Random;

/**
 */
public class FindMagic {
    static final long A1H8 = 0x8040201008040201L;
    static final long A8H1 = 0x0102040810204080L;
    static final long A1H1 = 0x8080808080808080L;
    static final long A1A8 = 0xFFL;
    static final long A1_CORNER = 0x0103070F;
    static final long limit = 10 * 1000 * 1000;

    /**
     * Find magic numbers.
     * <p/>
     * Currently hardcoded to the A1-H8 diagonal (mask = 0x8040201008040201L)
     * <p/>
     * Consider the set V of longs v such that v&~mask==0 (v is a sub-mask of mask).
     * A Magic is a set (a, b, c) such that the function f = ((v>>>a)*b)>>>c is one-to-one.
     * Since f is used as an index into a lookup table, higher values of c are better (the lookup table
     * will be smaller).
     * <p/>
     * For the A1-H8 diagonal, a must be 0 (otherwise the 1 bit is shifted off) so we only consider b and c.
     * This algorithm randomly finds b, then updates c.
     */
    public static void main(String[] args) {
        final long t0 = System.currentTimeMillis();
        long diagonal = A1H8;
        for (int i = 0; i < 6; i++) {
            printBestMagic(limit, diagonal);
            diagonal >>>= 8;
        }
        printBestMagic(limit, A1H1);
        printBestMagic(limit, A8H1);
        printBestMagic(limit, A1A8);
        printBestMagic(limit, A1_CORNER);

        final long dt = System.currentTimeMillis() - t0;
        System.out.println(dt + " ms elapsed");
    }

    private static void printBestMagic(long limit, long mask) {
        Magic best = getBestMagic(limit, mask);
        System.out.println("Best magic: " + best);
    }

    /**
     * Determine a good magic for the mask
     *
     * @param limit number of randomly generated multipliers to use
     * @param mask  mask to check
     * @return best found magic
     */
    private static Magic getBestMagic(long limit, long mask) {
        final int preShift = 0; // Long.numberOfTrailingZeros(mask);

        Magic best = new Magic(preShift, 1, mask, 0);
        best.c = best.calcC(-1);
        final int bestPossibleC = 64 - Long.bitCount(mask);

        final Random r = new Random(1337);
        for (int i = 0; i < limit; i++) {
            final Magic magic = new Magic(preShift, r.nextLong() & r.nextLong() & r.nextLong(), mask, i);
            magic.c = magic.calcC(best.c);
            if (magic.c > best.c) {
                best = magic;
                if (best.c == bestPossibleC) {
                    break;
                }
            }
        }
        return best;
    }

    static class Magic {
        final int preShift;
        final long b;
        final long mask;
        final long findCount;
        int c;

        Magic(int preShift, long b, long mask, long findCount) {
            this.preShift = preShift;
            this.b = b;
            this.mask = mask;
            this.findCount = findCount;
        }

        /**
         * @param bestC current best C (for the first call this should be 0).
         * @return the value of C for this a, b, mask, if it's higher than the current bestC,
         *         or bestC if the value of C can be proved &le; bestC.
         */
        int calcC(int bestC) {
            while (canUseC(bestC + 1)) {
                bestC++;
            }
            return bestC;
        }

        /**
         * @param c potential value for c
         * @return true if (this.a, this.b, c) is a valid Magic for mask
         */
        boolean canUseC(int c) {
            final TLongHashSet used = new TLongHashSet();
            used.add(0); // value for v==0, which is not handled below.

            long v = Long.lowestOneBit(mask);
            while (v != 0) {
                final long f = ((v >>> preShift) * b) >>> c;
                if (!used.add(f)) {
                    // collision at bestC+1, so can't improve bestC.
                    return false;
                }
                v = next(v);
            }
            return true;
        }

        /**
         * @param v a nonzero subset of mask
         * @return next subset of mask, or 0 if v==mask.
         */
        long next(long v) {
            // lowest 0 bit of v becomes 1; all bits below that become 0.
            final long low = Long.lowestOneBit(mask & ~v);
            return (v | low) & ~(low - 1);
        }

        @Override public String toString() {
            final String preShiftString = preShift==0 ? "v" : "(v >>> " + preShift +")";
            return String.format("[mask = 0x%16x] (%s * 0x%016x) >>> %d (found at %d)", mask, preShiftString, b, c, findCount);
        }
    }
}
