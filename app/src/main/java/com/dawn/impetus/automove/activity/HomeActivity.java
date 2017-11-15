package com.dawn.impetus.automove.activity;

import android.os.Process;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.Toast;

import com.dawn.impetus.automove.R;
import com.dawn.impetus.automove.fragments.ManageFragment;
import com.dawn.impetus.automove.fragments.MonitorFragment;
import com.dawn.impetus.automove.fragments.OverallFragment;
import com.dawn.impetus.automove.fragments.SettingFragment;
import com.dawn.impetus.automove.fragments.WorkFragment;
import com.dawn.impetus.automove.ui.TabIndicatorView;
import com.dawn.impetus.automove.utils.SSHUtil;

public class HomeActivity extends AppCompatActivity {

    //定义FragmentTabHost对象
    private FragmentTabHost mTabHost;
    //定义数组来存放Fragment界面
    private Class fragmentArray[] = {WorkFragment.class,MonitorFragment.class,OverallFragment.class,ManageFragment.class,SettingFragment.class};
    //定义数组来存放按钮图片
    private int mImageViewArrayNomal[] = {R.drawable.zuoye,R.drawable.jiankong,R.drawable.home,R.drawable.guanli,R.drawable.shezhi};
    //Tab选项卡的文字
    private String mTextviewArray[] = {"作业", "监控", "集群总览","管理","设置"};
    private TabIndicatorView[] mTabIndicatorView = new TabIndicatorView[5];

    private boolean isFirstPress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SSHUtil sshUtil= SSHUtil.getInstance();
        try {
            sshUtil.execCmd("df -h");
        }catch (Exception e)
        {
            Log.e("error ",e.getMessage());
        }

        initView();
        init();
    }

    private void init() {

        //得到fragment的个数
        int count = fragmentArray.length;
        for(int i = 0; i < count; i++) {
            mTabIndicatorView[i] = new TabIndicatorView(this);
            mTabIndicatorView[i].setDesc(mTextviewArray[i]);
            mTabIndicatorView[i].setIconId(mImageViewArrayNomal[i], mImageViewArrayNomal[i]);
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(mTabIndicatorView[i]);
            //将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
        }

        mTabHost.getTabWidget().setDividerDrawable(android.R.color.white);//设置分割线
        mTabHost.setCurrentTab(2);

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {


            }
        });

    }

    private void initView() {
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        /**
         * 在setup()里边，其才去获取到TabWidget，所以在此之前，不能直接调用getTabWidget()方法；
         */
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

    }

    @Override
    public void onBackPressed() {
        if(!isFirstPress) {
            Toast.makeText(this,"再按一次退出",Toast.LENGTH_SHORT).show();
            isFirstPress = true;
        } else {
            //Process.killProcess(Process.myPid());
            Process.killProcess(Process.myPid());
            //ActivityManager am = (ActivityManager)getSystemService (Context.ACTIVITY_SERVICE);
            //am.restartPackage(getPackageName());

        }

        //super.onBackPressed();
    }


}
