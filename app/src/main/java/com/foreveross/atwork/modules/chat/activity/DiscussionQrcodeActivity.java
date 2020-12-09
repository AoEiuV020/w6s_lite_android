package com.foreveross.atwork.modules.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.modules.chat.fragment.DiscussionQrcodeFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by dasunsy on 16/2/3.
 */
public class DiscussionQrcodeActivity extends SingleFragmentActivity{
    public static final String DATA_DISCUSSION = "data_discussion";
    private DiscussionQrcodeFragment mDiscussionQrcodeFragment;

    private Discussion mDiscussion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDiscussion = getIntent().getParcelableExtra(DATA_DISCUSSION);

        super.onCreate(savedInstanceState);
    }

    public static Intent getIntent(Context context, Discussion discussion) {
        Intent intent = new Intent();
        intent.setClass(context, DiscussionQrcodeActivity.class);
        intent.putExtra(DATA_DISCUSSION, discussion);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        mDiscussionQrcodeFragment = new DiscussionQrcodeFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(DATA_DISCUSSION, mDiscussion);
        mDiscussionQrcodeFragment.setArguments(bundle);
        return mDiscussionQrcodeFragment;
    }

}
