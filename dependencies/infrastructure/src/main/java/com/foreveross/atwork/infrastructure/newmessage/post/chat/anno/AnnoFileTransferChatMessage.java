package com.foreveross.atwork.infrastructure.newmessage.post.chat.anno;

import android.content.Context;

import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.IAtContactMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AnnoFileTransferChatMessage extends FileTransferChatMessage implements IAtContactMessage {

    @Expose
    public String comment = "";


    //at人员 (at_contacts)
    public ArrayList<TextChatMessage.AtUser> mAtUserList;

    @Expose
    public boolean atAll;

    public AnnoFileTransferChatMessage() {
        deliveryTime = TimeUtil.getCurrentTimeInMillis();
        deliveryId = UUID.randomUUID().toString();
    }


    @Override
    public String getSearchAbleString() {
        return name + comment;
    }

    public static AnnoFileTransferChatMessage getFileTransferChatMessageFromJson(Map<String, Object> jsonMap) {

        AnnoFileTransferChatMessage fileTransferChatMessage = (AnnoFileTransferChatMessage) getFileTransferChatMessageFromJson(new AnnoFileTransferChatMessage(), jsonMap);
        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);

        fileTransferChatMessage.comment = ChatPostMessage.getString(bodyMap, COMMENT);

        if (bodyMap.containsKey(TextChatMessage.AT_ALL)) {
            fileTransferChatMessage.atAll = TextChatMessage.parseBoolean(String.valueOf(bodyMap.get(TextChatMessage.AT_ALL))) ;
        }


        if (bodyMap.containsKey(TextChatMessage.AT_CONTACTS)) {
            fileTransferChatMessage.mAtUserList = (ArrayList<TextChatMessage.AtUser>) bodyMap.get(TextChatMessage.AT_CONTACTS);

        }

        return fileTransferChatMessage;
    }

    @Override
    public Map<String, Object> getChatBody() {
        Map<String, Object> chatBody = super.getChatBody();
        chatBody.put(COMMENT, comment);
        chatBody.put(TextChatMessage.AT_CONTACTS, mAtUserList);
        chatBody.put(TextChatMessage.AT_ALL, atAll);

        return chatBody;
    }

    @Override
    public ChatType getChatType() {
        return ChatType.ANNO_FILE;
    }

    @Override
    public String getSessionShowTitle() {
        return comment;
    }


    public void setAtUsers(List<UserHandleInfo> handleInfoList) {
        mAtUserList = new ArrayList<>();
        for (UserHandleInfo handleInfo : handleInfoList) {
            mAtUserList.add(new TextChatMessage.AtUser(handleInfo.mUserId, handleInfo.mShowName));

        }
    }

    public void setAtAll(boolean atAll) {
        this.atAll = atAll;
    }

    @Override
    public String getContent() {
        return comment;
    }

    @Override
    public boolean containAtMe(Context context) {
        String meUserId = LoginUserInfo.getInstance().getLoginUserId(context);
        return containAtUser(meUserId);
    }

    public boolean containAtUser(String userId) {
        boolean result = false;
        if (null != mAtUserList) {
            try {
                for (int i = 0; i < mAtUserList.size(); i++) {
                    Object object = mAtUserList.get(i);
                    if (object instanceof TextChatMessage.AtUser) {
                        TextChatMessage.AtUser atUser = (TextChatMessage.AtUser) object;
                        if (userId.equals(atUser.mUserId)) {
                            result = true;
                            break;
                        }
                    }
                    if (object instanceof LinkedTreeMap) {
                        LinkedTreeMap<String, String> map = (LinkedTreeMap) object;
                        String mapUserId = map.get("user_id");
                        if (userId.equalsIgnoreCase(mapUserId)) {
                            result = true;
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        return result;
    }

    @Override
    public boolean isAtMe(Context context) {
        String meUserId = LoginUserInfo.getInstance().getLoginUserId(context);
        return (atAll && !meUserId.equals(from)) || (containAtUser(meUserId));
    }

    public static AnnoFileTransferChatMessage.Builder newBuilder() {
        return new AnnoFileTransferChatMessage.Builder();
    }

    public static final class Builder extends ChatPostMessage.Builder<AnnoFileTransferChatMessage.Builder>{

        private FileData.FileType fileType;

        private String name;

        private long size;

        private Long expiredTime = null;

        private String mediaId;

        private int progress;

        private long breakPoint;

        private String filePath;

        private String comment;

        private FileData fileData;


        @Override
        protected BodyType getBodyType() {
            return BodyType.AnnoFile;
        }


        public Builder setFileType(FileData.FileType fileType) {
            this.fileType = fileType;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setSize(long size) {
            this.size = size;
            return this;
        }

        public Builder setExpiredTime(long expiredTime) {
            this.expiredTime = expiredTime;
            return this;
        }

        public Builder setMediaId(String mediaId) {
            this.mediaId = mediaId;
            return this;
        }


        public Builder setFileData(FileData fileData) {
            this.fileData = fileData;
            return this;
        }

        public Builder setProgress(int progress) {
            this.progress = progress;
            return this;
        }

        public Builder setBreakPoint(long breakPoint) {
            this.breakPoint = breakPoint;
            return this;
        }

        public Builder setFilePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder setComment(String comment) {
            this.comment = comment;
            return this;
        }


        public AnnoFileTransferChatMessage build() {
            AnnoFileTransferChatMessage annoFileTransferChatMessage = new AnnoFileTransferChatMessage();


            super.assemble(annoFileTransferChatMessage);

            if(null != expiredTime) {
                annoFileTransferChatMessage.expiredTime = expiredTime;

            } else {
                long overtime = DomainSettingsManager.getInstance().handleChatFileExpiredFeature() ? TimeUtil.getTimeInMillisDaysAfter(DomainSettingsManager.getInstance().getChatFileExpiredDay()) : -1;
                annoFileTransferChatMessage.expiredTime = overtime;
            }


            annoFileTransferChatMessage.mediaId = mediaId;
            annoFileTransferChatMessage.comment = comment;
            annoFileTransferChatMessage.filePath = filePath;


            if(null != fileData) {
                annoFileTransferChatMessage.name = fileData.title;
                annoFileTransferChatMessage.size = fileData.size;
                annoFileTransferChatMessage.fileType = fileData.fileType;
                annoFileTransferChatMessage.filePath = fileData.filePath;
                annoFileTransferChatMessage.mediaId = fileData.getMediaId();

            } else {
                annoFileTransferChatMessage.name = name;
                annoFileTransferChatMessage.size = size;
                annoFileTransferChatMessage.fileType = fileType;
                annoFileTransferChatMessage.filePath = filePath;
                annoFileTransferChatMessage.mediaId = mediaId;
            }

            annoFileTransferChatMessage.fileStatus = FileStatus.SENDING;

            return annoFileTransferChatMessage;
        }
    }

}
