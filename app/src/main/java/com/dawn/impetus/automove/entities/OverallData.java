package com.dawn.impetus.automove.entities;

/**
 * Created by Impetus on 2017/11/28.
 */
public class OverallData {

    private static OverallData datas = new OverallData();

    private String startTime;
    private String hoetName;
    private String ip;
    private String mac;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getHoetName() {
        return hoetName;
    }

    public void setHoetName(String hoetName) {
        this.hoetName = hoetName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public static OverallData getInstance(){
        return datas;
    }
}
