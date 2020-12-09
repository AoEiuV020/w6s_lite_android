package com.foreveross.atwork.modules.chat.util;

import android.text.TextUtils;

import com.foreverht.db.service.daoService.FileDaoService;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.model.file.ImageItem;
import com.foreveross.atwork.infrastructure.model.file.MediaItem;
import com.foreveross.atwork.infrastructure.model.file.SDCardFileData;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;

/**
 * Created by dasunsy on 15/12/20.
 */
public class FileDataUtil {

    /**
     * 转换ImageItem对象为FileData对象
     *
     * @param imageItem
     * @return
     */
    public static FileData convertImageItem2FileData(MediaItem imageItem) {
        FileData fileData = new FileData();
        fileData.isSelect = imageItem.isSelected;
        fileData.filePath = imageItem.filePath;
        fileData.title = imageItem.title;
        fileData.size = imageItem.size;
        fileData.identifier = imageItem.identifier;
        fileData.thumbnailPath = imageItem.thumbnailPath;
        fileData.fileType = FileData.FileType.File_Image;
        return fileData;
    }

    /**
     * 转换FileData对象为ImageItem对象
     *
     * @param fileData
     * @return
     */
    public static MediaItem convertFileData2ImageItem(FileData fileData) {
        MediaItem imageItem = new ImageItem();
        imageItem.isSelected = fileData.isSelect;
        imageItem.filePath = fileData.filePath;
        imageItem.thumbnailPath = fileData.thumbnailPath;
        imageItem.identifier = fileData.identifier;
        imageItem.size = fileData.size;
        imageItem.title = fileData.title;
        return imageItem;
    }

    public static void updateRecentFileDB(FileTransferChatMessage message) {
        FileData fileData = null;
        if (TextUtils.isEmpty(message.filePath)) {
            fileData = new FileData();
            fileData.mediaId = message.mediaId;
            fileData.fileType = message.fileType;
            fileData.size = message.size;
            fileData.title = message.name;
        } else {
            SDCardFileData.FileInfo fileInfo = new SDCardFileData.FileInfo(message.filePath);
            fileData = fileInfo.getFileData(fileInfo);
        }

        if (fileData == null) {
            return;
        }
        fileData.from = message.from;
        fileData.to = message.to;
        fileData.mediaId = message.mediaId;
        FileDaoService.getInstance().insertRecentFile(fileData);
    }


}
