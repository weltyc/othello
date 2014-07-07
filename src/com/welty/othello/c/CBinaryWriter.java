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

package com.welty.othello.c;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: May 9, 2009
 * Time: 8:15:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class CBinaryWriter {
    private final DataOutputStream out;

    public CBinaryWriter(String filename) {
        this(new File(filename));
    }

    public CBinaryWriter(ByteArrayOutputStream baos) {
        out = new DataOutputStream(baos);
    }

    public CBinaryWriter(File file) {
        this(file, false);
    }

    public CBinaryWriter(File file, boolean append) {
        try {
            out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file, append)));
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void writeLong(long a) {
        try {
            out.writeLong(Long.reverseBytes(a));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void writeInt(int a) {
        try {
            out.writeInt(Integer.reverseBytes(a));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void writeFloat(float a) {
        try {
            out.writeInt(Integer.reverseBytes(Float.floatToIntBits(a)));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void writeChar(char a) {
        try {
            out.writeChar(Character.reverseBytes(a));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void writeByte(byte a) {
        try {
            out.writeByte(a);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void writeShort(short a) {
        writeChar((char)a);
    }

    public void close() {
        try {
            out.close();
        }
        catch(IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void flush() {
        try {
            out.flush();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void writeBytes(byte[] bytes) {
        for (byte b : bytes) {
            writeByte(b);
        }
    }

    public void writeBoolean(boolean fSet) {
        writeInt(fSet?1:0);
    }
}
