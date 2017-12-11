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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dawn.impetus.automove.R;
import com.dawn.impetus.automove.entities.Node;
import com.dawn.impetus.automove.threadpool.ThreadManager;
import com.dawn.impetus.automove.utils.ServerUtil;

import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class MonitorFragment extends Fragment {

    private static final int REFRESHTIME = 30 * 1000;

    private TextView nodeFreeTv;
    private TextView nodeExclusiveTv;
    private TextView nodeBusyTv;
    private TextView nodeDownTv;

    private List<Node> nodeList;
    private ListView monitorLv;
    private MonitorListAdapter mAdapter;

    private View rootView;
    private Handler monitorHandler;

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
    }
    private void setText(Map<String, String> map){
        nodeFreeTv.setText(map.get("free"));
        nodeBusyTv.setText(map.get("busy").trim());
        nodeExclusiveTv.setText(map.get("exclusive").trim());
        nodeDownTv.setText(map.get("down").trim());
    }

    private void init() {


        monitorHandler = new Handler(){
          @Override
          public void handleMessage(Message msg){
              super.handleMessage(msg);
              nodeList = ServerUtil.getNodeInfos();
              Map<String, String> map = ServerUtil.getNodeStateNum();
              setText(map);
              if(mAdapter == null){
                  mAdapter = new MonitorListAdapter();
                  monitorLv.setAdapter(mAdapter);
              }else {
                  mAdapter.notifyDataSetChanged();
              }
              Toast.makeText(MonitorFragment.this.getActivity(),"监控已更新",Toast.LENGTH_SHORT).show();
          }
        };
        Runnable updateUITask = new Runnable() {
            @Override
            public void run() {
                while(true) {
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
                if(node.getState().trim().equals("busy")){
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

}
