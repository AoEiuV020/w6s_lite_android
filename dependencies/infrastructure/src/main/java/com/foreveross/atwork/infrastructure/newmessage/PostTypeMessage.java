package com.foreveross.atwork.infrastructure.newmessage;

import android.content.Context;
import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.model.user.LoginUserBasic;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ChatSendType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.EmergencyInfo;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.OrgPositionInfo;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lingen on 15/4/16.
 * Description:
 * 消息类主体
 */
public abstract class PostTypeMessage extends HasBodyMessage {

    public static final String FROM_TYPE = "from_type";
    public static final String FROM_DOMAIN = "from_domain";
    public static final String FROM = "from";
    public static final String DELIVER_ID = "delivery_id";
    public static final String TO_TYPE = "to_type";
    public static final String TO_DOMAIN = "to_domain";
    public static final String TO = "to";
    public static final String DELIVER_TIME = "delivery_time";
    public static final String BODY_TYPE = "body_type";
    public static final String BODY = "body";
    public static final String CONTENT = "content";
    public static final String CONTENTS = "contents";
    public static final String MEDIA_ID = "media_id";
    public static final String SOURCE = "source";
    public static final String DELETION_POLICY = "deletion_policy";
    public static final String DELETION_ON = "deletion_on";
    public static final String BING_FROM = "bing_from";
    public static final String RETRIES = "retries";
    public static final String CONVERSATION_ID =  "conversation_id";
    public static final String CONVERSATION_TYPE = "conversation_type";
    public static final String CONVERSATION_DOMAIN = "conversation_domain";
    public static final String DEVICE_ID = "device_id";
    public static final String RETAINED = "retained";

    /**
     * 说明：消息提示样式
     * 是否必须：否
     * 取值范围
     * •	dot                   圆点
     * •	digit                  数字
     * •	digitAcc               数字累加
     * •	attachmentUrl            附加图标
     */
    private static final String NOTIFY_TYPE = "@notify_type";

    private static final String ORG_ID = "org_id";

    /**
     * 说明：附加图标，如果type为attachment，则从此值获取附加图标
     * 是否必须：否
     */
    private static final String ATTACHMENT_URL = "@attachment_url";

    /**
     * 说明：跳转URL,如果有，则在跳转到URL
     * 是否必须：否
     */
    public static final String TARGET_URL = "@target_url";

    public static final String TARGET_ID = "@target_id";

    /**
     * 说明：任务栏图标
     * 是否必须：否
     * 取值范围：
     * 客户端内置的图标。
     */
    private static final String TASK_BAR_ICON = "@task_bar_icon";

    /**
     * 说明：消息提示音，
     * 是否必须：否，即默认无提示音
     * 取值范围：
     * 客户端内置的声音。
     */
    private static final String SOUND = "@sound";

    /**
     * 说明：消息震动提示
     * 是否必须：否，即默认不震动
     * 取值范围：
     * •	0                  不震动
     * •	1                  短震动
     * •	2                  长震动
     */
    private static final String VIBRATION = "@vibration";

    /**
     * 服务号推送的消息新增
     */
    private static final String TARGET_SCOPE = "@target_scope";

    public String from = StringUtils.EMPTY;

    public ParticipantType mFromType;

    public String mFromDomain = AtworkConfig.DOMAIN_ID;

    public String deliveryId;

    public ParticipantType mToType;

    public String mToDomain = StringUtils.EMPTY;

    public BodyType mBodyType;

    public String to = StringUtils.EMPTY;

    @Expose
    @SerializedName("source")
    public String source = "";


    public long deliveryTime;

    public ChatSendType chatSendType;

    public ChatStatus chatStatus;

    public String orgId;

    //消息提醒机制属性，决定 显示数字，红点还是图标
    private SpecialNotice specialNotice;

    public SpecialAction specialAction;

    /**
     * 发送者的名字
     */
    @Expose
    public String mMyName = StringUtils.EMPTY;

    /**
     * 发送者的头像
     */
    @Expose
    public String mMyAvatar = StringUtils.EMPTY;


    /**
     * 发送者的签名
     */
    @Expose
    public String mMyStatus = StringUtils.EMPTY;


    /**
     * 接收者的签名
     */
    @Expose
    public String mDisplayStatus = StringUtils.EMPTY;

    /**
     * 发送者在群聊里显示的名字(雇员名字)
     */
    @Expose
    public String mMyNameInDiscussion = StringUtils.EMPTY;

    /**
     * 发送者在群聊里显示的头像(雇员头像)
     */
    @Expose
    public String mMyAvatarInDiscussion = StringUtils.EMPTY;

    /**
     * 接收者的名字(user, app, discussion)
     */
    @Expose
    public String mDisplayName;

    /**
     * 接收者的头像 (user, app, discussion)
     */
    @Expose
    public String mDisplayAvatar;


    /**
     * 删除策略
     */
    @Expose
    public String mDeletionPolicy;

    /**
     * 消息过期时间
     */
    @Expose
    public long mDeletionOn = -1;

    /**
     * 必应消息创建者
     * */
    @Expose
    public String mBingCreatorId;

    @Expose
    public int mRetries = 0;


    public int mRetriesAuto = 0;

    private long mRetriesTime = -1;

    /**
     * 服务号推送
     */
    @Expose
    public double mTargetScope = 0;

    /**
     * (后台)是否持久化该消息
     * */
    public boolean retained = true;

    public boolean isRetriesExpiredTimeLegal(long currentTimeInMillis) {
        return currentTimeInMillis - getRetriesTime() < getRetriesExpiredTime();
    }

    public long getRetriesExpiredTime() {
        int expiredTime = 2000 + mRetriesAuto * 2000;
        if(expiredTime > 8000) {
            expiredTime = 8000; //max
        }
        return expiredTime;
    }

    public long getRetriesTime() {
        if(-1 != mRetriesTime) {
            return mRetriesTime;
        }

        return deliveryTime;
    }

    public void setRetriesTime(long retriesTime) {
        this.mRetriesTime = retriesTime;
    }

    @Override
    public int getMsgType() {
        return Message.POST_TYPE;
    }

    /**
     * 用于发消息时, 解析成 body
     */
    public abstract Map<String, Object> getChatBody();

    public void setChatStatus(ChatStatus chatStatus) {
        if (ChatStatus.UnDo != this.chatStatus && ChatStatus.Hide != this.chatStatus) {
            this.chatStatus = chatStatus;
        }
    }

    public void initPostTypeMessageValue(Map<String, Object> jsonMap) {
        from = (String) jsonMap.get(FROM);
        mFromDomain = (String) jsonMap.get(FROM_DOMAIN);
        mFromType = ParticipantType.toParticipantType((String) jsonMap.get(FROM_TYPE));

        if (jsonMap.containsKey(DELIVER_ID)) {
            deliveryId = (String) jsonMap.get(DELIVER_ID);
        }

        to = (String) jsonMap.get(TO);
        mToDomain = (String) jsonMap.get(TO_DOMAIN);
        mToType = ParticipantType.toParticipantType((String) jsonMap.get(TO_TYPE));

        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);

        mBodyType = BodyType.toBodyType((String) jsonMap.get(BODY_TYPE));
        mBodyType = BodyType.makeParseBodyCompatible(bodyMap, mBodyType);

        if (jsonMap.containsKey(DELIVER_TIME)) {
            deliveryTime = ((Double) jsonMap.get(DELIVER_TIME)).longValue();
        }

        if (jsonMap.containsKey(DELETION_POLICY)) {
            mDeletionPolicy = (String) jsonMap.get(DELETION_POLICY);
        }

        if (jsonMap.containsKey(DELETION_ON)) {
            mDeletionOn = ((Double) jsonMap.get(DELETION_ON)).longValue();
        }

        if(jsonMap.containsKey(RETAINED)) {
            retained = (boolean) jsonMap.get(RETAINED);
        }

        initSpecialNoticeMessageValue(bodyMap);
    }

    protected void initChatTypeMessageValue(Map<String, Object> bodyMap) {
        mDisplayAvatar = (String) bodyMap.get(ChatPostMessage.DISPLAY_AVATAR);
        mDisplayName = (String) bodyMap.get(ChatPostMessage.DISPLAY_NAME);
        mMyAvatar = (String) bodyMap.get(ChatPostMessage.MY_AVATAR);
        mMyName = (String) bodyMap.get(ChatPostMessage.MY_NAME);

        if (bodyMap.containsKey(ChatPostMessage.MY_NAME_IN_DISCUSSION)) {
            mMyNameInDiscussion = (String) bodyMap.get(ChatPostMessage.MY_NAME_IN_DISCUSSION);

        }

        if (bodyMap.containsKey(TARGET_SCOPE)) {
            mTargetScope = (double) bodyMap.get(TARGET_SCOPE);
        }

        if (bodyMap.containsKey(ChatPostMessage.MY_AVATAR_IN_DISCUSSION)) {
            mMyAvatarInDiscussion = (String) bodyMap.get(ChatPostMessage.MY_AVATAR_IN_DISCUSSION);
        }

        if(bodyMap.containsKey(ChatPostMessage.MY_STATUS)) {
            mMyStatus = (String) bodyMap.get(ChatPostMessage.MY_STATUS);
        }

        if (this instanceof ChatPostMessage) {
            if (bodyMap.containsKey(ChatPostMessage.UNDO)) {
                boolean undo = (Boolean) bodyMap.get(ChatPostMessage.UNDO);
                if (undo) {
                    setChatStatus(ChatStatus.UnDo);
                }

            }

            if (bodyMap.containsKey(ChatPostMessage.REMOVED)) {
                boolean hide = (Boolean) bodyMap.get(ChatPostMessage.REMOVED);
                if (hide) {
                    setChatStatus(ChatStatus.Hide);
                }

            }

            ChatPostMessage chatPostMessage = (ChatPostMessage) this;
            chatPostMessage.mEmergencyInfo = EmergencyInfo.parse(bodyMap);
            chatPostMessage.orgPositionInfo = OrgPositionInfo.parse(bodyMap);

        }
    }

    protected void setBasicChatBody(Map<String, Object> chatBody) {
        chatBody.put(ChatPostMessage.MY_NAME, mMyName);
        chatBody.put(ChatPostMessage.MY_AVATAR, mMyAvatar);
        chatBody.put(ChatPostMessage.DISPLAY_AVATAR, mDisplayAvatar);
        chatBody.put(ChatPostMessage.DISPLAY_NAME, mDisplayName);
        if (!StringUtils.isEmpty(mMyStatus)) {
            chatBody.put(ChatPostMessage.MY_STATUS, mMyStatus);
        }

        if (!StringUtils.isEmpty(mDisplayStatus)) {
            chatBody.put(ChatPostMessage.DISPLAY_STATUS, mDisplayStatus);
        }

        chatBody.put(ChatPostMessage.TARGET_SCOPE, mTargetScope);
        if (!StringUtils.isEmpty(mMyNameInDiscussion)) {
            chatBody.put(ChatPostMessage.MY_NAME_IN_DISCUSSION, mMyNameInDiscussion);
        }

        if (!StringUtils.isEmpty(mMyAvatarInDiscussion)) {
            chatBody.put(ChatPostMessage.MY_AVATAR_IN_DISCUSSION, mMyAvatarInDiscussion);
        }

        if(!TextUtils.isEmpty(mBingCreatorId)) {
            chatBody.put(BING_FROM, mBingCreatorId);
        }
    }


    /**
     * 发送消息的格式转换兼容
     */
    private BodyType makeSendBodyCompatible(Map<String, Object> bodyMap) {
        return BodyType.makeGenerateBodyCompatible(bodyMap, mBodyType);
    }

    private void initSpecialNoticeMessageValue(Map<String, Object> bodyMap) {
        if (bodyMap.containsKey(TARGET_URL)) {
            specialAction = new SpecialAction();
            specialAction.targetUrl = (String) bodyMap.get(TARGET_URL);

        }
        if (bodyMap.containsKey(TARGET_ID)) {
            if (specialAction == null) {
                specialAction = new SpecialAction();
            }
            specialAction.targetId = (String) bodyMap.get(TARGET_ID);
        }


        if (bodyMap.containsKey(SOURCE)) {
            source = (String) bodyMap.get(SOURCE);
        }

        if (bodyMap.containsKey(NOTIFY_TYPE)) {
            specialNotice = new SpecialNotice();
            specialNotice.attachmentUrl = (String) bodyMap.get(ATTACHMENT_URL);
            specialNotice.noticeType = (String) bodyMap.get(NOTIFY_TYPE);
            specialNotice.sound = (String) bodyMap.get(SOUND);
            specialNotice.vibration = (String) bodyMap.get(VIBRATION);
        }

        if (bodyMap.containsKey(ORG_ID)) {
            orgId = (String) bodyMap.get(ORG_ID);
        }

        if(bodyMap.containsKey(BING_FROM)) {
            mBingCreatorId = (String) bodyMap.get(BING_FROM);
        }
    }

    public void buildSenderInfo(Context context) {
        LoginUserBasic loginUserInfo = LoginUserInfo.getInstance().getLoginUserBasic(context);
        this.from = loginUserInfo.mUserId;
        this.mFromDomain = loginUserInfo.mDomainId;
        this.mFromType = ParticipantType.User;
        this.mMyAvatar = loginUserInfo.mAvatar;
        this.mMyName = loginUserInfo.mName;
        this.mMyStatus = loginUserInfo.mStatus;
    }

    /**
     * 内部群需要雇员的身份来发送消息, 会带上 my_name_in_discussion, my_avatar_in_discussion
     */
    public boolean needEmpParticipant() {
        return !StringUtils.isEmpty(mMyNameInDiscussion);
    }


    /**
     * 是否是必应消息的回复消息类型
     */
    public boolean isBingReplyType() {
        if (!ParticipantType.Bing.equals(mToType)) {
            return false;
        }
        return BodyType.Text.equals(mBodyType) ||
                BodyType.File.equals(mBodyType) ||
                BodyType.Image.equals(mBodyType) ||
                BodyType.Voice.equals(mBodyType);
    }

    public Map<String, Object> getMessageBody() {
        Map<String, Object> messageBody = new HashMap<>();
        messageBody.put(FROM, from);
        messageBody.put(RETRIES, mRetries);
        if (null != mFromType) {
            messageBody.put(FROM_TYPE, mFromType.stringValue());

        }

        messageBody.put(FROM_DOMAIN, mFromDomain);

        if (!StringUtils.isEmpty(to)) {
            messageBody.put(TO, to);
            messageBody.put(TO_DOMAIN, mToDomain);
            if (null != mToType) {
                messageBody.put(TO_TYPE, mToType.stringValue());
            }
        }

        messageBody.put(DELIVER_ID, deliveryId);
        Map<String, Object> chatBody = getChatBody();
        messageBody.put(BODY, chatBody);
        BodyType bodyType = makeSendBodyCompatible(chatBody);
        messageBody.put(BODY_TYPE, bodyType.stringValue());
        messageBody.put(DELIVER_TIME, deliveryTime);

        if (-1 != mDeletionOn) {
            messageBody.put(DELETION_ON, mDeletionOn);
        }

        if (!StringUtils.isEmpty(mDeletionPolicy)) {
            messageBody.put(DELETION_POLICY, mDeletionPolicy);
        }
        return messageBody;
    }

    public String getMsgReadDeliveryId() {
        String keyId;
        keyId = this.deliveryId;
        return keyId;
    }

    public static class SpecialNotice {

        public String noticeType;

        public String attachmentUrl;

        public String taskBarIcon;

        public String sound;

        public String vibration;

    }

    public static class SpecialAction {
        public String targetUrl;

        public String targetId;
    }

    public SpecialNotice getSpecialNotice() {
        return specialNotice;
    }

    public SpecialAction getSpecialAction() {
        return specialAction;
    }


    public static abstract class Builder<T extends Builder> {
        private Context mContext;
        private String mDeliveryId;
        private long mDeliveryTime;
        private ParticipantType mToType;
        private String mTo;
        private String mToDomainId;
        private String mDisplayName;
        private String mDisplayAvatar;
        private String mOrgId;


        public T setDeliveryId(String deliveryId) {
            this.mDeliveryId = deliveryId;
            return (T) this;
        }

        public T setDeliveryTime(long deliveryTime) {
            this.mDeliveryTime = deliveryTime;
            return (T) this;
        }

        public T setContext(Context context) {
            this.mContext = context;
            return (T) this;
        }

        public T setTo(String to) {
            this.mTo = to;
            return (T) this;
        }

        public T setToDomainId(String toDomainId) {
            this.mToDomainId = toDomainId;
            return (T) this;
        }

        public T setDisplayName(String displayName) {
            this.mDisplayName = displayName;
            return (T) this;
        }

        public T setDisplayAvatar(String displayAvatar) {
            this.mDisplayAvatar = displayAvatar;
            return (T) this;
        }

        public T setOrgId(String orgId) {
            this.mOrgId = orgId;
            return (T) this;
        }



        public T setToType(ParticipantType toType) {
            this.mToType = toType;
            return (T) this;
        }




        public void assemble(PostTypeMessage postTypeMessage) {
            postTypeMessage.buildSenderInfo(this.mContext);
            postTypeMessage.to = this.mTo;
            postTypeMessage.mToDomain = this.mToDomainId;
            postTypeMessage.mDisplayName = this.mDisplayName;
            postTypeMessage.mDisplayAvatar = this.mDisplayAvatar;
            postTypeMessage.orgId = this.mOrgId;
            postTypeMessage.mToType = this.mToType;
            postTypeMessage.chatSendType = ChatSendType.SENDER;
            postTypeMessage.chatStatus = ChatStatus.Sending;

        }

    }


}
