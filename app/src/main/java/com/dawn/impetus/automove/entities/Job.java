package com.dawn.impetus.automove.entities;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/11/30 0030.
 * 作业实体
 */
public class Job implements Serializable{
    private String jobName;//名称
    private String user;//使用者
    private List<String> excluNodes;//占用节点
    private int excluCoreNum;//占用核数
    private String time;//时间
    private String state;//状态



    public Job() {
    }

    public String getUser() {
        return user;
    }




    public void setUser(String user) {
        this.user = user;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public List<String> getExcluNodes() {
        return excluNodes;
    }

    public Job(String jobName, String user, List<String> excluNodes, int excluCoreNum, String time, String state) {
        this.jobName = jobName;
        this.user = user;
        this.excluNodes = excluNodes;
        this.excluCoreNum = excluCoreNum;
        this.time = time;
        this.state = state;
    }

    @Override
    public String toString() {
        return "Job{" +
                "jobName='" + jobName + '\'' +
                ", user='" + user + '\'' +
                ", excluNodes=" + excluNodes +
                ", excluCoreNum='" + excluCoreNum + '\'' +
                ", time='" + time + '\'' +
                ", state='" + state + '\'' +

                '}';
    }

    public int getExcluCoreNum() {
        return excluCoreNum;
    }

    public void setExcluCoreNum(int excluCoreNum) {
        this.excluCoreNum = excluCoreNum;
    }

    public void setExcluNodes(List<String> excluNodes) {
        this.excluNodes = excluNodes;
    }

}
