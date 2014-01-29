package com.welty.othello.core;

import com.orbanova.common.misc.Require;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: May 1, 2009
 * Time: 8:43:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class CMoves {

    private MoveType moveToCheck;
    private final CMove bestMove = new CMove((byte) -2);
    private long all;

    public CMoves(long all, CMove bestMove, MoveType moveToCheck) {
        this.all = all;
        this.bestMove.Initialize(bestMove);
        this.moveToCheck = moveToCheck;
    }

    @Override public boolean equals(Object obj) {
        if (obj instanceof CMoves) {
            CMoves b = (CMoves) obj;
            final boolean bestSame = bestMove == null ? bestMove == b.bestMove : bestMove.equals(b.bestMove);
            return moveToCheck == b.moveToCheck && bestSame && all == b.all;
        } else {
            return false;
        }
    }

    public CMoves() {
    }

    public CMoves(CMoves b) {
        all = b.all;
        moveToCheck = b.moveToCheck;
        bestMove.Initialize(b.bestMove);
    }

    /**
     * @return bitboard containing all unused squares
     */
    public long All() {
        return all;
    }

    public int NMoves() {
        return Long.bitCount(all);
    }

    public static enum MoveType {
        CORNER(0x8100000000000081L), REGULAR(0x3C3CFFFFFFFF3C3CL), CX(-1);

        private final long mask;

        MoveType(long mask) {
            this.mask = mask;
        }

        public MoveType next() {
            switch (this) {
                case CORNER:
                    return REGULAR;
                case REGULAR:
                    return CX;
                case CX:
                    return null;
            }
            throw new IllegalStateException("Can't get here");
        }
    }

    public void set(long moveBits) {
        all = moveBits;
        moveToCheck = MoveType.CORNER;
        bestMove.MakeInvalid();
    }

    public boolean IsValid(CMove move) {
        return (move.mask() & all) != 0;
    }

    public boolean Consistent() {
        switch (moveToCheck) {
            case CORNER:
            case REGULAR:
            case CX:
                return true;
            default:
                return false;
        }
    }

//    @Override public boolean equals(Object obj) {
//        if (!(obj instanceof CMoves)) {
//            return false;
//        }
//        final CMoves b = (CMoves)obj;
//        return all==b.all;
//    }
//

    public void SetBest(CMove aBestMove) {
        bestMove.Initialize(aBestMove);
    }

    public CMove getBest() {
        return bestMove;
    }

    public boolean HasBest() {
        return bestMove != null;
    }

//    inline int __fastcall LowBitIndex(int n) {
//        __asm {
//            bsf eax, ecx;
//        }
//    }

    /**
     * If there are more moves to check, outputs the next move to check and returns true.
     * <p/>
     * If there are remaining moves in the movelist,
     * - sets move to the next move that should be checked
     * - deletes the move from the movelist
     * - returns true
     * <p/>
     * If there are no remaining moves
     * - returns false
     */
    public boolean GetNext(CMove move) {
        int square = -1;

        if (bestMove.Valid()) {
            square = bestMove.Square();
            Require.gt(square, "Square", -2);
            bestMove.MakeInvalid();
        } else {
            for (; moveToCheck != null; moveToCheck = moveToCheck.next()) {
                // find the lowest index of the movelist's squares which contains a move of this type (if any)
                long available = all & moveToCheck.mask;
                if (available != 0) {
                    square = Long.numberOfTrailingZeros(available);
                    break;
                }
            }
        }

        if (square == -1) {
            return false;
        } else {
            move.Set(square);
            all ^= move.mask();
            return true;
        }
    }

    /**
     * Remove a move from the list of available moves
     * <p/>
     * Warning: undefined behaviour if you remove the best move
     *
     * @param move
     */
    public void Delete(CMove move) {
        final long mask = move.mask();
        Require.eq(mask, "bit must be set", mask & all);
        all ^= mask;
    }

    @Override public String toString() {
        return new CBitBoard(all, ~all) + "" + bestMove + " " + moveToCheck;
    }
}
