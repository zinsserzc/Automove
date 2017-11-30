package com.dawn.impetus.automove.entities;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/30 0030.
 * 节点实体类
 */
public class Node implements Serializable{

    private String name;
    private String state;
    private int np;
    //private String properties;
    //private String ntype;
    private String jobs;
    //private String status;
    //private String mom_service_port;
    //private String mom_manager_port;

    public int getUsedCoreNum() {
        return usedCoreNum;
    }

    public void setUsedCoreNum(int usedCoreNum) {
        this.usedCoreNum = usedCoreNum;
    }

    public int getUnUsedCoreNum() {
        return unUsedCoreNum;
    }

    public void setUnUsedCoreNum(int unUsedCoreNum) {
        this.unUsedCoreNum = unUsedCoreNum;
    }


    private int usedCoreNum;//已使用的核数
    private int unUsedCoreNum;//可用的核数




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getNp() {
        return np;
    }

    public void setNp(int np) {
        this.np = np;
    }



    public String getJobs() {
        return jobs;
    }

    public void setJobs(String jobs) {
        this.jobs = jobs;
    }

}
