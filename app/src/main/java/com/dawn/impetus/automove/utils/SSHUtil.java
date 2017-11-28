package com.dawn.impetus.automove.utils;

/**
 * Created by Administrator on 2017/11/13 0013.
 * 连接linux服务器用工具类，用单例实现，执行命令并返回文本结果
 */

import android.content.Context;
import android.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class SSHUtil {

    public static String TAG = SSHUtil.class.getName();

    private String charset = "UTF-8"; // 设置编码格式
    private String username; // 用户名
    private String password; // 登录密码
    private String host = "116.236.169.100"; // 主机IP
    private int port = 22; //端口
    private JSch jsch = new JSch();//连接ssh
    private Session session;


    //单例模式
    private static SSHUtil instance = new SSHUtil();

    public static SSHUtil getInstance() {

        return instance;

    }


    /**
     * 初始化
     */
    private SSHUtil() {

    }

    /**
     * 设置用户名密码
     */
    private synchronized void getUser() {

        //取出用户名和密码并设置,生命周期是全局的
        Context context = ContextApplication.getAppContext();
        username = SPUtil.get(context, "userName", "").toString();
        password = SPUtil.get(context, "passWord", "").toString();
        Log.i(TAG, "user is" + username + " psw is" + password);
    }

    /**
     * 连接到指定的IP
     *
     * @throws JSchException
     */
    public synchronized void  connect() throws JSchException {
        getUser();
        if (session == null || !session.isConnected()) {
            session = jsch.getSession(username, host, port);
            session.setPassword(password);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            Log.i(TAG, "connected");
        }
    }

    /**
     * 关闭连接
     */
    public synchronized void disconnect() throws Exception {
        if (session != null && session.isConnected()) {
            session.disconnect();
            Log.i(TAG, "destroy connect");
        }
    }

    /**
     * 执行一条命令
     */
    public synchronized String execCmd(String command) throws Exception {

        connect();

        BufferedReader reader = null;
        Channel channel = null;

        channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);
        channel.setInputStream(null);
        ((ChannelExec) channel).setErrStream(System.err);
        channel.connect();
        InputStream in = channel.getInputStream();
        reader = new BufferedReader(new InputStreamReader(in,
                Charset.forName(charset)));
        StringBuffer buf = new StringBuffer();
        String line;
        line=reader.readLine();
        while (line!=null)
        {
            buf.append(line);
            buf.append("\n");
            line=reader.readLine();
        }
        channel.disconnect();
        in.close();
        return buf.toString();
    }

}
