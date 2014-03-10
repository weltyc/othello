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
     * A clock that lasts for a long time. The exact amount of time may change from release to release.
     */
    public static final OsClock LONG = new OsClock(24 * 60 * 60);

    /**
     * Remaining time for this move, in seconds
     */
    public final double tCurrent;
    /**
     * Amount added to clock after the player makes a legal move, in seconds
     */
    private final double tIncrement;
    /**
     * Amount added to the clock if the player times out when iTimeout=0, in seconds
     */
    private final double tGrace;

    /**
     * Player timeout status:
     * 0 = never timed out
     * 1 = timed out once
     * 2 = timed out more than once
     */
    private final int iTimeout;

    /**
     * Create a clock with no increment and a 2 minute grace period
     *
     * @param tCurrent time until first timeout, in seconds.
     */
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
     * <p/>
     * OsClock is immutable, so this creates a new clock.
     *
     * @param tElapsed time taken to make the move
     * @return the new clock
     */
    public OsClock update(double tElapsed) {
        return update(tElapsed, true);
    }

    /**
     * Create a clock from the text, as given in a GGF format game
     * @param s text of the clock
     */
    public OsClock(String s) {
        this(new CReader(s));
    }

    OsClock(CReader is) {
        double tIncrement = 0;

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
        } else {
            tGrace = 120;
        }

        this.tIncrement = tIncrement;
        this.iTimeout = 0;
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

        writeTime(sb, tCurrent);
        if (tIncrement != 0 || tGrace != 120) {
            sb.append("/");
            if (tIncrement != 0)
                writeTime(sb, tIncrement);
            if (tGrace != 120) {
                sb.append("/");
                writeTime(sb, tGrace);
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

    static void writeTime(StringBuilder sb, double nSeconds) {
        if (nSeconds >= 60) {
            final DecimalFormat df2 = new DecimalFormat("00");
             int nMinutes = (int)Math.floor(nSeconds / 60);
            nSeconds %= 60;
            if (nMinutes >= 60) {
                int nHours = nMinutes / 60;
                nMinutes %= 60;
                sb.append(nHours).append(':').append(df2.format(nMinutes));
            } else {
                sb.append(nMinutes);
            }

//            String secondString = Double.toString(nSeconds);
//            int i = secondString.indexOf('.');
//            if (i==1) {
//                secondString = "0" + secondString;
//            }
            sb.append(':').append(new DecimalFormat("00.###").format(nSeconds));
        } else {
            sb.append(new DecimalFormat("0.###").format(nSeconds));
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

    /**
     * Get the time allowed after the first timeout
     *
     * @return grace time, in seconds
     */
    public double getGraceTime() {
        return tGrace;
    }

    private static String[] displaySuffixes = {"", " (overtime)", " (timeout)"};

    public String toDisplayString() {
        int t = (int) tCurrent;
        final int s = t % 60;
        t /= 60;
        final int m = t % 60;
        t /= 60;
        final int h = t;
        final String suffix = displaySuffixes[iTimeout];
        if (h > 0) {
            return String.format("%d:%02d:%02d%s", h, m, s, suffix);
        } else {
            return String.format("%d:%02d%s", m, s, suffix);
        }

    }

    /**
     * Player timeout status:
     * 0 = never timed out
     * 1 = timed out once, in grace period
     * 2 = timed out after grace period
     */
    public int getITimeout() {
        return iTimeout;
    }
}
