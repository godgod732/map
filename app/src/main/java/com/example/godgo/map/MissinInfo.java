package com.example.godgo.map;

public class MissinInfo {
    private String droneAddr;
    private Integer missionIndex;
    public MissinInfo(String addr, Integer idx){
        this.droneAddr = addr;
        this.missionIndex = idx;
    }

    public Integer getMissionIndex() {
        return missionIndex;
    }
    public String getDroneAddr(){
        return droneAddr;
    }
}
