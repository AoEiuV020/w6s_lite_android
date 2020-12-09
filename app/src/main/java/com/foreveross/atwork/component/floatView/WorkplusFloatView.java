package com.foreveross.atwork.component.floatView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class WorkplusFloatView extends FrameLayout {

    protected WindowManager.LayoutParams mWmParams;

    // 当前X坐标
    protected float mInitXInScreen;
    // 当前Y坐标
    protected float mInitYInScreen;


    public WorkplusFloatView(@NonNull Context context) {
        super(context);
    }

    public WorkplusFloatView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setParams(WindowManager.LayoutParams params) {
        mWmParams = params;
    }

    public void setInitXY(float x, float y) {
        mInitXInScreen = x;
        mInitYInScreen = y;
    }
}
