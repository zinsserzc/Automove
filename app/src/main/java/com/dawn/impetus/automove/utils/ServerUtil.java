package com.dawn.impetus.automove.utils;

import android.util.Log;

/**
 * Created by Administrator on 2017/11/20 0020.
 * 此类用于获取服务器数据
 */
public class ServerUtil {

    public static final String TAG = ServerUtil.class.getName();

    private static SSHUtil ssh = SSHUtil.getInstance();

    /**
     * 连接服务器
     *
     * @return
     */
    public static boolean connect() {
        try {
            ssh.connect();
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }


    /**
     * 关闭连接
     */
    public static void disconnect() {

        try {
            ssh.disconnect();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }


    }

    /**
     * 获取开机时间
     *
     * @return
     */
    public static String getOpTime() {
        String res = null;
        try {
            res = ssh.execCmd("date -d \"$(awk -F. '{print $1}' /proc/uptime) second ago\" +\"%Y-%m-%d %H:%M:%S\"");

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            return res;
        }
    }

    /**
     * 获取系统运行时间
     *
     * @return
     */
    public static String getRunTime() {
        String res = null;
        try {
            res = ssh.execCmd("cat /proc/uptime| awk -F. '{run_days=$1 / 86400;run_hour=($1 % 86400)/3600;run_minute=($1 % 3600)/60;run_second=$1 % 60;printf(\"%d天%d时%d分%d秒\",run_days,run_hour,run_minute,run_second)}'");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            return res;
        }
    }

    /**
     * 获取主机名
     *
     * @return
     */
    public static String getHsotName() {
        String res = null;
        try {
            res = ssh.execCmd("hostname");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            return res;
        }
    }

    /**
     * 获取ip地址
     *
     * @return
     */
    public static String getIP() {
        String res = null;
        try {
            res = ssh.execCmd("ifconfig eth0 | grep \"inet addr\" | awk '{ print $2}' | awk -F: '{print $2}'");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            return res;
        }
    }

    /**
     * 获取mac地址
     *
     * @return
     */
    public static String getMAC() {
        String res = null;
        try {
            res = ssh.execCmd("cat /sys/class/net/eth0/address");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            return res;
        }
    }

    /**
     * 获取cpu名称
     *
     * @return
     */
    public static String getCPUName() {
        String res = null;
        try {
            res = ssh.execCmd("cat /proc/cpuinfo|grep name|uniq");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            return res;
        }
    }


    /**
     * 获取cpu主频
     *
     * @return
     */
    public static String getCPUHZ() {
        String res = null;
        try {
            res = ssh.execCmd("cat /proc/cpuinfo|grep MHz|head -1|cut -d':' -f 2");
            res = res.substring(0,5)+"MHz";
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            return res;
        }
    }

    /**
     * 获取cpu核数（总逻辑cpu数）
     *
     * # 总核数 = 物理CPU个数 X 每颗物理CPU的核数
     # 总逻辑CPU数 = 物理CPU个数 X 每颗物理CPU的核数 X 超线程数
     * # 查看物理CPU个数
     cat /proc/cpuinfo| grep "physical id"| sort| uniq| wc -l

     # 查看每个物理CPU中core的个数(即核数)
     cat /proc/cpuinfo| grep "cpu cores"| uniq

     # 查看逻辑CPU的个数
     cat /proc/cpuinfo| grep "processor"| wc -l
     * @return
     */
    public static String getCPUCores() {
        String res = null;
        try {
            res = ssh.execCmd("cat /proc/cpuinfo| grep processor| wc -l");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            return res;
        }
    }

    /**
     * 获取内存大小
     * @return
     */
    public static String getMemSize(){

        String res = null;
        try {
            res = ssh.execCmd("cat /proc/meminfo|grep MemTotal|cut -d':' -f 2").trim();

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            return res;
        }


    }

}
