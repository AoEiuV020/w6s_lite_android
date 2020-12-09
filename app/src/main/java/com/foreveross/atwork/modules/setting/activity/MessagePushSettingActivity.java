package com.foreveross.atwork.modules.setting.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.modules.setting.fragment.MessagePushSettingFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;


public class MessagePushSettingActivity extends SingleFragmentActivity {

    private static final String TAG = MessagePushSettingActivity.class.getSimpleName();

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, MessagePushSettingActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected Fragment createFragment() {
        return new MessagePushSettingFragment();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //界面回退动画
        this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

}
