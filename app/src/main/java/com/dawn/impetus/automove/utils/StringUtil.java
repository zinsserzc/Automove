package com.dawn.impetus.automove.utils;

import android.util.Log;

/**
 * Created by zinsser on 2017/12/1.
 */
public class StringUtil {

    public static float percentageToFloat(String s){
        //Log.e("floatString",s);
        String newS = s.replaceAll("%","");
        float f = Float.parseFloat(newS);
        return f/100;
    }

}
