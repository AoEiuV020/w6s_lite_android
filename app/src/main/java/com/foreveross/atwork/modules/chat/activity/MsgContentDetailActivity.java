package com.foreveross.atwork.modules.chat.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.modules.chat.fragment.MsgContentDetailFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by dasunsy on 2017/5/31.
 */

public class MsgContentDetailActivity extends SingleFragmentActivity{

    public static final String DATA_MESSAGE = "DATA_MESSAGE";

    /**
     * 调准的 intent
     * @param context
     * @param chatPostMessage 支持 text 跟 voice 两种类型
     * */
    public static Intent getIntent(Context context, ChatPostMessage chatPostMessage) {
        Intent intent = new Intent(context, MsgContentDetailActivity.class);
        intent.putExtra(DATA_MESSAGE, chatPostMessage);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return new MsgContentDetailFragment();
    }
}
