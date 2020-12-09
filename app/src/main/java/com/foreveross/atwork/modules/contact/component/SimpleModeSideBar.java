package com.foreveross.atwork.modules.contact.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;

public class SimpleModeSideBar extends View {
    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;

    private String[] mFullLetterArray = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};

    private String[] mLetterArray = mFullLetterArray;

    private int mChoose = -1;
    private Paint paint = new Paint();

    private TextView mTextDialog;

    private int mSingleHeight;

    private int mPaddingTop;

    public void setTextView(TextView mTextDialog) {
        this.mTextDialog = mTextDialog;
    }


    public SimpleModeSideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SimpleModeSideBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        refreshLetters(mFullLetterArray);
    }

    public SimpleModeSideBar(Context context) {
        super(context);
    }

    public void refreshLetters(String[] letterArray) {
        this.mLetterArray = letterArray;

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight();
        int width = getWidth();

        mSingleHeight = height / 27;

        mPaddingTop = (height - mSingleHeight * mLetterArray.length) / 2;


        for (int i = 0; i < mLetterArray.length; i++) {
            paint.setColor(getResources().getColor(R.color.common_text_color));
            // paint.setColor(Color.WHITE);
//			paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            paint.setAntiAlias(true);
            paint.setTextSize(DensityUtil.sp2px(11));
            if (i == mChoose) {
                paint.setColor(getResources().getColor(R.color.common_blue_bg));
                paint.setFakeBoldText(true);
            }
            float xPos = width / 2 - paint.measureText(mLetterArray[i]) / 2;
            float yPos = mSingleHeight * i + mSingleHeight + mPaddingTop;

            canvas.drawText(mLetterArray[i], xPos, yPos, paint);
            paint.reset();
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = mChoose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;

        int realHeight = mSingleHeight * mLetterArray.length;

        final int c = (int) ((y - mPaddingTop) / realHeight * mLetterArray.length);

        switch (action) {
            case MotionEvent.ACTION_UP:
                setBackgroundDrawable(new ColorDrawable(0x00000000));
                mChoose = -1;
                invalidate();
                if (mTextDialog != null) {
                    mTextDialog.setVisibility(View.INVISIBLE);
                }
                break;

            default:
                setBackgroundResource(R.color.transparent);
                if (oldChoose != c && 0 <= c) {
                    if (c >= 0 && c < mLetterArray.length) {
                        if (listener != null) {
                            listener.onTouchingLetterChanged(mLetterArray[c]);
                        }
                        if (mTextDialog != null) {
                            mTextDialog.setText(mLetterArray[c]);
                            mTextDialog.setTranslationY(mSingleHeight * c + mSingleHeight + mPaddingTop + DensityUtil.dip2px(80) - mTextDialog.getHeight() - DensityUtil.dip2px(1));
                            mTextDialog.setVisibility(View.VISIBLE);
                        }

                        mChoose = c;
                        invalidate();
                    }
                }

                break;
        }
        return true;
    }

    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    public interface OnTouchingLetterChangedListener {
        void onTouchingLetterChanged(String letter);
    }

}