package com.foreveross.atwork.infrastructure.model.dropbox;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.model.user.LoginUserBasic;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MicroVideoChatMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.spare.pinyin.HanziToPinyin;

import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.UUID;

/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 * 网盘对象
 * Created by reyzhang22 on 16/9/7.
 */
public class Dropbox implements Parcelable, Comparable {

    public String mFileId;

    public String mSourceId;

    public SourceType mSourceType = SourceType.User;

    public String mDomainId;

    public String mMediaId;

    public String mThumbnailMediaId;

    public String mThumbnail;

    public DropboxFileType mFileType = DropboxFileType.Other;

    public boolean mIsDir;

    public String mRootId;

    public String mParentId = "";

    public long mCreateTime;

    public long mLastModifyTime;

    public long mExpiredTime;

    public String mLocalPath;

    public String mFileName;

    public long mFileSize;

    public String mOwnerId;

    public String mOwnerName;

    public DownloadStatus mDownloadStatus = DownloadStatus.Not_Download;

    public UploadStatus mUploadStatus = UploadStatus.Not_Upload;

    public long mDownloadBreakPoint;

    public long mUploadBreakPoint;

    public String mExtension;

    public String mPinyin = "";

    public String mInitial;

    public State mState = State.Normal;

    public String mOperatorId;

    public String mOperatorName;

    public boolean mIsTimeline = false;

    public boolean mIsOverdueReport = false;

    public static Dropbox parser(JSONObject json) {
        if (json == null){
            return null;
        }
        Dropbox dropbox = new Dropbox();
        dropbox.mFileId = json.optString("id");
        dropbox.mSourceId = json.optString("source_id");
        dropbox.mSourceType = SourceType.valueOfString(json.optString("source_type"));
        dropbox.mParentId = json.optString("parent");
        dropbox.mDomainId = json.optString("domain_id");
        dropbox.mIsDir = "DIRECTORY".equalsIgnoreCase(json.optString("type"));
        dropbox.mState = State.valueOfString(json.optString("state"));
        dropbox.mFileName = json.optString("display_name");
        dropbox.mExtension = json.optString("extension");
        dropbox.mPinyin = json.optString("pinyin");
        dropbox.mInitial = json.optString("initial");
        dropbox.mFileSize = json.optLong("size");
        dropbox.mFileType = DropboxFileType.valueOfString(json.optString("file_type"));
        JSONObject owner = json.optJSONObject("owner");
        if (owner != null) {
            dropbox.mOwnerId = owner.optString("user_id");
            dropbox.mOwnerName = owner.optString("name");
        }
        JSONObject operator = json.optJSONObject("operator");
        if (operator != null) {
            dropbox.mOperatorId = operator.optString("user_id");
            dropbox.mOperatorName = operator.optString("name");
        }
        dropbox.mCreateTime = json.optLong("create_time");
        dropbox.mLastModifyTime = json.optLong("modify_time");
        dropbox.mExpiredTime = json.optLong("expire_time");
        dropbox.mMediaId = json.optString("file_id");
        dropbox.mThumbnailMediaId = json.optString("thumbnail");
        return dropbox;
    }

    public static Dropbox convertFromChatPostMessage(Context context, ChatPostMessage message) {
        Dropbox dropbox = new Dropbox();
        if (message instanceof FileTransferChatMessage) {
            FileTransferChatMessage fileTransferChatMessage = (FileTransferChatMessage) message;
            convertFromFileChatMessage(context, fileTransferChatMessage, dropbox);

        } else if(message instanceof ImageChatMessage) {
            ImageChatMessage imageChatMessage = (ImageChatMessage) message;
            convertFromImageChatMessage(context, dropbox, imageChatMessage);

        } else if(message instanceof MicroVideoChatMessage) {
            MicroVideoChatMessage microVideoChatMessage = (MicroVideoChatMessage) message;
            convertFromMicroVideoMessage(context, dropbox, microVideoChatMessage);
        }

        return dropbox;
    }

    private static void convertFromMicroVideoMessage(Context context, Dropbox dropbox, MicroVideoChatMessage microVideoChatMessage) {
        dropbox.mFileId = microVideoChatMessage.mediaId;
        dropbox.mMediaId = microVideoChatMessage.mediaId;
        dropbox.mIsDir = false;
        LoginUserBasic basic = LoginUserInfo.getInstance().getLoginUserBasic(context);
        dropbox.mSourceId = basic.mUserId;
        dropbox.mSourceType = SourceType.User;
        dropbox.mDomainId = AtworkConfig.DOMAIN_ID;
        dropbox.mFileName = microVideoChatMessage.mediaId + ".mp4";

        dropbox.mFileType = DropboxFileType.Image;
        dropbox.mLocalPath = microVideoChatMessage.filePath;
        if (FileUtil.isExist(microVideoChatMessage.filePath)) {
            dropbox.mFileSize = FileUtil.getSize(microVideoChatMessage.filePath);
        }

        dropbox.mOwnerName = basic.mName;
        dropbox.mOwnerId = LoginUserInfo.getInstance().getLoginUserId(context);
        dropbox.mUploadStatus = UploadStatus.Uploaded;
        dropbox.mCreateTime = System.currentTimeMillis();
        dropbox.mLastModifyTime = System.currentTimeMillis();

        dropbox.mExtension = "mp4";


    }

    private static void convertFromImageChatMessage(Context context, Dropbox dropbox, ImageChatMessage imageChatMessage) {
        dropbox.mFileId = imageChatMessage.mediaId;
        dropbox.mMediaId = imageChatMessage.mediaId;
        dropbox.mIsDir = false;
        LoginUserBasic basic = LoginUserInfo.getInstance().getLoginUserBasic(context);
        dropbox.mSourceId = basic.mUserId;
        dropbox.mSourceType = SourceType.User;
        dropbox.mDomainId = AtworkConfig.DOMAIN_ID;
        if(!StringUtils.isEmpty(imageChatMessage.filePath) && FileUtil.isExist(imageChatMessage.filePath)) {
            dropbox.mFileName = FileUtil.getName(imageChatMessage.filePath);
        } else {

            if (!StringUtils.isEmpty(imageChatMessage.mediaId)) {
                if(imageChatMessage.isGif) {
                    dropbox.mFileName = imageChatMessage.mediaId + ".gif";

                } else {
                    dropbox.mFileName = imageChatMessage.mediaId + ".jpg";

                }
            }

        }

        dropbox.mFileType = DropboxFileType.Image;
        if(0 < imageChatMessage.info.size) {
            dropbox.mFileSize = imageChatMessage.info.size;
        } else {
            if(FileUtil.isExist(imageChatMessage.filePath)) {
                dropbox.mFileSize = FileUtil.getFileDicSize(imageChatMessage.filePath);
            }
        }

        dropbox.mLocalPath = ImageShowHelper.getChatMsgImgPath(context, imageChatMessage);
        dropbox.mOwnerName = basic.mName;
        dropbox.mOwnerId = LoginUserInfo.getInstance().getLoginUserId(context);
        dropbox.mUploadStatus = UploadStatus.Uploaded;
        dropbox.mCreateTime = System.currentTimeMillis();
        dropbox.mLastModifyTime = System.currentTimeMillis();
//            dropbox.mPinyin = HanziToPinyin.getPinyin(fileTransferChatMessage.name);

        if(imageChatMessage.isGif) {
            dropbox.mExtension = "gif";

        } else {
            dropbox.mExtension = "jpg";

        }
    }

    private static void convertFromFileChatMessage(Context context, FileTransferChatMessage message, Dropbox dropbox) {
        FileTransferChatMessage fileTransferChatMessage = message;
        dropbox.mFileId = fileTransferChatMessage.mediaId;
        dropbox.mMediaId = fileTransferChatMessage.mediaId;
        dropbox.mIsDir = false;
        LoginUserBasic basic = LoginUserInfo.getInstance().getLoginUserBasic(context);
        dropbox.mSourceId = basic.mUserId;
        dropbox.mSourceType = SourceType.User;
        dropbox.mDomainId = AtworkConfig.DOMAIN_ID;
        dropbox.mFileName = fileTransferChatMessage.name;
        dropbox.mFileType = DropboxFileType.covertFromFileType(fileTransferChatMessage.fileType);
        if (0 < fileTransferChatMessage.size) {
            dropbox.mFileSize = fileTransferChatMessage.size;
        } else {
            if(FileUtil.isExist(fileTransferChatMessage.filePath)) {
                dropbox.mFileSize = FileUtil.getFileDicSize(fileTransferChatMessage.filePath);
            }
        }
        dropbox.mLocalPath = fileTransferChatMessage.filePath;
        dropbox.mOwnerName = basic.mName;
        dropbox.mOwnerId = LoginUserInfo.getInstance().getLoginUserId(context);
        dropbox.mUploadStatus = UploadStatus.Uploaded;
        dropbox.mCreateTime = System.currentTimeMillis();
        dropbox.mLastModifyTime = System.currentTimeMillis();
        dropbox.mPinyin = HanziToPinyin.getPinyin(fileTransferChatMessage.name);
        String suffix = "";
        if (!TextUtils.isEmpty(fileTransferChatMessage.name) && fileTransferChatMessage.name.contains(".")) {
            suffix = fileTransferChatMessage.name.substring(fileTransferChatMessage.name.lastIndexOf("."));
        }

        dropbox.mExtension = suffix;
    }

    public static Dropbox convertFromFileData(Context context,FileData fileData, String sourceId, SourceType sourceType, String parendId) {
        Dropbox dropbox = new Dropbox();
        dropbox.mFileId = UUID.randomUUID().toString();
        dropbox.mIsDir = fileData.isDir;
        dropbox.mDomainId = AtworkConfig.DOMAIN_ID;

        dropbox.mFileSize = fileData.size;
        dropbox.mFileType = DropboxFileType.covertFromFileType(fileData.fileType);
        dropbox.mLocalPath = fileData.filePath;
        File file = new File(fileData.filePath);
        if (file.exists()) {
            dropbox.mFileName = file.getName();
        } else {
            dropbox.mFileName = fileData.title;
        }
        LoginUserBasic basic = LoginUserInfo.getInstance().getLoginUserBasic(context);
        dropbox.mOwnerName = basic.mName;
        dropbox.mOwnerId = LoginUserInfo.getInstance().getLoginUserId(context);
        dropbox.mSourceId = sourceId;
        dropbox.mSourceType = sourceType;
        dropbox.mUploadStatus = UploadStatus.Uploading;
        dropbox.mCreateTime = System.currentTimeMillis();
        dropbox.mLastModifyTime = System.currentTimeMillis();
        dropbox.mParentId = parendId;
        dropbox.mPinyin = HanziToPinyin.getPinyin(fileData.title);
        String suffix = "";
        if (!TextUtils.isEmpty(fileData.title) && fileData.title.contains(".")) {
            suffix = fileData.title.substring(fileData.title.lastIndexOf("."));
        }
        dropbox.mExtension = suffix;
        dropbox.mMediaId = fileData.getMediaId();
        return dropbox;

    }

    public static Dropbox convertFromFilePath(Context context, String filePath, String mediaId) {
        Dropbox dropbox = new Dropbox();
        File file = new File(filePath);
        LoginUserBasic me = LoginUserInfo.getInstance().getLoginUserBasic(context);

        dropbox.mFileId = UUID.randomUUID().toString();
        dropbox.mIsDir = false;
        dropbox.mDomainId = AtworkConfig.DOMAIN_ID;
        dropbox.mFileName = file.getName();
        dropbox.mFileSize = file.length();
        dropbox.mFileType = DropboxFileType.covertFromFileType(FileData.getFileType(filePath));
        dropbox.mLocalPath = filePath;
        dropbox.mOwnerName = me.mName;
        dropbox.mOwnerId = me.mUserId;
        dropbox.mSourceId = me.mUserId;
        dropbox.mSourceType = SourceType.User;
        dropbox.mUploadStatus = UploadStatus.Uploaded;
        dropbox.mCreateTime = System.currentTimeMillis();
        dropbox.mLastModifyTime = System.currentTimeMillis();
        dropbox.mPinyin = HanziToPinyin.getPinyin(file.getName());
        String suffix = "";
        if (!TextUtils.isEmpty(file.getName()) && file.getName().contains(".")) {
            suffix = file.getName().substring(file.getName().lastIndexOf("."));
        }
        dropbox.mExtension = suffix;
        dropbox.mMediaId = mediaId;

        return dropbox;
    }

    @Override
    public int compareTo(Object another) {
        Dropbox dropbox = (Dropbox)another;
        if (dropbox == null) {
            return 0;
        }

        if(this.mPinyin.compareToIgnoreCase(dropbox.mPinyin) > 0) {
            return 1;
        }else if(this.mPinyin.compareToIgnoreCase(dropbox.mPinyin) < 0) {
            return -1;
        }else{
            return 0;
        }
    }


    public enum SourceType implements Serializable {
        User {
            @Override
            public int valueOfInt() {
                return 0;
            }

            @Override
            public String toString() {
                return "users";
            }

            @Override
            public String simpleString() {
                return "user";
            }


        },
        Organization {
            @Override
            public int valueOfInt() {
                return 1;
            }

            @Override
            public String toString() {
                return "orgs";
            }

            @Override
            public String simpleString() {
                return "org";
            }
        },
        Discussion {
            @Override
            public int valueOfInt() {
                return 2;
            }

            @Override
            public String toString() {
                return "discussions";
            }

            @Override
            public String simpleString() {
                return "discussion";
            }
        };

        public abstract int valueOfInt();

        public abstract String toString();

        public abstract String simpleString();

        public static SourceType valueOf(int value) {
            switch (value) {
                case 0:
                    return User;
                case 1:
                    return Organization;
                case 2:
                    return Discussion;
            }
            return User;
        }

        public static SourceType valueOfString(String value) {
            if ("USER".equalsIgnoreCase(value)) {
                return User;
            }
            if ("DISCUSSION".equalsIgnoreCase(value)) {
                return Discussion;
            }
            if ("ORG".equalsIgnoreCase(value)) {
                return Organization;
            }
            return User;
        }


    }

    public enum DropboxFileType implements Serializable {
        Other {
            @Override
            public int valueOfInt() {
                return 0;
            }
        },
        File {
            @Override
            public int valueOfInt() {
                return 1;
            }
        },
        Archive {
            @Override
            public int valueOfInt() {
                return 2;
            }
        },
        Image {
            @Override
            public int valueOfInt() {
                return 3;
            }
        },
        Video {
            @Override
            public int valueOfInt() {
                return 4;
            }
        },
        Audio {
            @Override
            public int valueOfInt() {
                return 5;
            }
        },
        Application {
            @Override
            public int valueOfInt() {
                return 6;
            }
        };

        public abstract int valueOfInt();

        public static DropboxFileType valueOf(int value) {
            switch (value) {
                case 0:
                    return Other;
                case 1:
                    return File;
                case 2:
                    return Archive;
                case 3:
                    return Image;
                case 4:
                    return Video;
                case 5:
                    return Audio;
                case 6:
                    return Application;
            }
            return Other;
        }

        public static DropboxFileType valueOfString(String value) {
            if ("OTHER".equalsIgnoreCase(value)) {
                return Other;
            }
            if ("FILE".equalsIgnoreCase(value) || "TEXT".equalsIgnoreCase(value)) {
                return File;
            }
            if ("ARCHIVE".equalsIgnoreCase(value)) {
                return Archive;
            }
            if ("IMAGE".equalsIgnoreCase(value)) {
                return Image;
            }
            if ("VIDEO".equalsIgnoreCase(value)) {
                return Video;
            }
            if ("AUDIO".equalsIgnoreCase(value)) {
                return Audio;
            }
            if ("APPLICATION".equalsIgnoreCase(value)) {
                return Application;
            }
            return Other;
        }



        public static DropboxFileType covertFromFileType(FileData.FileType fileType) {
            switch (fileType) {
                case File_APK:
                    return Application;

                case File_Audio:
                    return Audio;

                case File_Excel:
                case File_HTML:
                case File_Pdf:
                case File_Ppt:
                case File_Txt:
                case File_Word:
                    return File;

                case File_RAR:
                    return Archive;

                case File_Video:
                    return Video;

                case File_Image:
                    return Image;

                case File_Unknown:
                    return Other;
            }
            return Other;
        }

    }

    public enum DownloadStatus implements Serializable{
        Not_Download {
            @Override
            public int valueOfInt() {
                return 0;
            }
        },
        Downloaded {
            @Override
            public int valueOfInt() {
                return 1;
            }
        },
        Downloading {
            @Override
            public int valueOfInt() {
                return 2;
            }
        },
        Pause {
            @Override
            public int valueOfInt() {
                return 3;
            }
        },
        Fail {
            @Override
            public int valueOfInt() {
                return 4;
            }
        };

        public abstract int valueOfInt();

        public static DownloadStatus valueOf(int value) {
            switch (value) {
                case 0:
                    return Not_Download;
                case 1:
                    return Downloaded;
                case 2:
                    return Downloading;
                case 3:
                    return Pause;
                case 4:
                    return Fail;
            }
            return Not_Download;
        }
    }

    public enum UploadStatus implements Serializable {
        Not_Upload {
            @Override
            public int valueOfInt() {
                return 0;
            }
        },
        Uploaded {
            @Override
            public int valueOfInt() {
                return 1;
            }
        },
        Uploading {
            @Override
            public int valueOfInt() {
                return 2;
            }
        },
        Pause {
            @Override
            public int valueOfInt() {
                return 3;
            }
        },
        Fail {
            @Override
            public int valueOfInt() {
                return 4;
            }
        };

        public abstract int valueOfInt();

        public static UploadStatus valueOf(int value) {
            switch (value) {
                case 0:
                    return Not_Upload;
                case 1:
                    return Uploaded;
                case 2:
                    return Uploading;
                case 3:
                    return Pause;
                case 4:
                    return Fail;
            }
            return Not_Upload;
        }
    }

    public enum State implements Serializable {
        Normal {
            @Override
            public int valueOfInt() {
                return 0;
            }
        },
        Uploading {
            @Override
            public int valueOfInt() {
                return 1;
            }
        },
        Trashed {
            @Override
            public int valueOfInt() {
                return 2;
            }
        },
        Expired {
            @Override
            public int valueOfInt() {
                return 3;
            }
        },
        Removed {
            @Override
            public int valueOfInt() {
                return 4;
            }
        };

        public abstract int valueOfInt();

        public static State valueOf(int value) {
            switch (value) {
                case 0:
                    return Normal;

                case 1:
                    return Uploading;

                case 2:
                    return Trashed;

                case 3:
                    return Expired;

                case 4:
                    return Removed;
            }
            return Normal;
        }

        public static State valueOfString(String value) {
            if ("NORMAL".equalsIgnoreCase(value)) {
                return Normal;
            }
            if ("UPLOADING".equalsIgnoreCase(value)) {
                return Uploading;
            }
            if ("TRASHED".equalsIgnoreCase(value)) {
                return Trashed;
            }
            if ("EXPIRED".equalsIgnoreCase(value)) {
                return Expired;
            }
            if ("REMOVED".equalsIgnoreCase(value)) {
                return Removed;
            }
            return Normal;
        }


    }


    public Dropbox() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mFileId);
        dest.writeString(this.mSourceId);
        dest.writeInt(this.mSourceType == null ? -1 : this.mSourceType.ordinal());
        dest.writeString(this.mDomainId);
        dest.writeString(this.mMediaId);
        dest.writeString(this.mThumbnailMediaId);
        dest.writeString(this.mThumbnail);
        dest.writeInt(this.mFileType == null ? -1 : this.mFileType.ordinal());
        dest.writeByte(this.mIsDir ? (byte) 1 : (byte) 0);
        dest.writeString(this.mRootId);
        dest.writeString(this.mParentId);
        dest.writeLong(this.mCreateTime);
        dest.writeLong(this.mLastModifyTime);
        dest.writeLong(this.mExpiredTime);
        dest.writeString(this.mLocalPath);
        dest.writeString(this.mFileName);
        dest.writeLong(this.mFileSize);
        dest.writeString(this.mOwnerId);
        dest.writeString(this.mOwnerName);
        dest.writeInt(this.mDownloadStatus == null ? -1 : this.mDownloadStatus.ordinal());
        dest.writeInt(this.mUploadStatus == null ? -1 : this.mUploadStatus.ordinal());
        dest.writeLong(this.mDownloadBreakPoint);
        dest.writeLong(this.mUploadBreakPoint);
        dest.writeString(this.mExtension);
        dest.writeString(this.mPinyin);
        dest.writeString(this.mInitial);
        dest.writeInt(this.mState == null ? -1 : this.mState.ordinal());
        dest.writeString(this.mOperatorId);
        dest.writeString(this.mOperatorName);
        dest.writeByte(this.mIsTimeline ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mIsOverdueReport ? (byte) 1 : (byte) 0);
    }

    protected Dropbox(Parcel in) {
        this.mFileId = in.readString();
        this.mSourceId = in.readString();
        int tmpMSourceType = in.readInt();
        this.mSourceType = tmpMSourceType == -1 ? null : SourceType.values()[tmpMSourceType];
        this.mDomainId = in.readString();
        this.mMediaId = in.readString();
        this.mThumbnailMediaId = in.readString();
        this.mThumbnail = in.readString();
        int tmpMFileType = in.readInt();
        this.mFileType = tmpMFileType == -1 ? null : DropboxFileType.values()[tmpMFileType];
        this.mIsDir = in.readByte() != 0;
        this.mRootId = in.readString();
        this.mParentId = in.readString();
        this.mCreateTime = in.readLong();
        this.mLastModifyTime = in.readLong();
        this.mExpiredTime = in.readLong();
        this.mLocalPath = in.readString();
        this.mFileName = in.readString();
        this.mFileSize = in.readLong();
        this.mOwnerId = in.readString();
        this.mOwnerName = in.readString();
        int tmpMDownloadStatus = in.readInt();
        this.mDownloadStatus = tmpMDownloadStatus == -1 ? null : DownloadStatus.values()[tmpMDownloadStatus];
        int tmpMUploadStatus = in.readInt();
        this.mUploadStatus = tmpMUploadStatus == -1 ? null : UploadStatus.values()[tmpMUploadStatus];
        this.mDownloadBreakPoint = in.readLong();
        this.mUploadBreakPoint = in.readLong();
        this.mExtension = in.readString();
        this.mPinyin = in.readString();
        this.mInitial = in.readString();
        int tmpMState = in.readInt();
        this.mState = tmpMState == -1 ? null : State.values()[tmpMState];
        this.mOperatorId = in.readString();
        this.mOperatorName = in.readString();
        this.mIsTimeline = in.readByte() != 0;
        this.mIsOverdueReport = in.readByte() != 0;
    }

    public static final Creator<Dropbox> CREATOR = new Creator<Dropbox>() {
        @Override
        public Dropbox createFromParcel(Parcel source) {
            return new Dropbox(source);
        }

        @Override
        public Dropbox[] newArray(int size) {
            return new Dropbox[size];
        }
    };
}
