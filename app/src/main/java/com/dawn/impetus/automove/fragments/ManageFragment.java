package com.dawn.impetus.automove.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dawn.impetus.automove.R;
import com.dawn.impetus.automove.threadpool.ThreadManager;
import com.dawn.impetus.automove.utils.ServerUtil;
import com.dawn.impetus.automove.utils.VoiceUtil;

import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageFragment extends Fragment implements View.OnClickListener{

    //用户列表
    private List<String> userList = null;
    private ListView userLv;
    private ManageListAdapter mAdapter;

    //用户总数
    private TextView userCountTv;
    private View rootView;
    private Handler handler;
    private ImageView iconSearch;

    //Baidu语音
    private VoiceUtil voice;
    private boolean isStart= false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(rootView == null) {
            rootView = View.inflate(ManageFragment.this.getActivity(), R.layout.fragment_manage, null);
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
        userLv = (ListView) view.findViewById(R.id.lv_manage);
        userCountTv = (TextView) view.findViewById(R.id.tv_user_count);
        iconSearch = (ImageView) view.findViewById(R.id.icon_search);
    }

    private void init() {
        iconSearch.setOnClickListener(this);
        //初始百度语音
        voice = new VoiceUtil(this.getActivity());
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                userCountTv.setText(String.valueOf(userList.size()));
                userLv.setAdapter(mAdapter);
                if(mAdapter == null) {
                    mAdapter = new ManageListAdapter();
                    userLv.setAdapter(mAdapter);
                }else {
                    mAdapter.notifyDataSetChanged();
                }

            }
        };
        Runnable updateUITask = new Runnable() {
            @Override
            public void run() {
                userList = ServerUtil.getUserList();
                handler.sendMessage(new Message());
            }
        };

        ThreadManager.THREAD_POOL_EXECUTOR.execute(updateUITask);
    }

    class ManageListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return userList.size();
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
            ManageViewHolder holder = null;
            if(convertView == null) {
                holder = new ManageViewHolder();
                view = View.inflate(ManageFragment.this.getActivity(),R.layout.item_manage,null);
                holder.usernameTv = (TextView) view.findViewById(R.id.tv_username);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ManageViewHolder) view.getTag();
            }

            try {
                holder.usernameTv.setText(userList.get(position));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return view;
        }
    }

    public final class ManageViewHolder {
        public TextView usernameTv;

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
