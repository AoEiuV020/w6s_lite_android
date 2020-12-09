package com.foreveross.atwork.modules.voip.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.manager.VoipNoticeManager;
import com.foreveross.atwork.modules.voip.activity.agora.AgoraCallActivity;
import com.foreveross.atwork.modules.voip.service.CallService;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by dasunsy on 2016/9/22.
 */

public abstract class CallActivity extends SingleFragmentActivity {
    public static final String TAG = AgoraCallActivity.class.getSimpleName();

    public static final String EXTRA_START_FROM_OUTSIDE = "extra_start_from_outside";

    public static final String ACTION_FINISH = "ACTION_FINISH";

    //邀请语音会议 code
    public static final int CODE_INVITE_VOIP_MEETING = 1;

    public static boolean sIsOpening = false;

    public static boolean sIsVisible = false;


    public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(ACTION_FINISH.equals(action)) {
                CallActivity.this.finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sIsOpening = true;

        super.onCreate(savedInstanceState);
        registerBasicBroadcast();

    }

    @Override
    protected void onStart() {
        super.onStart();

        sIsVisible = true;

        CallService.init();

        VoipNoticeManager.getInstance().rejectNotificationsCancel(this);

    }

    @Override
    protected void onStop() {
        super.onStop();

        sIsVisible = false;


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        sIsOpening = false;
        unregisterBasicBroadcast();
    }

    @Override
    public void changeStatusBar() {
        //do nothing
    }

    @Override
    public boolean shouldInterceptOnStart() {
        return false;
    }

    public static void finishRemote() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(new Intent(ACTION_FINISH));
    }

    private void registerBasicBroadcast() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mBroadcastReceiver, new IntentFilter(ACTION_FINISH));
    }

    private void unregisterBasicBroadcast() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(mBroadcastReceiver);
    }
}
