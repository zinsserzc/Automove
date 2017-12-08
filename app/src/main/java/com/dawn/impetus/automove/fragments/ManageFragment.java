package com.dawn.impetus.automove.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dawn.impetus.automove.R;
import com.dawn.impetus.automove.threadpool.ThreadManager;
import com.dawn.impetus.automove.utils.ServerUtil;

import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageFragment extends Fragment {

    //用户列表
    private List<String> userList = null;
    private ListView userLv;
    private ManageListAdapter mAdapter;

    //用户总数
    private TextView userCountTv;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = View.inflate(ManageFragment.this.getActivity(),R.layout.fragment_manage,null);
        initView(view);
        init();
        return view;
    }

    private void initView(View view) {
        userLv = (ListView) view.findViewById(R.id.lv_manage);
        userCountTv = (TextView) view.findViewById(R.id.tv_user_count);
    }

    private void init() {

        Runnable updateUITask = new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        userList = ServerUtil.getUserList();
                        userCountTv.setText(String.valueOf(userList.size()));
                        mAdapter = new ManageListAdapter();
                        userLv.setAdapter(mAdapter);
                    }
                });
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

}
