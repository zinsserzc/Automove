package com.dawn.impetus.automove.utils;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2017/11/14 0014.
 * 此类用于在普通java类中获得context
 */
public class ContextApplication extends Application{

    private  static Context context;

    @Override
    public void onCreate(){
        super.onCreate();
        ContextApplication.context=getApplicationContext();


    }

    /**
     * 获得context
     * @return
     */
    public static Context getAppContext(){

        return ContextApplication.context;
    }


}
