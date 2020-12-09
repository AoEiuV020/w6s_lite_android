package com.foreveross.atwork.component;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;

/**
 * Created by lingen on 15/5/11.
 * Description:
 */
public class TitleItemView extends LinearLayout {

    public TextView mTvTitle;

    public TitleItemView(Context context) {
        super(context);
        initView();
    }

    public TitleItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.title_bar_category, this);
        mTvTitle = view.findViewById(R.id.title_bar_category);
    }

    public void white() {
        mTvTitle.setBackgroundColor(Color.WHITE);
    }

    public void center() {
//        mTvTitle.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
//
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        int dp2Point5 = DensityUtil.DP_1O_TO_PX >> 2;
//        layoutParams.setMargins(0, dp2Point5, 0, dp2Point5);
//        mTvTitle.setLayoutParams(layoutParams);
    }

    public void gray() {
//        mTvTitle.setBackgroundColor(Color.parseColor("#F4F4F4"));
//
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        int dp2Point5 = DensityUtil.DP_1O_TO_PX >> 2;
//        layoutParams.setMargins(dp2Point5, dp2Point5, dp2Point5, dp2Point5);
//        mTvTitle.setLayoutParams(layoutParams);

    }

    public void left() {
//        mTvTitle.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);

    }


    public void setTitle(String title) {
        this.mTvTitle.setText(title);
    }
}
