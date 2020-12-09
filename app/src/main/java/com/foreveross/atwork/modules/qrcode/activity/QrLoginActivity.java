package com.foreveross.atwork.modules.qrcode.activity;/**
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
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 *                            |__|
 */


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.modules.qrcode.fragment.QrLoginFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by reyzhang22 on 16/6/14.
 */
public class QrLoginActivity extends SingleFragmentActivity {

    private String mCode;

    private String mFrom;

    public static Intent getIntent(Context context,String code, String from) {
        Intent intent = new Intent(context, QrLoginActivity.class);
        intent.putExtra(QrLoginFragment.ARGUMENT_QR_LOGIN_CODE, code);
        intent.putExtra(QrLoginFragment.ARGUMENT_QR_LOGIN_FROM, from);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mCode = getIntent().getStringExtra(QrLoginFragment.ARGUMENT_QR_LOGIN_CODE);
        mFrom = getIntent().getStringExtra(QrLoginFragment.ARGUMENT_QR_LOGIN_FROM);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        Bundle bundle = new Bundle();
        bundle.putString(QrLoginFragment.ARGUMENT_QR_LOGIN_CODE, mCode);
        bundle.putString(QrLoginFragment.ARGUMENT_QR_LOGIN_FROM, mFrom);
        QrLoginFragment fragment = new QrLoginFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
}
