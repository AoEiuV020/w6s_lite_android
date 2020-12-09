package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.chat.SystemChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleChatMessage;
import com.foreveross.atwork.modules.chat.component.ChatDetailItemDataRefresh;
import com.foreveross.atwork.modules.chat.util.UndoMessageHelper;

import java.util.List;

/**
 * Created by lingen on 15/3/25.
 * Description:
 */
public class SystemChatItemView extends RelativeLayout implements ChatDetailItemDataRefresh {

    private TextView mMessageView;

    private SystemChatMessage mSystemChatMessage;


    public SystemChatItemView(Context context) {
        super(context);
        findView();
    }

    private void findView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_system_message, this);
        mMessageView = view.findViewById(R.id.chat_system_message);
    }

    @Override
    public void refreshItemView(ChatPostMessage message) {

        if (message instanceof ArticleChatMessage) {
            setVisibility(GONE);
            return;
        }


        if (message instanceof SystemChatMessage) {
            mSystemChatMessage = (SystemChatMessage) message;
            mMessageView.setText(mSystemChatMessage.content);

        } else {
            if(message.isUndo()) {
                setUndoContent(message);
            }
        }


    }

    @Override
    public void refreshMessagesContext(List<ChatPostMessage> messages) {

    }


    private void setUndoContent(ChatPostMessage chatPostMessage) {
        String content = UndoMessageHelper.getUndoContent(getContext(), chatPostMessage);

        mMessageView.setText(content);
    }

    @Override
    public String getMsgId() {
        if (this.mSystemChatMessage != null) {
            return mSystemChatMessage.deliveryId;
        }
        return null;
    }
}
