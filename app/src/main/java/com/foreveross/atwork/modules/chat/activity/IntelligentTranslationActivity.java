package com.foreveross.atwork.modules.chat.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.modules.chat.fragment.IntelligentTranslationFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by wuzejie on 19/12/6.
 * Description:
 */
public class IntelligentTranslationActivity extends SingleFragmentActivity {

    public static final String TAG = IntelligentTranslationActivity.class.getName();

    public static final String DATA_SESSION = "DATA_SESSION";
    public static final String DATA_SELECTEDLANGUAGE = "DATA_SELECTEDLANGUAGE";

    public static Intent getIntent(String  selectedItem, SessionType mChatInfoType, String  chatIdentifier, String  domainId) {
        Intent intent = new Intent();
        intent.setClass(BaseApplicationLike.baseContext, IntelligentTranslationActivity.class);
        intent.putExtra(DATA_SELECTEDLANGUAGE, selectedItem);
        intent.putExtra(ChatInfoActivity.CHAT_INFO_TYPE, mChatInfoType);
        intent.putExtra(ChatInfoActivity.DATA_CHAT_IDENTIFIER, chatIdentifier);
        intent.putExtra(ChatInfoActivity.DATA_CHAT_DOMAIN_ID, domainId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }



    @Override
    protected Fragment createFragment() {
        IntelligentTranslationFragment intelligentTranslationFragment = new IntelligentTranslationFragment();

        return intelligentTranslationFragment;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

}
