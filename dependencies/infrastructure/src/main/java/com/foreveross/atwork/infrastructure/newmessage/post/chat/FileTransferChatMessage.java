package com.foreveross.atwork.infrastructure.newmessage.post.chat;

import android.content.Context;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.model.file.FileStatusInfo;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.ReadStatus;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ChatSendType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.HasMediaChatPostMessage;
import com.foreveross.atwork.infrastructure.support.StringConstants;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.encryption.Base64Util;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by lingen on 15/4/16.
 * Description:
 */
public class FileTransferChatMessage extends HasMediaChatPostMessage implements HasFileStatus {

    public static final String NAME = "name";

    public static final String SIZE = "size";

    public static final String EXPIRE_TIME = "expired_time";

    public static final String THUMBNAIL = "thumbnail";

    public static final String THUMBNAIL_MEDIA_ID = "thumbnail_media_id";

    public static final String DROPBOX_FILE_ID = "dropbox_file_id";

    public static final String LOCAL_FILE_STATUS = "local_file_status";
    public static final String LOCAL_FILE_PATH = "local_file_path";

    @Expose
    public FileData.FileType fileType;

    /**
     * 文件名称
     */
    @Expose
    public String name;

    /**
     * 文件大小
     */
    @Expose
    public long size;

    /**
     * 过期时间
     */
    @Expose
    @SerializedName("expired_time")
    public long expiredTime = -1;

    /**
     * 文件缩略图
     */
    public byte[] thumbnail;

    @Expose
    public String mediaId;

    @Expose
    public String thumbnailMediaId;

    @Expose
    public FileStatus fileStatus;

    @Expose
    public int progress;

    @Expose
    public long breakPoint;

    @Expose
    public String filePath;

    public String tmpDownloadPath;

    @Expose
    public String dropboxFileId;

    public FileTransferChatMessage() {
        this.deliveryId = UUID.randomUUID().toString();
        this.deliveryTime = TimeUtil.getCurrentTimeInMillis();
    }

    public static FileTransferChatMessage getFileTransferChatMessageFromJson(Map<String, Object> jsonMap) {
        FileTransferChatMessage fileTransferChatMessage = new FileTransferChatMessage();
        return getFileTransferChatMessageFromJson(fileTransferChatMessage, jsonMap);
    }

    public static FileTransferChatMessage getFileTransferChatMessageFromJson(FileTransferChatMessage fileTransferChatMessage, Map<String, Object> jsonMap) {

        fileTransferChatMessage.initPostTypeMessageValue(jsonMap);
        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);

        fileTransferChatMessage.initChatTypeMessageValue(bodyMap);

        if (bodyMap.containsKey(MEDIA_ID)) {
            fileTransferChatMessage.mediaId = (String) bodyMap.get(MEDIA_ID);
        }

        if (bodyMap.containsKey(NAME)) {
            fileTransferChatMessage.name = (String) bodyMap.get(NAME);
        }
        if (bodyMap.containsKey(SIZE)) {
            fileTransferChatMessage.size = ((Double) bodyMap.get(SIZE)).longValue();
        }
        if (bodyMap.containsKey(EXPIRE_TIME)) {
            if ( bodyMap.get(EXPIRE_TIME) == null) {
                fileTransferChatMessage.expiredTime = -1;
            } else {
                fileTransferChatMessage.expiredTime = ((Double) bodyMap.get(EXPIRE_TIME)).longValue();
            }
        }
        if (bodyMap.get(THUMBNAIL) != null) {
            fileTransferChatMessage.thumbnail = Base64Util.decode((String) bodyMap.get(THUMBNAIL));
        }

        if (bodyMap.containsKey(SOURCE)) {
            fileTransferChatMessage.source = (String)bodyMap.get(SOURCE);
        }

        if (jsonMap.containsKey(LOCAL_FILE_STATUS)) {
            fileTransferChatMessage.fileStatus = FileStatus.valueOf((String)jsonMap.get(LOCAL_FILE_STATUS));

        } else {
            fileTransferChatMessage.fileStatus = FileStatus.NOT_DOWNLOAD;
        }

        if(jsonMap.containsKey(LOCAL_FILE_PATH)) {
            fileTransferChatMessage.filePath = (String)jsonMap.get(LOCAL_FILE_PATH);

        }

        fileTransferChatMessage.fileType = FileData.getFileType(fileTransferChatMessage.name);

        if(bodyMap.containsKey(ORG_ID)) {
            fileTransferChatMessage.mOrgId = (String) bodyMap.get(ORG_ID);

        }

        if (bodyMap.containsKey(DROPBOX_FILE_ID)) {
            fileTransferChatMessage.dropboxFileId = (String)bodyMap.get(DROPBOX_FILE_ID);
        }


        return fileTransferChatMessage;
    }

    public static FileTransferChatMessage getFileTransferChatMessageFromPath(String filePath) {
        FileData fileData = FileData.fromPath(filePath);
        return getFIleTransferChatMessageFromFileData(fileData);
    }

    @NonNull
    public static FileTransferChatMessage getFIleTransferChatMessageFromFileData(FileData fileData) {
        FileTransferChatMessage fileTransferChatMessage = new FileTransferChatMessage();

        fileTransferChatMessage.filePath = fileData.filePath;
        fileTransferChatMessage.size = fileData.size;
        fileTransferChatMessage.name = fileData.title;
        fileTransferChatMessage.fileType = fileData.fileType;
        fileTransferChatMessage.mBodyType = BodyType.File;
        fileTransferChatMessage.chatSendType = ChatSendType.SENDER;
        fileTransferChatMessage.chatStatus = ChatStatus.Sending;
        fileTransferChatMessage.fileStatus = FileStatus.SENDING;

        return fileTransferChatMessage;
    }


    public static FileTransferChatMessage getFileTransferChatMessageFromFileStatusInfo(FileStatusInfo fileStatusInfo) {
        FileTransferChatMessage fileTransferChatMessage = new FileTransferChatMessage();
        fileTransferChatMessage.mediaId = fileStatusInfo.getMediaId();
        if (0 < fileStatusInfo.getSize()) {
            fileTransferChatMessage.size = fileStatusInfo.getSize();
        } else {
            if(FileUtil.isExist(fileStatusInfo.getPath())) {
                fileTransferChatMessage.size = FileUtil.getSize(fileStatusInfo.getPath());
            }
        }
        fileTransferChatMessage.name = fileStatusInfo.getName();
        fileTransferChatMessage.fileType = fileStatusInfo.getFileType();
        fileTransferChatMessage.mBodyType = BodyType.File;
        fileTransferChatMessage.chatSendType = ChatSendType.SENDER;
        fileTransferChatMessage.chatStatus = ChatStatus.Sending;
        if (FileUtil.isExist(fileStatusInfo.getPath())) {
            fileTransferChatMessage.filePath = fileStatusInfo.getPath();
            fileTransferChatMessage.fileStatus = FileStatus.SENDING;
        }


        return fileTransferChatMessage;
    }


    /**
     * new一个文件传输的
     */
    public static FileTransferChatMessage newFileTransferChatMessage(Context context, FileData fileData, ShowListItem sender, String to,
                                                                     ParticipantType fromType, ParticipantType toType, String toDomain,
                                                                     String toAvatar, String toName,
                                                                     BodyType bodyType , String orgId, long expiredTime, String bingCreatorId) {
        FileTransferChatMessage fileTransferChatMessage = new FileTransferChatMessage();
        fileTransferChatMessage.buildSenderInfo(context);
//        fileTransferChatMessage.from = sender.getId();
//        fileTransferChatMessage.mMyAvatar = sender.getAvatar();
//        fileTransferChatMessage.mMyName = sender.getTitle();
        fileTransferChatMessage.to = to;
        fileTransferChatMessage.mToType = toType;
        fileTransferChatMessage.chatSendType = ChatSendType.SENDER;
        fileTransferChatMessage.chatStatus = ChatStatus.Sending;
        fileTransferChatMessage.name = fileData.title;
        fileTransferChatMessage.size = fileData.size;
        fileTransferChatMessage.fileType = fileData.fileType;
        fileTransferChatMessage.fileStatus = FileStatus.SENDING;
        fileTransferChatMessage.filePath = fileData.filePath;
//        fileTransferChatMessage.mFromType = fromType;
        fileTransferChatMessage.expiredTime = expiredTime;
        fileTransferChatMessage.read = ReadStatus.AbsolutelyRead;
        fileTransferChatMessage.mBodyType = bodyType;
        fileTransferChatMessage.mToDomain = toDomain;
        fileTransferChatMessage.mOrgId = orgId;
        fileTransferChatMessage.mDisplayAvatar = toAvatar;
        fileTransferChatMessage.mDisplayName = toName;
        fileTransferChatMessage.mediaId = fileData.getMediaId();
        fileTransferChatMessage.mBingCreatorId = bingCreatorId;
        return fileTransferChatMessage;
    }

    public static FileTransferChatMessage newFileTransferChatMessage(Context context, FileData fileData, ShowListItem sender, String to,
                                                                     ParticipantType fromType, ParticipantType toType, String toDomain,
                                                                     BodyType bodyType , String orgId, ShowListItem chatItem, long expiredTime, String bingCreatorId) {
        String toAvatar = "";
        String toName = "";
        if(null != chatItem) {
            toAvatar = chatItem.getAvatar();
            toName = chatItem.getTitle();
        }

        return newFileTransferChatMessage(context, fileData, sender, to, fromType, toType, toDomain, toAvatar, toName, bodyType, orgId, expiredTime, bingCreatorId);
    }

    @Override
    public void reGenerate(Context context, String senderId, String receiverId, String receiverDomainId, ParticipantType fromType,
                           ParticipantType toType, BodyType bodyType, String orgId, ShowListItem showListItem,String myName, String myAvatar) {
        byte[] thumbnail = ImageShowHelper.getOriginalImage(context, deliveryId);
        super.reGenerate(context, senderId, receiverId, receiverDomainId, fromType, toType, bodyType, orgId, showListItem,myName,myAvatar);
        if (0 != thumbnail.length) {
            ImageShowHelper.saveOriginalImage(context, deliveryId, thumbnail);
        }
    }

    @Override
    public Map<String, Object> getChatBody() {
        Map<String, Object> chatBody = new HashMap<>();
        chatBody.put(NAME, name);
        chatBody.put(SIZE, size);
        chatBody.put(EXPIRE_TIME, expiredTime);
        chatBody.put(THUMBNAIL, thumbnail);
        chatBody.put(THUMBNAIL_MEDIA_ID,thumbnailMediaId);
        chatBody.put(MEDIA_ID, mediaId);

        if (!TextUtils.isEmpty(mOrgId)) {
            chatBody.put(ORG_ID, mOrgId);
        }

        chatBody.put(DROPBOX_FILE_ID, dropboxFileId);

        setBasicChatBody(chatBody);

        return chatBody;
    }

    @Override
    public ChatType getChatType() {
        return ChatType.File;
    }

    @Override
    public String getSessionShowTitle() {
        return StringConstants.SESSION_CONTENT_FILE + name;
    }

    @Override
    public String getSearchAbleString() {
        return name;
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

    /**
     * 是否静态图片类型
     * */
    public boolean isStaticImgType() {
        return FileData.FileType.File_Image.equals(fileType);
    }

    /**
     * 是否gif 类型
     * */
    public boolean isGifType() {
        return FileData.FileType.File_Gif.equals(fileType);
    }

    @Override
    public String[] getMedias() {
        if (!StringUtils.isEmpty(mediaId)) {
            return new String[]{mediaId};
        }

        return new String[0];
    }
}
