package com.foreveross.atwork.component.scrollView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.foreveross.atwork.infrastructure.utils.ScreenUtils;

/**
 * Created by dasunsy on 16/5/27.
 */
public class MaxHeightScrollView extends ScrollView{


    public MaxHeightScrollView(Context context) {
        super(context);

    }

    public MaxHeightScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Context context = getContext();
        if(null != context) {
            int screenHeight = ScreenUtils.getScreenHeight(context);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(screenHeight * 2 / 3, MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
