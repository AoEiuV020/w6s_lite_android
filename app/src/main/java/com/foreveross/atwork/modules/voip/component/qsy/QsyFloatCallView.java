package com.foreveross.atwork.modules.voip.component.qsy;


import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.voip.CallState;
import com.foreveross.atwork.infrastructure.model.voip.PhoneState;
import com.foreveross.atwork.modules.voip.component.BaseVoipFloatCallView;
import com.foreveross.atwork.modules.voip.support.qsy.TangSDKInstance;
import com.foreveross.atwork.modules.voip.support.qsy.interfaces.ICallDelegatePrivate;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.modules.voip.utils.qsy.PromptUtil;

/**
 * @author zhen.liu
 * @brief 呼中的悬浮状态层, 系统顶层窗口，用来显示通话状态及视频画面
 * @date 2015/12/28
 */

public class QsyFloatCallView extends BaseVoipFloatCallView implements ICallDelegatePrivate {
    private static final String TAG = "FloatCallView";


    private Context mContext;

    private boolean mIsVideoCall;

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

    private SurfaceView m_surfaceLocal;

    private OnClickListener mOnClickListener;

    private View mRootView;
    // 时间状态显示
    private TextView mTvTimeState;
    // 通过结束提示
    private TextView mTvFinishState;

    private WindowManager mWindowManager;

    private WindowManager.LayoutParams mWmParams;

    private boolean mIsReconnecting = false;
    private int mRetriesTimes = 0;
    // 浮层是否正在销毁
    private boolean mViewIsDestroying;

    private Handler mHandler;

    public QsyFloatCallView(Context context) {
        super(context);

        mIsReconnecting = false;
        mRetriesTimes = 0;
        this.mContext = context;
        this.mIsVideoCall = TangSDKInstance.getInstance().haveVideoNeedRestore();
        if (mIsVideoCall) {
            mResId = R.layout.layout_qsy_float_call_video;
        } else {
            mResId = R.layout.layout_qsy_float_call_voice;
        }
        LayoutInflater.from(mContext).inflate(mResId, this);

        initView();
        mWindowManager = (WindowManager) getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        TangSDKInstance.getInstance().setDelegatePrivate(QsyFloatCallView.this);
        showVideo();

        mHandler = new Handler(Looper.getMainLooper());

    }

    private void initView() {
        if (mIsVideoCall) {
            mRootView = findViewById(R.id.video_call_layout);
        } else {
            mRootView = findViewById(R.id.voice_call_layout);
            mTvTimeState = (TextView) findViewById(R.id.call_state_prompt_text);
            mTvFinishState = (TextView) findViewById(R.id.call_state_text_end);
        }

    }

    public void setParams(WindowManager.LayoutParams params) {
        mWmParams = params;
    }

    public void showVideo() {
        if (!TangSDKInstance.getInstance().haveVideoNeedRestore()) {
            return;
        }
        TangSDKInstance.getInstance().openVideoCall();
        TangSDKInstance.getInstance().restoreNeedShowVideo(true);
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
                        TangSDKInstance.getInstance().saveShowingVideo(-1);
                        TangSDKInstance.getInstance().setDelegatePrivate(null);
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

    @Override
    public void onUserListUpdated() {

    }

    @Override
    public void onUserBusy(String busyTip) {

    }

    @Override
    public void onLoudSpeakerStatusChanged(boolean bLoudSpeaker) {

    }

    @Override
    public void onIsSpeakingChanged(String strSpeakingNames) {

    }

    @Override
    public void onVideoItemAdded(String userId) {

    }

    @Override
    public void onVideoItemDeleted(String userId) {

    }

    @Override
    public Object onVideoItemAttachSurface(String userId) {
        Log.d(TAG, "videoitem start attach");

        //fix bug B160121-034
        SurfaceView videoSurfaceView = new SurfaceView(mContext);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                (int) PromptUtil.convertDipToPx(mContext, 90),
                (int) PromptUtil.convertDipToPx(mContext, 90 / mRatio));
        params.gravity = Gravity.CENTER;
//        params.leftMargin = (int) PromptUtil.convertDipToPx(mContext, 1);
//        params.topMargin = (int) PromptUtil.convertDipToPx(mContext, 1);
        ((FrameLayout) mRootView).addView(videoSurfaceView, params);

        return videoSurfaceView;
    }


    @Override
    public void onVideoItemDetachSurface(String userId, Object videoSurface) {
        //fix bug B160121-034
        SurfaceView videoSurfaceView = (SurfaceView) videoSurface;
        if (videoSurfaceView != null) {
            videoSurfaceView.setVisibility(View.GONE);
            if (videoSurfaceView.getParent() != null) {
                ((FrameLayout) videoSurfaceView.getParent()).removeView(videoSurfaceView);
            }
        }
    }

    @Override
    public void onVideoItemShowed(String userId, String domainId) {

    }

    /**
     * 桌面共享开始
     */
    @Override
    public void onDesktopShared() {

    }

    /**
     * 桌面共享停止
     */
    @Override
    public void onDesktopShareStopped() {

    }

    /**
     * 桌面首帧显示通知
     */
    @Override
    public void onDesktopViewerShowed() {

    }

    /**
     * 桌面显示关闭通知
     */
    @Override
    public void onDesktopViewerStopped() {

    }

    /**
     * 通知视频呼叫结束了
     */
    @Override
    public void onVideoCallClosed() {
        this.mIsVideoCall = false;
        LayoutInflater.from(mContext).inflate(R.layout.layout_qsy_float_call_voice, this);
        initView();

//        postInvalidate();
        invalidate();
    }

    /**
     * 当前呼状态
     *
     * @param callState
     */
    @Override
    public void onCallStateChanged(final CallState callState) {
        mHandler.post(() -> handleCallStateChanged(callState));
    }

    public void handleCallStateChanged(CallState callState) {
        Log.d(TAG, "onCallStateChanged: " + callState.name());
        switch (callState) {
            case CallState_Calling: {
                mIsReconnecting = false;
                mRetriesTimes = 0;
            }
            break;
            case CallState_Waiting: {
                if (mTvTimeState != null) {
                    mTvTimeState.setText(mContext.getString(R.string.tangsdk_call_state_waiting));
                }
            }
            break;
            case CallState_Disconnected: {
                if (!mIsReconnecting) {
                    Toast.makeText(mContext, mContext.getString(R.string.tangsdk_chat_audio_reconnect_msg), Toast.LENGTH_SHORT).show();
                    //fix bug [B160114-002]
                    mHandler.postDelayed(() -> {
                        TangSDKInstance.getInstance().reconnectCall();
                        mRetriesTimes++;
                    }, 5000);
                } else {
                    if (mRetriesTimes < 6) {
                        //fix bug [B160114-002]
                        mHandler.postDelayed(() -> {
                            TangSDKInstance.getInstance().reconnectCall();
                            mRetriesTimes++;
                        }, 5000);

                    } else {
                        Toast.makeText(mContext, mContext.getString(R.string.tangsdk_reconnect_failed), Toast.LENGTH_SHORT).show();
                        TangSDKInstance.getInstance().stopCall();
                        mIsReconnecting = false;
                        mRetriesTimes = 0;
                    }
                }
            }
            break;
            case CallState_ReConnecting: {
                mIsReconnecting = true;
            }
            break;
            case CallState_Ended: {
                if (mTvTimeState != null) {
                    mTvTimeState.setVisibility(View.GONE);
                }

                if (mTvFinishState != null) {
                    mTvFinishState.setVisibility(View.VISIBLE);
                }

                // 移除当前View
                mHandler.postDelayed(() -> {
                    try {
                        mWindowManager.removeView(QsyFloatCallView.this);
                    } catch (IllegalArgumentException e) {
                        Log.e(TAG, "IllegalArgumentException: view not attached to window manager");
                    }
                }, 1500);
            }
            break;
            default: {
                if (mTvTimeState != null) {
                    mTvTimeState.setText(mContext.getString(R.string.tangsdk_call_state_waiting));
                }
            }
            break;
        }
    }

    /**
     * 当前通话时间
     *
     * @param nSeconds
     */
    @Override
    public void onCallingTimeElpased(long nSeconds) {
        Log.d(TAG, "call duration time: " + nSeconds);

        String durationTimeStr = VoipHelper.toCallDurationShow(nSeconds);

        Log.d(TAG, "durationTimeStr: " + durationTimeStr + ", mIsVideoCall: " + mIsVideoCall);

        if (!mIsVideoCall) {
            mTvTimeState.setText(durationTimeStr);
        }
    }



    @Override
    public void onVOIPQualityIsBad() {

    }

    @Override
    public void onPhoneCallResult(boolean bSucceeded) {

    }

    @Override
    public void onPhoneStateChanged(PhoneState phoneState) {

    }


}