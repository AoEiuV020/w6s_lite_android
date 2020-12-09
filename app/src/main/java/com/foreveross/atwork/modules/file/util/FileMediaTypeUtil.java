package com.foreveross.atwork.modules.file.util;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;

/**
 * Created by dasunsy on 15/10/23.
 */
public class FileMediaTypeUtil {
    /**
     * 获取图标
     * @see #getFileTypeIcon(FileData.FileType)
     *
     * @param fileTransferChatMessage
     * @return mipmap
    item_dialog_listview*/
    public static int getFileTypeIcon(FileTransferChatMessage fileTransferChatMessage) {

        fileTransferChatMessage.fileType = FileData.getFileType(fileTransferChatMessage.name);

        return getFileTypeIcon(fileTransferChatMessage.fileType);
    }

    /**
     * 获取图标
     * @param fileType
     * @return mipmap
     */
    public static int getFileTypeIcon(FileData.FileType fileType) {
        if (FileData.FileType.File_RAR.equals(fileType)) {
            return R.mipmap.icon_b_rar;
        }

        if (FileData.FileType.File_Image.equals(fileType)) {
            return R.mipmap.icon_b_photo;
        }

        if (FileData.FileType.File_Gif.equals(fileType)) {
            return R.mipmap.icon_b_gif;
        }

        if (FileData.FileType.File_Audio.equals(fileType)) {
            return R.mipmap.icon_b_music;
        }

        if (FileData.FileType.File_Excel.equals(fileType)) {
            return R.mipmap.icon_b_excel;
        }

        if (FileData.FileType.File_HTML.equals(fileType)) {
            return R.mipmap.icon_b_txt;
        }

        if (FileData.FileType.File_Pdf.equals(fileType)) {
            return R.mipmap.icon_b_pdf;
        }

        if (FileData.FileType.File_Txt.equals(fileType)) {
            return R.mipmap.icon_b_txt;
        }

        if (FileData.FileType.File_Word.equals(fileType)) {
            return R.mipmap.icon_b_word;
        }

        if (FileData.FileType.File_Video.equals(fileType)) {
            return R.mipmap.icon_b_video;
        }

        if (FileData.FileType.File_Ppt.equals(fileType)) {
            return R.mipmap.icon_b_ppt;
        }

        if (FileData.FileType.File_APK.equals(fileType)) {
            return R.mipmap.icon_b_app;
        }
        return R.mipmap.icon_b_nofile;
    }


    public static int getFileTypeByExtension(String extension) {
        FileData.FileType fileType = FileData.getFileTypeByExtension(extension);
        if (FileData.FileType.File_RAR.equals(fileType)) {
            return R.mipmap.icon_dropbox_item_archive;
        }

        if (FileData.FileType.File_Image.equals(fileType)) {
            return R.mipmap.icon_dropbox_item_pic;
        }
        if (FileData.FileType.File_Gif.equals(fileType)) {
            return R.mipmap.icon_dropbox_item_gif;
        }

        if (FileData.FileType.File_Audio.equals(fileType)) {
            return R.mipmap.icon_dropbox_item_audio;
        }

        if (FileData.FileType.File_Excel.equals(fileType)) {
            return R.mipmap.icon_dropbox_item_xls;
        }

        if (FileData.FileType.File_HTML.equals(fileType)) {
            return R.mipmap.icon_dropbox_item_txt;
        }

        if (FileData.FileType.File_Pdf.equals(fileType)) {
            return R.mipmap.icon_dropbox_item_pdf;
        }

        if (FileData.FileType.File_Txt.equals(fileType)) {
            return R.mipmap.icon_dropbox_item_txt;
        }

        if (FileData.FileType.File_Word.equals(fileType)) {
            return R.mipmap.icon_dropbox_item_word;
        }

        if (FileData.FileType.File_Video.equals(fileType)) {
            return R.mipmap.icon_dropbox_item_video;
        }

        if (FileData.FileType.File_Ppt.equals(fileType)) {
            return R.mipmap.icon_dropbox_item_ppt;
        }

        if (FileData.FileType.File_APK.equals(fileType)) {
            return R.mipmap.icon_dropbox_application;
        }
        return R.mipmap.icon_dropbox_item_others;
    }
}
