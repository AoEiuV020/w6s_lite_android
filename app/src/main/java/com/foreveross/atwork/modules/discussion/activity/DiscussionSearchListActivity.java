package com.foreveross.atwork.modules.discussion.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.modules.discussion.fragment.DiscussionSearchListFragment;
import com.foreveross.atwork.modules.discussion.model.DiscussionSelectControlAction;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by dasunsy on 2018/1/16.
 */

public class DiscussionSearchListActivity extends SingleFragmentActivity {


    public static Intent getIntent(Context context, DiscussionSelectControlAction discussionSelectControlAction) {
        Intent intent = new Intent(context, DiscussionSearchListActivity.class);
        intent.putExtra(SelectDiscussionListActivity.DATA_DISCUSSION_SELECT_CONTROL_ACTION, discussionSelectControlAction);
        return intent;

    }

    @Override
    protected Fragment createFragment() {
        return new DiscussionSearchListFragment();
    }
}
