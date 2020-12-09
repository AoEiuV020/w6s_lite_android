package com.foreveross.atwork.modules.voip.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.support.VoipSdkType;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.ServiceCompat;
import com.foreveross.atwork.modules.voip.activity.agora.AgoraCallActivity;
import com.foreveross.atwork.modules.voip.activity.qsy.QsyCallActivity;
import com.foreveross.atwork.modules.voip.component.BaseVoipFloatCallView;
import com.foreveross.atwork.modules.voip.component.WorkplusFloatCallView;
import com.foreveross.atwork.modules.voip.component.qsy.QsyFloatCallView;
import com.foreveross.atwork.modules.voip.support.qsy.TangSDKInstance;
import com.foreveross.atwork.utils.FloatWinHelper;

public class CallService extends Service implements View.OnClickListener {

    public static final String ACTION_SHOW_CALL_FLOAT_VIEW = "action_show_call_float_view";

    public static final String ACTION_KILL_CALL_FLOAT_VIEW = "action_kill_call_float_view";

    private static BaseVoipFloatCallView sFloatCallView;

    private WindowManager wm;

    private BroadcastReceiver mFloatViewBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_SHOW_CALL_FLOAT_VIEW.equals(action)) {
                createFloatingWindow();

            } else if (ACTION_KILL_CALL_FLOAT_VIEW.equals(action)) {
                removeFloatView();
            }
        }
    };

    public static void init() {
        Context theAppContext = BaseApplicationLike.baseContext;

        ServiceCompat.startServiceCompat(theAppContext, CallService.class);

    }


    @Override
    public void onCreate() {
        super.onCreate();

        Context appContext = getApplicationContext();
        if (AtworkConfig.OPEN_VOIP) {
            if (VoipSdkType.QSY == AtworkConfig.VOIP_SDK_TYPE) {
                TangSDKInstance.getInstance().init(appContext, AtWorkDirUtils.getInstance().getQsyVoipLOG());

            }

        }
        wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SHOW_CALL_FLOAT_VIEW);
        filter.addAction(ACTION_KILL_CALL_FLOAT_VIEW);

        LocalBroadcastManager.getInstance(appContext).registerReceiver(mFloatViewBroadcastReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        removeFloatView();
        LocalBroadcastManager.getInstance(AtworkApplicationLike.baseContext).unregisterReceiver(mFloatViewBroadcastReceiver);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public static void sendCreateFloatingWindow() {
        init();
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(ACTION_SHOW_CALL_FLOAT_VIEW));
    }

    public static void sendRemoveFloatingWindow() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(ACTION_KILL_CALL_FLOAT_VIEW));
    }

    /**
     * 创建悬浮层用于显示通话状态，视频图像
     */
    private void createFloatingWindow() {

        if (null == sFloatCallView || !sFloatCallView.isShown()) {

            WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

            createFloatView();

            sFloatCallView.setOnClickListener(this);

            int screenWidth = wm.getDefaultDisplay().getWidth();
            int screenHeight = wm.getDefaultDisplay().getHeight();

            // 设置悬浮窗类型
            FloatWinHelper.setFloatType(wmParams);


            // 设置背景透明
            wmParams.format = PixelFormat.RGBA_8888;

            wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

            // 确定悬浮窗的对齐方式
            wmParams.gravity = Gravity.LEFT | Gravity.TOP;
            // 设置悬浮层初始位置
            wmParams.x = screenWidth;
            wmParams.y = screenHeight / 2;

            wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            sFloatCallView.setParams(wmParams);

            // show float view
            wm.addView(sFloatCallView, wmParams);

        }
    }



    private void createFloatView() {
        if (VoipSdkType.QSY == AtworkConfig.VOIP_SDK_TYPE) {
            sFloatCallView = new QsyFloatCallView(this);

        } else {
            sFloatCallView = new WorkplusFloatCallView(this);

        }
    }

    private void removeFloatView() {
        try {
            if (null != sFloatCallView) {
                wm.removeView(sFloatCallView);
                sFloatCallView = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isModeNeedScreenControl() {
        return BaseVoipFloatCallView.sIsModeNeedScreenControl;
    }


    @Override
    public void onClick(View v) {
        // 移除悬浮窗，并重建ACTIVITY
        removeFloatView();

        Intent intent = null;
        if (VoipSdkType.QSY == AtworkConfig.VOIP_SDK_TYPE) {
            intent = QsyCallActivity.getIntent(BaseApplicationLike.baseContext);
            intent.putExtra(QsyCallActivity.EXTRA_START_FROM_OUTSIDE, true);

        } else {
            intent = AgoraCallActivity.getIntent(BaseApplicationLike.baseContext);
            intent.putExtra(AgoraCallActivity.EXTRA_START_FROM_OUTSIDE, true);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
