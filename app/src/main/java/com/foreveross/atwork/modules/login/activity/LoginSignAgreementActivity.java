package com.foreveross.atwork.modules.login.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.infrastructure.model.face.FaceBizInfo;
import com.foreveross.atwork.modules.login.fragment.BaseAgreementFragment;
import com.foreveross.atwork.modules.login.fragment.FaceAgreementFragment;
import com.foreveross.atwork.modules.login.fragment.FaceLoginAgreementFragment;
import com.foreveross.atwork.modules.login.fragment.LoginSignAgreementFragment;
import com.foreveross.atwork.support.NoFilterSingleFragmentActivity;

/**
 * Created by dasunsy on 2017/7/20.
 */

public class LoginSignAgreementActivity extends NoFilterSingleFragmentActivity {

    public static String ACTION_CLEAR_OTHER_SHADOW = "ACTION_CLEAR_OTHER_SHADOW";

    public static String INTENT_AGREEMENT_TYPE = "INTENT_AGREEMENT_TYPE";

    public static String INTENT_FACE_AGREEMENT_INFO = "INTENT_FACE_AGREEMENT_INFO";

    public static String INTENT_FACE_LOGIN_TICKET = "INTENT_FACE_LOGIN_TICKET";

    private BaseAgreementFragment mFragment;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(ACTION_CLEAR_OTHER_SHADOW.equals(action)) {
                try {
                    LoginSignAgreementActivity.this.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, LoginSignAgreementActivity.class);
        return intent;
    }

    public static Intent getFaceProtocolIntent(Context context, FaceBizInfo faceBizInfo) {
        Intent intent = new Intent(context, LoginSignAgreementActivity.class);
        intent.putExtra(INTENT_FACE_AGREEMENT_INFO, faceBizInfo);
        intent.putExtra(INTENT_AGREEMENT_TYPE, 1);
        return intent;
    }

    public static Intent getFaceLoginProtocalIntent(Context context, String ticket) {
        Intent intent = new Intent(context, LoginSignAgreementActivity.class);
        intent.putExtra(INTENT_FACE_LOGIN_TICKET, ticket);
        intent.putExtra(INTENT_AGREEMENT_TYPE, 2);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        if (getIntent() != null && getIntent().getIntExtra(INTENT_AGREEMENT_TYPE, 0) == 1) {
            mFragment = new FaceAgreementFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(INTENT_FACE_AGREEMENT_INFO, getIntent().getParcelableExtra(INTENT_FACE_AGREEMENT_INFO));
            mFragment.setArguments(bundle);
        } else if(getIntent() != null && getIntent().getIntExtra(INTENT_AGREEMENT_TYPE, 0) == 2) {
            mFragment = new FaceLoginAgreementFragment();
            Bundle bundle = new Bundle();
            bundle.putString(INTENT_FACE_LOGIN_TICKET, getIntent().getStringExtra(INTENT_FACE_LOGIN_TICKET));
            mFragment.setArguments(bundle);
        } else {
            mFragment = new LoginSignAgreementFragment();
        }
        return mFragment;
    }

    @Override
    protected void onStart() {
        super.onStart();

        unregisterReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();

        registerReceiver();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        unregisterReceiver();
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

    private void registerReceiver() {
        LocalBroadcastManager.getInstance(AtworkApplicationLike.baseContext).registerReceiver(mReceiver, new IntentFilter(ACTION_CLEAR_OTHER_SHADOW));
    }

    private void unregisterReceiver() {
        LocalBroadcastManager.getInstance(AtworkApplicationLike.baseContext).unregisterReceiver(mReceiver);
    }

    public static void clearShadow() {
        Intent intent = new Intent(ACTION_CLEAR_OTHER_SHADOW);
        LocalBroadcastManager.getInstance(AtworkApplicationLike.baseContext).sendBroadcast(intent);

    }

    public interface OnAgreedListener {
        void isAgreed(boolean status);
    }


}
