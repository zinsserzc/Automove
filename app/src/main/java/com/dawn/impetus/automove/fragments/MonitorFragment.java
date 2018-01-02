package com.dawn.impetus.automove.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dawn.impetus.automove.R;
import com.dawn.impetus.automove.entities.Node;
import com.dawn.impetus.automove.threadpool.ThreadManager;
import com.dawn.impetus.automove.utils.ServerUtil;
import com.dawn.impetus.automove.utils.VoiceUtil;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class MonitorFragment extends Fragment  implements View.OnClickListener{

    private static final int REFRESHTIME = 50 * 1000;

    private TextView nodeFreeTv;
    private TextView nodeExclusiveTv;
    private TextView nodeBusyTv;
    private TextView nodeDownTv;

    private List<Node> nodeList;
    private ListView monitorLv;
    private MonitorListAdapter mAdapter;
    //标题栏提示更新时间textview
    private TextView topNotifyTv;
    private View rootView;
    private Handler monitorHandler;
    private ImageView iconSearch;

    //Baidu语音
    private VoiceUtil voice;
    private boolean isStart= false;


    private Map<String, String> map;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView == null) {
            rootView = View.inflate(MonitorFragment.this.getActivity(), R.layout.fragment_monitor, null);
            initView(rootView);
            init();
            // Inflate the layout for this fragment
        }

        //用来避免fragment重复创建问题
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }

        return rootView;
    }

    private void initView(View view) {
        nodeFreeTv = (TextView) view.findViewById(R.id.tv_node_free);
        nodeExclusiveTv = (TextView) view.findViewById(R.id.tv_node_exclusive);
        nodeBusyTv = (TextView) view.findViewById(R.id.tv_node_busy);
        nodeDownTv = (TextView) view.findViewById(R.id.tv_node_down);
        monitorLv = (ListView) view.findViewById(R.id.lv_monitor);
        iconSearch = (ImageView) view.findViewById(R.id.icon_search);
        topNotifyTv=(TextView)view.findViewById(R.id.topNotifyTv);

    }
    /**
     * 改变标题更新时间
     */
    private void changeNotifyTv(){

        Calendar c = Calendar.getInstance();
        String info ="更新于"+c.get(Calendar.HOUR_OF_DAY)+"时"+c.get(Calendar.MINUTE)+"分"+c.get(Calendar.SECOND)+"秒";
        topNotifyTv.setText(info);
    }
    private void setText(Map<String, String> map){
        nodeFreeTv.setText(map.get("free"));
        nodeBusyTv.setText(map.get("busy"));
        nodeExclusiveTv.setText(map.get("exclusive"));
        nodeDownTv.setText(map.get("down"));
    }

    private void init() {
        iconSearch.setOnClickListener(this);
        //初始百度语音
        voice = new VoiceUtil(this.getActivity());
        monitorHandler = new Handler(){
          @Override
          public void handleMessage(Message msg){
              super.handleMessage(msg);
              setText(map);
              if(mAdapter == null){
                  mAdapter = new MonitorListAdapter();
                  monitorLv.setAdapter(mAdapter);
              }else {
                  mAdapter.notifyDataSetChanged();
              }
              changeNotifyTv();
          }
        };
        Runnable updateUITask = new Runnable() {
            @Override
            public void run() {
                while(true) {
                    nodeList = ServerUtil.getNodeInfos();
                    map = ServerUtil.getNodeStateNum();
                    monitorHandler.sendMessage(new Message());
                    try {
                        Thread.currentThread().sleep(REFRESHTIME);
                    }catch (Exception e){

                    }
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                });
                }
            }
        };

        ThreadManager.THREAD_POOL_EXECUTOR.execute(updateUITask);
    }

    class MonitorListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return nodeList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            MonitorViewHolder holder = null;
            if(convertView == null) {
                holder = new MonitorViewHolder();
                view = View.inflate(MonitorFragment.this.getActivity(),R.layout.item_monitor,null);
                holder.nodeNameTv = (TextView) view.findViewById(R.id.tv_node_name);
                holder.coreUsedTv = (TextView) view.findViewById(R.id.tv_core_used);
                holder.coreFreeTv = (TextView) view.findViewById(R.id.tv_core_free);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (MonitorViewHolder) view.getTag();
            }

            try {
                Node node = nodeList.get(position);
                holder.nodeNameTv.setText(node.getName());
                holder.coreFreeTv.setText(String.valueOf(node.getUnUsedCoreNum()));
                holder.coreUsedTv.setText(String.valueOf(node.getUsedCoreNum()));
                if(node.getState().equals("busy")){
                    holder.nodeNameTv.setTextColor(getResources().getColor(R.color.red));
                }else if(node.getState().equals("free")){
                    holder.nodeNameTv.setTextColor(getResources().getColor(R.color.green));
                }else if(node.getState().equals("down")){
                    holder.nodeNameTv.setTextColor(getResources().getColor(R.color.orange));
                }
                else {
                    holder.nodeNameTv.setTextColor(getResources().getColor(R.color.blue));
                }
                if(node.getUsedCoreNum() > 0){
                    holder.coreUsedTv.setTextColor(getResources().getColor(R.color.red));
                }else{
                    holder.coreUsedTv.setTextColor(getResources().getColor(R.color.green));
                }
                if(node.getUnUsedCoreNum() > 0){
                    holder.coreFreeTv.setTextColor(getResources().getColor(R.color.green));
                }else{
                    holder.coreFreeTv.setTextColor(getResources().getColor(R.color.red));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return view;
        }
    }

    public final class MonitorViewHolder {
        public TextView nodeNameTv;
        public TextView coreUsedTv;
        public TextView coreFreeTv;

    }

    public void onClick(View v){

        switch (v.getId()){
            case R.id.icon_search:
                isStart = !isStart;
                if(isStart){
                    iconSearch.setImageResource(R.drawable.icon_after_click);
                    voice.start();
                }else {
                    iconSearch.setImageResource(R.drawable.icon_before_click);
                    voice.stop();
                }
                break;
            default:
                break;
        }

    }

}
