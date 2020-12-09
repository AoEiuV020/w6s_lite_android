package com.foreveross.atwork.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 监听点击空白处的 RecyclerView
 */
public class WhiteClickRecycleView extends RecyclerView {


    public WhiteClickRecycleView(Context context) {
        super(context);
    }

    public WhiteClickRecycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WhiteClickRecycleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private OnTouchInvalidPositionListener mTouchInvalidPosListener;


    public interface OnTouchInvalidPositionListener {
        /**
         * motionEvent 可使用 MotionEvent.ACTION_DOWN 或者 MotionEvent.ACTION_UP等来按需要进行判断
         * @return 是否要终止事件的路由
         */
        boolean onTouchInvalidPosition(int motionEvent);
    }

    /**
     * 点击空白区域时的响应和处理接口
     */
    public void setOnTouchInvalidPositionListener(OnTouchInvalidPositionListener listener) {
        mTouchInvalidPosListener = listener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        // The findChildViewUnder() method returns null if the touch event
        // occurs outside of a child View.
        // Change the MotionEvent action as needed. Here we use ACTION_DOWN
        // as a simple, naive indication of a click.
        if (MotionEvent.ACTION_DOWN == event.getAction()
                && null == findChildViewUnder(event.getX(), event.getY()))
        {
            if (null != mTouchInvalidPosListener)
            {
                mTouchInvalidPosListener.onTouchInvalidPosition(event.getActionMasked());
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
