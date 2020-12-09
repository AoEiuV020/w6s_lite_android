package com.foreveross.atwork.modules.chat.component;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by dasunsy on 15/8/10.
 */
public class InterceptRecyclerView extends RecyclerView {
    private OnInterceptListener mOnInterceptListener;
    public boolean mIsSmoothScrolling = false;
    private int mLastSmoothScrollToPosition;

    public InterceptRecyclerView(Context context) {
        super(context);
    }

    public InterceptRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        //处理完触碰事件后, 不影响各个子 view 的 Event 分发
        if (null != mOnInterceptListener) {
            mOnInterceptListener.onIntercept(event);
        }
        return super.onInterceptTouchEvent(event);
    }

    public void setOnInterceptListener(OnInterceptListener onInterceptListener) {
        mOnInterceptListener = onInterceptListener;
    }

    public interface OnInterceptListener {
        void onIntercept(MotionEvent event);
    }




}
