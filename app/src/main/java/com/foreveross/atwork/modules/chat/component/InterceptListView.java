package com.foreveross.atwork.modules.chat.component;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

/**
 * Created by dasunsy on 15/8/10.
 */
public class InterceptListView extends ListView {
    private OnInterceptListener mOnInterceptListener;
    public boolean mIsSmoothScrolling = false;
    private int mLastSmoothScrollToPosition;

    public InterceptListView(Context context) {
        super(context);
    }

    public InterceptListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptListView(Context context, AttributeSet attrs, int defStyleAttr) {
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

    /**
     * android 的smoothScrollToPositionFromTop方法存在着比较多 bug, 包括当 有listitem 的高度大于ListView
     * 的高度时, 的smoothScrollToPositionFromTop会突然停在该 listitem.<br/>
     * 该处通过监听 ListView 的滑动状态, 当是SCROLL_STATE_IDLE时做setSelection的跳转来弥补android 该接口的
     * 问题
     */
    @Override
    public void smoothScrollToPositionFromTop(final int position, final int offset, final int duration) {
        View child = getChildAtPosition(position);
        // There's no need to scroll if child is already at top or view is already scrolled to its end
        if ((null != child) && ((0 == child.getTop()) || ((0 < child.getTop()) && !canScrollVertically(1)))) {
            return;
        }
        mLastSmoothScrollToPosition = position;
        mIsSmoothScrolling = true;

//         Perform scrolling to position
        new Handler().post(() -> InterceptListView.super.smoothScrollToPositionFromTop(position, offset, duration));
    }

    public View getChildAtPosition(final int position) {
        final int index = position - getFirstVisiblePosition();
        if ((index >= 0) && (index < getChildCount())) {
            return getChildAt(index);
        } else {
            return null;
        }
    }

    public void setSmoothScrolling(boolean isSmoothScrolling) {
        this.mIsSmoothScrolling = isSmoothScrolling;
    }

    public boolean isSmoothScrolling() {
        return mIsSmoothScrolling;
    }

    public void tryJumpAgain(int scrollState, final int currentFirstVisibleItem, final int offset) {
        if (OnScrollListener.SCROLL_STATE_IDLE == scrollState) {
            // Fix for scrolling bug
            new Handler().postDelayed(() -> {
                /*
                smoothScrollToPositionFromTop在滚动时间比较短的时候会回调SCROLL_STATE_IDLE多
                次, 为了得到准确的 ListView 停止滑动的时机, 该处通过延迟100ms来比对FirstVisiblePosition
                是否有变化来判断
                */
                if (currentFirstVisibleItem == getFirstVisiblePosition()) {
                    setSmoothScrolling(false);
                    //该API从1开始就能调用了, 但 as 会其实错误说需要 api 21, 为 as 的 bug
                    setSelectionFromTop(mLastSmoothScrollToPosition, offset);
                }

            }, 100);

        } else if (OnScrollListener.SCROLL_STATE_TOUCH_SCROLL == scrollState) {
            setSmoothScrolling(false);
        }

    }


}
