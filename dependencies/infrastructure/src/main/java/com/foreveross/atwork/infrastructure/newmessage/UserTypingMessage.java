package com.foreveross.atwork.infrastructure.newmessage;

import android.content.Context;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class UserTypingMessage extends HasBodyMessage {

    public static final String FROM_ID = "from_id";
    public static final String TO_ID = "to_id";

    public String mFrom;

    public String mFromDomainId;

    public ParticipantType mFromType;

    public String mTo;

    public String mToDomainId;

    public ParticipantType mToType;

    public String mConversationId;

    public String mConversationDomainId;

    public ParticipantType mConversationType;

    public String getSessionChatId() {

        if(!StringUtils.isEmpty(mConversationId)) {
            if(!User.isYou(BaseApplicationLike.baseContext, mConversationId)) {
                return mConversationId;
            }
        }

        if(User.isYou(BaseApplicationLike.baseContext, mFrom)) {
            return mTo;
        }

        return mFrom;

    }


    @Override
    public int getMsgType() {
        return Message.USER_TYPING;
    }

    @Override
    public boolean encryptHandle() {
        return false;
    }

    @Override
    public Map<String, Object> getMessageBody() {

        Map<String, Object> messageBody = new HashMap<>();
        messageBody.put(FROM_ID, mFrom);
        messageBody.put(PostTypeMessage.FROM_DOMAIN, mFromDomainId);
        messageBody.put(PostTypeMessage.FROM_TYPE, mFromType.stringValue());
        messageBody.put(TO_ID, mTo);
        messageBody.put(PostTypeMessage.TO_DOMAIN, mToDomainId);
        messageBody.put(PostTypeMessage.TO_TYPE, mToType.stringValue());
        messageBody.put(PostTypeMessage.CONVERSATION_ID, mConversationId);
        messageBody.put(PostTypeMessage.CONVERSATION_DOMAIN, mConversationDomainId);
        messageBody.put(PostTypeMessage.CONVERSATION_TYPE, mConversationType.stringValue());

        return messageBody;
    }


    public static final class UserTypingMessageBuilder {
        private String mFrom;
        private String mFromDomainId;
        private ParticipantType mFromType;
        private String mTo;
        private String mToDomainId;
        private ParticipantType mToType;
        private String mConversationId;
        private String mConversationDomainId;
        private ParticipantType mConversationType;

        private UserTypingMessageBuilder() {
        }

        public static UserTypingMessageBuilder anUserTypingMessage() {
            return new UserTypingMessageBuilder();
        }

        public UserTypingMessageBuilder withBasicFrom() {
            Context context = BaseApplicationLike.baseContext;
            this.mFrom = LoginUserInfo.getInstance().getLoginUserId(context);
            this.mFromDomainId = LoginUserInfo.getInstance().getLoginUserDomainId(context);
            this.mFromType = ParticipantType.User;
            return this;
        }

        public UserTypingMessageBuilder withFrom(String from) {
            this.mFrom = from;
            return this;
        }

        public UserTypingMessageBuilder withFromDomainId(String fromDomainId) {
            this.mFromDomainId = fromDomainId;
            return this;
        }

        public UserTypingMessageBuilder withFromType(ParticipantType fromType) {
            this.mFromType = fromType;
            return this;
        }

        public UserTypingMessageBuilder withTo(String to) {
            this.mTo = to;
            return this;
        }

        public UserTypingMessageBuilder withToDomainId(String toDomainId) {
            this.mToDomainId = toDomainId;
            return this;
        }

        public UserTypingMessageBuilder withToType(ParticipantType toType) {
            this.mToType = toType;
            return this;
        }

        public UserTypingMessageBuilder withConversationId(String conversationId) {
            this.mConversationId = conversationId;
            return this;
        }

        public UserTypingMessageBuilder withConversationDomainId(String conversationDomainId) {
            this.mConversationDomainId = conversationDomainId;
            return this;
        }

        public UserTypingMessageBuilder withConversationType(ParticipantType conversationType) {
            this.mConversationType = conversationType;
            return this;
        }

        public UserTypingMessage build() {
            UserTypingMessage userTypingMessage = new UserTypingMessage();
            userTypingMessage.mConversationType = this.mConversationType;
            userTypingMessage.mFromType = this.mFromType;
            userTypingMessage.mToDomainId = this.mToDomainId;
            userTypingMessage.mConversationId = this.mConversationId;
            userTypingMessage.mConversationDomainId = this.mConversationDomainId;
            userTypingMessage.mFromDomainId = this.mFromDomainId;
            userTypingMessage.mFrom = this.mFrom;
            userTypingMessage.mToType = this.mToType;
            userTypingMessage.mTo = this.mTo;
            return userTypingMessage;
        }
    }
}
