package com.foreveross.atwork.component;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * Created by reyzhang22 on 2017/9/29.
 */

public class WorkplusSurfaceView extends SurfaceView {

    public  float width1;
    public float height1;

    public WorkplusSurfaceView(Context context) {
        super(context);
    }

    public void setMeasure(float width,float height){
        this.width1 = width;
        this.height1 = height;
    }

    public WorkplusSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WorkplusSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WorkplusSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int hight = MeasureSpec.getSize(heightMeasureSpec);
        if (this.width1>0 ){
            width = (int) width1;
        }
        setMeasuredDimension(width, hight);
    }
}
