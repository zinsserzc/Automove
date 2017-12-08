package com.dawn.impetus.automove.entities;

import android.util.Log;

import java.util.Map;

/**
 * Created by Impetus on 2017/11/28.
 */
public class OverallData {

    private static OverallData datas = new OverallData();

    private String startTime = null;
    private String hostName = null;
    private String ip = null;
    private String mac = null;
    private String[] runningTime = null;

    //主机
    private String host = null;
    //节点
    private String node1 = null;
    private String node2 = null;
    private String node3 = null;
   //GPU
    private String GPU1 = null;
    private String GPU2 = null;
    private String GPU3 = null;
    //交换机
    private String changer = null;
    //GPU型号
    private String CPUNmae = null;

    //主频
    private String dominateFrequency = null;
    //核数
    private String coreCount = null;
    //内存型号
    private String memoryType = null;
    //大小
    private String size;
    //硬盘型号
    private String hardTypeTv;
    //mount
    private String mount1;
    private String mount2;
    private String mount3;
    //size
    private String size1;
    private String size2;
    private String size3;
    //used
    private String used1;
    private String used2;
    private String used3;
    //占用率
    private String usePer1;
    private String usePer2;
    private String usePer3;

    //CPU使用率
    private String CPUUsage;
    //RAM使用率
    private String RAMUsage;
    //CPU温度
    private float CPUtemp;

    public float getCPUtemp() {
        return CPUtemp;
    }

    public void setCPUtemp(float CPUtemp) {
        this.CPUtemp = CPUtemp;
    }

    public String getRAMUsage() {
        return RAMUsage;
    }

    public void setRAMUsage(String RAMUsage) {
        this.RAMUsage = RAMUsage;
    }

    public String getCPUUsage() {
        return CPUUsage;
    }

    public void setCPUUsage(String CPUUsage) {
        this.CPUUsage = CPUUsage;
    }

    public void setHardData(String[][] info){
        this.mount1 = info[0][0];
        this.mount2 = info[1][0];
        this.mount3 = info[2][0];
        this.size1 = info[0][1];
        this.size2 = info[1][1];
        this.size3 = info[2][1];
        this.used1 = info[0][2];
        this.used2 = info[1][2];
        this.used3 = info[2][2];
        this.usePer1 = info[0][3];
        this.usePer2 = info[1][3];
        this.usePer3 = info[2][3];
    }

    public String getMount1() {
        return mount1;
    }

    public String getMount2() {
        return mount2;
    }

    public String getMount3() {
        return mount3;
    }

    public String getSize1() {
        return size1;
    }

    public String getSize2() {
        return size2;
    }

    public String getSize3() {
        return size3;
    }

    public String getUsed1() {
        return used1;
    }

    public String getUsed2() {
        return used2;
    }

    public String getUsed3() {
        return used3;
    }

    public String getUsePer1() {
        return usePer1;
    }

    public String getUsePer2() {
        return usePer2;
    }

    public String getUsePer3() {
        return usePer3;
    }

    public void setNodes(Map<String,String> map){
//        Log.e("exclusive",map.get("exclusive"));
//        Log.e("busy",map.get("busy"));
        int exclusive = Integer.parseInt(map.get("exclusive").trim());
        int busy = Integer.parseInt(map.get("busy").trim());
        this.node1 = map.get("free");
        this.node2 = String.valueOf(busy + exclusive);
        this.node3 = map.get("down");
    }

    public String getNode1() {
        return node1==null?"":node1;
    }

    public String getNode2() {
        return node2==null?"":node2;
    }

    public String getNode3() {
        return node3==null?"":node3;
    }

    public String getHost() {
        return host==null?"":host;
    }

    public void setHost(String host) {
       // Log.e("host",host);
        this.host = host;
    }

    public String getChanger() {
        return changer==null?"":changer;
    }

    public void setChanger(String changer) {
        this.changer = changer;
    }

    public String getCPUNmae() {
        return CPUNmae==null?"":CPUNmae;
    }

    public void setCPUNmae(String CPUNmae) {
        this.CPUNmae = CPUNmae;
    }

    public String getDominateFrequency() {
        return dominateFrequency==null?"":dominateFrequency;
    }

    public void setDominateFrequency(String dominateFrequency) {
        this.dominateFrequency = dominateFrequency;
    }

    public String getCoreCount() {
        return coreCount==null?"":coreCount;
    }

    public void setCoreCount(String coreCount) {
        this.coreCount = coreCount;
    }

    public String getMemoryType() {
        return memoryType==null?"":memoryType;
    }

    public void setMemoryType(String memoryType) {
        this.memoryType = memoryType;
    }

    public String getSize() {
        return size==null?"":size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getHardTypeTv() {
        return hardTypeTv==null?"":hardTypeTv;
    }

    public void setHardTypeTv(String hardTypeTv) {
        this.hardTypeTv = hardTypeTv;
    }


    public String[] getRunningTime() {
        return runningTime==null?new String[3]:runningTime;
    }

    public void setRunningTime(String[] runningTime) {
        //Log.e("runningTimeData",runningTime[0]+" "+runningTime[1]+" "+runningTime[2]);
        this.runningTime = runningTime;
    }

    public String getStartTime() {
        return startTime==null?"":startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getHostName() {
        return hostName==null?"":hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getIp() {
        return ip==null?"":ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac==null?"":mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public static OverallData getInstance(){
        return datas;
    }
}
