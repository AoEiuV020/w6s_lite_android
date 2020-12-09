package com.foreveross.atwork.modules.discussion.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.modules.discussion.fragment.DiscussionModifyFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by lingen on 15/4/30.
 * Description:
 * 群修改ACTIVITY
 */
public class DiscussionModifyActivity extends SingleFragmentActivity {

    private static final String MODIFY_TYPE = "GROUP_MODIFY_TYPE";

    private static final String DISCUSSION_INFO = "DISCUSSION_INFO";


    private Discussion mDiscussion;

    private DiscussionModifyFragment.DiscussionModifyType discussionModifyType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDiscussion = getIntent().getParcelableExtra(DISCUSSION_INFO);
        discussionModifyType = (DiscussionModifyFragment.DiscussionModifyType) getIntent().getSerializableExtra(MODIFY_TYPE);
        super.onCreate(savedInstanceState);

    }

    @Override
    protected Fragment createFragment() {
        DiscussionModifyFragment discussionModifyFragment = new DiscussionModifyFragment();
        discussionModifyFragment.initBundle(mDiscussion, discussionModifyType);
        return discussionModifyFragment;
    }

    public static Intent getIntent(Context context, Discussion discussion, DiscussionModifyFragment.DiscussionModifyType discussionModifyType) {
        Intent intent = new Intent();
        intent.putExtra(DISCUSSION_INFO, discussion);
        intent.putExtra(MODIFY_TYPE, discussionModifyType);
        intent.setClass(context, DiscussionModifyActivity.class);
        return intent;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //界面退出动画
        this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

}
