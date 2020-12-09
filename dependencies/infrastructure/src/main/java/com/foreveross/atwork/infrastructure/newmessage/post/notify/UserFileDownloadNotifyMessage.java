package com.foreveross.atwork.infrastructure.newmessage.post.notify;

import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.NotifyPostMessage;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserFileDownloadNotifyMessage extends NotifyPostMessage {

    private final static String FILE_NAME = "file_name";

    private final static String FILE_SIZE = "file_size";

    private final static String PLATFORM = "platform";

    @Expose
    public Operation mOperation;

    @Expose
    public String mFileName;

    @Expose
    public long mFileSize;

    @Expose
    public String mPlatform;


    public boolean isMobile() {
        return "android".equalsIgnoreCase(mPlatform)
                || "ios".equalsIgnoreCase(mPlatform);
    }

    public boolean isPc() {
        return "pc".equalsIgnoreCase(mPlatform);
    }




    public static UserFileDownloadNotifyMessage getFileDownloadNotifyMessageFromJson(Map<String, Object> jsonMap) {
        UserFileDownloadNotifyMessage fileDownloadNotifyMessage = new UserFileDownloadNotifyMessage();
        fileDownloadNotifyMessage.initPostTypeMessageValue(jsonMap);
        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);
        fileDownloadNotifyMessage.mOperation = Operation.fromStringValue(ChatPostMessage.getString(bodyMap, OPERATION));
        fileDownloadNotifyMessage.mFileName = ChatPostMessage.getString(bodyMap, FILE_NAME);
        fileDownloadNotifyMessage.mFileSize = ChatPostMessage.getLong(bodyMap, FILE_SIZE);
        fileDownloadNotifyMessage.mPlatform = ChatPostMessage.getString(bodyMap, PLATFORM);


        return fileDownloadNotifyMessage;
    }

    @Override
    public Map<String, Object> getChatBody() {
        Map<String, Object> chatBody = new HashMap<>();
        chatBody.put(FILE_NAME, mFileName);
        chatBody.put(FILE_SIZE, mFileSize);
        chatBody.put(OPERATION, mOperation);
        chatBody.put(PLATFORM, mPlatform);

        setBasicChatBody(chatBody);
        return chatBody;
    }

    public static UserFileDownloadNotifyMessage.Builder newBuilder() {
        return new UserFileDownloadNotifyMessage.Builder();
    }


    public enum Operation {

        /**
         * 文件下载成功
         * */
        FILE_DOWNLOAD_SUCCESS,


        UNKNOWN;


        public static Operation fromStringValue(String operation) {
            if ("FILE_DOWNLOAD_SUCCESS".equalsIgnoreCase(operation)) {
                return FILE_DOWNLOAD_SUCCESS;
            }

            return UNKNOWN;
        }
    }


    public static final class Builder extends NotifyPostMessage.Builder<Builder>{
        private Operation mOperation;
        private String mFileName;
        private long mFileSize;
        private String mPlatform = "android";

        private Builder() {

        }

        public Builder setOperation(Operation operation) {
            this.mOperation = operation;
            return this;
        }

        public Builder setFileName(String fileName) {
            this.mFileName = fileName;
            return this;
        }

        public Builder setFileSize(long fileSize) {
            this.mFileSize = fileSize;
            return this;
        }

        public Builder setPlatform(String platform) {
            mPlatform = platform;
            return this;
        }

        public UserFileDownloadNotifyMessage build() {

            UserFileDownloadNotifyMessage userFileDownloadNotifyMessage = new UserFileDownloadNotifyMessage();
            super.assemble(userFileDownloadNotifyMessage);

            userFileDownloadNotifyMessage.deliveryId = UUID.randomUUID().toString();
            userFileDownloadNotifyMessage.deliveryTime = TimeUtil.getCurrentTimeInMillis();

            userFileDownloadNotifyMessage.mBodyType = BodyType.Notice;
            userFileDownloadNotifyMessage.mOperation = mOperation;
            userFileDownloadNotifyMessage.mFileName = mFileName;
            userFileDownloadNotifyMessage.mFileSize = mFileSize;
            userFileDownloadNotifyMessage.mPlatform = mPlatform;

            return userFileDownloadNotifyMessage;
        }
    }
}
