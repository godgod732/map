package com.example.godgo.map;

import java.math.BigInteger;


public class Listviewitem {
    private BigInteger dstlat;
    private BigInteger dstlon;
    private String commander;
    private BigInteger count;
    private int index;

    public int getIndex(){
        return index;
    }

    public BigInteger getCount() {
        return count;
    }

    public BigInteger getDstlat() {
        return dstlat;
    }

    public BigInteger getDstlon() {
        return dstlon;
    }

    public String getCommander() {
        return commander;
    }

    public Listviewitem(BigInteger dstlat, BigInteger dstlon, String commander, BigInteger count, int index) {
        this.dstlat = dstlat;
        this.dstlon = dstlon;
        this.commander = commander;
        this.count = count;
        this.index = index;
    }
}