package com.foreveross.atwork.infrastructure.newmessage.post.notify.wallet;

import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.NotifyPostMessage;

import java.util.Map;

/**
 * Created by dasunsy on 2018/1/5.
 */

public abstract class WalletNotifyMessage extends NotifyPostMessage {

    public static String FROM = "ASSETS_HELPER";

    public static String TRANSACTION_DOMAIN_ID = "transaction_domain_id";

    public static String TRANSACTION_USER_ID = "transaction_user_id";

    public static String ENVELOP_CONVERSATION_ID = "envelop_conversation_id";

    public static String ENVELOP_CONVERSATION_TYPE = "envelop_conversation_type";

    public static String ENVELOP_CONVERSATION_NAME = "envelop_conversation_name";

    public static String RED_ENVELOPE_ID = "transaction_id";


    public String mRedEnvelopeId;

    public Operation mOperation;


    public ParticipantType mConversationType;

    public String mConversationId;

    public String mConversationName;

    public String mTransactionDomainId;

    public String mTransactionUserId;



    protected void pareInfo(Map<String, Object> jsonMap) {
        this.initPostTypeMessageValue(jsonMap);
        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);

        mRedEnvelopeId = ChatPostMessage.getString(bodyMap, RED_ENVELOPE_ID);
        mOperation = Operation.fromStringValue(ChatPostMessage.getString(bodyMap, OPERATION));
        mConversationType = ParticipantType.toParticipantType(ChatPostMessage.getString(bodyMap, ENVELOP_CONVERSATION_TYPE));
        mConversationId = ChatPostMessage.getString(bodyMap, ENVELOP_CONVERSATION_ID);
        mConversationName = ChatPostMessage.getString(bodyMap, ENVELOP_CONVERSATION_NAME);
        mTransactionDomainId = ChatPostMessage.getString(bodyMap, TRANSACTION_DOMAIN_ID);
        mTransactionUserId = ChatPostMessage.getString(bodyMap, TRANSACTION_USER_ID);
    }


    public enum Operation {
        RED_ENVELOPE,

        RED_ENVELOPE_BACK,

        UNKNOWN;

        public static Operation fromStringValue(String value) {
            if ("RED_ENVELOPE".equalsIgnoreCase(value)) {
                return RED_ENVELOPE;
            }


            if("RED_ENVELOPE_BACK".equalsIgnoreCase(value)) {
                return RED_ENVELOPE_BACK;
            }

            return UNKNOWN;
        }

    }


}
