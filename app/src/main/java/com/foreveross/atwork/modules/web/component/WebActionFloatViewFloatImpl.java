package com.foreveross.atwork.modules.web.component;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.floatView.WorkplusFloatView;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.modules.web.view.DonutProgress;

public class WebActionFloatViewFloatImpl extends WorkplusFloatView implements View.OnClickListener {

    private static final String TAG = "WebUrlHookingFloatView";

    public static final int TYPE = 1;

    Context mContext;

    private DonutProgress mVCircleProgress;

    // View中X坐标
    private float mXInView;
    // View中Y坐标
    private float mYInView;
    // 当前X坐标
    private float mXInScreen;
    // 当前Y坐标
    private float mYInScreen;
    // 记录移动前X坐标
    private float mStartX;
    // 记录移动前Y坐标
    private float mStartY;



    WindowManager mWindowManager;


    // 浮层是否正在销毁
    private boolean mViewIsDestroying;

    public WebActionFloatViewFloatImpl(@NonNull Context context) {
        super(context);

        this.mContext = context;
        mWindowManager = (WindowManager) getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        initView();
    }

    public WebActionFloatViewFloatImpl(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_workplus_float_web_action_btn, this);
        mVCircleProgress = view.findViewById(R.id.v_circle_progress);

        view.setOnClickListener(this);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mViewIsDestroying) {
            return true;
        }
        // 获取状态栏高度
        Rect frame = new Rect();
        getWindowVisibleDisplayFrame(frame);
        int statusBar = frame.top;
        Log.d(TAG, "statusBar: " + statusBar);


        // 获取相对屏幕的坐标，即以屏幕左上角为原点
        mXInScreen = event.getRawX();
        mYInScreen = event.getRawY() - statusBar;
        Log.i("onTouchEvent", "x: " + mXInScreen + ", y: " + mYInScreen);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 获取相对View的坐标，即以此View左上角为原点
                mXInView = event.getX();
                mYInView = event.getY();
                mStartX = mXInScreen;
                mStartY = mYInScreen;
                Log.i("ACTION_DOWN", "mXInView: " + mXInView + ", mTouchStartY: " + mYInView);


                mVCircleProgress.setProgressAnimate(1000, 100);

                break;

            case MotionEvent.ACTION_MOVE:
                mXInScreen = event.getRawX();
                mYInScreen = event.getRawY() - statusBar;
                Log.i("ACTION_MOVE", "mXInScreen: " + mXInScreen + ", mYInScreen: " + mYInScreen + ", mXInView: " + mXInView + ", mYInView: " + mYInView);
//                updateViewPosition(mXInScreen);
                break;

            case MotionEvent.ACTION_UP:
                if (Math.abs(mXInScreen - mStartX) < 5.0 && Math.abs(mYInScreen - mStartY) < 5.0) {

                    this.onClick(this);
                    mViewIsDestroying = true;
                    Log.i(TAG, "click floating window");


                } else {
                    int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
                    float x = mXInScreen;
                    if (x < screenWidth / 2) {
                        x = DensityUtil.dip2px(20f);
                    } else {
                        x = mInitXInScreen;
                    }
//                    updateViewPosition(x);
                }
                break;
        }
        return true;
    }


    private void updateViewPosition(float x) {


        mWmParams.x = (int) (x);
        mWmParams.y = (int) (mInitYInScreen);
        mWindowManager.updateViewLayout(this, mWmParams);
    }

    @Override
    public void onClick(View v) {

    }
}
