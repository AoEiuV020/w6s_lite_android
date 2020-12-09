package com.foreveross.atwork.layout;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.ScreenUtils;
import com.foreveross.atwork.listener.OnKeyBoardHeightListener;


public class KeyboardRelativeLayout extends RelativeLayout {

    private static final String TAG = KeyboardRelativeLayout.class.getSimpleName();

    /**
     * 输入法键盘显示
     */
    public static final int KEYBOARD_STATE_SHOW = -3;

    /**
     * 输入法键盘隐藏
     */
    public static final int KEYBOARD_STATE_HIDE = -2;

    public static final int KEYBOARD_STATE_INIT = -1;

    /**
     * 是否初始化
     */
    private boolean mHasInit;

    /**
     * 是否有键盘
     */
    private boolean mHasKeyboard;

    private int mHeight;


    private OnKeyboardChangeListener onKeyboardChangeListener;

    private OnKeyBoardHeightListener onKeyBoardHeightListener;

    private int mHasKeyboardCount = 0;

    private int mScreenHeight = -1;


    public KeyboardRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.getViewTreeObserver().addOnGlobalLayoutListener(
                () -> {
                    Rect r = new Rect();
                    getWindowVisibleDisplayFrame(r);


                    if (-1 == mScreenHeight) {
                        mScreenHeight = r.bottom;
                    }

                    LogUtil.e(TAG, "mScreenHeight -> " + mScreenHeight + "   r.bottom  -> " + r.bottom);

                    if (mScreenHeight == r.bottom) {
                        mHasKeyboardCount = 0;
                        return;
                    }

                    if(10 < mHasKeyboardCount) {

                        return;
                    }

                    int heightDifference = mScreenHeight - r.bottom;

                    //非法高度
                    if(100 >= heightDifference) {
                        mScreenHeight = r.bottom;
                        return;
                    }

                    if (onKeyboardChangeListener != null) {
                        mHasKeyboard = true;

                        mHasKeyboardCount++;
                        LogUtil.e(TAG,   "screen error count -> " + mHasKeyboardCount);

                        onKeyboardChangeListener.onKeyBoardStateChange(KEYBOARD_STATE_SHOW);
                    }


                    if (onKeyBoardHeightListener != null) {
                        onKeyBoardHeightListener.keyBoardHeight(heightDifference);
                    }

                }
        );
    }

    public KeyboardRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (!mHasInit) {
            mHasInit = true;
            mHeight = bottom;

            LogUtil.e("mHeight -> " + mHeight);

            if (onKeyboardChangeListener != null) {
                onKeyboardChangeListener.onKeyBoardStateChange(KEYBOARD_STATE_INIT);
            }
        } else {
            mHeight = mHeight < bottom ? bottom : mHeight;
        }
        if (mHasInit && mHasKeyboard && mHeight == bottom) {
            mHasKeyboard = false;
            if (onKeyboardChangeListener != null) {
                onKeyboardChangeListener.onKeyBoardStateChange(KEYBOARD_STATE_HIDE);
            }
            LogUtil.e(TAG, "hide keyboard.......:" + (mHeight - top));
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    public void setOnKeyboardChangeListener(OnKeyboardChangeListener onKeyboardChangeListener) {
        this.onKeyboardChangeListener = onKeyboardChangeListener;
    }

    public void setOnKeyBoardHeightListener(OnKeyBoardHeightListener onKeyBoardHeightListener) {
        this.onKeyBoardHeightListener = onKeyBoardHeightListener;
    }


    public int getScreenHeight() {
        return mScreenHeight;
    }

    /**
     * 键盘监控事件
     */
    public interface OnKeyboardChangeListener {
        void onKeyBoardStateChange(int state);
    }
}
