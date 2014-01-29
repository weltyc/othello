package com.welty.othello.c;

import com.orbanova.common.misc.Require;

import java.io.*;

/**
 * Mimic the C class Binary Reader.
 *
 * This class assumes data is stored in little-endian (e.g. Intel) format, and thus
 * reverses the bytes to read it in big-endian (java) format.
 */
public class CBinaryReader {
    private final DataInputStream in;

    /**
     * Construct a binary file reader
     *
     * @throws IllegalArgumentException if file doesn't exist
     */
    public CBinaryReader(String filename) {
        try {
            in = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public CBinaryReader(InputStream bais) {
        in = new DataInputStream(bais);
    }

    public long readLong() {
        try {
            return Long.reverseBytes(in.readLong());
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public int readInt() {
        try {
            return Integer.reverseBytes(in.readInt());
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public char readChar() {
        try {
            return Character.reverseBytes(in.readChar());
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public byte readByte() {
        try {
            return in.readByte();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void close() {
        try {
            in.close();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public byte[] readBytes(int nBytes) {
        byte[] bytes = new byte[nBytes];
        try {
            int nRead = in.read(bytes);
            Require.eq(nRead, "nRead", nBytes);
            return bytes;
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public short readShort() {
        try {
            return Short.reverseBytes(in.readShort());
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public boolean readBoolean() {
        return readInt() != 0;
    }

    public int available() {
        try {
            return in.available();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public float readFloat() {
        final int i = readInt();
        return Float.intBitsToFloat(i);
    }
}
