package com.welty.othello.gdk;

import com.orbanova.common.misc.Require;
import com.welty.othello.c.CReader;

import java.text.DecimalFormat;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: May 2, 2009
 * Time: 3:49:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class OsClock {
    public double tCurrent;
    private double tIncrement;
    private double tGrace;
    private int iTimeout;

    OsClock() {
    }

    public OsClock(double tCurrent) {
        this(tCurrent, 0);
    }

    OsClock(double tCurrent, double tIncrement) {
        this(tCurrent, tIncrement, 120);
    }

    OsClock(double tCurrent, double tIncrement, double tGrace) {
        this(tCurrent, tIncrement, tGrace, 0);
    }

    private OsClock(double atCurrent, double atIncrement, double atGrace, int aiTimeout) {
        tCurrent = atCurrent;
        tIncrement = atIncrement;
        tGrace = atGrace;
        iTimeout = aiTimeout;
    }

    /**
     * Copy constructor
     *
     * @param clock
     */
    public OsClock(OsClock clock) {
        this(clock.tCurrent, clock.tIncrement, clock.tGrace, clock.iTimeout);
    }

    void Update(double tElapsed) {
        Update(tElapsed, true);
    }

    void In(CReader is) {
        Clear();
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
    }

    CReader InIOS(CReader is) {
        char c;

        c = is.read();
        Require.eq(c, "c", '(');
        tCurrent = is.readDoubleNoExponent();
        tCurrent *= 60;
        tIncrement = is.readDoubleNoExponent();
        tGrace = is.readDoubleNoExponent();
        tGrace *= 60;
        c = is.read();
        Require.eq(c, "c", ')');

        return is;
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

    void Clear() {
        iTimeout = 0;
        tCurrent = tGrace = tIncrement = 0;
    }

    void Update(double tElapsed, boolean fIncludeIncrement) {
        tCurrent -= tElapsed;

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
        if (fIncludeIncrement && iTimeout < 2)
            tCurrent += tIncrement;
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

    boolean EqualsToNearestSecond(final OsClock b) {
        return iTimeout == b.iTimeout &&
                (int) tCurrent == (int) b.tCurrent &&
                (int) tGrace == (int) b.tGrace &&
                (int) tIncrement == (int) b.tIncrement;
    }

}
