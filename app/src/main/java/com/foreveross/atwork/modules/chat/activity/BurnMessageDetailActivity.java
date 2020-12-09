package com.foreveross.atwork.modules.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil;
import com.foreveross.atwork.modules.chat.fragment.BurnMessageDetailFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by dasunsy on 2017/4/21.
 */

public class BurnMessageDetailActivity extends SingleFragmentActivity {

    public static final String DATA_MESSAGE = "DATA_MESSAGE";
    public static final String DATA_SESSION = "DATA_SESSION";

    private ChatPostMessage mChatPostMessage;

    public static Intent getIntent(Context context, ChatPostMessage message, String sessionId) {
        Intent intent = new Intent();
        intent.setClass(context, BurnMessageDetailActivity.class);
        intent.putExtra(DATA_MESSAGE, message);
        intent.putExtra(DATA_SESSION, sessionId);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mChatPostMessage = (ChatPostMessage) getIntent().getSerializableExtra(DATA_MESSAGE);

        super.onCreate(savedInstanceState);
    }


    @Override
    protected Fragment createFragment() {
        return new BurnMessageDetailFragment();
    }

    @Override
    public void changeStatusBar() {
        StatusBarUtil.setColorNoTranslucent(this, Color.parseColor("#222222"));
    }
}
