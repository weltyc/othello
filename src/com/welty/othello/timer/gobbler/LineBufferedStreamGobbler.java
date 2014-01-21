package com.welty.othello.timer.gobbler;

import java.io.*;

/**
 * A thread that gobbles input from InputStream by repeated calls to readLine(), for use by Runtime.exec().
 * <PRE>
 * User: Chris
 * Date: Jul 23, 2009
 * Time: 8:14:40 PM
 * </PRE>
 */
public abstract class LineBufferedStreamGobbler extends AbstractStreamGobbler {
    /**
     * Creates a StreamGobbler thread. The gobbler has not been started; the caller must call start() to start it.
     * @param is inputStream from the Exec process
     */
    protected LineBufferedStreamGobbler(InputStream is) {
        super(is);
    }

    /**
     * creates readers to handle the text created by the external program
     */
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                handleLine(line);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        try {
            close();
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * called for each line of text read by the gobbler
     */
    protected abstract void handleLine(String line);
}
