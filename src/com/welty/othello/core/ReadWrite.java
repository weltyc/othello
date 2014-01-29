package com.welty.othello.core;

import com.welty.othello.c.CBinaryReader;
import com.welty.othello.c.CBinaryWriter;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: May 10, 2009
 * Time: 11:34:34 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ReadWrite {
    void Write(CBinaryWriter out);

    void Read(CBinaryReader in);

    int sizeof();
}
