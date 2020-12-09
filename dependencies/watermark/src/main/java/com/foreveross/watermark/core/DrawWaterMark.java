package com.foreveross.watermark.core;

/**
 * Created by dasunsy on 2017/4/18.
 */

public class DrawWaterMark {
    public String mName;
    public String mNum;
    public int mBgColor;
    public int mTextColor;
    public int mTextSize;
    public int mPadding;
    public double mAlpha;
    public String mAddVaule;

    public DrawWaterMark(String mName, String mNum, int mBgColor, int mTextColor, int textSize, int padding, double alpha, String addValue) {
        this.mName = mName;
        this.mNum = mNum;
        this.mBgColor = mBgColor;
        this.mTextColor = mTextColor;
        this.mTextSize = textSize;
        this.mPadding = padding;
        this.mAlpha = alpha;
        this.mAddVaule = addValue;
    }
}
