package com.foreverht.workplus.ui.component.recyclerview;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.RecyclerView;

import com.foreverht.workplus.ui.component.common.MaxHeightDelegator;


public class MaxHeightRecyclerView extends RecyclerView {


    private MaxHeightDelegator mMaxHeightDelegator = new MaxHeightDelegator();




    public void setMaxListViewHeight(int listViewHeight) {
        this.mMaxHeightDelegator.setMaxHeight(listViewHeight);
    }

    public MaxHeightRecyclerView(Context context) {
        super(context);
    }

    public MaxHeightRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MaxHeightRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        super.onMeasure(widthMeasureSpec, mMaxHeightDelegator.getHeightMeasureSpec(heightMeasureSpec));
    }
}
