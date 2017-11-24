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
import com.dawn.impetus.automove.utils.VoiceUtil;
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
 * 总览
 */
public class OverallFragment extends Fragment implements View.OnClickListener{

    private ImageView iconSearch;


    //Baidu语音
    private VoiceUtil voice;
    private boolean isStart;

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

        //初始百度语音
        voice = new VoiceUtil(this.getActivity());

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
