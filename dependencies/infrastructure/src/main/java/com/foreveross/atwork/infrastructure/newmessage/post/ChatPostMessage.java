package com.foreveross.atwork.infrastructure.newmessage.post;

import android.content.Context;

import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.ReadStatus;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ChatSendType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.BurnInfo;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.EmergencyInfo;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MultipartChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.OrgPositionInfo;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.reference.ReferenceMessage;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.google.gson.annotations.Expose;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by lingen on 15/4/16.
 * Description:
 * 聊天类型的消息
 */
public abstract class ChatPostMessage extends PostTypeMessage implements Comparable, Cloneable {


    public static final String UNDO = "undo";

    public static final String REMOVED = "removed";

    public static final String DISPLAY_NAME = "display_name";

    public static final String DISPLAY_AVATAR = "display_avatar";

    public static final String ORG_ID = "org_id";

    public static final String MY_NAME = "my_name";

    public static final String MY_AVATAR = "my_avatar";

    public static final String MY_STATUS = "my_status";

    public static final String DISPLAY_STATUS = "display_status";

    public static final String MY_NAME_IN_DISCUSSION = "my_name_in_discussion";

    public static final String MY_AVATAR_IN_DISCUSSION = "my_avatar_in_discussion";

    public static final String BURN = "burn";

    public static final String READ_TIME = "read_time";

    public static final String EMERGENCY = "emergency";

    public static final String REPEAT_PER_SECONDS = "repeat_per_seconds";

    public static final String PLAN_ID = "plan_id";

    public static final String ORG_POSITION = "org_position";

    public static final String ORG_DEPT_INFOS = "org_dept_infos";

    public static final String COMMENT = "comment";

    public static final String TARGET_SCOPE = "target_scope";
    public static final double TARGET_SCOPE_NEWS_SUMMARY = 1;
    public static final double TARGET_SCOPE_APP = 2;
    public static final double TARGET_SCOPE_ALL = 3;

    @Expose
    public String mOrgId;

    /**
     * 消息是否已读
     */
    public ReadStatus read = ReadStatus.Unread;


    /**
     * 阅后即焚信息
     * */
    @Expose
    public BurnInfo mBurnInfo;

    /**
     * 是否选中
     * */
    public boolean select;

    @Expose
    public EmergencyInfo mEmergencyInfo;

    @Expose
    public OrgPositionInfo orgPositionInfo;

    @Expose
    public long undoSuccessTime;


    /**
     * 聊天场景
     * */
    public ChatEnvironment chatEnvironment = ChatEnvironment.CHAT;

    /**
     * 关联的父类合并消息
     * */
    public MultipartChatMessage parentMultipartChatMessage;


    /**
     * 关联的父类引用消息
     * */
    public ReferenceMessage parentReferenceMessage;


    public int forcedSerial = -1;

    public String showableString;


    public abstract ChatType getChatType();

    /**
     * 返回每个消息类型在聊天会话列表中的显示内容
     *
     * @return
     */
    public abstract String getSessionShowTitle();

    /**
     * 返回每个消息可以被搜索到的内容
     *
     * @return
     */
    public abstract String getSearchAbleString();

    public abstract boolean needNotify();

    public abstract boolean needCount();


    /**
     * 是否是紧急消息
     * */
    public boolean isEmergency() {
        return null != mEmergencyInfo && mEmergencyInfo.mEmergency;
    }

    /**
     * 紧急消息是否已经确认过了
     * */
    public boolean isEmergencyConfirmed() {
        if(isEmergency()) {
            return mEmergencyInfo.mConfirmed;
        }

        return false;
    }


    public boolean isEmergencyUnconfirmed() {
        if(isEmergency()) {
            return !mEmergencyInfo.mConfirmed;
        }

        return false;
    }

    public void setEmergencyConfirm() {
        if(null != mEmergencyInfo) {
            mEmergencyInfo.mConfirmed = true;
        }
    }

    public String getPlanId() {
        if(null != mEmergencyInfo) {
            return mEmergencyInfo.mPlanId;
        }

        return StringUtils.EMPTY;
    }

    public boolean notSent() {

        return null != chatStatus && ChatStatus.Sended != chatStatus && ChatStatus.At_All != chatStatus;
    }


    public boolean isSelectLegal() {
        return !isUndo() && select;
    }

    /**
     * 是否是阅后即焚消息
     * */
    public boolean isBurn() {
        return null != mBurnInfo;
    }

    /**
     * 消息是否已经被撤销
     * */
    public boolean isUndo() {
        return ChatStatus.UnDo.equals(chatStatus);
    }

    public boolean isDiscussionAtAllNeedNotify() {
        return ChatStatus.At_All == chatStatus && ReadStatus.Unread == read;
    }

    public boolean isExpired() {
//        return false;
        return isMayBeDeleted() && mDeletionOn <= TimeUtil.getCurrentTimeInMillis();
    }

    /**
     * 是否存在删除策略
     * */
    public boolean isMayBeDeleted() {
        return !StringUtils.isEmpty(mDeletionPolicy);
    }

    public long getReadTime() {
        if(null != mBurnInfo) {
            return mBurnInfo.mReadTime;
        }
        return  -1;
    }

    /**
     * 是否隐藏
     * */
    public boolean isHide() {
        return ChatStatus.Hide == chatStatus;
    }


    @Override
    protected void setBasicChatBody(Map<String, Object> chatBody) {
        super.setBasicChatBody(chatBody);

        if(null != orgPositionInfo) {
            chatBody.put(ORG_POSITION, orgPositionInfo.jobTitle);
            chatBody.put(ORG_DEPT_INFOS, orgPositionInfo.orgDeptInfos);
        }
    }

    /**
     * COPY出一个新消息进行转发
     * @param context
     * @param senderId
     * @param receiverId
     * @param receiverDomainId
     * @param fromType
     * @param toType
     * @param bodyType
     */
    public void reGenerate(Context context, String senderId, String receiverId, String receiverDomainId, ParticipantType fromType,
                           ParticipantType toType, BodyType bodyType, String orgId, ShowListItem chatItem, String myName, String myAvatar) {
        deliveryTime = TimeUtil.getCurrentTimeInMillis();
        deliveryId = UUID.randomUUID().toString();

        this.mFromDomain = AtworkConfig.DOMAIN_ID;
        this.mFromType = ParticipantType.User;

        this.from = senderId;
        this.mFromType = fromType;
        this.mMyAvatar = myAvatar;
        this.mMyName = myName;
        this.to = receiverId;
        this.mToType = toType;
        this.mToDomain = receiverDomainId;
        this.mBodyType = bodyType;
        this.chatSendType = ChatSendType.SENDER;
        this.read = ReadStatus.AbsolutelyRead;
        this.mOrgId = orgId;

        if (chatItem != null) {
            this.mDisplayAvatar = chatItem.getAvatar();
            this.mDisplayName = chatItem.getTitle();
        }

        if(chatItem instanceof User) {
            User userChatItem = (User) chatItem;
            this.mDisplayStatus = userChatItem.getSignature();
        } else {
            this.mDisplayStatus = StringUtils.EMPTY;
        }

        if (this instanceof FileTransferChatMessage) {
            FileTransferChatMessage fileTransferChatMessage = (FileTransferChatMessage) this;
            fileTransferChatMessage.chatStatus = ChatStatus.Sending;
            fileTransferChatMessage.progress = 0;
        } else if (this instanceof TextChatMessage) {
            TextChatMessage textChatMessage = (TextChatMessage) this;

            if (null != textChatMessage.mAtUserList && 0 != textChatMessage.mAtUserList.size()) {
                textChatMessage.mAtUserList.clear();
            }
            textChatMessage.textType = TextChatMessage.COMMON;
            textChatMessage.atAll = false;
        }
    }


    @Override
    public boolean equals(Object o) {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        if (o == null || o instanceof ChatPostMessage == false) {
            return false;
        }
        ChatPostMessage other = (ChatPostMessage) o;
        if (StringUtils.isEmpty(deliveryId) == false) {
            return deliveryId.equals(other.deliveryId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return deliveryId != null ? deliveryId.hashCode() : 0;
    }

    @Override
    public int compareTo(Object another) {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        if (another == null || another instanceof ChatPostMessage == false) {
            return 0;
        }
        ChatPostMessage anotherChatPostMessage = (ChatPostMessage) another;
        return TimeUtil.compareTo(deliveryTime, anotherChatPostMessage.deliveryTime);
    }


    /**
     * 消息是否从群组里发出来的
     * */
    public boolean isFromDiscussionChat() {
        return ParticipantType.Discussion == mToType;
    }

    public String getDiscussionId() {
        if(isFromDiscussionChat()) {
            return to;
        }

        return StringUtils.EMPTY;
    }

    public boolean isParentLegalP2pUserChat(Context context) {
        return null != parentMultipartChatMessage && parentMultipartChatMessage.isLegalP2pUserChat(context);
    }

    /**
     * 是否是合法的单聊, to, from 必须有1个是自己
     * */
    public boolean isLegalP2pUserChat(Context context) {
        return isFromUserChat() && (User.isYou(context, from) || User.isYou(context, to));
    }

    /**
     * 消息是否从单聊会话里发出来的
     * */
    public boolean isFromUserChat() {
        return ParticipantType.User == mToType;
    }


    public enum ChatType {
        //文本消息
        Text {
            @Override
            public int intValue() {
                return 0;
            }
        },

        Image {
            @Override
            public int intValue() {
                return 1;
            }
        },

        //语音消息
        Voice {
            @Override
            public int intValue() {
                return 2;
            }
        },

        //文件传输消息
        File {
            @Override
            public int intValue() {
                return 3;
            }
        },


        ImageText {
            @Override
            public int intValue() {
                return 4;
            }
        },

        System {
            @Override
            public int intValue() {
                return 5;
            }
        },

        Article {
            @Override
            public int intValue() {
                return 6;
            }
        },

        Event {
            @Override
            public int intValue() {
                return 7;
            }
        },

        HistoryDivider {
            @Override
            public int intValue() {
                return 8;
            }
        },
        //分享消息
        Share {
            @Override
            public int intValue() {
                return 9;
            }
        },
        //微视频消息
        MicroVideo {
            public int intValue() {
                return 10;
            }
        },

        //ESpace
        AUDIOMEETING {
            public int intValue() {
                return 11;
            }
        },

        //voip
        VOIP {
            public int intValue() {
                return 12;
            }
        },
        //阅后即焚
        BURN {
            @Override
            public int intValue() {
                return 13;
            }
        },

        //合并转发
        MULTIPART {
            @Override
            public int intValue() {
                return 14;
            }
        },

        TEMPLATE {
            @Override
            public int intValue() {
                return 15;
            }
        },

        BING_TEXT {
            @Override
            public int intValue() {
                return 16;
            }
        },

        BING_VOICE {
            @Override
            public int intValue() {
                return 17;
            }
        },
        //必应消息确认
        BING_CONFIRM {
            @Override
            public int intValue() {
                return 18;
            }
        },

        MEETING_NOTICE {
            @Override
            public int intValue() {
                return 19;
            }
        },

        STICKER {
            @Override
            public int intValue() {
                return 23;
            }
        },

        QUOTED {
            @Override
            public int intValue() {
                return 24;
            }
        },

        ANNO_FILE {
            @Override
            public int intValue() {
                return 25;
            }
        },

        ANNO_IMAGE {
            @Override
            public int intValue() {
                return 26;
            }
        },

        DOC {
            @Override
            public int intValue() {
                return 27;
            }
        },

        UNKNOWN {
            @Override
            public int intValue() {
                return -1;
            }
        };

        public static ChatType fromIntValue(int value) {
            if (0 == value) {
                return Text;
            }
            if (2 == value) {
                return Voice;
            }
            if (1 == value) {
                return Image;
            }
            if (3 == value) {
                return File;
            }
            if (4 == value) {
                return ImageText;
            }
            if (5 == value) {
                return System;
            }
            if (6 == value) {
                return Article;
            }
            if (7 == value) {
                return Event;
            }
            if (8 == value) {
                return HistoryDivider;
            }
            if (9 == value){
                return Share;
            }
            if (10 == value) {
                return MicroVideo;
            }
            if (11 == value) {
                return AUDIOMEETING;
            }
            if (12 == value) {
                return VOIP;
            }
            if (13 == value) {
                return BURN;
            }
            if (14 == value) {
                return MULTIPART;
            }
            if (15 == value) {
                return TEMPLATE;
            }

            if (16 == value) {
                return BING_TEXT;
            }

            if (17 == value) {
                return BING_VOICE;
            }

            if(18 == value) {
                return BING_CONFIRM;
            }

            if(19 == value) {
                return MEETING_NOTICE;
            }

            if (23 == value) {
                return STICKER;
            }

            if(24 == value) {
                return QUOTED;
            }

            if(25 == value) {
                return ANNO_FILE;
            }

            if(26 == value) {
                return ANNO_IMAGE;
            }

            return UNKNOWN;
        }

        public static ChatType fromStringValue(String value) {
            if ("Text".equalsIgnoreCase(value)) {
                return Text;
            }
            if ("Voice".equalsIgnoreCase(value)) {
                return Voice;
            }
            if ("Image".equalsIgnoreCase(value)) {
                return Image;
            }
            if ("File".equalsIgnoreCase(value)) {
                return File;
            }
            if ("ImageText".equalsIgnoreCase(value)) {
                return ImageText;
            }
            if ("System".equalsIgnoreCase(value)) {
                return System;
            }
            if ("article".equalsIgnoreCase(value)) {
                return Article;
            }
            if ("Event".equalsIgnoreCase(value)) {
                return Event;
            }
            if ("HistoryDivider".equalsIgnoreCase(value)) {
                return HistoryDivider;
            }
            if ("Share".equalsIgnoreCase(value)){
                return Share;
            }
            if ("MicroVideo".equalsIgnoreCase(value)) {
                return MicroVideo;
            }
            if ("AUDIOMEETING".equalsIgnoreCase(value)) {
                return AUDIOMEETING;
            }
            if("VOIP".equalsIgnoreCase(value)) {
                return VOIP;
            }
            if("BURN".equalsIgnoreCase(value)) {
                return BURN;
            }
            if("MULTIPART".equalsIgnoreCase(value)) {
                return MULTIPART;
            }
            if ("TEMPLATE".equalsIgnoreCase(value)) {
                return TEMPLATE;
            }

            if ("BING_TEXT".equalsIgnoreCase(value)) {
                return BING_TEXT;
            }

            if ("BING_VOICE".equalsIgnoreCase(value)) {
                return BING_VOICE;
            }

            if("BING_CONFIRM".equalsIgnoreCase(value)) {
                return BING_CONFIRM;
            }

            if("MEETING_NOTICE".equalsIgnoreCase(value)) {
                return MEETING_NOTICE;
            }

            if ("STICKER".equalsIgnoreCase(value)) {
                return STICKER;
            }

            if("QUOTED".equalsIgnoreCase(value)) {
                return QUOTED;
            }

            if("ANNO_FILE".equalsIgnoreCase(value)) {
                return ANNO_FILE;
            }


            if("ANNO_IMAGE".equalsIgnoreCase(value)) {
                return ANNO_IMAGE;
            }

            return UNKNOWN;
        }

        public abstract int intValue();
    }

    public static String getString(Map<String, Object> bodyMap, String key) {
        try {
            Object strObj = bodyMap.get(key);
            if(null == strObj){
                return StringUtils.EMPTY;
            } else {
                return (String) strObj;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return StringUtils.EMPTY;
    }

    public static List<String> getStringList(Map<String, Object> bodyMap, String key) {
        try {
            Object strObj = bodyMap.get(key);
            if(null == strObj){
                return null;
            } else {
                return (List<String>) strObj;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int getInt(Map<String, Object> bodyMap, String key) {
        try {
            Object intObj = bodyMap.get(key);
            if(null == intObj){
                return -1;
            } else {
                return ((Double)intObj).intValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static long getLong(Map<String, Object> bodyMap, String key) {
        try {
            Object longObj = bodyMap.get(key);
            if(null == longObj){
                return -1;
            } else {
                return ((Double)longObj).longValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static boolean getBoolean(Map<String, Object> bodyMap, String key) {
        boolean booleanObj = false;
        try {
            booleanObj = (Boolean)bodyMap.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return booleanObj;
    }

    public static boolean getBooleanFromInt(Map<String, Object> bodyMap, String key) {
        int intValue = getInt(bodyMap, key);
        return 1 == intValue;
    }



    public static abstract class Builder<T extends ChatPostMessage.Builder> extends PostTypeMessage.Builder<T> {

        private OrgPositionInfo mOrgPositionInfo;

        public void setOrgPositionInfo(OrgPositionInfo orgPositionInfo) {
            this.mOrgPositionInfo = orgPositionInfo;
        }

        protected abstract BodyType getBodyType();


        public void assemble(ChatPostMessage chatPostMessage) {
            super.assemble(chatPostMessage);

            chatPostMessage.read = ReadStatus.AbsolutelyRead;
            chatPostMessage.chatSendType = ChatSendType.SENDER;
            chatPostMessage.chatStatus = ChatStatus.Sending;
            chatPostMessage.orgPositionInfo = mOrgPositionInfo;
            chatPostMessage.mBodyType = getBodyType();
        }
    }

}

