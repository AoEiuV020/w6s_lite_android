package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.chat.MeetingNoticeChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.modules.chat.component.ChatSendStatusView;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;

/**
 * Created by dasunsy on 2017/11/14.
 */

public class RightMeetingNoticeChatItemView extends RightBasicUserChatItemView {

    private View mVRoot;

    private ImageView mIvAvatar;

    private MeetingNoticeItemView mViewMeetingNotice;

    private ImageView mIvSelect;

    private MeetingNoticeChatMessage mMeetingNoticeChatMessage;


    @Override
    protected ChatSendStatusView getChatSendStatusView() {
        return null;
    }

    public RightMeetingNoticeChatItemView(Context context) {
        super(context);
        findViews();
        registerListener();
    }

    public RightMeetingNoticeChatItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        findViews();
        registerListener();
    }

    private void findViews() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_right_meeting_notice_message, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mIvAvatar = view.findViewById(R.id.chat_right_text_avatar);
        mIvSelect = view.findViewById(R.id.right_text_select);
        mViewMeetingNotice = view.findViewById(R.id.view_meeting_notice);

    }

    @Override
    protected View getMessageRootView() {
        return mVRoot;
    }

    @Override
    protected void registerListener() {
        super.registerListener();

        mViewMeetingNotice.setOnClickListener(v -> handleClick());

        mViewMeetingNotice.setOnLongClickListener(v -> handleLongClick());
    }

    private boolean handleLongClick() {
        AnchorInfo anchorInfo = getAnchorInfo();
        if (!mSelectMode) {
            mChatItemLongClickListener.meetingLongClick(mMeetingNoticeChatMessage, anchorInfo);
            return true;
        }
        return false;
    }

    private void handleClick() {
        if (mSelectMode) {
            mMeetingNoticeChatMessage.select = !mMeetingNoticeChatMessage.select;
            select(mMeetingNoticeChatMessage.select);
        } else {
            if (mChatItemClickListener != null) {
                mChatItemClickListener.meetingClick(mMeetingNoticeChatMessage);
            }
        }
    }

    @Override
    public void refreshItemView(ChatPostMessage message) {
        super.refreshItemView(message);
        mMeetingNoticeChatMessage = (MeetingNoticeChatMessage) message;
        mViewMeetingNotice.refreshUI(mMeetingNoticeChatMessage);
    }

    @NonNull
    @Override
    protected View getContentRootView() {
        return mViewMeetingNotice.mLlRoot;
    }

    @Override
    protected ImageView getSelectView() {
        return mIvSelect;
    }

    @Override
    protected ImageView getAvatarView() {
        return mIvAvatar;
    }

    @Override
    protected MessageSourceView getMessageSourceView() {
        return null;
    }

    @Override
    protected ChatPostMessage getMessage() {
        return mMeetingNoticeChatMessage;
    }

    @Override
    protected Drawable getBlinkDrawable() {
        return ContextCompat.getDrawable(getContext(), R.drawable.shape_chat_message_blink_with_corners);
    }


}
