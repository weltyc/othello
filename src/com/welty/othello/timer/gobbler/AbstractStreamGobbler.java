package com.welty.othello.timer.gobbler;

import java.io.Closeable;
import java.io.InputStream;

/**
 * <PRE>
 * User: Chris
 * Date: Jul 24, 2009
 * Time: 11:06:08 PM
 * </PRE>
 */
public abstract class AbstractStreamGobbler extends Thread implements Closeable {
    InputStream is;

    AbstractStreamGobbler(InputStream is) {
        this.is = is;
    }
}
