package com.foreveross.atwork.modules.discussion.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.modules.discussion.fragment.DiscussionMemberSelectFragment;
import com.foreveross.atwork.modules.discussion.model.DiscussionMemberSelectControlAction;
import com.foreveross.atwork.support.SingleFragmentActivity;


public class DiscussionMemberSelectActivity extends SingleFragmentActivity {

    public static final String DATA_DISCUSSION_MEMBER_SELECT_CONTROL_ACTION = "DATA_DISCUSSION_MEMBER_SELECT_CONTROL_ACTION";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    protected Fragment createFragment() {
        DiscussionMemberSelectFragment discussionMemberSelectFragment = new DiscussionMemberSelectFragment();
        return discussionMemberSelectFragment;
    }


    public static Intent getIntent(final Context context, DiscussionMemberSelectControlAction discussionMemberSelectControlAction) {
        Intent intent = new Intent();
        intent.putExtra(DATA_DISCUSSION_MEMBER_SELECT_CONTROL_ACTION, discussionMemberSelectControlAction);
        intent.setClass(context, DiscussionMemberSelectActivity.class);

        return intent;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //界面退出动画
        this.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }


    public static final class Mode {

        /**
         * AT人
         */
        public static final int AT = 0;

        /**
         * 语音会议选人
         */
        public static final int VOIP = 1;

        /**
         * 单选人
         */
        public static final int SINGLE = 2;

        /**
         * 通用的群组选人
         */
        public static final int SELECT = 3;

    }
}
