package com.foreveross.atwork.component.scrollView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.foreveross.atwork.infrastructure.utils.DensityUtil;

/**
 * Created by dasunsy on 16/5/27.
 * todo 后期改成可配置
 */
public class MaxHeightSwitchOrgScrollView extends ScrollView {


    public MaxHeightSwitchOrgScrollView(Context context) {
        super(context);

    }

    public MaxHeightSwitchOrgScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Context context = getContext();
        if (null != context) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(DensityUtil.dip2px(290), MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
