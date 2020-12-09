package com.foreveross.atwork.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.foreveross.atwork.infrastructure.utils.Logger;

/**
 * 创建时间：2016年07月18日 下午6:35
 * 创建人：赖梓瀚
 * 类名：AdjustLinearLayout
 * 用途：
 */
public class AdjustLinearLayout extends LinearLayout {
    public AdjustLinearLayout(Context context) {
        super(context);
    }

    public AdjustLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdjustLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Logger.e("adjust","onsizechange");
    }
}
