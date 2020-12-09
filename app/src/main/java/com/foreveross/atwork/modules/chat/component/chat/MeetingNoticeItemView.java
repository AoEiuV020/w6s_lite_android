package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.chat.MeetingNoticeChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.manager.model.SetReadableNameParams;
import com.foreveross.atwork.modules.chat.component.ChatDetailItemDataRefresh;
import com.foreveross.atwork.modules.meeting.util.MeetingHelper;
import com.foreveross.atwork.utils.ContactShowNameHelper;

import java.util.List;

/**
 * Created by dasunsy on 2017/11/14.
 */

public class MeetingNoticeItemView extends LinearLayout implements ChatDetailItemDataRefresh {

    public LinearLayout mLlRoot;
    private TextView mTvAction;
    private TextView mTvTitle;
    private TextView mTvHost;
    private TextView mTvParticipant;
    private TextView mTvTime;

    private MeetingNoticeChatMessage mMeetingNotifyMessage;

    public MeetingNoticeItemView(Context context) {
        super(context);
        initViews();
        registerListener();
    }

    public MeetingNoticeItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews();
        registerListener();
    }

    private void initViews() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.component_meeting_notice, this);
        mLlRoot = view.findViewById(R.id.ll_root);
        mTvAction = view.findViewById(R.id.tv_action);
        mTvTitle = view.findViewById(R.id.tv_title);
        mTvHost = view.findViewById(R.id.tv_host);
        mTvParticipant = view.findViewById(R.id.tv_participant);
        mTvTime = view.findViewById(R.id.tv_time);
    }


    public void refreshUI(MeetingNoticeChatMessage meetingNotifyMessage) {
        mMeetingNotifyMessage = meetingNotifyMessage;

        setActionUIShow(meetingNotifyMessage);

        mTvTitle.setText(meetingNotifyMessage.mTitle);
        mTvParticipant.setText(meetingNotifyMessage.mMeetingParticipantsShow);
        mTvTime.setText(TimeUtil.getStringForMillis(meetingNotifyMessage.mMeetingTime, TimeUtil.MULTIPART_YYYY_MM_DD));
        if (null != meetingNotifyMessage.mHost) {
            SetReadableNameParams setReadableNameParams = SetReadableNameParams.newSetReadableNameParams()
                    .setUserId(meetingNotifyMessage.mHost.mUserId)
                    .setDomainId(meetingNotifyMessage.mHost.mDomainId)
                    .setFailName(meetingNotifyMessage.mHost.mShowName)
                    .setLoadingHolder(true)
                    .setTextView(mTvHost);

            ContactShowNameHelper.setReadableNames(setReadableNameParams);
        }
    }

    private void setActionUIShow(MeetingNoticeChatMessage meetingNotifyMessage) {
        if(!StringUtils.isEmpty(meetingNotifyMessage.mOperationTitle)) {
            mTvAction.setText(meetingNotifyMessage.mOperationTitle);
            return;
        }

        setActionsUIShowByOperation(meetingNotifyMessage);
    }

    private void setActionsUIShowByOperation(MeetingNoticeChatMessage meetingNotifyMessage) {
        switch (meetingNotifyMessage.mOperation) {
            case CREATED:
                mTvAction.setText(R.string.meeting_action_invite);
                break;

            case UPDATE:
                mTvAction.setText(R.string.meeting_action_update);
                break;

            case CANCEL:
                mTvAction.setText(R.string.meeting_action_cancel);
                break;
        }
    }

    @Override
    public void refreshItemView(ChatPostMessage message) {
        refreshUI((MeetingNoticeChatMessage) message);
    }

    @Override
    public void refreshMessagesContext(List<ChatPostMessage> messages) {

    }

    public void setBg() {

    }

    @Override
    public String getMsgId() {
        if (mMeetingNotifyMessage != null) {
            return mMeetingNotifyMessage.deliveryId;
        }
        return null;

    }



    private void registerListener() {
        setOnClickListener((v)-> {
            MeetingHelper.handleClick(getContext(), mMeetingNotifyMessage);
        });

    }
}
