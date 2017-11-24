package com.dawn.impetus.automove.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.dawn.impetus.automove.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ObjectPool;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class OverallFragment extends Fragment implements View.OnClickListener,EventListener{

    private ImageView iconSearch;


    //Baidu语音
    private EventManager asr;
    private boolean isStart = false;
    private String msgContent = "";

    //图表
    private PieChart chartCPU;
    private PieChart charRAM;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = View.inflate(OverallFragment.this.getActivity(),R.layout.fragment_overall,null);
        initView(view);
        init();
        // Inflate the layout for this fragment
        return view;

    }

    private void init() {
        iconSearch.setOnClickListener(this);

        //初始化百度语音manager
        initPermission();
        asr = EventManagerFactory.create(this.getActivity(),"asr");
        asr.registerListener(this);

        drawChart();

    }

    private void drawChart() {
        //pieChart
        ArrayList<PieEntry> entriesCPU = new ArrayList<>();
        entriesCPU.add(new PieEntry(18.5f,"Green"));
        entriesCPU.add(new PieEntry(81.5f,"Red"));

        ArrayList<Integer> colorsCPU = new ArrayList<>();
        colorsCPU.add(Color.BLUE);
        colorsCPU.add(Color.CYAN);

        PieDataSet setCPU = new PieDataSet(entriesCPU,"CPU利用率");
        setCPU.setDrawValues(false);
        PieData dataCPU = new PieData(setCPU);
        setCPU.setColors(colorsCPU);
        chartCPU.setData(dataCPU);
        chartCPU.setUsePercentValues(false);
        chartCPU.setDescription("");
        chartCPU.setBackgroundColor(Color.alpha(0));
        chartCPU.setDrawEntryLabels(false);
        chartCPU.setDrawHoleEnabled(true);
        chartCPU.setDrawCenterText(true);
        chartCPU.setCenterText("18.5%");
        chartCPU.setCenterTextColor(Color.BLACK);
        chartCPU.invalidate();

        //ramChart
        ArrayList<PieEntry> entriesRAM = new ArrayList<>();
        entriesRAM.add(new PieEntry(60f,"Green"));
        entriesRAM.add(new PieEntry(40f,"Red"));

        ArrayList<Integer> colorsRAM = new ArrayList<>();
        colorsRAM.add(Color.BLUE);
        colorsRAM.add(Color.CYAN);

        PieDataSet setRAM = new PieDataSet(entriesRAM,"CPU利用率");
        setRAM.setDrawValues(false);
        PieData dataRAM = new PieData(setRAM);
        setRAM.setColors(colorsRAM);
        charRAM.setData(dataRAM);
        charRAM.setUsePercentValues(false);
        charRAM.setDescription("");
        charRAM.setBackgroundColor(Color.alpha(0));
        charRAM.setDrawEntryLabels(false);
        charRAM.setDrawHoleEnabled(true);
        charRAM.setDrawCenterText(true);
        charRAM.setCenterText("60%");
        charRAM.setCenterTextColor(Color.BLACK);
        charRAM.invalidate();
    }

    private void initView(View view) {
        chartCPU =  (PieChart) view.findViewById(R.id.piechart_cpu);
        charRAM = (PieChart) view.findViewById(R.id.piechart_ram);

        iconSearch = (ImageView) view.findViewById(R.id.icon_search);
    }

    public void onClick(View v){

        switch (v.getId()){
            case R.id.icon_search:
                isStart = !isStart;
                if(isStart){
                    start();
                }else {
                    stop();
                }
                break;
            default:
                break;
        }

    }

    private void start(){
        Map<String,Object> params = new LinkedHashMap<>();
        String event = null;
        event = SpeechConstant.ASR_START;

        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME,false);
        params.put(SpeechConstant.VAD,SpeechConstant.VAD_TOUCH);
        String json = null; //可以替换成自己的json
        json = new JSONObject(params).toString();
        Toast.makeText(this.getActivity(),"开始说话",Toast.LENGTH_SHORT).show();
        asr.send(event, json, null, 0, 0);
    }

    private void stop(){
        Toast.makeText(this.getActivity(),"结束说话",Toast.LENGTH_SHORT).show();
        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0); //
    }

    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        String logTxt = "name: " + name;
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
        Toast.makeText(this.getActivity(),text,Toast.LENGTH_SHORT).show();
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
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this.getActivity(), perm)) {
                toApplyList.add(perm);
                //进入到这里代表没有权限.

            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()){
            ActivityCompat.requestPermissions(this.getActivity(), toApplyList.toArray(tmpList), 123);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);

    }
}
