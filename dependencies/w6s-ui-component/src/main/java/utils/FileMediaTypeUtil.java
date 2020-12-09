package utils;

import com.foreverht.workplus.ui.component.R;
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
     */
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
            return R.mipmap.icon_doc_archive;
        }

        if (FileData.FileType.File_Image.equals(fileType)) {
            return R.mipmap.icon_doc_image;
        }

        if (FileData.FileType.File_Gif.equals(fileType)) {
            return R.mipmap.icon_doc_gif;
        }

        if (FileData.FileType.File_Audio.equals(fileType)) {
            return R.mipmap.icon_doc_audio;
        }

        if (FileData.FileType.File_Excel.equals(fileType)) {
            return R.mipmap.icon_doc_xls;
        }

        if (FileData.FileType.File_HTML.equals(fileType)) {
            return R.mipmap.icon_doc_txt;
        }

        if (FileData.FileType.File_Pdf.equals(fileType)) {
            return R.mipmap.icon_doc_pdf;
        }

        if (FileData.FileType.File_Txt.equals(fileType)) {
            return R.mipmap.icon_doc_txt;
        }

        if (FileData.FileType.File_Word.equals(fileType)) {
            return R.mipmap.icon_doc_docx;
        }

        if (FileData.FileType.File_Video.equals(fileType)) {
            return R.mipmap.icon_doc_video;
        }

        if (FileData.FileType.File_Ppt.equals(fileType)) {
            return R.mipmap.icon_doc_ppt;
        }

        if (FileData.FileType.File_APK.equals(fileType)) {
            return R.mipmap.icon_doc_apk;
        }

        return R.mipmap.icon_doc_unknown;
    }


    public static int getFileTypeByExtension(String extension) {
        FileData.FileType fileType = FileData.getFileTypeByExtension(extension);
        if (FileData.FileType.File_RAR.equals(fileType)) {
            return R.mipmap.icon_doc_archive;
        }

        if (FileData.FileType.File_Image.equals(fileType)) {
            return R.mipmap.icon_doc_image;
        }
        if (FileData.FileType.File_Gif.equals(fileType)) {
                return R.mipmap.icon_doc_gif;
        }

        if (FileData.FileType.File_Audio.equals(fileType)) {
            return R.mipmap.icon_doc_audio;
        }

        if (FileData.FileType.File_Excel.equals(fileType)) {
            return R.mipmap.icon_doc_xls;
        }

        if (FileData.FileType.File_HTML.equals(fileType)) {
            return R.mipmap.icon_doc_txt;
        }

        if (FileData.FileType.File_Pdf.equals(fileType)) {
            return R.mipmap.icon_doc_pdf;
        }

        if (FileData.FileType.File_Txt.equals(fileType)) {
            return R.mipmap.icon_doc_txt;
        }

        if (FileData.FileType.File_Word.equals(fileType)) {
            return R.mipmap.icon_doc_docx;
        }

        if (FileData.FileType.File_Video.equals(fileType)) {
            return R.mipmap.icon_doc_video;
        }

        if (FileData.FileType.File_Ppt.equals(fileType)) {
            return R.mipmap.icon_doc_ppt;
        }

        if (FileData.FileType.File_APK.equals(fileType)) {
            return R.mipmap.icon_doc_apk;
        }
        return R.mipmap.icon_doc_unknown;
    }
}
