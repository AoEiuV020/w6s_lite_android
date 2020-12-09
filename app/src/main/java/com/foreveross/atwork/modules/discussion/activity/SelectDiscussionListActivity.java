package com.foreveross.atwork.modules.discussion.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.modules.discussion.fragment.SelectDiscussionListFragment;
import com.foreveross.atwork.modules.discussion.model.DiscussionSelectControlAction;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by dasunsy on 2018/1/16.
 */

public class SelectDiscussionListActivity extends SingleFragmentActivity {

    public static final String DATA_DISCUSSION_SELECT_CONTROL_ACTION = "DATA_DISCUSSION_SELECT_CONTROL_ACTION";
    public static final String DATA_DISCUSSION_LIST = "DATA_DISCUSSION_LIST";
    public static final String ACTION_SELECT = "ACTION_SELECT";
    public static final String DATA_HANDLE = "DATA_HANDLE";

    public static Intent getIntent(Context context, DiscussionSelectControlAction action) {
        Intent intent = new Intent(context, SelectDiscussionListActivity.class);
        intent.putExtra(DATA_DISCUSSION_SELECT_CONTROL_ACTION, action);
        return intent;
    }

    @Override
    protected Fragment createFragment() {

        return new SelectDiscussionListFragment();
    }

    public static void handleSelect(Discussion discussion) {
        Intent intent = new Intent(ACTION_SELECT);
        intent.putExtra(DATA_HANDLE, discussion);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intent);
    }
}
