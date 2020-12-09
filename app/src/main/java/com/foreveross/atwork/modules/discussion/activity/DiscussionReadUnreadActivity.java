package com.foreveross.atwork.modules.discussion.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.infrastructure.newmessage.ReceiptMessage;
import com.foreveross.atwork.modules.discussion.fragment.DiscussionReadUnreadFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lingen on 15/5/15.
 * Description:
 */
public class DiscussionReadUnreadActivity extends SingleFragmentActivity {

    public final static String READ_OR_UNREAD = "read_or_unread";

    public final static String DISCUSSION_ID = "discussion_id";

    public final static String MESSAGE_ID = "message_id";

    public final static String DISCUSSION_CONTACTS = "group_contacts";

    public final static String DISCUSSION_READ_RECEIPT = "DISCUSSION_READ_RECEIPT";

    public final static String DISCUSSION_ORG_CODE = "DISCUSSION_ORG_CODE";

    public static Intent getIntent(Context context, String deliveryId, DiscussionReadUnreadFragment.ReadOrUnread groupReadOrUnread, String discussionId, String discussionOrgCode) {
        Intent intent = new Intent();
        intent.putExtra(READ_OR_UNREAD, groupReadOrUnread);
        intent.putExtra(MESSAGE_ID, deliveryId);
        intent.putExtra(DISCUSSION_ID, discussionId);
        intent.putExtra(DISCUSSION_ORG_CODE, discussionOrgCode);
        intent.setClass(context, DiscussionReadUnreadActivity.class);
        return intent;
    }

    public static Intent getUnReadIntent(Context context, DiscussionReadUnreadFragment.ReadOrUnread groupReadOrUnread, List<String> contacts) {
        Intent intent = new Intent();
        intent.putExtra(READ_OR_UNREAD, groupReadOrUnread);
        intent.putExtra(DISCUSSION_CONTACTS, (Serializable) contacts);
        intent.setClass(context, DiscussionReadUnreadActivity.class);
        return intent;
    }


    public static Intent getReadIntent(Context context, DiscussionReadUnreadFragment.ReadOrUnread groupReadOrUnread, List<ReceiptMessage> contacts) {
        Intent intent = new Intent();
        intent.putExtra(READ_OR_UNREAD, groupReadOrUnread);
        intent.putExtra(DISCUSSION_READ_RECEIPT, (Serializable) contacts);
        intent.setClass(context, DiscussionReadUnreadActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected Fragment createFragment() {
        DiscussionReadUnreadFragment discussionReadUnreadFragment = new DiscussionReadUnreadFragment();
        return discussionReadUnreadFragment;
    }

}
