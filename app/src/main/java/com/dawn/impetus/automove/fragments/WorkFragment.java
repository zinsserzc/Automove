package com.dawn.impetus.automove.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dawn.impetus.automove.R;
import com.dawn.impetus.automove.entities.Job;
import com.dawn.impetus.automove.threadpool.ThreadManager;
import com.dawn.impetus.automove.utils.ContextApplication;
import com.dawn.impetus.automove.utils.SPUtil;
import com.dawn.impetus.automove.utils.ServerUtil;
import com.dawn.impetus.automove.utils.VoiceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkFragment extends Fragment  implements View.OnClickListener{



    private View rootView;
    private final int REFRESH_TIME=1000*60;
    private ListView jobListView;
    private JobAdapter jobAdapter;
    static List<Job> jobs=new ArrayList<>();
    private Handler jobsRefreshHandler;
    private ImageView iconSearch;

    //Baidu语音
    private VoiceUtil voice;
    private boolean isStart= false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (rootView == null) {
            rootView = View.inflate(WorkFragment.this.getActivity(), R.layout.fragment_work, null);
            jobListView = (ListView) rootView.findViewById(R.id.jobListView);
            iconSearch = (ImageView) rootView.findViewById(R.id.icon_search);

            init();
            setListItemDeleMethod();
            //设置list更新处理handler
            jobsRefreshHandler = new Handler() {

                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    Toast.makeText(WorkFragment.this.getActivity(), "作业已更新", Toast.LENGTH_SHORT).show();
                    if (jobs==null||jobs.size() == 0) {
                        Toast.makeText(WorkFragment.this.getActivity(), "暂无作业", Toast.LENGTH_SHORT).show();
                    }
                    jobAdapter.notifyDataSetChanged();
                }

            };

        }
        //用来避免fragment重复创建问题
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }

        return rootView;
    }

    private void setListItemDeleMethod() {
        //长按删除事件
        jobListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view,
                                           final int position, long id) {
                // TODO Auto-generated method stub
                // When clicked, show a toast with the TextView text
                String jobID = ((TextView) view.findViewById(R.id.jobName)).getText().toString();
                showDeleteJobDialog(position,jobID);
                return true;
            }
        });

        //设置单击弹出作业详情事件
        jobListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
            final int position, long id) {
                int jobName = Integer.valueOf(jobs.get(position).getJobName());
                showJobDetail(jobName);
            }

        });

    }

    /**
     * 展示作业详情
     * @param jobName 作业id 用int防止识别错误
     */
    public void showJobDetail(int jobName){

        AlertDialog.Builder builder = new AlertDialog.Builder(WorkFragment.this.getActivity());
        builder.setTitle("作业"+jobName+"详情");

        String content = "";
        content = ServerUtil.getJobDetail(String.valueOf(jobName));

        if(content.equals("")) {
            content="无法获取作业详情！";
        }

        builder.setMessage(content);


        builder.setPositiveButton("知道了",new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();
    }






    /**
     * 根据作业id返回list中的位置,未找到则返回-1
     * @return
     */
    public final int getPositionByJobName(String JobName){

        int position=-1;
        if(jobs!=null&&jobs.size()>0) {
            for (Job j : jobs) {
            if(j.getJobName().equals(JobName))
            {
                position=jobs.indexOf(j);
            }
            }
        }
        return position==-1?-1:position;
    }


    public boolean jobExist(String jobName){
        //boolean exist = false;
        for(Job job : jobs){
            if(job.getJobName().equals(jobName)){
                return true;
            }
        }
        return  false;
    }


    /**
     * 删除作业对话框
     */
    public void showDeleteJobDialog(final int position,final String jobID){
        AlertDialog.Builder builder = new AlertDialog.Builder(WorkFragment.this.getActivity());
        //final String jobID = ((TextView) view.findViewById(R.id.jobName)).getText().toString();

        //判断是否是登录用户自身的作业
        Context context = ContextApplication.getAppContext();
        for(Job j:jobs){

            if(j.getJobName().equals(jobID))
            {
                if(!j.getUser().equals(SPUtil.get(context, "userName", "").toString())){

                    Toast.makeText(WorkFragment.this.getActivity(),"无删除他人作业权限",Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        }

        builder.setMessage("确认删除作业"+jobID+"?");

        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {


                if(ServerUtil.deleteJob(jobID))
                {
                    //删除并更新作业
                    jobs.remove(position);
                    jobsRefreshHandler.sendMessage(new Message());
                    Toast.makeText(WorkFragment.this.getActivity(), "删除job成功", Toast.LENGTH_SHORT).show();
                }else {

                    Toast.makeText(WorkFragment.this.getActivity(),"删除job失败",Toast.LENGTH_SHORT).show();
                }

            }

        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }


        });
        builder.create().show();

    }
    private void init() {
        iconSearch.setOnClickListener(this);
        //初始百度语音
        voice = new VoiceUtil(this.getActivity());
        //初始化listview
        jobs = new ArrayList<>();
        jobAdapter = new JobAdapter();
        jobListView.setAdapter(jobAdapter);

        if (jobs==null||jobs.size() == 0) {
            Toast.makeText(WorkFragment.this.getActivity(), "作业获取中", Toast.LENGTH_LONG).show();
        }


        Runnable updateList = new Runnable() {
            @Override
            public void run() {

                while (true) {

                    jobs = ServerUtil.getJobsInfo();
                    Message msg = new Message();
                    jobsRefreshHandler.sendMessage(msg);
                    try {
                        Thread.sleep(REFRESH_TIME);
                    } catch (Exception e) {

                    }

                }

            }
        };
        ThreadManager.THREAD_POOL_EXECUTOR.execute(updateList);


//        Runnable updateUITask = new Runnable() {
//            @Override
//            public void run() {
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        jobs = ServerUtil.getJobsInfo();
//                        if (jobs.size()==0)
//                        {
//                            Toast.makeText(WorkFragment.this.getActivity(),"暂无任务",Toast.LENGTH_SHORT).show();
//                        }
//                        jobAdapter.notifyDataSetChanged();
////                        jobAdapter = new JobAdapter();
////                        jobListView.setAdapter(jobAdapter);
//                    }
//                });
//            }
//        };
//
//        ThreadManager.THREAD_POOL_EXECUTOR.execute(updateUITask);

    }



    final class JobAdapter extends BaseAdapter {


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
            JobViewHolder holder = null;
            if (convertView == null) {
                holder = new JobViewHolder();
                view = View.inflate(WorkFragment.this.getActivity(), R.layout.item_work, null);
                holder.jobName = (TextView) view.findViewById(R.id.jobName);
                holder.user = (TextView) view.findViewById(R.id.user);
                holder.excluNodes = (TextView) view.findViewById(R.id.excluNodes);
                holder.excluCoreNum = (TextView) view.findViewById(R.id.excluCoreNum);
                holder.time = (TextView) view.findViewById(R.id.time);
                holder.state = (TextView) view.findViewById(R.id.state);
                view.setTag(holder);
            } else {

                view = convertView;
                holder = (JobViewHolder) view.getTag();

            }

            holder.jobName.setText(jobs.get(position).getJobName());
            holder.user.setText(jobs.get(position).getUser());
            List<String> nodes = jobs.get(position).getExcluNodes();
            String nodeStr = "";
            for (String n : nodes) {
                nodeStr += n + " ";
            }
            holder.excluNodes.setText(nodeStr);
            holder.excluCoreNum.setText(String.valueOf(jobs.get(position).getExcluCoreNum()));
            holder.time.setText(jobs.get(position).getTime());
            holder.state.setText(jobs.get(position).getState());


            return view;
        }
    }

    static class JobViewHolder {

        public TextView jobName;
        public TextView user;
        public TextView excluNodes;
        public TextView excluCoreNum;
        public TextView time;
        public TextView state;

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
