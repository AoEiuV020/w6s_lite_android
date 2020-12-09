package com.foreveross.atwork.infrastructure.newmessage.post.chat;/**
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 __           __
 .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 |________|_____|__|  |__|__|   __||__||_____|_____|
 |__|
 */


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
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.encryption.Base64Util;
import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by reyzhang22 on 15/12/23.
 */
public class MicroVideoChatMessage extends HasMediaChatPostMessage implements HasFileStatus{

    public static final String THUMBNAIL_MEDIA_ID = "thumbnail_media_id";

    public static final String DESCRIPTION = "description";

    public static final String SIZE = "size";


    /**
     * 缩略图
     */
    public byte[] thumbnails;

    /**
     * 图片ID,原图ID
     */
    @Expose
    public String mediaId;

    @Expose
    public String thumbnailMediaId;


    @Expose
    public int progress;

    @Expose
    public FileStatus fileStatus;

    @Expose
    public String filePath;

    @Expose
    public long size = -1;

    public String desc;


    public MicroVideoChatMessage() {
        deliveryTime = TimeUtil.getCurrentTimeInMillis();
        deliveryId = UUID.randomUUID().toString();
    }

    /**
     * 组装视频消息
     * @param bytes
     * @param sender
     * @param to
     * @param fromType
     * @param toType
     * @return
     */
    private static MicroVideoChatMessage newSendMicroVideoMessage(Context context, byte[] bytes, ShowListItem sender, String to, ParticipantType fromType,
                                                                  ParticipantType toType, String toDomain, String toAvatar, String toName, BodyType bodyType, String orgId, long size) {
        MicroVideoChatMessage microVideoChatMessage = new MicroVideoChatMessage();
        microVideoChatMessage.buildSenderInfo(context);
        microVideoChatMessage.thumbnails = bytes;
        microVideoChatMessage.size = size;
//        microVideoChatMessage.from = sender.getId();
//        microVideoChatMessage.mMyAvatar = sender.getAvatar();
//        microVideoChatMessage.mMyName = sender.getTitle();
        microVideoChatMessage.to = to;
        microVideoChatMessage.chatSendType = ChatSendType.SENDER;
        microVideoChatMessage.chatStatus = ChatStatus.Sending;
        microVideoChatMessage.read = ReadStatus.AbsolutelyRead;
        microVideoChatMessage.fileStatus = FileStatus.SENDING;
        microVideoChatMessage.mBodyType = bodyType;
//        microVideoChatMessage.mFromType = fromType;
        microVideoChatMessage.mToType = toType;
        microVideoChatMessage.mToDomain = toDomain;
        microVideoChatMessage.mOrgId = orgId;
        microVideoChatMessage.desc = "小视频";
        microVideoChatMessage.mDisplayAvatar = toAvatar;
        microVideoChatMessage.mDisplayName = toName;

        return microVideoChatMessage;
    }

    public static MicroVideoChatMessage newSendMicroVideoMessage(Context context, byte[] bytes, ShowListItem sender, String to, ParticipantType fromType,
                                                                 ParticipantType toType, String toDomain, BodyType bodyType, String orgId , ShowListItem showListItem, long size) {
        String toAvatar = "";
        String toName = "";
        if(null != showListItem) {
            toAvatar = showListItem.getAvatar();
            toName = showListItem.getTitle();
        }

        return newSendMicroVideoMessage(context, bytes, sender, to, fromType, toType, toDomain, toAvatar, toName, bodyType, orgId, size);
    }


    @Override
    public void reGenerate(Context context, String senderId, String receiverId, String receiverDomainId, ParticipantType fromType,
                           ParticipantType toType, BodyType bodyType, String orgId, ShowListItem chatItem,String myName, String myAvatar) {
        byte[] contents = ImageShowHelper.getThumbnailImage(context, deliveryId);
        super.reGenerate(context, senderId, receiverId, receiverDomainId, fromType, toType, bodyType, orgId, chatItem,myName,myAvatar);
        ImageShowHelper.saveThumbnailImage(context, deliveryId, contents);
    }

    @Override
    public ChatType getChatType() {
        return ChatType.MicroVideo;
    }

    @Override
    public String getSessionShowTitle() {
        return StringConstants.SESSION_CONTENT_MICRO_VIDEO;
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
    public Map<String, Object> getChatBody() {
        Map<String, Object> chatBody = new HashMap<>();
        chatBody.put(MEDIA_ID, mediaId);
        chatBody.put(CONTENT, thumbnails);
        chatBody.put(THUMBNAIL_MEDIA_ID, thumbnailMediaId);

        chatBody.put(DESCRIPTION, "小视频");
        if (!TextUtils.isEmpty(mOrgId)) {
            chatBody.put(ORG_ID, mOrgId);
        }

        if(-1 != size) {
            chatBody.put(SIZE, size);
        }

        setBasicChatBody(chatBody);
        return chatBody;
    }

    @Override
    public void setFileStatus(FileStatus fileStatus) {
        this.fileStatus = fileStatus;
    }

    public static MicroVideoChatMessage getMicroVideoMessageFromJson(Map<String, Object> jsonMap) {
        MicroVideoChatMessage microVideoChatMessage = new MicroVideoChatMessage();
        microVideoChatMessage.initPostTypeMessageValue(jsonMap);
        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);

        microVideoChatMessage.initChatTypeMessageValue(bodyMap);

        microVideoChatMessage.mediaId = (String) bodyMap.get(MEDIA_ID);
        if (bodyMap.containsKey(CONTENT)) {
            microVideoChatMessage.thumbnails = Base64Util.decode((String) bodyMap.get(CONTENT));
        }

        if (bodyMap.containsKey(THUMBNAIL_MEDIA_ID)) {
            microVideoChatMessage.thumbnailMediaId = (String) bodyMap.get(THUMBNAIL_MEDIA_ID);
        }

        if (bodyMap.containsKey(SOURCE)) {
            microVideoChatMessage.source = (String)bodyMap.get(SOURCE);
        }

        microVideoChatMessage.fileStatus = FileStatus.NOT_DOWNLOAD;

        microVideoChatMessage.size = ChatPostMessage.getLong(bodyMap, SIZE);


        return microVideoChatMessage;
    }


    @Override
    public String[] getMedias() {
        if (!StringUtils.isEmpty(mediaId)) {
            return new String[]{mediaId};
        }

        return new String[0];
    }
}
