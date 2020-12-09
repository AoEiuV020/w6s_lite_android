package com.foreverht.workplus.ui.component.scrollView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import androidx.recyclerview.widget.RecyclerView;

import com.foreverht.workplus.ui.component.common.MaxHeightDelegator;


public class MaxHeightScrollView extends ScrollView {

    private MaxHeightDelegator mMaxHeightDelegator = new MaxHeightDelegator();


    public void setMaxHeight(int maxHeight) {
        this.mMaxHeightDelegator.setMaxHeight(maxHeight);
    }

    public MaxHeightScrollView(Context context) {
        super(context);
    }

    public MaxHeightScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MaxHeightScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, mMaxHeightDelegator.getHeightMeasureSpec(heightMeasureSpec));
    }
}
