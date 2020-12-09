package com.foreveross.atwork.component.scrollView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.foreveross.atwork.infrastructure.utils.DensityUtil;

/**
 * Created by dasunsy on 16/5/27.
 */
public class MinHeightScrollView extends ScrollView{


    public MinHeightScrollView(Context context) {
        super(context);

    }

    public MinHeightScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Context context = getContext();
        if(null != context) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(DensityUtil.dip2px(100), MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.onInterceptTouchEvent(ev);
    }
}
