package com.dawn.impetus.automove.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.dawn.impetus.automove.R;
import com.dawn.impetus.automove.activity.HomeActivity;
import com.dawn.impetus.automove.fragments.WorkFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
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

    private HashMap<String,String> map ;
    private JSONObject json = new JSONObject();
    private HomeActivity homeActivity;

    public VoiceUtil(Activity activity){
        this.activity = activity;
        this.homeActivity = (HomeActivity)activity;
        //初始化百度语音manager
        initPermission();
        asr = EventManagerFactory.create(activity,"asr");
        asr.registerListener(this);
        map = new HashMap<>();
        try {
            json.put("type", "wordcorrect");
            json.put("modelid", "0");
            json.put("from", "test");
        }catch (Exception e){

        }
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
            showMessage("请开始说指令");
        }

        if(name.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)){
            // 识别结束
            //showMessage("请等待结果");
            try{
                JSONObject resultJson = new JSONObject(msgContent);
                String result = resultJson.getString("best_result");
                //showMessage(resultJson.getString("best_result"));
                json.put("value",result);
                StringBuilder num = new StringBuilder();
                for(int i = 0; i < result.length(); i++){
                    if(result.charAt(i) <58 && result.charAt(i) > 47){
                        num.append(result.charAt(i));
                    }
                }
                handleMessage(num.toString());
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
        Toast.makeText(activity,"语音模块启动中",Toast.LENGTH_SHORT).show();
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

    private void handleMessage(String numS){
        try {
           // JSONObject result = new JSONObject(HttpUtil.postRequest(HttpUtil.BASE_URL, map));
            Log.e("json",json.toString());
            String result = HttpUtil.doPost(HttpUtil.BASE_URL, json);

            Log.e("return voice",result);
            //showMessage(result);
            JSONObject jsonObject = new JSONObject(result);
            String command = jsonObject.getString("return");
            Log.e("command",command);
            String[] returns = command.split(",");
            String ans = returns[1];
            Log.e("ans",ans);
            if("\"总览\"".equals(ans)){
                homeActivity.getTab().setCurrentTab(2);
            }
            if("\"监控\"".equals(ans)){
                homeActivity.getTab().setCurrentTab(1);
            }
            if("\"作业\"".equals(ans)){
                //Log.e("作业","in");
                homeActivity.getTab().setCurrentTab(0);
            }

            if("\"杀死作业\"".equals(ans)){
                //Log.e("作业","in");

                homeActivity.getTab().setCurrentTab(0);
                String tag = homeActivity.getTab().getCurrentTabTag();
                Log.e("fragmentworktag",tag);
                WorkFragment fragment = (WorkFragment)homeActivity.getSupportFragmentManager().findFragmentByTag(tag);
                Log.e("fragmentwork",fragment.toString());
                if(fragment.jobExist(numS)){
                    fragment.showDeleteJobDialog(fragment.getPositionByJobName(numS),numS);
                }else{
                    showMessage("不存在该作业");
                }
            }

            if("\"查看作业\"".equals(ans)){
                //Log.e("作业","in");
                homeActivity.getTab().setCurrentTab(0);
                String tag = homeActivity.getTab().getCurrentTabTag();
                Log.e("fragmentworktag",tag);
                WorkFragment fragment = (WorkFragment)homeActivity.getSupportFragmentManager().findFragmentByTag(tag);
                Log.e("fragmentwork",fragment.toString());
                if(fragment.jobExist(numS)){
                    fragment.showJobDetail(Integer.parseInt(numS));
                }else{
                    showMessage("不存在该作业");
                }
            }

            if("\"管理\"".equals(ans)){
                homeActivity.getTab().setCurrentTab(3);
            }
        }catch (Exception e){
            Log.e("return voice err",e.toString());
            showMessage("服务器返回错误信息");
        }
    }


}
