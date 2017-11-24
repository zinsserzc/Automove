package com.dawn.impetus.automove.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Impetus on 2017/11/24.
 */
public class VoiceUtil implements EventListener{

    //Baidu语音
    private EventManager asr;
    //private boolean isStart = false;
    private String msgContent = "";
    private Activity activity;

    public VoiceUtil(Activity activity){
        this.activity = activity;
        //初始化百度语音manager
        initPermission();
        asr = EventManagerFactory.create(activity,"asr");
        asr.registerListener(this);
    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String permissions[] = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm :permissions){
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(activity, perm)) {
                toApplyList.add(perm);
                //进入到这里代表没有权限.

            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()){
            ActivityCompat.requestPermissions(activity, toApplyList.toArray(tmpList), 123);
        }

    }



    //语音识别回调事件，处理各种结果
    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        //String logTxt = "name: " + name;
        //自动识别
//        if (params != null && !params.isEmpty()) {
//            logTxt += " ;params :" + params;
//        }
//        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
//            if (params.contains("\"nlu_result\"")) {
//                if (length > 0 && data.length > 0) {
//                    logTxt += ", 语义解析结果：" + new String(data, offset, length);
//                }
//            }
//        } else if (data != null) {
//            logTxt += " ;data length=" + data.length;
//        }
        if(name.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)){
            // 引擎就绪，可以说话，一般在收到此事件后通过UI通知用户可以说话了
            showMessage("请开始你的表演");
        }

        if(name.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)){
            // 识别结束
            showMessage("请等待结果");
            try{
                JSONObject resultJson = new JSONObject(msgContent);
                showMessage(resultJson.getString("best_result"));
            }catch (JSONException e){
                showMessage("识别失败！");
            }
        }

        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
            if(params != null && !params.isEmpty()){
                msgContent = params;
            }
        }
        //showMessage(logTxt);
    }

    private void showMessage(String text){
        Toast.makeText(activity,text,Toast.LENGTH_SHORT).show();
    }

    public void start(){
        Map<String,Object> params = new LinkedHashMap<>();
        String event = null;
        event = SpeechConstant.ASR_START;

        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME,false);
        params.put(SpeechConstant.VAD,SpeechConstant.VAD_TOUCH);
        String json = null; //可以替换成自己的json
        json = new JSONObject(params).toString();
        Toast.makeText(activity,"开始说话",Toast.LENGTH_SHORT).show();
        asr.send(event, json, null, 0, 0);
    }

    public void stop(){
        Toast.makeText(activity,"结束说话",Toast.LENGTH_SHORT).show();
        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0); //
    }

    private void cancel(){
        asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
    }

    public void destroy(){
        cancel();
        asr = null;
    }


}
