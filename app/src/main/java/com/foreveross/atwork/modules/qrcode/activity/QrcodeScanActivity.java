package com.foreveross.atwork.modules.qrcode.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreverht.workplus.ui.component.statusbar.WorkplusStatusBarHelper;
import com.foreveross.atwork.R;
import com.foreveross.atwork.cordova.plugin.WorkPlusBarcodeScannerPlugin;
import com.foreveross.atwork.modules.qrcode.fragment.QrcodeScanFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;


public class QrcodeScanActivity extends SingleFragmentActivity {


    private boolean cordovaRequest;

    private boolean mScannedType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        cordovaRequest = intent.getBooleanExtra(WorkPlusBarcodeScannerPlugin.DATA_FROM_CORDOVA,false);
        mScannedType = intent.getBooleanExtra(WorkPlusBarcodeScannerPlugin.DATA_IS_NATIVE_ACTION, false);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        QrcodeScanFragment qrcodeScanFragment = new QrcodeScanFragment();
        qrcodeScanFragment.initBundle(cordovaRequest, mScannedType);
        return qrcodeScanFragment;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }


    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, QrcodeScanActivity.class);
        return intent;
    }

    @Override
    public void changeStatusBar() {
        WorkplusStatusBarHelper.setCommonFullScreenStatusBar(this, false);
//        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.translation_333), 176);

    }
}