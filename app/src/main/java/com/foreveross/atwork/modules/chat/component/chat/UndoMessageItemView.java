package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.modules.chat.component.ChatDetailItemDataRefresh;
import com.foreveross.atwork.modules.chat.inter.ChatItemClickListener;
import com.foreveross.atwork.modules.chat.inter.HasChatItemClickListener;
import com.foreveross.atwork.modules.chat.util.UndoMessageHelper;

import java.util.List;


public class UndoMessageItemView extends RelativeLayout implements ChatDetailItemDataRefresh, HasChatItemClickListener {

    private TextView mMessageView;
    private TextView mTvReEdit;

    private ChatPostMessage mChatMessage;

    private ChatItemClickListener mChatItemClickListener;


    public UndoMessageItemView(Context context) {
        super(context);
        findView();
        registerListener();
    }

    private void findView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_undo_message, this);
        mMessageView = view.findViewById(R.id.chat_system_message);
        mTvReEdit = view.findViewById(R.id.tv_edit);
    }

    @Override
    public void refreshItemView(ChatPostMessage message) {

        if(message.isUndo()) {
            setUndoContent(message);

            ViewUtil.setVisible(mTvReEdit, isUndoCanEdit(message));

            setVisibility(VISIBLE);

            return;
        }

        setVisibility(GONE);

    }

    @Override
    public void refreshMessagesContext(List<ChatPostMessage> messages) {

    }

    private boolean isUndoCanEdit(ChatPostMessage message) {
        mChatMessage = message;

        if(message instanceof TextChatMessage) {

            TextChatMessage textChatMessage = (TextChatMessage) message;
            if(User.isYou(AtworkApplicationLike.baseContext, textChatMessage.from)
                    && TimeUtil.getCurrentTimeInMillis() - textChatMessage.undoSuccessTime < 5 * 60 * 1000L) {
                return true;
            }

        }

        return false;
    }


    private void setUndoContent(ChatPostMessage chatPostMessage) {
        String content = UndoMessageHelper.getUndoContent(getContext(), chatPostMessage);

        mMessageView.setText(content);
    }

    private void registerListener() {
        mTvReEdit.setOnClickListener(v -> {
            if(null != mChatItemClickListener && null != mChatMessage) {
                mChatItemClickListener.reEditUndoClick(mChatMessage);
            }
        });
    }

    @Override
    public void setChatItemClickListener(ChatItemClickListener chatItemClickListener) {
        mChatItemClickListener = chatItemClickListener;
    }

    @Override
    public String getMsgId() {

        return null;
    }
}
