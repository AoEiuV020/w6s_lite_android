package com.foreveross.atwork.modules.chat.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.infrastructure.newmessage.post.chat.MultipartChatMessage;
import com.foreveross.atwork.modules.chat.fragment.MultiPartDetailFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by dasunsy on 2017/6/22.
 */

public class MultiPartDetailActivity extends SingleFragmentActivity {

    public static final String DATA_MULTIPART_MSG = "DATA_MULTIPART_MSG";
    public static final String DATA_TRANSLATE_LANGUAGE = "DATA_TRANSLATE_LANGUAGE";

    public static Intent getIntent(Context context, MultipartChatMessage multipartChatMessage) {
        Intent intent = new Intent();
        intent.setClass(context, MultiPartDetailActivity.class);
        intent.putExtra(DATA_MULTIPART_MSG, multipartChatMessage);

        return intent;
    }

    public static Intent getIntent(Context context, MultipartChatMessage multipartChatMessage, String translateLanguage) {
        Intent intent = new Intent();
        intent.setClass(context, MultiPartDetailActivity.class);
        intent.putExtra(DATA_MULTIPART_MSG, multipartChatMessage);
        intent.putExtra(DATA_TRANSLATE_LANGUAGE, translateLanguage);
        return intent;
    }



    @Override
    protected Fragment createFragment() {
        return new MultiPartDetailFragment();
    }

}
