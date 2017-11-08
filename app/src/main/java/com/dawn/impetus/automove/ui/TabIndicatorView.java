package com.dawn.impetus.automove.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dawn.impetus.automove.R;


/**
 * Created by QHF on 2016/5/7.
 */
public class TabIndicatorView extends RelativeLayout {


    private TextView mDesc;
    private ImageView mIcon;
    private int normalIconId;
    private int focusIconId;

    public TabIndicatorView(Context context) {
        this(context,null);
    }

    public TabIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);

        View.inflate(context, R.layout.tab_indicator, this);
        mDesc = (TextView)findViewById(R.id.tab_indicator_hint);
        mIcon = (ImageView)findViewById(R.id.tab_indicator_icon);


    }
    public void setTabSelected(boolean selected) {
        if (selected) {
            mIcon.setImageResource(focusIconId);
            setTextColor(Color.RED);
        } else {
            mIcon.setImageResource(normalIconId);
            setTextColor(Color.GRAY);
        }
    }

    public int getNormalIconId() {
        return normalIconId;
    }



    public int getFocusIconId() {
        return focusIconId;
    }

    public void setIconId(int focusIconId,int normalIconId) {
        this.focusIconId = focusIconId;
        this.normalIconId = normalIconId;
        mIcon.setImageResource(normalIconId);
    }
    public void setIconFocus() {
        mIcon.setImageResource(focusIconId);
    }
    public void setTextColor(int color) {
        mDesc.setTextColor(color);
    }
    public TabIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TextView getDesc() {
        return mDesc;
    }

    public void setDesc(String desc) {
        mDesc.setText(desc);
    }

}
