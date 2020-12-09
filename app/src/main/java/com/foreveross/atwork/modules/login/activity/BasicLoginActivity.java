package com.foreveross.atwork.modules.login.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.R;
import com.foreveross.atwork.support.AtworkBaseActivity;
import com.foreveross.atwork.support.NoFilterSingleFragmentActivity;

/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 * Created by reyzhang22 on 16/7/13.
 */
public abstract class BasicLoginActivity extends NoFilterSingleFragmentActivity {

    public static final String TAG_FINISH_LOGIN = "TAG_FINISH_LOGIN";

    private BroadcastReceiver mKickErrorReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (AtworkBaseActivity.LICENSE_OVERDUE.equals(action)) {
                showKickAlert(R.string.license_overdue);

            } else if (AtworkBaseActivity.ACCOUNT_IS_LOCKED.equals(action)) {
                showKickAlert(R.string.account_is_locked);

            } else if (AtworkBaseActivity.DEVICE_FORBIDDEN.equals(action)) {
                showKickAlert(R.string.device_forbidden);

            } else if (AtworkBaseActivity.DEVICE_BINDING.equals(action)) {
                showKickAlert(R.string.device_binding);

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFinishChainTag(TAG_FINISH_LOGIN);
        super.onCreate(savedInstanceState);
        registerBroadcast();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
    }

    @Override
    public boolean isInLoginFlow() {
        return true;
    }


    public void showKickAlert(int alertStringRes) {
        AtworkAlertDialog dialog = new AtworkAlertDialog(BasicLoginActivity.this);
        dialog.setBrightBtnText(R.string.ok);
        dialog.hideDeadBtn();
        dialog.hideTitle();
        dialog.setContent(alertStringRes);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setClickBrightColorListener(dia -> dialog.dismiss());
        dialog.show();
    }

    private void registerBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AtworkBaseActivity.LICENSE_OVERDUE);
        filter.addAction(AtworkBaseActivity.ACCOUNT_IS_LOCKED);
        filter.addAction(AtworkBaseActivity.DEVICE_FORBIDDEN);
        filter.addAction(AtworkBaseActivity.DEVICE_BINDING);

        LocalBroadcastManager.getInstance(this).registerReceiver(mKickErrorReceiver, filter);
    }

    private void unregisterBroadcast() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mKickErrorReceiver);
    }

}
