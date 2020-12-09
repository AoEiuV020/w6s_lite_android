package com.foreveross.atwork.modules.gesturecode.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.infrastructure.utils.InterceptHelper;
import com.foreveross.atwork.modules.gesturecode.fragment.GestureCodeLockFragment;
import com.foreveross.atwork.support.NoFilterSingleFragmentActivity;

/**
 * Created by dasunsy on 16/1/14.
 */
public class GestureCodeLockActivity extends NoFilterSingleFragmentActivity {

    public static final String ACTION_ROUTE_CLASS = "action_route_class";
    public static final String ACTION_SWITCH = "action_close_switch";
    public static final String ACTION_LOCKING = "action_locking";

    private GestureCodeLockFragment mFragment;
    private Class mClassRouted;
    private int mFromActSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (null != getIntent()) {
            mClassRouted = (Class) getIntent().getSerializableExtra(ACTION_ROUTE_CLASS);
            mFromActSwitch = getIntent().getIntExtra(ACTION_SWITCH, -1);
            InterceptHelper.sIsLocking = getIntent().getBooleanExtra(ACTION_LOCKING, false);
        }

        boolean canBack = (null != mClassRouted) || GestureCodeLockFragment.ActionFromSwitch.CLOSE == mFromActSwitch;
        if (!canBack) {
            setFullScreen();
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static Intent getLockIntent(Context context) {
        Intent intent = new Intent(context, GestureCodeLockActivity.class);
        intent.putExtra(ACTION_LOCKING, true);
        return intent;
    }


    public static Intent getIntent(Context context, Class<?> cls) {
        return getIntent(context, cls, -1);
    }

    public static Intent getIntent(Context context, int switchAction) {
        return getIntent(context, null, switchAction);
    }

    public static Intent getIntent(Context context, Class<?> cls, int switchAction) {
        Intent intent = new Intent(context, GestureCodeLockActivity.class);
        intent.putExtra(ACTION_ROUTE_CLASS, cls);
        intent.putExtra(ACTION_SWITCH, switchAction);
        return intent;
    }


    @Override
    protected Fragment createFragment() {
        mFragment = new GestureCodeLockFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTION_ROUTE_CLASS, mClassRouted);
        bundle.putInt(ACTION_SWITCH, mFromActSwitch);
        mFragment.setArguments(bundle);
        return mFragment;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (null != mFragment) {
                return mFragment.onBackPressed();
            }

        }
        return super.onKeyDown(keyCode, event);
    }


}
