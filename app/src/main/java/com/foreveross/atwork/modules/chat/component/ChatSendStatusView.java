package com.foreveross.atwork.modules.chat.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.inter.ReSendListener;

/**
 * Created by lingen on 15/4/19.
 * Description:
 * 消息发送状态View
 */
public class ChatSendStatusView extends LinearLayout {

    private ProgressBar mProgressBar;

    private ImageView mSendFailView;

    private ChatPostMessage mChatPostMessage;

    private ReSendListener mReSendListener;

    public ChatSendStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        findView();
        registerListener();
    }

    private void registerListener() {
        mSendFailView.setOnClickListener(v -> mReSendListener.reSendMessage(mChatPostMessage));
    }

    private void findView() {
        mProgressBar = findViewById(R.id.send_message_progress);
        mSendFailView = findViewById(R.id.send_message_fail);
        mProgressBar.setVisibility(GONE);

    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.component_send, this);
    }

    private void refresh() {
        if (ChatStatus.Sending.equals(mChatPostMessage.chatStatus)) {
            sending();

        } else if (ChatStatus.Sended.equals(mChatPostMessage.chatStatus)) {
            sendOk();

        } else if (ChatStatus.Not_Send.equals(mChatPostMessage.chatStatus)) {
            sendFail();
        }
    }

    /**
     * 发送成功
     */
    public void sendOk() {
        mProgressBar.setVisibility(GONE);
        mSendFailView.setVisibility(GONE);
    }

    /**
     * 正在发送中
     */
    private void sending() {
        mProgressBar.setVisibility(VISIBLE);
        mSendFailView.setVisibility(GONE);
    }


    /**
     * 发送失败
     */
    private void sendFail() {
        mProgressBar.setVisibility(GONE);
        mSendFailView.setVisibility(VISIBLE);
    }

    public void setChatPostMessage(ChatPostMessage chatPostMessage) {
        this.mChatPostMessage = chatPostMessage;
        refresh();
    }

    public void setReSendListener(ReSendListener reSendListener) {
        this.mReSendListener = reSendListener;
    }
}
