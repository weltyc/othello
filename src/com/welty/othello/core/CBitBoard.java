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

package com.welty.othello.core;

import com.orbanova.common.misc.Require;
import com.welty.othello.c.CBinaryReader;
import com.welty.othello.c.CBinaryWriter;
import static com.welty.othello.core.BitBoardBlock.getRow;
import static com.welty.othello.core.Utils.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static java.lang.Long.bitCount;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: May 1, 2009
 * Time: 10:19:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class CBitBoard implements Comparable<CBitBoard>, ReadWrite {
    public long empty;
    public long mover;
    private static final long FLIP_SIGN = 0x8000000000000000L;

    public CBitBoard(CBitBoard bitBoard) {
        empty = bitBoard.empty;
        mover = bitBoard.mover;
    }

    public CBitBoard(long mover, long empty) {
        this.mover = mover;
        this.empty = empty;
    }

    public CBitBoard(String boardText, boolean blackMove) {
        Initialize(boardText, blackMove);
    }

    public CBitBoard(CBinaryReader in) {
        empty = in.readLong();
        mover = in.readLong();
    }

    public void Out(CBinaryWriter out) {
        out.writeLong(empty);
        out.writeLong(mover);
    }


    /**
     * Make the bitboard an impossible position. This is used when clearing the hashtable.
     */
    public void SetImpossible() {
        mover = empty = -1L;
    }

    /**
     * Check whether the bitboard is impossible. Impossible positions are stored in unused
     * hashtable locations.
     *
     * @return true if the position is impossible
     */
    public boolean IsImpossible() {
        return (mover & empty) != 0;
    }

    /**
     * Check whether the bitboard is possible. Possible positions have no squares that are both empty and mover.
     *
     * @return true if the position is impossible
     */
    public boolean IsPossible() {
        return (mover & empty) == 0;
    }

    @Override public boolean equals(Object obj) {
        if (!(obj instanceof CBitBoard)) {
            return false;
        }
        final CBitBoard b = (CBitBoard) obj;

        return mover == b.mover && empty == b.empty;
    }

    void FlipVertical() {
        empty = BitBoardBlock.flipVertical(empty);
        mover = BitBoardBlock.flipVertical(mover);
    }

    void FlipHorizontal() {
        empty = BitBoardBlock.flipHorizontal(empty);
        mover = BitBoardBlock.flipHorizontal(mover);
    }

    void FlipDiagonal() {
        empty = BitBoardBlock.flipDiagonal(empty);
        mover = BitBoardBlock.flipDiagonal(mover);
    }

    public void InvertColors() {
        mover ^= ~empty;
    }

    public void Initialize(String boardText, boolean fBlackMove) {
        boardText = boardText.replaceAll(" ", "");
        Require.eq(boardText.length(), "board text length", NN);

        empty = mover = 0;

        for (int i = 0; i < NN; i++) {
            final char c = boardText.charAt(i);
            if (c != ' ') {
                final long mask = 1L << i;
                switch (c) {
                    case '.':
                    case '-':
                    case '_':
                        empty |= mask;
                        break;
                    case 'x':
                    case 'X':
                    case '*':
                    case 'b':
                    case 'B':
                        mover |= mask;
                        break;
                    case 'o':
                    case 'O':
                    case 'w':
                    case 'W':
                        break;
                    default:
                        throw new IllegalArgumentException("unknown text");
                }
            }
        }

        // if white to move, swap around so mover is to move
        if (!fBlackMove) {
            InvertColors();
        }
    }

    public void Initialize(CBitBoard bb) {
        empty = bb.empty;
        mover = bb.mover;
    }

    /**
     * Write the bitboard to a file, in Windows order
     * todo need to test this is the correct order for existing files
     *
     * @param out location to write
     * @throws IllegalArgumentException if there's an error writing to file (the original C code printed
     *                                  a message and returned false in this case)
     */
    public void Write(CBinaryWriter out) {
        out.writeLong(empty);
        out.writeLong(mover);
    }

    public void Read(CBinaryReader in) {
        empty = in.readLong();
        mover = in.readLong();
    }

    @Override public int hashCode() {
        int a, b, c, d;
        a = (int) empty;
        b = (int) (empty >>> 32);
        c = (int) mover;
        d = (int) (mover >>> 32);
        return BobHash.hash4(a, b, c, d);
    }

    public CBitBoard Symmetry(int sym) {
        return new CBitBoard(BitBoardBlock.symmetry(mover, sym), BitBoardBlock.symmetry(empty, sym));
    }


    public CBitBoard MinimalReflection() {
        CBitBoard result, temp;
        int i, j, k;

        temp = new CBitBoard(this);
        result = new CBitBoard(this);
        for (i = 0; i < 2; i++) {
            for (j = 0; j < 2; j++) {
                for (k = 0; k < 2; k++) {
                    if (temp.compareTo(result) < 0)
                        result.Initialize(temp);
                    temp.FlipVertical();
                }
                temp.FlipHorizontal();
            }
            temp.FlipDiagonal();
        }
        return result;
    }

    public void Print(boolean fBlackMove) {
        FPrint(System.out, fBlackMove);
    }

    void FPrintHeader(PrintStream fp) {
        int col;

        fp.print("  ");
        for (col = 0; col < N; col++)
            fp.print(" " + (char) ('A' + col));
        fp.println();
    }

    public void FPrint(PrintStream fp, boolean fBlackMove) {
        int row, col;
        int e, b;
        int value;

        fp.println();

        // print the board
        FPrintHeader(fp);
        for (row = 0; row < N; row++) {
            fp.format("%2d ", row + 1);
            e = getRow(empty, row);
            b = getRow(mover, row);
            if (!fBlackMove)
                b ^= ~e;
            for (col = 0; col < N; col++) {
                if ((e & 1) != 0)
                    value = EMPTY;
                else
                    value = ((b & 1) != 0) ? BLACK : WHITE;
                e >>= 1;
                b >>= 1;
                fp.format("%c ", ValueToText(value));
            }
            fp.format("%2d\n", row + 1);
        }
        FPrintHeader(fp);

        // disc info
        NDiscs nDiscs = new NDiscs(fBlackMove);
        fp.println(nDiscs);
    }

    /**
     * Convert to a string of 64 characters, one for each element of the board
     *
     * @param fBlackMove the conversion depends on whose move it is
     * @return string of characters
     */
    public String GetSBoard(boolean fBlackMove) {
        int row, col, e, b;
        char c;

        StringBuilder sb = new StringBuilder(65);

        for (row = 0; row < N; row++) {
            e = getRow(empty, row);
            b = getRow(mover, row);
            if (!fBlackMove)
                b ^= ~e;
            for (col = 0; col < N; col++) {
                if ((e & 1) != 0)
                    c = ValueToText(EMPTY);
                else if ((b & 1) != 0)
                    c = ValueToText(BLACK);
                else
                    c = ValueToText(WHITE);
                sb.append(c);
                e >>= 1;
                b >>= 1;
            }
        }
        return sb.toString();
    }

    public int NEmpty() {
        return bitCount(empty);
    }

    public int NMover() {
        return bitCount(mover);
    }

    /**
     * Calculate moves
     *
     * @param moves store moves in this structure
     * @return true if at least one move is available
     */
    public boolean CalcMoves(CMoves moves) {
        final long moveBits = calcMoves();
        moves.set(moveBits);
        return moveBits != 0;
    }

    private long calcMoves() {
        return Mobility.calcMoves(mover, empty);
    }


    public void Initialize() {
        mover = 0x0000000810000000L;
        empty = 0xFFFFFFE7E7FFFFFFL;
    }

    /**
     * @return the number of mobilities for the player to move.
     */
    int nMoverMoves() {
        return bitCount(calcMoves());
    }

    private int nEnemyMoves() {
        return bitCount(Mobility.calcMoves(getEnemy(), empty));
    }

    /**
     * simultaneously calculate the mobility of the mover and the enemy.
     *
     * @return mobility result
     *         <p/>
     *         Port Note: original C code returned the pass code, and returned the mobilities via references.
     */
    public MobilityResult CalcMobility() {
        final int nMoverMoves = nMoverMoves();
        final int nEnemyMoves = nEnemyMoves();
        return new MobilityResult(nMoverMoves, nEnemyMoves);
    }

    public boolean isEnemySquare(int sq) {
        return !Utils.isSet(mover | empty, sq);
    }

    public boolean isMoverSquare(int sq) {
        return Utils.isSet(mover, sq);
    }

    public boolean isEmptySquare(int sq) {
        return Utils.isSet(empty, sq);
    }

    public void flipMoverBit(int sq) {
        mover ^= (1L << sq);
    }

    public void flipEmptyBit(int sq) {
        empty ^= (1L << sq);
    }

    /**
     * @return number of empty squares
     */
    public int nEmpty() {
        return bitCount(empty);
    }

    /**
     * Make a move on the bitboard
     *
     * @param mask mask of the new disc that was placed
     * @param flip mask with set bits corresponding to discs that are flipped
     */
    public void MakeMove(long mask, long flip) {
        mover ^= flip;
        InvertColors();
        empty ^= mask;
    }

    /**
     * Undo a move on the bitboard
     *
     * @param mask mask of the new disc that was placed
     * @param flip mask with set bits corresponding to discs that are flipped
     */
    public void UndoMove(long mask, long flip) {
        empty ^= mask;
        InvertColors();
        mover ^= flip;
    }


    public static class MobilityResult {
        public final int nMoverMoves;
        public final int nEnemyMoves;
        public final int pass;  // pass code: 0 = mover has a move, 1=mover has no move but opponent does, 2=no moves

        public MobilityResult(int nMoverMoves, int nEnemyMoves) {
            this.nMoverMoves = nMoverMoves;
            this.nEnemyMoves = nEnemyMoves;
            if (nMoverMoves != 0)
                pass = 0;
            else if (nEnemyMoves != 0)
                pass = 1;
            else
                pass = 2;
        }

        @Override public boolean equals(Object obj) {
            if (obj instanceof MobilityResult) {
                MobilityResult b = (MobilityResult) obj;
                return pass == b.pass && nMoverMoves == b.nMoverMoves && nEnemyMoves == b.nEnemyMoves;
            } else {
                return false;
            }
        }

        @Override public String toString() {
            return "Mover : " + nMoverMoves + "/ Enemy: " + nEnemyMoves + "/ pass=" + pass;
        }
    }

    //

    public int compareTo(CBitBoard o) {
        if (mover != o.mover) {
            return funkyCompare(mover, o.mover);
        }
        if (empty != o.empty) {
            return funkyCompare(empty, o.empty);
        }
        return 0;
    }

    // comparison used in C version of ntest, caused by using comparing the low 4 bytes first, then the high 4 bytes
    private int funkyCompare(long a, long b) {
        if (a == b) {
            return 0;
        }
        // FLIP_SIGN is used to make signed comparisons work the same way unsigned comparisons work in C++
        final long aa = a ^ FLIP_SIGN;
        final long bb = b ^ FLIP_SIGN;

        return (aa < bb) ? -1 : 1;
    }

    public long getMover() {
        return mover;
    }

    public long getEmpty() {
        return empty;
    }

    public long getEnemy() {
        return ~(mover | empty);
    }

    class NDiscs {
        final int nBlack, nWhite, nEmpty;

        public NDiscs(boolean fBlackMove) {
            nEmpty = bitCount(empty);
            if (fBlackMove) {
                nBlack = bitCount(mover);
                nWhite = NN - nEmpty - nBlack;
            } else {
                nWhite = bitCount(mover);
                nBlack = NN - nEmpty - nWhite;
            }
        }

        @Override public String toString() {
            return "Black: " + nBlack + "  White: " + nWhite + "  Empty: " + nEmpty;
        }
    }


    public int sizeof() {
        return 16;
    }

    @Override public String toString() {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FPrint(new PrintStream(baos), true);
        return baos.toString();
    }
}
