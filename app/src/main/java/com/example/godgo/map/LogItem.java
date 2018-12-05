package com.example.godgo.map;

public class LogItem {
    private double dstlat;
    private double dstlon;
    private String commander;
    private double count;
    private int index;

    public int getIndex(){
        return index;
    }
    public double getCount() {
        return count;
    }

    public double getDstlat() {
        return dstlat;
    }

    public double getDstlon() {
        return dstlon;
    }

    public String getCommander() {
        return commander;
    }

    public LogItem(double dstlat, double dstlon, String commander, double count,int index) {
        this.dstlat = dstlat;
        this.dstlon = dstlon;
        this.commander = commander;
        this.count = count;
        this.index = index;
    }
}