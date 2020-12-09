package com.foreveross.atwork.component;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * Created by lingen on 15/3/26.
 * Description:
 */
public class WhiteClickGridView extends GridView {


    public WhiteClickGridView(Context context) {
        super(context);
        setSelector(new ColorDrawable(Color.TRANSPARENT));
    }

    public WhiteClickGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSelector(new ColorDrawable(Color.TRANSPARENT));
    }

    public WhiteClickGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSelector(new ColorDrawable(Color.TRANSPARENT));
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
    public boolean onTouchEvent(MotionEvent event) {

        if(mTouchInvalidPosListener == null) {
            return super.onTouchEvent(event);
        }

        if (!isEnabled()) {
            // A disabled view that is clickable still consumes the touch
            // events, it just doesn't respond to them.
            return isClickable() || isLongClickable();
        }

        final int motionPosition = pointToPosition((int) event.getX(), (int) event.getY());

        if(event.getAction()== MotionEvent.ACTION_DOWN && motionPosition == INVALID_POSITION ) {
            super.onTouchEvent(event);
            return mTouchInvalidPosListener.onTouchInvalidPosition(event.getActionMasked());
        }

        return super.onTouchEvent(event);
    }
}
