package com.foreveross.atwork.modules.chat.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.modules.chat.fragment.ChatInfoFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by lingen on 15/3/26.
 * Description:
 */
public class ChatInfoActivity extends SingleFragmentActivity {

    public static final String TAG = ChatInfoActivity.class.getName();
    public static final String CHAT_INFO_TYPE = "CHAT_INFO_TYPE";

    public static final String DATA_CHAT_IDENTIFIER = "DATA_CHAT_IDENTIFIER";
    public static final String DATA_CHAT_DOMAIN_ID = "DATA_CHAT_DOMAIN_ID";
    public static final String DATA_ORG_ID = "DATA_ORG_ID";
    public static final String DATA_SESSION = "DATA_SESSION";

    public static Intent getIntent(SessionType chatInfoType, String chatIdentifier, String domainId, String orgId) {
        Intent intent = new Intent();
        intent.setClass(BaseApplicationLike.baseContext, ChatInfoActivity.class);
        intent.putExtra(CHAT_INFO_TYPE, chatInfoType);
        intent.putExtra(DATA_CHAT_IDENTIFIER, chatIdentifier);
        intent.putExtra(DATA_CHAT_DOMAIN_ID, domainId);
        intent.putExtra(DATA_ORG_ID, orgId);
        return intent;
    }

    public static Intent getIntent(Session session) {
        Intent intent = new Intent();
        intent.setClass(BaseApplicationLike.baseContext, ChatInfoActivity.class);
        intent.putExtra(DATA_SESSION, session);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }



    @Override
    protected Fragment createFragment() {
        ChatInfoFragment chatInfoFragment = new ChatInfoFragment();

        return chatInfoFragment;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

}
