package com.foreveross.atwork.modules.voip.component;


import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class BaseVoipFloatCallView extends FrameLayout {
    private static final String TAG = "VoipFloatCallView";

    Context mContext;

    // 视频画面宽高比例
    public static float mRatio = (float) 9 / 16;

    private int mResId;
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

    private OnClickListener mOnClickListener;


    WindowManager mWindowManager;

    private WindowManager.LayoutParams mWmParams;

    // 浮层是否正在销毁
    private boolean mViewIsDestroying;

    /**
     * 在一些手机, 例如 vivo 在设置成 TYPE_TOAST后(非设不可), 会悬浮在锁屏之上, 此时需要代码的控制才能让悬浮窗隐藏掉
     * */
    public static boolean sIsModeNeedScreenControl = false;

    Handler mHandler;

    public BaseVoipFloatCallView(Context context) {
        super(context);

        this.mContext = context;

        mWindowManager = (WindowManager) getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        mHandler = new Handler(Looper.getMainLooper());

    }


    public void setParams(WindowManager.LayoutParams params) {
        mWmParams = params;
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

        int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
        int screenHeight = mWindowManager.getDefaultDisplay().getHeight();

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
                break;

            case MotionEvent.ACTION_MOVE:
                mXInScreen = event.getRawX();
                mYInScreen = event.getRawY() - statusBar;
                Log.i("ACTION_MOVE", "mXInScreen: " + mXInScreen + ", mYInScreen: " + mYInScreen + ", mXInView: " + mXInView + ", mYInView: " + mYInView);
                updateViewPosition();
                break;

            case MotionEvent.ACTION_UP:
                if (Math.abs(mXInScreen - mStartX) < 5.0 && Math.abs(mYInScreen - mStartY) < 5.0) {
                    if (mOnClickListener != null) {
                        mOnClickListener.onClick(this);
                        mViewIsDestroying = true;
                        Log.i(TAG, "click floating window");
                    }
                } else {
                    if (mXInScreen < screenWidth / 2) {
                        mXInScreen = 0;
                    } else {
                        mXInScreen = screenWidth;
                    }
                    updateViewPosition();
                }
                break;
        }
        return true;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        this.mOnClickListener = l;
    }

    private void updateViewPosition() {
        mWmParams.x = (int) (mXInScreen - mXInView);
        mWmParams.y = (int) (mYInScreen - mYInView);
        mWindowManager.updateViewLayout(this, mWmParams);
    }




}