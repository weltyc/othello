package com.welty.othello.gdk;

import com.orbanova.common.misc.Require;
import com.welty.othello.c.CReader;

import java.text.DecimalFormat;

/**
 * Game clock
 */
public class OsClock {
    public static final OsClock DEFAULT = new OsClock(0);
    /**
     * Remaining time for this move, in seconds
     */
    public final double tCurrent;
    /**
     * Amount added to clock after the player makes a legal move, in seconds
     */
    private double tIncrement;
    /**
     * Amount added to the clock if the player times out when iTimeout=0, in seconds
     */
    private double tGrace;

    /**
     * Player timeout status:
     * 0 = never timed out
     * 1 = timed out once
     * 2 = timed out more than once
     */
    private int iTimeout;

    public OsClock(double tCurrent) {
        this(tCurrent, 0);
    }

    OsClock(double tCurrent, double tIncrement) {
        this(tCurrent, tIncrement, 120);
    }

    OsClock(double tCurrent, double tIncrement, double tGrace) {
        this(tCurrent, tIncrement, tGrace, 0);
    }

    OsClock(double atCurrent, double atIncrement, double atGrace, int aiTimeout) {
        tCurrent = atCurrent;
        tIncrement = atIncrement;
        tGrace = atGrace;
        iTimeout = aiTimeout;
    }

    /**
     * Update the clock after a move has been made.
     *
     * @param tElapsed time taken to make the move
     * @return the new clock
     */
    OsClock update(double tElapsed) {
        return update(tElapsed, true);
    }

    OsClock(CReader is) {
        double tIncrement = 0;
        tGrace = 120;

        tCurrent = ReadTime(is);

        if (is.peek() == '/') {
            is.ignore(1);
            // 0 is passed as a blank spot
            if (is.peek() == '/')
                tIncrement = 0;
            else
                tIncrement = ReadTime(is);
        }

        if (is.peek() == '/') {
            is.ignore(1);
            tGrace = ReadTime(is);
        }

        this.tIncrement = tIncrement;
    }

    static OsClock InIOS(CReader is) {
        char c;

        c = is.read();
        Require.eq(c, "c", '(');
        double tCurrent = is.readDoubleNoExponent() * 60;
        double tIncrement = is.readDoubleNoExponent();
        double tGrace = is.readDoubleNoExponent() * 60;

        c = is.read();
        Require.eq(c, "c", ')');

        return new OsClock(tCurrent, tIncrement, tGrace);
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();

        WriteTime(sb, (int) tCurrent);
        if (tIncrement != 0 || tGrace != 120) {
            sb.append("/");
            if (tIncrement != 0)
                WriteTime(sb, (int) tIncrement);
            if (tGrace != 120) {
                sb.append("/");
                WriteTime(sb, (int) tGrace);
            }
        }
        return sb.toString();
    }

    OsClock update(double tElapsed, boolean fIncludeIncrement) {
        double tCurrent = this.tCurrent - tElapsed;
        int iTimeout = this.iTimeout;

        // adjust for timeouts and grace periods
        if (tCurrent < 0) {
            if (iTimeout == 0) {
                tCurrent += tGrace;
                iTimeout = 1;
            }
            if (tCurrent < 0) {
                tCurrent = 0;
                iTimeout = 2;
            }
        }
        if (fIncludeIncrement && iTimeout < 2) {
            tCurrent += tIncrement;
        }

        return new OsClock(tCurrent, tIncrement, tGrace, iTimeout);
    }

    static double ReadTime(CReader is) {
        int i;
        char c;
        double t = 0;

        // parse into numbers
        // format is days.hours:minutes:seconds
        // leading 0s can be skipped
        for (i = 0; i < 4; i++) {
            t += is.readInt(0);
            c = is.peek();
            if (c == '.') {
                Require.eq(i, "i", 0);
                is.ignore(1);
                t = t * 24;
            } else if (c == ':') {
                is.ignore(1);
                t = t * 60;
            } else
                break;
        }

        return t;
    }

    static void WriteTime(StringBuilder sb, int nSeconds) {
        final DecimalFormat df2 = new DecimalFormat("00");
        if (nSeconds > 60) {
            int nMinutes = nSeconds / 60;
            nSeconds %= 60;
            if (nMinutes > 60) {
                int nHours = nMinutes / 60;
                nMinutes %= 60;
                sb.append(nHours).append(':').append(df2.format(nMinutes));
            } else {
                sb.append(nMinutes);
            }
            sb.append(':').append(df2.format(nSeconds));
        } else {
            sb.append(nSeconds);
        }
    }

    @Override public boolean equals(Object obj) {
        if (obj instanceof OsClock) {
            OsClock b = (OsClock) obj;
            return iTimeout == b.iTimeout &&
                    tCurrent == b.tCurrent &&
                    tGrace == b.tGrace &&
                    tIncrement == b.tIncrement;
        } else {
            return false;
        }
    }
}
