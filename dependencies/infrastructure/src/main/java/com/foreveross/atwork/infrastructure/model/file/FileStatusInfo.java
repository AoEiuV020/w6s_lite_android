package com.foreveross.atwork.infrastructure.model.file;

import android.os.Parcelable;

import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileStatus;

/**
 * Created by dasunsy on 2017/9/18.
 */

public interface FileStatusInfo extends Parcelable {

    String getKeyId();

    String getName();

    String getPath();

    void setPath(String path);

    long getSize();

    FileStatus getFileStatus();

    void setFileStatus(FileStatus fileStatus);

    String getMediaId();

    int getProgress();

    void setProgress(int progress);

    FileData.FileType getFileType();

    boolean needActionMore();

    /**
     * 是否遵循 workplus 聊天文件 域设置规则
     * */
    boolean needW6sChatFileBehavior();

}
