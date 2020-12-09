package com.foreveross.atwork.infrastructure.newmessage.post.chat;

import android.content.Context;
import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.ReadStatus;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ChatSendType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.HasMediaChatPostMessage;
import com.foreveross.atwork.infrastructure.support.StringConstants;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.google.gson.annotations.Expose;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by dasunsy on 2017/6/21.
 */

public class MultipartChatMessage extends HasMediaChatPostMessage {

    public static final String FILE_ID = "file_id";

    public static final String TITLE = "title";

    public static final String TITLE_PARTICIPATORS = "participators";

    public static final String MEDIAS = "medias";

    public static final String CONTENT = "content";

    public static final String WATERMARK_ENABLE = "watermark_enable";

    @Expose
    public String mFileId;

    @Expose
    public String mTitle;

    @Expose
    public List<String> mParticipators;

    @Expose
    public String mContent;

    @Expose
    public FileStatus fileStatus;

    @Expose
    public boolean mWatermarkEnable;

    public List<ChatPostMessage> mMsgList;

    @Expose
    public List<String> medias = new ArrayList<>();

    public boolean mIsFromInternalDiscussion = false;
    public String mSourceOrgCode;

    public MultipartType multipartType = MultipartType.MULTIPART;


    public MultipartChatMessage() {
        deliveryTime = TimeUtil.getCurrentTimeInMillis();
        deliveryId = UUID.randomUUID().toString();
    }

    public static MultipartChatMessage getMultipartChatMessage(Map<String, Object> jsonMap) throws JSONException {
        MultipartChatMessage multipartChatMessage = new MultipartChatMessage();
        multipartChatMessage.initPostTypeMessageValue(jsonMap);

        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);

        multipartChatMessage.initChatTypeMessageValue(bodyMap);

        multipartChatMessage.mFileId = getString(bodyMap, FILE_ID);
        multipartChatMessage.mTitle = getString(bodyMap, TITLE);
        multipartChatMessage.mParticipators = getStringList(bodyMap, TITLE_PARTICIPATORS);
        multipartChatMessage.mContent = getString(bodyMap, CONTENT);
        multipartChatMessage.mWatermarkEnable = getBooleanFromInt(bodyMap, WATERMARK_ENABLE);

        multipartChatMessage.mOrgId = getString(bodyMap, ORG_ID);


        return multipartChatMessage;
    }


    public boolean hasMedias() {
        return !ListUtil.isEmpty(medias);
    }


    @Override
    public ChatType getChatType() {
        return ChatType.MULTIPART;
    }

    @Override
    public String getSessionShowTitle() {
        return StringConstants.SESSION_CONTENT_MULTIPART;
    }

    @Override
    public String getSearchAbleString() {
        return StringUtils.EMPTY;
    }

    @Override
    public boolean needNotify() {
        return true;
    }

    @Override
    public boolean needCount() {
        return true;
    }


    @Override
    public void reGenerate(Context context, String senderId, String receiverId, String receiverDomainId, ParticipantType fromType, ParticipantType toType, BodyType bodyType, String orgId, ShowListItem chatItem, String myName, String myAvatar) {
        super.reGenerate(context, senderId, receiverId, receiverDomainId, fromType, toType, bodyType, orgId, chatItem, myName, myAvatar);

        multipartType = MultipartType.MULTIPART;

    }

    @Override
    public Map<String, Object> getChatBody() {
        Map<String, Object> chatBody = new HashMap<>();
        chatBody.put(FILE_ID, mFileId);
        chatBody.put(CONTENT, mContent);
        chatBody.put(TITLE, mTitle);
        chatBody.put(TITLE_PARTICIPATORS, mParticipators);
        chatBody.put(MEDIAS, medias);

        if (!TextUtils.isEmpty(mOrgId)) {
            chatBody.put(ORG_ID, mOrgId);
        }
        if (mWatermarkEnable) {
            chatBody.put(WATERMARK_ENABLE, 1);
        } else {
            chatBody.put(WATERMARK_ENABLE, 0);

        }
        setBasicChatBody(chatBody);
        return chatBody;

    }

    @Override
    public String[] getMedias() {
        List<String> allMedias = new ArrayList<>(medias);
//        if(!StringUtils.isEmpty(mFileId)) {
//            allMedias.add(mFileId);
//        }
        return allMedias.toArray(new String[0]);
    }

    public static class Builder {
        public Context mContext;
        public String mTo;
        public String mToDomainId;
        public String mDisplayName;
        public String mDisplayAvatar;
        public String mOrgId;
        public String mTitle;
        public String mContent;
        public ParticipantType mToType;
        public List<ChatPostMessage> mMsgList;
        public boolean mIsFromInternalDiscussion;
        public boolean mWatermarkEnable;
        public List<String> mMedias = new ArrayList<>();


        public Builder setContext(Context context) {
            this.mContext = context;
            return this;
        }

        public Builder setTo(String to) {
            this.mTo = to;
            return this;
        }

        public Builder setToDomainId(String toDomainId) {
            this.mToDomainId = toDomainId;
            return this;
        }

        public Builder setDisplayName(String displayName) {
            this.mDisplayName = displayName;
            return this;
        }

        public Builder setDisplayAvatar(String displayAvatar) {
            this.mDisplayAvatar = displayAvatar;
            return this;
        }

        public Builder setOrgId(String orgId) {
            this.mOrgId = orgId;
            return this;
        }

        public Builder setTitle(String title) {
            this.mTitle = title;
            return this;
        }

        public Builder setContent(String content) {
            this.mContent = content;
            return this;
        }

        public Builder setToType(ParticipantType toType) {
            this.mToType = toType;
            return this;
        }

        public Builder setMsgList(List<ChatPostMessage> msgList) {
            for (ChatPostMessage message : msgList) {

                if(message instanceof HasMediaChatPostMessage) {
                    HasMediaChatPostMessage mediaChatPostMessage = (HasMediaChatPostMessage) message;
                    List<String> mediasInMsg = Arrays.asList(mediaChatPostMessage.getMedias());
                    if (!ListUtil.isEmpty(mediasInMsg)) {
                        this.mMedias.addAll(mediasInMsg);
                    }
                }

            }
            this.mMsgList = msgList;
            return this;
        }

        public Builder setIsFromInternalDiscussion(boolean isFromInternalDiscussion) {
            this.mIsFromInternalDiscussion = isFromInternalDiscussion;
            return this;
        }

        public Builder setWatermarkEnable(boolean watermarkEnable) {
            this.mWatermarkEnable = watermarkEnable;
            return this;
        }

        public MultipartChatMessage build() {
            MultipartChatMessage multipartChatMessage = new MultipartChatMessage();
            multipartChatMessage.buildSenderInfo(this.mContext);
            multipartChatMessage.to = this.mTo;
            multipartChatMessage.mToDomain = this.mToDomainId;
            multipartChatMessage.mDisplayName = this.mDisplayName;
            multipartChatMessage.mDisplayAvatar = this.mDisplayAvatar;
            multipartChatMessage.mOrgId = this.mOrgId;
            multipartChatMessage.mTitle = this.mTitle;
            multipartChatMessage.mContent = this.mContent;
            multipartChatMessage.mMsgList = this.mMsgList;
            multipartChatMessage.mToType = this.mToType;
            multipartChatMessage.mFromType = ParticipantType.User;
            multipartChatMessage.mBodyType = BodyType.Multipart;
            multipartChatMessage.read = ReadStatus.AbsolutelyRead;
            multipartChatMessage.chatSendType = ChatSendType.SENDER;
            multipartChatMessage.chatStatus = ChatStatus.Sending;
            multipartChatMessage.fileStatus = FileStatus.SENDING;
            multipartChatMessage.mIsFromInternalDiscussion = this.mIsFromInternalDiscussion;
            multipartChatMessage.mWatermarkEnable = this.mWatermarkEnable;
            multipartChatMessage.medias = this.mMedias;
            return multipartChatMessage;
        }

    }
}
