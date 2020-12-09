package com.foreveross.atwork.component.scrollView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.foreveross.atwork.infrastructure.utils.ScreenUtils;

/**
 * Created by dasunsy on 16/5/27.
 */
public class FullHeightScrollView extends ScrollView{


    public FullHeightScrollView(Context context) {
        super(context);

    }

    public FullHeightScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Context context = getContext();
        if(null != context) {
            int screenHeight = ScreenUtils.getScreenHeight(context)* 9 /10;
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(screenHeight, MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
