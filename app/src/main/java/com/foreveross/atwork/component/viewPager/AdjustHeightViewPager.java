package com.foreveross.atwork.component.viewPager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.viewpager.widget.ViewPager;


public class AdjustHeightViewPager extends ViewPager {

    public static final String TAG = AdjustHeightViewPager.class.getSimpleName();


    private View mCurrentView = getChildAt(0);

    public AdjustHeightViewPager(Context context) {
        super(context);
    }

    public AdjustHeightViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (mCurrentView == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        try {
            View child = mCurrentView;
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void reMeasureCurrentPage(View currentView) {
        mCurrentView = currentView;
        requestLayout();
    }


}
