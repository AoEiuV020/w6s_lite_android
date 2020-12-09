package com.foreveross.atwork.infrastructure.newmessage.post.chat;

import android.content.Context;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.ReadStatus;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ChatSendType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.HasMediaChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoImageChatMessage;
import com.foreveross.atwork.infrastructure.support.StringConstants;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.encryption.Base64Util;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by lingen on 15/4/16.
 * Description:
 */
public class ImageChatMessage extends HasMediaChatPostMessage implements HasFileStatus {


    public static final String ORIGINAL_SUFFIX = "-original";

    public static final String THUMBNAIL_SUFFIX = "-thumbnail";

    public static final String THUMBNAIL_MEDIA_ID = "thumb_media_id";

    public static final String THUMBNAIL_MEDIA_ID2 = "thumbnail_media_id";

    public static final String THUMBNAIL_MEDIA_ID3 = "thumbnail_id";

    public static final String IMAGE_TYPE = "is_gif";

    public static final String ORIGINAL_MEDIA_ID = "original_media_id";

    public static final String WIDTH = "width";

    public static final String HEIGHT = "height";

    public static final String SIZE = "size";

    /**
     * 缩略图
     */
    private byte[] thumbnails;

    /**
     * 压缩图片ID
     */
    @Expose
    public String mediaId = "";

    /**
     * 原图 mediaId
     */
    @Expose
    public String fullMediaId = "";

    @Expose
    public String thumbnailMediaId = "";


    @Expose
    public int progress;

    @Expose
    public FileStatus fileStatus;

    @Expose
    public boolean isGif = false;

    @Expose
    public ImageInfo info = new ImageInfo();

    @Expose
    public String fullImgPath = "";

    @Expose
    public String filePath = "";

    /**
     * 关联的父类anno 图片消息
     * */
    public AnnoImageChatMessage parentAnnoImageChatMessage;



    public ImageChatMessage() {
        deliveryTime = TimeUtil.getCurrentTimeInMillis();
        deliveryId = UUID.randomUUID().toString();
    }

    public static ImageChatMessage getImageChatMessageFromJson(Map<String, Object> jsonMap) {
        ImageChatMessage imageChatMessage = new ImageChatMessage();
        imageChatMessage.initPostTypeMessageValue(jsonMap);

        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);

        imageChatMessage.initChatTypeMessageValue(bodyMap);

        imageChatMessage.mediaId = (String) bodyMap.get(MEDIA_ID);

        if (bodyMap.containsKey(CONTENT)) {
            imageChatMessage.thumbnails = Base64Util.decode((String) bodyMap.get(CONTENT));
        }

        if (bodyMap.containsKey(THUMBNAIL_MEDIA_ID)) {
            imageChatMessage.thumbnailMediaId = (String) bodyMap.get(THUMBNAIL_MEDIA_ID);
        }

        if (bodyMap.containsKey(THUMBNAIL_MEDIA_ID2)) {
            imageChatMessage.thumbnailMediaId = (String) bodyMap.get(THUMBNAIL_MEDIA_ID2);
        }

        if (bodyMap.containsKey(ORIGINAL_MEDIA_ID)) {
            imageChatMessage.fullMediaId = (String) bodyMap.get(ORIGINAL_MEDIA_ID);
        }

        if (bodyMap.containsKey(SOURCE)) {
            imageChatMessage.source = (String) bodyMap.get(SOURCE);
        }


        imageChatMessage.fileStatus = FileStatus.NOT_DOWNLOAD;


        try {
            imageChatMessage.isGif = (null != bodyMap.get(IMAGE_TYPE) && 1 == ((Double) bodyMap.get(IMAGE_TYPE)).intValue());
        } catch (Exception e) {
            e.printStackTrace();
        }

        imageChatMessage.info.width = getInt(bodyMap, WIDTH);
        imageChatMessage.info.height = getInt(bodyMap, HEIGHT);
        imageChatMessage.info.size = getLong(bodyMap, SIZE);
        if (bodyMap.containsKey(ORG_ID)) {
            imageChatMessage.mOrgId = (String) bodyMap.get(ORG_ID);

        }

        if (!StringUtils.isEmpty(imageChatMessage.mDeletionPolicy) && bodyMap.containsKey(BURN)) {
            imageChatMessage.mBurnInfo = BurnInfo.parseFromMap((Map<String, Object>) bodyMap.get(BURN));
        }

        return imageChatMessage;
    }


    public static ImageChatMessage getImageChatMessageFromPath(String filePath) {
        ImageChatMessage imageChatMessage = new ImageChatMessage();

        FileData fileData = FileData.fromPath(filePath);

        imageChatMessage.filePath = fileData.filePath;
//        if(FileData.FileType.File_Gif == fileData.fileType) {
//            imageChatMessage.isGif = true;
//        }

        imageChatMessage.mBodyType = BodyType.Image;
        imageChatMessage.chatSendType = ChatSendType.SENDER;
        imageChatMessage.chatStatus = ChatStatus.Sending;
        imageChatMessage.fileStatus = FileStatus.SENDING;

        return imageChatMessage;
    }

    public static ImageChatMessage newSendImageMessage(Context context, byte[] bytes, @Nullable ShowListItem empSender, String to,
                                                       ParticipantType toType, String toDomain, boolean isGif, BodyType bodyType,
                                                       String orgId, ShowListItem chatItem, boolean burn, long readTime, long deletionOnTime, String bingCreatorId) {
        String toAvatar = "";
        String toName = "";
        if (null != chatItem) {
            toAvatar = chatItem.getAvatar();
            toName = chatItem.getTitle();
        }

        return newSendImageMessage(context, bytes, empSender, to, toType, toDomain, toAvatar, toName, isGif, bodyType, orgId, burn, readTime, deletionOnTime, bingCreatorId);
    }


    public static ImageChatMessage newSendImageMessage(Context context, byte[] bytes, @Nullable ShowListItem empSender, String to,
                                                       ParticipantType toType, String toDomain, String toAvatar, String toName,
                                                       boolean isGif, BodyType bodyType, String orgId, boolean burn, long readTime, long deletionOnTime, String bingCreatorId) {
        ImageChatMessage imageChatMessage = new ImageChatMessage();
        imageChatMessage.thumbnails = bytes;
        imageChatMessage.buildSenderInfo(context);
        if (null != empSender) {
            imageChatMessage.mMyNameInDiscussion = empSender.getTitle();
        }
        imageChatMessage.to = to;
        imageChatMessage.mToDomain = toDomain;
        imageChatMessage.chatSendType = ChatSendType.SENDER;
        imageChatMessage.chatStatus = ChatStatus.Sending;
        imageChatMessage.fileStatus = FileStatus.SENDING;
        imageChatMessage.read = ReadStatus.AbsolutelyRead;
        imageChatMessage.isGif = isGif;
        imageChatMessage.mBodyType = bodyType;
        imageChatMessage.mToType = toType;
        imageChatMessage.mOrgId = orgId;
        imageChatMessage.mDisplayAvatar = toAvatar;
        imageChatMessage.mDisplayName = toName;
        if (burn) {
            imageChatMessage.mBurnInfo = new BurnInfo();
            imageChatMessage.mBurnInfo.mReadTime = readTime;
            imageChatMessage.mDeletionPolicy = "LOGICAL";
            imageChatMessage.mDeletionOn = deletionOnTime;
        }
        imageChatMessage.mBingCreatorId = bingCreatorId;

        return imageChatMessage;
    }


    @Override
    public int compareTo(Object another) {

        if (another == null || another instanceof ImageChatMessage == false) {
            return super.compareTo(another);
        }
        ImageChatMessage anotherImageChatMessage = (ImageChatMessage) another;


        if(null != parentAnnoImageChatMessage && null != anotherImageChatMessage.parentAnnoImageChatMessage
                && parentAnnoImageChatMessage.equals(anotherImageChatMessage.parentAnnoImageChatMessage)) {
            return Integer.compare(forcedSerial, forcedSerial);
        }



        return super.compareTo(another);

    }

    @Override
    public void reGenerate(Context context, String senderId, String receiverId, String receiverDomainId, ParticipantType fromType,
                           ParticipantType toType, BodyType bodyType, String orgId, ShowListItem chatItem, String myName, String myAvatar) {

        byte[] contents = ImageShowHelper.getThumbnailImage(context, deliveryId);
        byte[] originalContents = ImageShowHelper.getOriginalImage(context, deliveryId);
        super.reGenerate(context, senderId, receiverId, receiverDomainId, fromType, toType, bodyType, orgId, chatItem, myName, myAvatar);
        if (0 != contents.length) {
            ImageShowHelper.saveThumbnailImage(context, deliveryId, contents);
        }

        if (0 != originalContents.length) {
            ImageShowHelper.saveOriginalImage(context, deliveryId, originalContents);
        }
    }

    @Override
    public Map<String, Object> getChatBody() {
        Map<String, Object> chatBody = new HashMap<>();
        chatBody.put(MEDIA_ID, mediaId);
        if(!StringUtils.isEmpty(fullMediaId)) {
            chatBody.put(ORIGINAL_MEDIA_ID, fullMediaId);
        }

        chatBody.put(CONTENT, thumbnails);
        chatBody.put(THUMBNAIL_MEDIA_ID, thumbnailMediaId);

        if (isGif) {
            chatBody.put(IMAGE_TYPE, 1);
        } else {
            chatBody.put(IMAGE_TYPE, 0);

        }

        chatBody.put(WIDTH, info.width);
        chatBody.put(HEIGHT, info.height);
        chatBody.put(SIZE, info.size);
        if (!TextUtils.isEmpty(mOrgId)) {
            chatBody.put(ORG_ID, mOrgId);
        }

        if (isBurn()) {
            chatBody.put(BURN, mBurnInfo.getChatMapBody());
        }

        setBasicChatBody(chatBody);

        return chatBody;
    }

    @Override
    public ChatType getChatType() {
        return ChatType.Image;
    }

    @Override
    public String getSessionShowTitle() {
        if (isBurn()) {
            return StringConstants.SESSION_CONTENT_BURN_MESSAGE;
        }

        return StringConstants.SESSION_CONTENT_IMG;
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
    public void setFileStatus(FileStatus fileStatus) {
        this.fileStatus = fileStatus;
    }


    public void clearThumbnails() {
        setThumbnails(null);
    }

    public void setThumbnails(byte[] thumbnails) {
        this.thumbnails = thumbnails;
    }

    @Nullable
    public byte[] getThumbnails() {
        return this.thumbnails;
    }

    /**
     * 是否原图
     */
    public boolean hasFullImg() {
        return !StringUtils.isEmpty(fullMediaId);
    }

    public boolean isFullMode() {
        return !StringUtils.isEmpty(fullImgPath);
    }


    /**
     * 原图文件是否存在
     * */
    public boolean isFullImgExist() {
        return isFullMode() && FileUtil.isExist(fullImgPath);
    }

    @Override
    public String[] getMedias() {
        List<String> mediaList = new ArrayList<>();
        if(!StringUtils.isEmpty(mediaId)) {
            mediaList.add(mediaId);
        }

        if(!StringUtils.isEmpty(fullMediaId)) {
            mediaList.add(fullMediaId);
        }

        return mediaList.toArray(new String[0]);
    }

    public static class ImageInfo implements Serializable {
        @Expose
        public int height = -1;

        @Expose
        public int width = -1;

        @Expose
        public long size = -1;

        @Expose
        public String type;
    }

}
