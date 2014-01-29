package com.welty.othello.gdk;

import com.welty.othello.c.CReader;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: May 2, 2009
 * Time: 5:03:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class COsResult {
    public double dResult;

    public enum TStatus {
        kUnfinished, kNormalEnd, kTimeout, kResigned, kAgreedScore, kAdjourned, kAborted
    }

    public TStatus status;

    public COsResult() {
    }

    public COsResult(COsResult b) {
        dResult = b.dResult;
        status = b.status;
    }

    public double getDResult() {
        return dResult;
    }

    void Set(double dResult) {
        Set(dResult, TStatus.kNormalEnd);
    }

    // "nasai left" in matchdelta messages means game adjourned
    //	"?" in other messages
    void In(CReader is) {
        is.ignoreWhitespace();

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
    }

    void Out(StringBuilder sb) {
        if (status == TStatus.kUnfinished || status == TStatus.kAdjourned)
            sb.append('?');
        else {
            if (dResult == (int) dResult) {
                sb.append((int) dResult);
            } else {
                sb.append(dResult);
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

    void Set(double adResult, TStatus astatus) {
        dResult = adResult;
        status = astatus;
    }

    public void Clear() {
        status = TStatus.kUnfinished;
        dResult = 0;
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        Out(sb);
        return sb.toString();
    }
}
