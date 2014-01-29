package com.welty.othello.core;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: May 1, 2009
 * Time: 9:52:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class BobHash {
    public int a;
    public int b;
    public int c;
    private int d;

    public BobHash(int a, int b, int c, int d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

//////////////////////////////////////////
// hash functions
//////////////////////////////////////////

    /**
     *    bob jenkins hash.
     * usage example with four u4s of data:
     *	a=b=0; c=data[0];
     *	bobMix(a,b,c);
     *	a+=data[1]; b+=data[2]; c+=data[3];
     *	bobMix(a,b,c);
     *	return c;
     */

    public void mix() {
        a-=b; a-=c; a^= (c>>>13);
        b-=c; b-=a; b^= (a<<8);
        c-=a; c-=b; c^= (b>>>13);
        a-=b; a-=c; a^= (c>>>12);
        b-=c; b-=a; b^= (a<<16);
        c-=a; c-=b; c^= (b>>>5);
        a-=b; a-=c; a^= (c>>>3);
        b-=c; b-=a; b^= (a<<10);
        c-=a; c-=b; c^= (b>>>15);
    }

/**
 * bob jenkins older hash, 'lookup'. May not be as great but it works on 4 bytes at a time
 */
    void lookup() {
        a+=d; d+=a; a^=(a>>>7);
        b+=a; a+=b; b^=(b<<13);
        c+=b; b+=c; c^=(c>>>17);
        d+=c; c+=d; d^=(d<<9);
        a+=d; d+=a; a^=(a>>>3);
        b+=a; a+=b; b^=(b<<7);
        c+=b; b+=c; c^=(c>>>15);
        d+=c; c+=d; d^=(d<<11);
    }

    static int hash4(int a, int b, int c, int d){
        final BobHash hash = new BobHash(a, b, c, d);
        hash.lookup();
        return hash.d;
    }

    public int getA() {
        return a;
    }
}
