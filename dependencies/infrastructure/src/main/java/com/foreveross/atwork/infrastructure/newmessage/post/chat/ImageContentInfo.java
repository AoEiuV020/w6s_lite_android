package com.foreveross.atwork.infrastructure.newmessage.post.chat;

import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoImageChatMessage;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.google.gson.annotations.Expose;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ImageContentInfo implements Serializable {

    public ImageContentInfo() {
        deliveryId = UUID.randomUUID().toString();
//        deliveryTime  = TimeUtil.getCurrentTimeInMillis();

    }

    @Expose
    public String deliveryId;

    public long deliveryTime;

    @Expose
    public String mediaId;

    @Expose
    public String fullMediaId;

    @Expose
    public String thumbnailMediaId;

    @Expose
    public byte[] thumbnail;

    @Expose
    public int progress;

    @Expose
    public boolean isGif = false;

    @Expose
    public ImageChatMessage.ImageInfo info = new ImageChatMessage.ImageInfo();

    @Expose
    public String fullImgPath;

    @Expose
    public String filePath;


    @NotNull
    public static ImageContentInfo getImageContentInfoFromJsonMap(AnnoImageChatMessage parentMessage, Map<String, Object> contentMap) {
        ImageContentInfo imageContentInfo = new ImageContentInfo();
        imageContentInfo.deliveryTime = parentMessage.deliveryTime;
        imageContentInfo.mediaId = ChatPostMessage.getString(contentMap, ImageChatMessage.MEDIA_ID);
        imageContentInfo.fullMediaId = ChatPostMessage.getString(contentMap, ImageChatMessage.ORIGINAL_MEDIA_ID);
        imageContentInfo.thumbnailMediaId = ChatPostMessage.getString(contentMap, ImageChatMessage.THUMBNAIL_MEDIA_ID3);
        imageContentInfo.deliveryId = ChatPostMessage.getString(contentMap, ImageChatMessage.DELIVER_ID);
        try {
            imageContentInfo.isGif = (null != contentMap.get(ImageChatMessage.IMAGE_TYPE) && 1 == ((Double) contentMap.get(ImageChatMessage.IMAGE_TYPE)).intValue());
        } catch (Exception e) {
            e.printStackTrace();
        }

        imageContentInfo.info.width = ChatPostMessage.getInt(contentMap, ImageChatMessage.WIDTH);
        imageContentInfo.info.height = ChatPostMessage.getInt(contentMap, ImageChatMessage.HEIGHT);
        imageContentInfo.info.size = ChatPostMessage.getLong(contentMap, ImageChatMessage.SIZE);
        return imageContentInfo;
    }

    public Map<String, Object> getChatBody() {
        Map<String, Object> chatBody = new HashMap<>();

        if (!StringUtils.isEmpty(deliveryId)) {
            chatBody.put(ImageChatMessage.DELIVER_ID, deliveryId);
        }

        chatBody.put(ImageChatMessage.MEDIA_ID, mediaId);
        if(!StringUtils.isEmpty(fullMediaId)) {
            chatBody.put(ImageChatMessage.ORIGINAL_MEDIA_ID, fullMediaId);
        }



        chatBody.put(ImageChatMessage.THUMBNAIL_MEDIA_ID3, thumbnailMediaId);

        if (isGif) {
            chatBody.put(ImageChatMessage.IMAGE_TYPE, 1);
        } else {
            chatBody.put(ImageChatMessage.IMAGE_TYPE, 0);

        }

        chatBody.put(ImageChatMessage.WIDTH, info.width);
        chatBody.put(ImageChatMessage.HEIGHT, info.height);
        chatBody.put(ImageChatMessage.SIZE, info.size);

        return chatBody;
    }


    public ImageChatMessage transformImageChatMessage(AnnoImageChatMessage annoImageChatMessage) {
        ImageChatMessage imageChatMessage = new ImageChatMessage();
        imageChatMessage.deliveryId = deliveryId;
        imageChatMessage.deliveryTime = deliveryTime;
        imageChatMessage.mediaId = mediaId;
        imageChatMessage.fullMediaId = fullMediaId;
        imageChatMessage.fullImgPath = fullImgPath;

        imageChatMessage.thumbnailMediaId = thumbnailMediaId;
        imageChatMessage.setThumbnails(thumbnail);
        imageChatMessage.isGif = isGif;
        imageChatMessage.info = info;
//        imageChatMessage.from = annoImageChatMessage.from;
//        imageChatMessage.mFromDomain = annoImageChatMessage.mFromDomain;
        imageChatMessage.mBodyType = BodyType.Image;
        imageChatMessage.parentAnnoImageChatMessage = annoImageChatMessage;

        return imageChatMessage;

    }

}
