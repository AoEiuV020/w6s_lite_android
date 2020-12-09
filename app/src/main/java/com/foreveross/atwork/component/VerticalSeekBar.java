package com.foreveross.atwork.component;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class VerticalSeekBar extends SeekBar {
    protected OnSeekBarChangeListener mOnSeekBarChangeListener;
    protected int mCurrentWidth;
    protected int mCurrentHeight;
    protected int mPreviousWidth;
    protected int mPreviousHeight;

    public VerticalSeekBar(Context context) {
        super(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public VerticalSeekBar(Context context, AttributeSet attributeSet, int defaultStyle) {
        super(context, attributeSet, defaultStyle);
    }

    @Override
    public synchronized void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener) {
        mOnSeekBarChangeListener = onSeekBarChangeListener;
    }

    @Override
    protected synchronized void onSizeChanged(int currentWidth, int currentHeight, int previousWidth, int previousHeight) {
        super.onSizeChanged(currentWidth, currentHeight, previousWidth, previousHeight);

        mCurrentWidth = currentWidth;
        mCurrentHeight = currentHeight;
        mPreviousWidth = previousWidth;
        mPreviousHeight = previousHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.rotate(-90);
        canvas.translate(-getHeight(), 0);

        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean isEventConsumed = false;
        if (isEnabled()) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    setSelected(true);
                    setPressed(true);
                    if (mOnSeekBarChangeListener != null) {
                        mOnSeekBarChangeListener.onStartTrackingTouch(this);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    setSelected(false);
                    setPressed(false);
                    if (mOnSeekBarChangeListener != null) {
                        mOnSeekBarChangeListener.onStopTrackingTouch(this);
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    int progress = getMax() - (int) (getMax() * motionEvent.getY() / getHeight());
                    setProgress(progress);
                    onSizeChanged(getWidth(), getHeight(), 0, 0);
                    if (mOnSeekBarChangeListener != null) {
                        mOnSeekBarChangeListener.onProgressChanged(this, progress, true);
                    }
                    break;

                case MotionEvent.ACTION_CANCEL:
                    break;
            }

            isEventConsumed = true;
        }

        return isEventConsumed;
    }

    @Override
    public synchronized void setProgress(int progress) {
        if (progress >= 0) {
            super.setProgress(progress);
        } else {
            super.setProgress(0);
        }

        onSizeChanged(mCurrentWidth, mCurrentHeight, mPreviousWidth, mPreviousHeight);
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onProgressChanged(this, progress, false);
        }
    }
}
