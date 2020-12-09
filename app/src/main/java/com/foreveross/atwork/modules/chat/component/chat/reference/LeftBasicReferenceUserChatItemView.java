package com.foreveross.atwork.modules.chat.component.chat.reference;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.reference.ReferenceMessage;
import com.foreveross.atwork.manager.model.SetReadableNameParams;
import com.foreveross.atwork.modules.chat.component.chat.LeftBasicUserChatItemView;
import com.foreveross.atwork.utils.ContactShowNameHelper;

public abstract class LeftBasicReferenceUserChatItemView extends LeftBasicUserChatItemView {

    public LeftBasicReferenceUserChatItemView(Context context) {
        super(context);
    }

    public LeftBasicReferenceUserChatItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    abstract TextView getAuthorNameView();

    abstract TextView getReplyView();

    @Override
    public void refreshItemView(ChatPostMessage message) {
        super.refreshItemView(message);

        if(message instanceof ReferenceMessage) {
            ReferenceMessage referenceMessage = (ReferenceMessage) message;

            setAuthorNameView(referenceMessage);

            getReplyView().setText(referenceMessage.mReply);

        }
    }

    private void setAuthorNameView(ReferenceMessage message) {
        SetReadableNameParams setReadableNameParams = SetReadableNameParams.newSetReadableNameParams()
                .setDiscussionId(message.mReferencingMessage.to)
                .setTextView(getAuthorNameView())
                .setUserId(message.mReferencingMessage.from)
                .setDomainId(message.mReferencingMessage.mFromDomain);

        ContactShowNameHelper.setReadableNames(setReadableNameParams);
    }

    @Override
    protected Drawable getBlinkDrawable() {
        return ContextCompat.getDrawable(getContext(), R.drawable.shape_chat_message_blink_with_corners);
    }

    @Override
    protected void handlePresenter(ChatPostMessage message) {
        if(message instanceof ReferenceMessage) {

            if(null != mChatViewRefreshUIPresenter) {
                ReferenceMessage referenceMessage = (ReferenceMessage) message;
                mChatViewRefreshUIPresenter.refreshItemView(referenceMessage.mReferencingMessage);
            }

        }
    }
}
