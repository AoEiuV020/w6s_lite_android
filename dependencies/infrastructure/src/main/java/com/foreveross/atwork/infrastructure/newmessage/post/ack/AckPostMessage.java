package com.foreveross.atwork.infrastructure.newmessage.post.ack;

import android.content.Context;
import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.UserFileDownloadNotifyMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by lingen on 15/4/16.
 * Description:
 */
public class AckPostMessage extends PostTypeMessage {

    private static final String ACK_ID = "ack_id";
    private static final String ACK_IDS = "ack_ids";
    private static final String ACK_TIME = "ack_time";
    private static final String ACK_TYPE = "ack_type";
    private static final String ACK_FORWARD = "ack_forward";
    private static final String SESSION_IDENTIFIER = "session_identifier";


    private static final String BEGIN_TIME = "begin_time";
    private static final String END_TIME = "end_time";


    public List<String> ackIds = new ArrayList<>();

    public long ackTime;

    public AckType type;

    public int ackForward;

    /**
     * 单聊已读： A->B  （A读了B发的消息），已读回执：from:A   to:B   session_identifier:A

     * 群聊已读： A->B in C  （在群C中，A读了B发的消息），已读回执: from:A to: B session_identifier:C
     * */
    public String sessionIdentifier;

    /**
     * bingId, 用来标记该消息是否是来自必应消息
     * */
    public String bingFrom;


    public String conversationId;
    public ParticipantType conversationType;
    public String conversationDomain;

    public Long beginTime;
    public Long endTime;


    public static AckPostMessage.Builder newBuilder() {
        return new AckPostMessage.Builder();
    }



    public String getSessionChatId(Context context) {

        String compareChatId = sessionIdentifier;

        if(StringUtils.isEmpty(compareChatId)) {
            compareChatId = conversationId;
        }

        if(StringUtils.isEmpty(compareChatId)) {
            return getSessionChatIdFromFromOrTo(context);
        }

        String meUserId = LoginUserInfo.getInstance().getLoginUserId(context);
        if (meUserId.equals(compareChatId)) {

            return getSessionChatIdFromFromOrTo(context);

        } else {
            return compareChatId;

        }
    }

    public String getSessionChatIdFromFromOrTo(Context context) {
        String meUserId = LoginUserInfo.getInstance().getLoginUserId(context);
        if(meUserId.equals(from)) {
            return to;
        }

        return from;
    }

    /**
     * 创建删除消息的回执(阅后即焚会使用到)
     * */
    public static AckPostMessage createRemoveAckPostMessage(List<String> ids, String from, ParticipantType fromType, String fromDomain,
                                                            String to, ParticipantType toType, String toDomain, int ackForward, String sessionIdentifier) {
        AckPostMessage ackPostMessage = new AckPostMessage();
        ackPostMessage.deliveryId = UUID.randomUUID().toString();
        ackPostMessage.type = AckType.REMOVE;
        ackPostMessage.deliveryTime = TimeUtil.getCurrentTimeInMillis();
        ackPostMessage.ackTime = TimeUtil.getCurrentTimeInMillis();
        ackPostMessage.mBodyType = BodyType.Ack;
        ackPostMessage.from = from;
        ackPostMessage.mFromType = fromType;
        ackPostMessage.mFromDomain = fromDomain;
        ackPostMessage.mToType = toType;
        ackPostMessage.mToDomain = toDomain;
        ackPostMessage.to = to;
        ackPostMessage.ackForward = ackForward;
        ackPostMessage.addAckIds(ids);
        ackPostMessage.sessionIdentifier = sessionIdentifier;
        ackPostMessage.conversationId = sessionIdentifier;
        ackPostMessage.conversationDomain = toDomain;
        ackPostMessage.conversationType = toType;

        return ackPostMessage;

    }

    /**
     * 创建已读回执
     * @param ids
     * @param from
     * @param to
     * @param ackForward 单聊时为 true, 群聊时为 false, 为了避免多人群聊回执太多的情况, 群聊的已读未读通过接口拉取
     * */
    public static AckPostMessage createReadAckPostMessage(List<String> ids, String from, ParticipantType fromType, String fromDomain,
                                                          String to, ParticipantType toType, String toDomain, int ackForward, String sessionIdentifier) {
        AckPostMessage ackPostMessage = new AckPostMessage();
        ackPostMessage.deliveryId = UUID.randomUUID().toString();
        ackPostMessage.type = AckType.READ;
        ackPostMessage.deliveryTime = TimeUtil.getCurrentTimeInMillis();
        ackPostMessage.ackTime = TimeUtil.getCurrentTimeInMillis();
        ackPostMessage.mBodyType = BodyType.Ack;
        ackPostMessage.from = from;
        ackPostMessage.mFromType = fromType;
        ackPostMessage.mFromDomain = fromDomain;
        ackPostMessage.mToType = toType;
        ackPostMessage.mToDomain = toDomain;
        ackPostMessage.to = to;
        ackPostMessage.ackForward = ackForward;
        ackPostMessage.addAckIds(ids);
        ackPostMessage.sessionIdentifier = sessionIdentifier;
        return ackPostMessage;
    }


    /**
     * 根据JSON返回一个AckPostMessage对象
     *
     * @param jsonMap
     * @return
     */
    public static AckPostMessage getAckPostMessageFromJson(Map<String, Object> jsonMap) {
        AckPostMessage ackPostMessage = new AckPostMessage();
        ackPostMessage.initPostTypeMessageValue(jsonMap);
        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);
        ackPostMessage.mBodyType = BodyType.Ack;
        ackPostMessage.ackIds = (List<String>) bodyMap.get(ACK_IDS);
        if(bodyMap.containsKey(ACK_ID)){
            ackPostMessage.addAckId((String) bodyMap.get(ACK_ID));
        }
        ackPostMessage.type = AckType.fromValue((String) bodyMap.get(ACK_TYPE));
        ackPostMessage.sessionIdentifier = (String)bodyMap.get(SESSION_IDENTIFIER);

        if (bodyMap.containsKey(ACK_FORWARD)) {
            ackPostMessage.ackForward = ((Double)bodyMap.get(ACK_FORWARD)).intValue();
        }

        if(bodyMap.containsKey(BING_FROM)) {
            ackPostMessage.bingFrom = (String)bodyMap.get(BING_FROM);
        }

        if (bodyMap.containsKey(CONVERSATION_ID)) {
            ackPostMessage.conversationId = (String) bodyMap.get(CONVERSATION_ID);
        }


        if (bodyMap.containsKey(CONVERSATION_DOMAIN)) {
            ackPostMessage.conversationDomain = (String) bodyMap.get(CONVERSATION_DOMAIN);
        }

        if (bodyMap.containsKey(CONVERSATION_TYPE)) {
            ackPostMessage.conversationType = ParticipantType.toParticipantType((String) bodyMap.get(CONVERSATION_TYPE));
        }

        if(bodyMap.containsKey(BEGIN_TIME)) {
            ackPostMessage.beginTime = ((Double) bodyMap.get(BEGIN_TIME)).longValue();
        }


        if(bodyMap.containsKey(END_TIME)) {
            ackPostMessage.endTime = ((Double) bodyMap.get(END_TIME)).longValue();
        }

        if(bodyMap.containsKey(ACK_TIME)) {
            ackPostMessage.ackTime = ((Double) bodyMap.get(ACK_TIME)).longValue();
        }




        return ackPostMessage;

    }

    public void addAckId(String id) {
        if (ackIds == null) {
            ackIds = new ArrayList<>();
        }
        ackIds.add(id);
    }

    public void addAckIds(List<String> ids) {
        if (ackIds == null) {
            ackIds = new ArrayList<>();
        }
        ackIds.addAll(ids);
    }

    public boolean isFromBing() {
        return !StringUtils.isEmpty(bingFrom);
    }

    public boolean isReadAckInDurationAndAckIdsEmpty() {
        return ListUtil.isEmpty(ackIds) && isReadAckInDuration();
    }

    public boolean isReadAckInDuration() {
        return !StringUtils.isEmpty(conversationId)
                && !StringUtils.isEmpty(conversationDomain)
                && null != conversationType
                && null != beginTime
                && null != endTime;

    }

    @Override
    public Map<String, Object> getChatBody() {
        HashMap<String, Object> body = new HashMap<>();
        body.put(ACK_IDS, ackIds);
        body.put(ACK_TIME, ackTime);
        body.put(ACK_TYPE, type);
        body.put(ACK_FORWARD, ackForward);
        body.put(SESSION_IDENTIFIER, sessionIdentifier);
        if(!TextUtils.isEmpty(bingFrom)) {
            body.put(BING_FROM, bingFrom);
        }

        if (!StringUtils.isEmpty(conversationId)) {
            body.put(CONVERSATION_ID, conversationId);
        }

        if (!StringUtils.isEmpty(conversationDomain)) {
            body.put(CONVERSATION_DOMAIN, conversationDomain);
        }

        if (null != conversationType) {
            body.put(CONVERSATION_TYPE, conversationType.stringValue());
        }

        if (null != beginTime) {
            body.put(BEGIN_TIME, beginTime);
        }

        if (null != endTime) {
            body.put(END_TIME, endTime);
        }

        return body;
    }

    public enum AckType {

        /**
         * 收到回执，向IM发送，表明收到这个消息
         * ps : 旧版本里离线同步消息依赖 Recv 回执, 现已经更改机制
         */
        @Deprecated
        RECV,

        /**
         * 发送回执，IM发送过来，表明消息已被IM接收到
         */
        WRITE,

        /**
         * 已读回执，表明消息已经被阅读
         */
        READ,

        /**
         * 删除消息的回执
         * */
        REMOVE,

        /**
         * 未知类型
         * */
        UNKNOWN;

        public static AckType fromValue(String value) {
            if ("WRITE".equals(value.toUpperCase())) {
                return WRITE;
            }

            if ("RECV".equals(value.toUpperCase())) {
                return RECV;
            }

            if ("READ".equals(value.toUpperCase())) {
                return READ;
            }

            if("REMOVE".equals(value.toUpperCase())) {
                return REMOVE;
            }

            return UNKNOWN;
        }
    }


    public static final class Builder extends PostTypeMessage.Builder<AckPostMessage.Builder>{

        private List<String> mAckIds;

        private String mSessionIdentifier;

        private AckType mType;

        private int mAckForward;

        private String mBingFrom;

        private String mConversationId;
        private ParticipantType mConversationType;
        private String mConversationDomain;

        private Long mBeginTime;
        private Long mEndTime;

        private Builder() {

        }


        public Builder setAckIds(List<String> ackIds) {
            mAckIds = ackIds;
            return this;
        }

        public Builder setSessionIdentifier(String sessionIdentifier) {
            mSessionIdentifier = sessionIdentifier;
            return this;
        }

        public Builder setType(AckType type) {
            mType = type;
            return this;
        }

        public Builder setAckForward(int ackForward) {
            mAckForward = ackForward;
            return this;
        }

        public Builder setBingFrom(String bingFrom) {
            mBingFrom = bingFrom;
            return this;
        }

        public Builder setConversationId(String conversationId) {
            mConversationId = conversationId;
            return this;
        }

        public Builder setConversationType(ParticipantType conversationType) {
            mConversationType = conversationType;
            return this;
        }

        public Builder setConversationDomain(String conversationDomain) {
            mConversationDomain = conversationDomain;
            return this;
        }

        public Builder setBeginTime(Long beginTime) {
            mBeginTime = beginTime;
            return this;
        }

        public Builder setEndTime(Long endTime) {
            mEndTime = endTime;
            return this;
        }


        public AckPostMessage build() {
            AckPostMessage ackPostMessage = new AckPostMessage();
            super.assemble(ackPostMessage);

            ackPostMessage.deliveryId = UUID.randomUUID().toString();
            ackPostMessage.deliveryTime = TimeUtil.getCurrentTimeInMillis();
            ackPostMessage.ackTime = TimeUtil.getCurrentTimeInMillis();
            ackPostMessage.mBodyType = BodyType.Ack;

            if (null != mAckIds) {
                ackPostMessage.ackIds = mAckIds;
            }
            ackPostMessage.sessionIdentifier = mSessionIdentifier;
            ackPostMessage.type = mType;
            ackPostMessage.ackForward = mAckForward;
            ackPostMessage.bingFrom = mBingFrom;
            ackPostMessage.conversationId = mConversationId;
            ackPostMessage.conversationDomain = mConversationDomain;
            ackPostMessage.conversationType = mConversationType;
            ackPostMessage.beginTime = mBeginTime;
            ackPostMessage.endTime = mEndTime;

            return ackPostMessage;
        }
    }


}
