package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.chat.MeetingNoticeChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;

/**
 * Created by dasunsy on 2017/11/14.
 */

public class LeftMeetingNoticeChatItemView extends LeftBasicUserChatItemView {

    private View mVRoot;

    private ImageView mIvAvatar;

    private TextView mTvName;

    private TextView mTvSubTitle;

    private MeetingNoticeItemView mViewMeetingNotice;

    private ImageView mIvSelect;

    private LinearLayout mLlTags;


    private MeetingNoticeChatMessage mMeetingNoticeChatMessage;


    public LeftMeetingNoticeChatItemView(Context context) {
        super(context);
        findViews();
        registerListener();
    }

    public LeftMeetingNoticeChatItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        findViews();
        registerListener();
    }

    private void findViews() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_left_meeting_notice_message, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mIvAvatar = view.findViewById(R.id.chat_left_text_avatar);
        mTvName = view.findViewById(R.id.chat_left_text_username);
        mTvSubTitle = view.findViewById(R.id.chat_left_text_sub_title);
        mIvSelect = view.findViewById(R.id.left_text_select);
        mViewMeetingNotice = view.findViewById(R.id.view_meeting_notice);
        mLlTags = view.findViewById(R.id.ll_tags);
    }

    @Override
    protected View getMessageRootView() {
        return mVRoot;
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
    protected ImageView getSelectView() {
        return mIvSelect;
    }

    @Override
    protected TextView getNameView() {
        return mTvName;
    }

    @Override
    protected TextView getSubTitleView() {
        return mTvSubTitle;
    }

    @Nullable
    @Override
    public View getTagLinerLayout() {
        return mLlTags;
    }

    @Override
    protected TextView getConfirmEmergencyView() {
        return null;
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
