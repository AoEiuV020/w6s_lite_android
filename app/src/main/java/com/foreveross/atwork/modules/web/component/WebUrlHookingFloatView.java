package com.foreveross.atwork.modules.web.component;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.floatView.WorkplusFloatView;
import com.foreveross.atwork.component.floatView.service.WorkplusFloatService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;

public class WebUrlHookingFloatView extends WorkplusFloatView implements View.OnClickListener {

    private static final String TAG = "WebUrlHookingFloatView";

    public static final int TYPE = 0;

    Context mContext;

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

    private OnClickListener mOnMainClickListener;


    WindowManager mWindowManager;


    private ImageView mIvFloating;

    // 浮层是否正在销毁
    private boolean mViewIsDestroying;

    public WebUrlHookingFloatView(@NonNull Context context) {
        super(context);

        this.mContext = context;
        mWindowManager = (WindowManager) getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        initView();
    }

    public WebUrlHookingFloatView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_workplus_float_web_url_hooking, this);
        mIvFloating = view.findViewById(R.id.iv_float);


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

                    this.onClick(this);
                    mViewIsDestroying = true;
                    Log.i(TAG, "click floating window");

//                    if (mOnMainClickListener != null) {
//                        mOnMainClickListener.onClick(this);
//                        mViewIsDestroying = true;
//                        Log.i(TAG, "click floating window");
//                    }
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
        this.mOnMainClickListener = l;
    }

    private void updateViewPosition() {
        mWmParams.x = (int) (mXInScreen - mXInView);
        mWmParams.y = (int) (mYInScreen - mYInView);
        mWindowManager.updateViewLayout(this, mWmParams);
    }

    @Override
    public void onClick(View v) {
        String url = PersonalShareInfo.getInstance().getUrlHookingForFloat(BaseApplicationLike.baseContext);

        if (StringUtils.isEmpty(url)) {
            return;
        }

        WebViewControlAction webViewControlAction = WebViewControlAction.newAction()
                .setUrl(url)
                .setHookingFloatMode(true);

        mContext.startActivity(WebViewActivity.getIntent(mContext, webViewControlAction));

        WorkplusFloatService.Companion.sendRemoveFloatingWindow(TYPE);
    }
}
