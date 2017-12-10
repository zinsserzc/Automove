package com.dawn.impetus.automove.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dawn.impetus.automove.R;
import com.dawn.impetus.automove.entities.Job;
import com.dawn.impetus.automove.threadpool.ThreadManager;
import com.dawn.impetus.automove.utils.ServerUtil;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkFragment extends Fragment {

    private ListView jobListView;
    private JobAdapter jobAdapter;
    List<Job> jobs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = View.inflate(WorkFragment.this.getActivity(), R.layout.fragment_work, null);
        jobListView = (ListView) view.findViewById(R.id.jobListView);

        jobListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                // TODO Auto-generated method stub
                // When clicked, show a toast with the TextView text
                TextView tv = (TextView)arg1.findViewById(R.id.jobName);
                Toast.makeText(WorkFragment.this.getActivity(), tv.getText(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        init();


        return view;
        //return inflater.inflate(R.layout.fragment_work, container, false);
    }

    private void init(){

        Runnable updateUITask = new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        jobs = ServerUtil.getJobsInfo();
                        jobAdapter=new JobAdapter();
                        jobListView.setAdapter(jobAdapter);
                    }
                });
            }
        };

        ThreadManager.THREAD_POOL_EXECUTOR.execute(updateUITask);

    }


    final class JobAdapter extends BaseAdapter{


        @Override
        public int getCount() {
            return jobs.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            JobViewHolder holder=null;
            if (convertView==null)
            {
                holder = new JobViewHolder();
                view=View.inflate(WorkFragment.this.getActivity(),R.layout.item_work,null);
                holder.jobName=(TextView)view.findViewById(R.id.jobName);
                holder.user=(TextView)view.findViewById(R.id.user);
                holder.excluNodes=(TextView)view.findViewById(R.id.excluNodes);
                holder.excluCoreNum=(TextView)view.findViewById(R.id.excluCoreNum);
                holder.time=(TextView)view.findViewById(R.id.time);
                holder.state=(TextView)view.findViewById(R.id.state);
                view.setTag(holder);
            }
            else {

                view=convertView;
                holder =(JobViewHolder)view.getTag();

            }

            holder.jobName.setText(jobs.get(position).getJobName());
            holder.user.setText(jobs.get(position).getUser());
            List<String> nodes=jobs.get(position).getExcluNodes();
            String nodeStr="";
            for (String n:nodes)
            {
                nodeStr+=n+" ";
            }
            holder.excluNodes.setText(nodeStr);
            holder.excluCoreNum.setText(String.valueOf(jobs.get(position).getExcluCoreNum()));
            holder.time.setText(jobs.get(position).getTime());
            holder.state.setText(jobs.get(position).getState());


            return view;
        }
    }

    static class JobViewHolder{

        public TextView jobName;
        public TextView user;
        public TextView excluNodes;
        public TextView excluCoreNum;
        public TextView time;
        public TextView state;

    }

}
