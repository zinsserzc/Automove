package com.dawn.impetus.automove.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;
import java.util.List;

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
     * 获取系统版本
     *
     * @return
     */
    public static String getSysVersion() {
        String res = null;
        try {
            res = ssh.execCmd("cat /etc/issue | sed -n '1p;1q'").trim();

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            return res;
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
            res = ssh.execCmd("date -d \"$(awk -F. '{print $1}' /proc/uptime) second ago\" +\"%Y-%m-%d %H:%M:%S\"").trim();

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            return res;
        }
    }


    /**
     * 获取系统运行时间
     *
     * @return 分别代表 天，时，分
     */
    public static String[] getRunTime() {
        String[] res = null;
        try {
            res = ssh.execCmd("cat /proc/uptime| awk -F. '{run_days=$1 / 86400;run_hour=($1 % 86400)/3600;run_minute=($1 % 3600)/60;printf(\"%d %d %d\",run_days,run_hour,run_minute)}'").trim().split(" ");
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
            res = ssh.execCmd("hostname").trim();
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
            res = ssh.execCmd("ifconfig eth0 | grep \"inet addr\" | awk '{ print $2}' | awk -F: '{print $2}'").trim();
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
            res = ssh.execCmd("cat /sys/class/net/eth0/address").trim();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            return res;
        }
    }

    /**
     * 获取总节点数目
     *
     * @return
     */
    public static String getNodeNum() {

        String res = null;
        try {
            res = ssh.execCmd("pbsnodes|grep -c  '^comput'").trim();

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            return res;
        }


    }

    /**
     * Json格式获取节点状态
     * 里边可以获取节点所有信息
     *
     * @return
     */
    public static JSONObject getNodeStates() {

        JSONObject res = new JSONObject();
        String xml = "";
        try {
            xml = ssh.execCmd("pbsnodes -x");
            res = XML.toJSONObject(xml);
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
            res = ssh.execCmd("cat /proc/cpuinfo|grep name|uniq").trim();
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
            res = ssh.execCmd("cat /proc/cpuinfo|grep MHz|head -1|cut -d':' -f 2").trim();
            res = res.substring(0, 5) + "MHz";
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            return res;
        }
    }

    /**
     * 获取cpu核数（总逻辑cpu数）
     * <p/>
     * # 总核数 = 物理CPU个数 X 每颗物理CPU的核数
     * # 总逻辑CPU数 = 物理CPU个数 X 每颗物理CPU的核数 X 超线程数
     * # 查看物理CPU个数
     * cat /proc/cpuinfo| grep "physical id"| sort| uniq| wc -l
     * <p/>
     * # 查看每个物理CPU中core的个数(即核数)
     * cat /proc/cpuinfo| grep "cpu cores"| uniq
     * <p/>
     * # 查看逻辑CPU的个数
     * cat /proc/cpuinfo| grep "processor"| wc -l
     *
     * @return
     */
    public static String getCPUCores() {
        String res = null;
        try {
            res = ssh.execCmd("cat /proc/cpuinfo| grep processor| wc -l").trim();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            return res;
        }
    }

    /**
     * 获取内存大小
     *
     * @return
     */
    public static String getMemSize() {

        String res = null;
        try {
            res = ssh.execCmd("cat /proc/meminfo|grep MemTotal|cut -d':' -f 2").trim();

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            return res;
        }


    }

    /**
     * 获取硬盘信息
     * 第一列表示路径
     * 第二列表示总大小
     * 第三列表示占用大小
     * 第四列表示使用率
     *
     * @return
     */
    public static String[][] getDiskInfo() {

        String[][] res = new String[3][4];
        try {
            res[0] = ssh.execCmd("df -h|awk 'NR==2{print $6\" \"$2\" \"$3\" \"$5}'").split(" ");
            res[1] = ssh.execCmd("df -h|awk 'NR==4{print $6\" \"$2\" \"$3\" \"$5}'").split(" ");
            res[2] = ssh.execCmd("df -h|awk 'NR==6{print $6\" \"$2\" \"$3\" \"$5}'").split(" ");

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            return res;
        }


    }

    /**
     * 获取cpu使用率
     *
     * @return
     */
    public static String getCPUUsage() {

        String res = null;
        try {
            res = ssh.execCmd("mpstat|awk 'NR==4{print 100- $12\"%\"}'").trim();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            return res;
        }

    }

    /**
     * 获取内存使用率
     *
     * @return
     */
    public static String getMemUsage() {

        String res = null;
        try {
            res = ssh.execCmd("free -m|sed -n '2p'|awk '{print $3/$2*100\"%\"}'").trim();

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            return res;
        }

    }

    /**
     * 获取cpu温度
     *
     * @return
     */
    public static String getCPUTemp() {

        String res = null;
        try {
            res = ssh.execCmd("cat /sys/class/hwmon/hwmon0/device/temp1_input").trim();

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            return res;
        }

    }

/////////////////////////////////////////作业信息/////////////////////////////////////////


/////////////////////////////////////////用户管理//////////////////////////////////////////


    /**
     * 获取所有(普通)用户
     *
     * @return
     */
    public static List<String> getUserList() {

        List<String> res = new ArrayList<>();
        try {
            String[] oriString = ssh.execCmd("gawk -F: '/(home).*(bash$)/{print $1}' /etc/passwd").split("\n");
            for (String a : oriString) {
                res.add(a);
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            return res;
        }

    }

    /**
     * 判断登录用户是否是root用户
     * @return
     */
    public static boolean isRootUser() {
        Context context = ContextApplication.getAppContext();
        String userName="";
        userName = SPUtil.get(context, "userName", "").toString();

        boolean res =false;
        try {
            String str="";
            str = ssh.execCmd("cat /etc/passwd|grep ^"+userName+"|awk -F':' '{print $3}'").trim();
            if(Integer.valueOf(str)<500)
                res=true;

        }catch (Exception e)
        {
            Log.e(TAG,e.getLocalizedMessage());
        }finally {

            return  res;
        }

    }


}
