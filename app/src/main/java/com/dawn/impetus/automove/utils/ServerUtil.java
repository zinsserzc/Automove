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
            Log.e(TAG,e.getMessage());
            return false;
        }
    }


    /**
     * 获取开机时间
     *
     * @return
     */
    public static String getOptime() {

        return "";
    }

}
