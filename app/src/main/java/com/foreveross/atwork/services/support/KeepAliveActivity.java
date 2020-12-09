package com.foreveross.atwork.services.support;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.services.ImSocketService;

/**
 * Created by dasunsy on 16/8/28.
 */
public class KeepAliveActivity extends Activity {

    public final static String DATA_IS_INIT = "data_is_init";

    private boolean mIsInit = true;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, KeepAliveActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(null != savedInstanceState) {
            mIsInit = savedInstanceState.getBoolean(DATA_IS_INIT);
        }

        LogUtil.e(ImSocketService.TAG, "KeepAliveActivity onCreate");

        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);

        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;

        window.setAttributes(params);

        KeepLiveSupport.setKeepLiveActivity(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        /**
         * 非第一次启动, 需要销毁自己
         * */
        if(!mIsInit) {
            try {
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsInit = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(DATA_IS_INIT, mIsInit);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LogUtil.e(ImSocketService.TAG, "KeepAliveActivity onDestroy");


        KeepLiveSupport.clearKeepLiveActivity();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
