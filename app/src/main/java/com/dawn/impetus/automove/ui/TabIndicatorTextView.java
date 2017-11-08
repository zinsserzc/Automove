package com.dawn.impetus.automove.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dawn.impetus.automove.R;


/**
 * Created by QHF on 2016/5/7.
 */
public class TabIndicatorTextView extends RelativeLayout {


    private TextView mDesc;


    public TabIndicatorTextView(Context context) {
        this(context,null);
    }

    public TabIndicatorTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        View.inflate(context, R.layout.tab_indicator_textview, this);
        mDesc = (TextView)findViewById(R.id.tab_title);

    }
    public void setTabSelected(boolean selected) {
        if (selected) {

            setTextColor(Color.RED);
        } else {

            setTextColor(Color.GRAY);
        }
    }



    public void setTextColor(int color) {
        mDesc.setTextColor(color);
    }
    public TabIndicatorTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TextView getDesc() {
        return mDesc;
    }

    public void setDesc(String desc) {
        mDesc.setText(desc);
    }

}
