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

package com.welty.othello.gdk;

import com.welty.othello.c.CReader;
import lombok.EqualsAndHashCode;

/**
 * Result of an Os game.
 */
@EqualsAndHashCode
public class OsResult {
    public static final OsResult INCOMPLETE = new OsResult(TStatus.kUnfinished, 0);

    /**
     * Net score to Black, in disks
     */
    public final double score;

    public enum TStatus {
        kUnfinished, kNormalEnd, kTimeout, kResigned, kAgreedScore, kAdjourned, kAborted
    }

    public final TStatus status;

    public OsResult(TStatus status, double score) {
        this.status = status;
        this.score = score;
    }

    public double getScore() {
        return score;
    }

    /**
     * Result with a status of kNormalEnd
     * @param score score, net disks to black
     */
    public OsResult(double score) {
        this.score = score;
        status = TStatus.kNormalEnd;
    }

    // "nasai left" in matchdelta messages means game adjourned
    //	"?" in other messages
    public static OsResult of(CReader is) {
        is.ignoreWhitespace();
        TStatus status;
        final double dResult;

        char c;
        c = is.peek();
        if (c == '?') {
            status = TStatus.kUnfinished;
            dResult = 0;
        } else if (Character.isLetter(c)) {
            dResult = 0;
            String sResult = is.readString();
            if (sResult.equals("aborted"))
                status = TStatus.kAborted;
            else
                status = TStatus.kAdjourned;
        } else {
            dResult = is.readDoubleNoExponent();
            status = TStatus.kNormalEnd;
            c = is.peek();
            if (c == ':') {
                is.read();
                c = is.read();
                switch (c) {
                    case 'r':
                        status = TStatus.kResigned;
                        break;
                    case 't':
                        status = TStatus.kTimeout;
                        break;
                    case 'l':
                        status = TStatus.kAdjourned;
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown game status code : " + c);
                }
            }
        }

        return new OsResult(status, dResult);
    }

    void Out(StringBuilder sb) {
        if (status == TStatus.kUnfinished || status == TStatus.kAdjourned)
            sb.append('?');
        else {
            if (score == (int) score) {
                sb.append((int) score);
            } else {
                sb.append(score);
            }
            switch (status) {
                case kResigned:
                    sb.append(":r");
                    break;
                case kTimeout:
                    sb.append(":t");
                    break;
                case kNormalEnd:
                    break;
                default:
                    throw new IllegalArgumentException("unknown status : ");
            }
        }
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        Out(sb);
        return sb.toString();
    }
}
