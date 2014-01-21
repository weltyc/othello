package com.welty.othello.timer.gobbler;

import java.io.IOException;
import java.io.InputStream;

/**
 * <PRE>
 * User: Chris
 * Date: Jul 24, 2009
 * Time: 11:06:53 PM
 * </PRE>
 */
public abstract class CharBufferedStreamGobbler extends AbstractStreamGobbler {
    protected CharBufferedStreamGobbler(InputStream is) {
        super(is);
    }

    @Override public void run() {
        int c;
        try {
            while (-1 != (c=is.read())) {
                handleChar((char)c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            close();
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    protected abstract void handleChar(char c);
}
