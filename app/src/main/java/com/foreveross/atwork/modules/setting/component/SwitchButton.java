package com.foreveross.atwork.modules.setting.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.core.view.ViewCompat;

import com.foreveross.atwork.R;
import com.foreveross.theme.manager.SkinHelper;

/**
 * 设置开关
 * Created by ReyZhang on 2015/5/6.
 */
public class SwitchButton extends View {

    private Drawable switch_btn_round;
    private PaintFlagsDrawFilter paintFlagsDrawFilter;
    private int btnLength;
    private Drawable backgroundBottom;
    private Drawable backgroundTop;
    private int baccgroundWidth;
    private int bacgroundHeight;
    private VelocityTracker mVelocityTracker;
    private float mLastFocusX;
    private float mDownFocusX;
    private int mTouchSlopSquare;
    private int mMinimumFlingVelocity;
    private boolean isOpen = false;
    private Drawable round_rect_wihte_alpha;
    private static final int MSG_WHAT_SWITCH_BUTTON_SCROLL = 0;
    private static final int MSG_WHAT_SWITCH_BUTTON_INIT = 1;

    public boolean isOpen() {
        return isOpen;
    }

    private static final float ACCELERATION = 0.01f;// 加速度
    private int MSG_SEND_UNIT_TIME = 20;// 单位时间
    private float unit_time = 0.1f;// 开始运动到当前的总时间

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_WHAT_SWITCH_BUTTON_SCROLL:
                    boolean result = false;
                    unit_time += unit_time;
                    float deltX = ACCELERATION * unit_time * unit_time;
                    if (isOpen) {
                        result = isScroll(deltX);
                    } else {
                        result = isScroll(baccgroundWidth - deltX - btnLength);
                    }

                    if (!result) {
                        handler.sendEmptyMessageDelayed(MSG_WHAT_SWITCH_BUTTON_SCROLL, MSG_SEND_UNIT_TIME);
                    }
                    break;
                case MSG_WHAT_SWITCH_BUTTON_INIT:
                    if (isOpen) {
                        isScroll(baccgroundWidth - btnLength);
                    } else {
                        isScroll(0);
                    }
                    break;

            }
        }

    };

    public SwitchButton(Context context) {
        super(context);
        initView(context);
    }

    public SwitchButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public SwitchButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        baccgroundWidth = context.getResources().getDimensionPixelSize(R.dimen.dp_51);
        bacgroundHeight = context.getResources().getDimensionPixelSize(R.dimen.dp_31);
        btnLength = context.getResources().getDimensionPixelSize(R.dimen.dp_31);

        backgroundBottom = context.getResources().getDrawable(R.drawable.round_rect_blue);
        backgroundBottom.setBounds(0, 0, baccgroundWidth, bacgroundHeight);
        backgroundTop = context.getResources().getDrawable(R.drawable.round_rect_gray);
        round_rect_wihte_alpha = context.getResources().getDrawable(R.drawable.round_rect_withe_alpha);
        round_rect_wihte_alpha.setBounds(0, 0, baccgroundWidth, bacgroundHeight);
        switch_btn_round = getContext().getResources().getDrawable(R.mipmap.icon_switch_btn_unpressed);
        switch_btn_round.setCallback(this);
        backgroundTop.setBounds(0, 0, baccgroundWidth, bacgroundHeight);
        switch_btn_round.setBounds(0, 0, btnLength, btnLength);

        paintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        int touchSlop;
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        touchSlop = configuration.getScaledTouchSlop();
        mTouchSlopSquare = touchSlop * touchSlop;
        mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        changeBackground();
    }



    public int getDefaultColor() {
        return SkinHelper.parseColorFromTag("c13");
    }




    public void changeBackground() {
        if (backgroundBottom instanceof ShapeDrawable) {
            // cast to 'ShapeDrawable'
            ShapeDrawable shapeDrawable = (ShapeDrawable) backgroundBottom;
            shapeDrawable.getPaint().setColor(getDefaultColor());
        } else if (backgroundBottom instanceof GradientDrawable) {
            // cast to 'GradientDrawable'
            GradientDrawable gradientDrawable = (GradientDrawable) backgroundBottom;
            gradientDrawable.setColor(getDefaultColor());
        } else if (backgroundBottom instanceof ColorDrawable) {
            // alpha value may need to be set again after this call
            ColorDrawable colorDrawable = (ColorDrawable) backgroundBottom;
            colorDrawable.setColor(getDefaultColor());
        }

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(baccgroundWidth, bacgroundHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.setDrawFilter(paintFlagsDrawFilter);
        backgroundBottom.draw(canvas);
        backgroundTop.draw(canvas);
        switch_btn_round.draw(canvas);
        if (!isEnabled()) {
            round_rect_wihte_alpha.draw(canvas);
        }
        canvas.restore();
    }

    private boolean isScroll(float deltaX) {
        boolean result = false;
        setClickable(false);
        if (isOpen) {
            if (deltaX >= baccgroundWidth - btnLength) {
                deltaX = baccgroundWidth - btnLength;
                setClickable(true);
                result = true;
                //动画结束时, 变换背景
                backgroundTop = getContext().getResources().getDrawable(R.drawable.round_rect_blue);
            }
            switch_btn_round = getContext().getResources().getDrawable(R.mipmap.icon_switch_btn_unpressed);

        } else {
            if (deltaX <= 0) {
                deltaX = 0;
                setClickable(true);
                result = true;
            }
            switch_btn_round = getContext().getResources().getDrawable(R.mipmap.icon_switch_btn_unpressed);
            backgroundTop = getContext().getResources().getDrawable(R.drawable.round_rect_gray);

        }
        backgroundTop.setBounds((int) deltaX, 0, baccgroundWidth, bacgroundHeight);

        switch_btn_round.setBounds((int) deltaX, 0, (int) deltaX + btnLength, btnLength);
        ViewCompat.postInvalidateOnAnimation(this);
        if (result) {
            handler.removeMessages(MSG_WHAT_SWITCH_BUTTON_SCROLL);
        }
        return result;
    }

    @Override
    protected void drawableStateChanged() {
        Drawable drawableState = switch_btn_round;
        if (drawableState != null && drawableState.isStateful()) {
            drawableState.setState(getDrawableState());
        }
        super.drawableStateChanged();
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == switch_btn_round || super.verifyDrawable(who);
    }

    public void toggle() {
        setButtonStatusWithAnim(!isOpen);
    }


    /**
     * 有延迟效果
     */
    public void setButtonStatusWithAnim(boolean isOpen) {
        if (this.isOpen != isOpen) {
            this.isOpen = isOpen;
            unit_time = 1.5f;
            handler.sendEmptyMessageDelayed(MSG_WHAT_SWITCH_BUTTON_SCROLL, MSG_SEND_UNIT_TIME);
        }
    }

    public void setButtonStatus(boolean isOpen) {
        if (isOpen) {
            setOpen();
        } else {
            setClose();
        }
    }

    public void setOpen() {
        this.isOpen = true;
        handler.sendEmptyMessage(MSG_WHAT_SWITCH_BUTTON_INIT);
    }

    public void setClose() {
        this.isOpen = false;
        handler.sendEmptyMessage(MSG_WHAT_SWITCH_BUTTON_INIT);
    }
}
