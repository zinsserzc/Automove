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
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.dawn.impetus.automove.R;
import com.dawn.impetus.automove.entities.OverallData;
import com.dawn.impetus.automove.threadpool.ThreadManager;
import com.dawn.impetus.automove.utils.ServerUtil;
import com.dawn.impetus.automove.utils.StringUtil;
import com.dawn.impetus.automove.utils.VoiceUtil;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ObjectPool;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 总览
 */
public class OverallFragment extends Fragment implements View.OnClickListener{

    private static final float WARNING_TEMP = 80;
    private static final float WARNING_CPU_USAGE = 0.8f;

    private final long REFRESHTIME = 10*1000;

    private ImageView iconSearch;

    //数据封装对象
    private OverallData datas = OverallData.getInstance();

    //Baidu语音
    private VoiceUtil voice;
    private boolean isStart;

    //图表
    private PieChart chartCPU;
    private PieChart charRAM;

    //磁盘、内存、温度、故障、消息图片
    private ImageView diskStateImg;
    private ImageView memoryStateImg;
    private ImageView tempStateImg;
    private ImageView faultStateImg;
    private ImageView messageStateImg;

    //开机时间
    private TextView startTimeTv;
    //运行时间
    private TextView runningTimeDayTv;
    private TextView runningTimeHourTv;
    private TextView runningTimeMinuteTv;
    //主机名
    private TextView hostNameTv;
    //ip
    private TextView ipTv;
    //mac
    private TextView macTv;
    //主机
    private TextView hostTv;
    //节点
    private TextView nodeTv1;
    private TextView nodeTv2;
    private TextView nodeTv3;
    //GPU
    private TextView GPUTv1;
    private TextView GPUTv2;
    private TextView GPUTv3;
    //交换机
    private TextView changerTv;
    //GPU型号
    private TextView GPUTypeTv;
    //主频
    private TextView dominateFrequencyTv;
    //核数
    private TextView coreCountTv;
    //内存型号
    private TextView memoryTypeTv;
    //大小
    private TextView sizeTv;
    //硬盘型号
    private TextView hardTypeTv;

    //mount
    private TextView mount1Tv;
    private TextView mount2Tv;
    private TextView mount3Tv;
    //size
    private TextView size1Tv;
    private TextView size2Tv;
    private TextView size3Tv;
    //used
    private TextView used1Tv;
    private TextView used2Tv;
    private TextView used3Tv;
    //百分比
    private TextView per1Tv;
    private TextView per2Tv;
    private TextView per3Tv;
    //百分比图片
    private TextView per1Img1;
    private TextView per1Img2;
    private TextView per1Img3;
    private TextView per1Img4;
    private TextView per1Img5;
    private TextView per2Img1;
    private TextView per2Img2;
    private TextView per2Img3;
    private TextView per2Img4;
    private TextView per2Img5;
    private TextView per3Img1;
    private TextView per3Img2;
    private TextView per3Img3;
    private TextView per3Img4;
    private TextView per3Img5;

    //更新ui线程
    private Thread updateThread = null;
    //标记线程是否结束
    private boolean isRunning = true;
    //防止多次产生ui线程
    private boolean isNewRunnable = true;

    //是否第一次加载
    private boolean isFirst = true;


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
        if(isFirst){
            startUpdate();
        }

        //初始百度语音
        voice = new VoiceUtil(this.getActivity());

    }

    private void changeState(){
        if(datas.getCPUtemp() > WARNING_TEMP){
            tempStateImg.setImageResource(R.drawable.icon_warning_state);
        }
        if(StringUtil.percentageToFloat(datas.getCPUUsage()) > WARNING_CPU_USAGE){
            memoryStateImg.setImageResource(R.drawable.icon_warning_state);
        }
    }

    private void startUpdate(){
        //多线程刷新数据
        Runnable r = new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setText();
                        //状态图标显示
                        changeState();
                        drawChart();
                    }
                });

            }
        };
        ThreadManager.THREAD_POOL_EXECUTOR.execute(r);

    }

    @Override
    public void onResume(){
        super.onResume();
        if(!isFirst){
            startUpdate();
        }
        isFirst = false;
        isRunning = true;
        Runnable updateTask = new Runnable() {
            @Override
            public void run() {
                while(isRunning){
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setText();
                                //状态图标显示
                                changeState();
                                drawChart();
                            }
                        });
                        Thread.currentThread().sleep(60000);
                    }catch (Exception e){
                        Toast.makeText(getActivity(),"网络连接中断！",Toast.LENGTH_SHORT).show();
                    }
                }
                //可以新建线程
                isNewRunnable = true;
            }
        };
        if(isNewRunnable) {
            //阻止新建线程
            isNewRunnable = false;
            ThreadManager.THREAD_POOL_EXECUTOR.execute(updateTask);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        isRunning = false;
    }

    private void setText(){
        getData();
        setData();

    }

    private void setData(){
        hostNameTv.setText(datas.getHostName());
        startTimeTv.setText(datas.getStartTime());
        ipTv.setText(datas.getIp());
        macTv.setText(datas.getMac());
        runningTimeDayTv.setText(datas.getRunningTime()[0]);
        runningTimeHourTv.setText(datas.getRunningTime()[1]);
        runningTimeMinuteTv.setText(datas.getRunningTime()[2]);
        hostTv.setText(datas.getHost());
        nodeTv1.setText(datas.getNode1());
        nodeTv2.setText(datas.getNode2());
        nodeTv3.setText(datas.getNode3());
        changerTv.setText(datas.getChanger());
        GPUTypeTv.setText(datas.getCPUNmae());
        dominateFrequencyTv.setText(datas.getDominateFrequency());
        coreCountTv.setText(datas.getCoreCount());
        memoryTypeTv.setText(datas.getMemoryType());
        hardTypeTv.setText(datas.getHardTypeTv());
        sizeTv.setText(datas.getSize());
        mount1Tv.setText(datas.getMount1());
        mount2Tv.setText(datas.getMount2());
        mount3Tv.setText(datas.getMount3());
        size1Tv.setText(datas.getSize1());
        size2Tv.setText(datas.getSize2());
        size3Tv.setText(datas.getSize3());
        used1Tv.setText(datas.getUsed1());
        used2Tv.setText(datas.getUsed2());
        used3Tv.setText(datas.getUsed3());
        per1Tv.setText(datas.getUsePer1());
        per2Tv.setText(datas.getUsePer2());
        per3Tv.setText(datas.getUsePer3());
        showPercentageImage();
    }

    private void showPercentageImage(){
        String per1 = datas.getUsePer1();
        String per2 = datas.getUsePer2();
        String per3 = datas.getUsePer3();
        float f1 = StringUtil.percentageToFloat(per1);
        float f2 = StringUtil.percentageToFloat(per2);
        float f3 = StringUtil.percentageToFloat(per3);
        if(f1 >= 1){
            per1Img1.setVisibility(View.VISIBLE);
            per1Img2.setVisibility(View.VISIBLE);
            per1Img3.setVisibility(View.VISIBLE);
            per1Img4.setVisibility(View.VISIBLE);
            per1Img5.setVisibility(View.VISIBLE);
        }else if(f1 >= 0.8){
            per1Img1.setVisibility(View.VISIBLE);
            per1Img2.setVisibility(View.VISIBLE);
            per1Img3.setVisibility(View.VISIBLE);
            per1Img4.setVisibility(View.VISIBLE);
            per1Img5.setVisibility(View.VISIBLE);
        }else if(f1 >= 0.6){
            per1Img1.setVisibility(View.VISIBLE);
            per1Img2.setVisibility(View.VISIBLE);
            per1Img3.setVisibility(View.VISIBLE);
            per1Img5.setVisibility(View.INVISIBLE);
            per1Img4.setVisibility(View.VISIBLE);
        }else if(f1 >= 0.4){
            per1Img1.setVisibility(View.VISIBLE);
            per1Img2.setVisibility(View.VISIBLE);
            per1Img5.setVisibility(View.INVISIBLE);
            per1Img4.setVisibility(View.INVISIBLE);
            per1Img3.setVisibility(View.VISIBLE);
        }else if(f1 >= 0.2){
            per1Img1.setVisibility(View.VISIBLE);
            per1Img5.setVisibility(View.INVISIBLE);
            per1Img4.setVisibility(View.INVISIBLE);
            per1Img3.setVisibility(View.INVISIBLE);
            per1Img2.setVisibility(View.VISIBLE);
        }else{
            per1Img5.setVisibility(View.INVISIBLE);
            per1Img4.setVisibility(View.INVISIBLE);
            per1Img3.setVisibility(View.INVISIBLE);
            per1Img2.setVisibility(View.INVISIBLE);
            per1Img1.setVisibility(View.VISIBLE);
        }
        if(f2 >= 1){
            per2Img1.setVisibility(View.VISIBLE);
            per2Img2.setVisibility(View.VISIBLE);
            per2Img3.setVisibility(View.VISIBLE);
            per2Img4.setVisibility(View.VISIBLE);
            per2Img5.setVisibility(View.VISIBLE);
        }else if(f2 >= 0.8){
            per2Img1.setVisibility(View.VISIBLE);
            per2Img2.setVisibility(View.VISIBLE);
            per2Img3.setVisibility(View.VISIBLE);
            per2Img4.setVisibility(View.VISIBLE);
            per2Img5.setVisibility(View.VISIBLE);
        }else if(f2 >= 0.6){
            per2Img1.setVisibility(View.VISIBLE);
            per2Img2.setVisibility(View.VISIBLE);
            per2Img3.setVisibility(View.VISIBLE);
            per2Img5.setVisibility(View.INVISIBLE);
            per2Img4.setVisibility(View.VISIBLE);
        }else if(f2 >= 0.4){
            per2Img1.setVisibility(View.VISIBLE);
            per2Img2.setVisibility(View.VISIBLE);
            per2Img5.setVisibility(View.INVISIBLE);
            per2Img4.setVisibility(View.INVISIBLE);
            per2Img3.setVisibility(View.VISIBLE);
        }else if(f2 >= 0.2){
            per2Img1.setVisibility(View.VISIBLE);
            per2Img5.setVisibility(View.INVISIBLE);
            per2Img4.setVisibility(View.INVISIBLE);
            per2Img3.setVisibility(View.INVISIBLE);
            per2Img2.setVisibility(View.VISIBLE);
        }else{
            per2Img5.setVisibility(View.INVISIBLE);
            per2Img4.setVisibility(View.INVISIBLE);
            per2Img3.setVisibility(View.INVISIBLE);
            per2Img2.setVisibility(View.INVISIBLE);
            per2Img1.setVisibility(View.VISIBLE);
        }
        if(f3 >= 1){
            per3Img1.setVisibility(View.VISIBLE);
            per3Img2.setVisibility(View.VISIBLE);
            per3Img3.setVisibility(View.VISIBLE);
            per3Img4.setVisibility(View.VISIBLE);
            per3Img5.setVisibility(View.VISIBLE);
        }else if(f3 >= 0.8){
            per3Img1.setVisibility(View.VISIBLE);
            per3Img2.setVisibility(View.VISIBLE);
            per3Img3.setVisibility(View.VISIBLE);
            per3Img4.setVisibility(View.VISIBLE);
            per3Img5.setVisibility(View.VISIBLE);
        }else if(f3 >= 0.6){
            per3Img1.setVisibility(View.VISIBLE);
            per3Img2.setVisibility(View.VISIBLE);
            per3Img3.setVisibility(View.VISIBLE);
            per3Img5.setVisibility(View.INVISIBLE);
            per3Img4.setVisibility(View.VISIBLE);
        }else if(f3 >= 0.4){
            per3Img1.setVisibility(View.VISIBLE);
            per3Img2.setVisibility(View.VISIBLE);
            per3Img5.setVisibility(View.INVISIBLE);
            per3Img4.setVisibility(View.INVISIBLE);
            per3Img3.setVisibility(View.VISIBLE);
        }else if(f3 >= 0.2){
            per3Img1.setVisibility(View.VISIBLE);
            per3Img5.setVisibility(View.INVISIBLE);
            per3Img4.setVisibility(View.INVISIBLE);
            per3Img3.setVisibility(View.INVISIBLE);
            per3Img2.setVisibility(View.VISIBLE);
        }else{
            per3Img5.setVisibility(View.INVISIBLE);
            per3Img4.setVisibility(View.INVISIBLE);
            per3Img3.setVisibility(View.INVISIBLE);
            per3Img2.setVisibility(View.INVISIBLE);
            per3Img1.setVisibility(View.VISIBLE);
        }
    }

    private void getData(){
        datas.setHostName(ServerUtil.getHsotName());
        datas.setIp(ServerUtil.getIP());
        datas.setMac(ServerUtil.getMAC());
        datas.setStartTime(ServerUtil.getOpTime());
        datas.setHost(ServerUtil.getNodeNum());
        datas.setRunningTime(ServerUtil.getRunTime());
        datas.setNodes(ServerUtil.getNodeStateNum());
        datas.setDominateFrequency(ServerUtil.getCPUHZ());
        datas.setCoreCount(ServerUtil.getCPUCores());
        datas.setSize(ServerUtil.getMemSize());
        datas.setHost(ServerUtil.getNodeNum());
        datas.setCPUNmae(ServerUtil.getCPUName());
        datas.setHardData(ServerUtil.getDiskInfo());
        datas.setCPUUsage(ServerUtil.getCPUUsage());
        datas.setRAMUsage(ServerUtil.getMemUsage());
        datas.setMemoryType(ServerUtil.getMemType());
        datas.setCPUtemp(Float.parseFloat(ServerUtil.getCPUTemp()));
    }

    private void drawChart() {
        //pieChart
        ArrayList<PieEntry> entriesCPU = new ArrayList<>();
        float cpuFloat = StringUtil.percentageToFloat(datas.getCPUUsage());
        entriesCPU.add(new PieEntry(cpuFloat,"Usage"));
        entriesCPU.add(new PieEntry(1-cpuFloat,"Free"));

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
        chartCPU.setCenterText(datas.getCPUUsage());
        chartCPU.setCenterTextColor(Color.BLACK);
        chartCPU.setHoleRadius(60f);
        chartCPU.setCenterTextSize(6f);
        chartCPU.setDrawMarkerViews(false);
        chartCPU.invalidate();

        //ramChart
        ArrayList<PieEntry> entriesRAM = new ArrayList<>();
        float RAMFloat = StringUtil.percentageToFloat(datas.getRAMUsage());
        entriesRAM.add(new PieEntry(RAMFloat,"Usage"));
        entriesRAM.add(new PieEntry(1-RAMFloat,"Free"));

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
        charRAM.setCenterText(datas.getRAMUsage());
        charRAM.setCenterTextColor(Color.BLACK);
        charRAM.setHoleRadius(60f);
        charRAM.setCenterTextSize(6f);
        charRAM.invalidate();
    }

    private void initView(View view) {
        chartCPU =  (PieChart) view.findViewById(R.id.piechart_cpu);
        charRAM = (PieChart) view.findViewById(R.id.piechart_ram);

        iconSearch = (ImageView) view.findViewById(R.id.icon_search);

        //界面数据textview
        startTimeTv = (TextView) view.findViewById(R.id.tv_start_time);
        hostNameTv = (TextView) view.findViewById(R.id.tv_host_name);
        ipTv = (TextView) view.findViewById(R.id.tv_ip);
        macTv = (TextView) view.findViewById(R.id.tv_mac);
        runningTimeDayTv = (TextView) view.findViewById(R.id.tv_running_time_day);
        runningTimeHourTv = (TextView) view.findViewById(R.id.tv_running_time_hour);
        runningTimeMinuteTv = (TextView) view.findViewById(R.id.tv_running_time_minute);
        hostTv = (TextView) view.findViewById(R.id.tv_host);
        nodeTv1 = (TextView) view.findViewById(R.id.tv_node1);
        nodeTv2 = (TextView) view.findViewById(R.id.tv_node2);
        nodeTv3 = (TextView) view.findViewById(R.id.tv_node3);
        GPUTv1 = (TextView) view.findViewById(R.id.tv_GPU1);
        GPUTv2 = (TextView) view.findViewById(R.id.tv_GPU2);
        GPUTv3 = (TextView) view.findViewById(R.id.tv_GPU3);
        changerTv = (TextView) view.findViewById(R.id.tv_changer);
        GPUTypeTv = (TextView) view.findViewById(R.id.tv_GPU_type);
        dominateFrequencyTv = (TextView) view.findViewById(R.id.tv_dominate_frequency);
        coreCountTv = (TextView) view.findViewById(R.id.tv_core_count);
        memoryTypeTv = (TextView) view.findViewById(R.id.tv_memory_type);
        coreCountTv = (TextView) view.findViewById(R.id.tv_core_count);
        hardTypeTv = (TextView) view.findViewById(R.id.tv_hard_type);
        sizeTv = (TextView) view.findViewById(R.id.tv_size);
        mount1Tv = (TextView) view.findViewById(R.id.tv_mount1);
        mount2Tv = (TextView) view.findViewById(R.id.tv_mount2);
        mount3Tv = (TextView) view.findViewById(R.id.tv_mount3);
        size1Tv = (TextView) view.findViewById(R.id.tv_size1);
        size2Tv = (TextView) view.findViewById(R.id.tv_size2);
        size3Tv = (TextView) view.findViewById(R.id.tv_size3);
        used1Tv = (TextView) view.findViewById(R.id.tv_used1);
        used2Tv = (TextView) view.findViewById(R.id.tv_used2);
        used3Tv = (TextView) view.findViewById(R.id.tv_used3);
        per1Tv = (TextView) view.findViewById(R.id.tv_per1);
        per2Tv = (TextView) view.findViewById(R.id.tv_per2);
        per3Tv = (TextView) view.findViewById(R.id.tv_per3);
        per1Img1 = (TextView) view.findViewById(R.id.img_per1_1);
        per1Img2 = (TextView) view.findViewById(R.id.img_per1_2);
        per1Img3 = (TextView) view.findViewById(R.id.img_per1_3);
        per1Img4 = (TextView) view.findViewById(R.id.img_per1_4);
        per1Img5 = (TextView) view.findViewById(R.id.img_per1_5);
        per2Img1 = (TextView) view.findViewById(R.id.img_per2_1);
        per2Img2 = (TextView) view.findViewById(R.id.img_per2_2);
        per2Img3 = (TextView) view.findViewById(R.id.img_per2_3);
        per2Img4 = (TextView) view.findViewById(R.id.img_per2_4);
        per2Img5 = (TextView) view.findViewById(R.id.img_per2_5);
        per3Img1 = (TextView) view.findViewById(R.id.img_per3_1);
        per3Img2 = (TextView) view.findViewById(R.id.img_per3_2);
        per3Img3 = (TextView) view.findViewById(R.id.img_per3_3);
        per3Img4 = (TextView) view.findViewById(R.id.img_per3_4);
        per3Img5 = (TextView) view.findViewById(R.id.img_per3_5);

        diskStateImg = (ImageView) view.findViewById(R.id.icon_disk_state);
        memoryStateImg = (ImageView) view.findViewById(R.id.icon_memory_state);
        tempStateImg = (ImageView) view.findViewById(R.id.icon_temperature_state);
        faultStateImg = (ImageView) view.findViewById(R.id.icon_fault_state);
        messageStateImg = (ImageView) view.findViewById(R.id.icon_message_state);
    }

    public void onClick(View v){

        switch (v.getId()){
            case R.id.icon_search:
                isStart = !isStart;
                if(isStart){
                    voice.start();
                }else {
                    voice.stop();
                }
                break;
            default:
                break;
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        voice.destroy();

    }
}
