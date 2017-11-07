package com.dawn.impetus.automove.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dawn.impetus.automove.R;

/**
 * Created by Impetus on 2017/11/7.
 */
public class TitleView extends RelativeLayout {

    private TextView title;
    private RelativeLayout iconBack;
    private RelativeLayout iconMore;
    private ImageView ivMore;
    private TextView moreTextView;

    public TitleView(Context context) {
        super(context);
        init(context);
    }

    public void init(Context context) {
        //把一个布局文件setting_item 并且加载在MoreInfoView
        View.inflate(context, R.layout.item_title, this);
        iconMore = (RelativeLayout)findViewById(R.id.icon_more);
        ivMore = (ImageView)findViewById(R.id.img_more);
        //View.inflate(context, R.layout.setting_click_item, this);
        title = (TextView)findViewById(R.id.tv_title);
        moreTextView = (TextView)findViewById(R.id.text_more);
    }

    //返回按钮
    public void setBackIconOnClickListener(OnClickListener clickListener) {
        if(iconBack != null)
            iconBack.setOnClickListener(clickListener);
    }


    //更多按钮
    public void setMoreIconOnClickListener(OnClickListener clickListener) {
        if(iconMore != null) {
            iconMore.setOnClickListener(clickListener);
        }
        if(moreTextView.length()>0){
            moreTextView.setOnClickListener(clickListener);
        }
    }

    //更多文字
    public void setMoreText(String text){
        moreTextView.setText(text);
        ivMore.setVisibility(View.GONE);
    }

    //中央标题
    public void setTitle(String text) {
        title.setText(text);
    }

    public TitleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public TitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.posView);

        String myTitle = a.getString(R.styleable.posView_myTitle);
        boolean backIconIsVisible = a.getBoolean(R.styleable.posView_backIconIsVisible, true);
        //Log.e("TitleView",backIconIsVisible+"");
        if (!backIconIsVisible) {

            ImageView img_back = (ImageView)findViewById(R.id.img_back);
            img_back.setImageResource(R.drawable.btn_nomal);
        } else {
            iconBack = (RelativeLayout)findViewById(R.id.icon_back);
        }


        int iconMoreId = a.getResourceId(R.styleable.posView_rightIcon, R.drawable.btn_nomal);
        ivMore.setImageResource(iconMoreId);
        title.setText(myTitle);

        a.recycle();

    }

}
