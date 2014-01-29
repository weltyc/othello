package com.welty.othello.lp;

/**
 * Created by IntelliJ IDEA.
* User: HP_Administrator
* Date: Jun 21, 2009
* Time: 11:09:52 PM
* To change this template use File | Settings | File Templates.
*/
public class SystemOutLinePrinter extends PrintStreamLinePrinter {
    public SystemOutLinePrinter() {
        super(System.out);
    }
}

