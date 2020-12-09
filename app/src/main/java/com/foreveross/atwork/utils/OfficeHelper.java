package com.foreveross.atwork.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.modules.file.activity.OfficeViewActivity;

/**
 * Created by dasunsy on 2018/1/18.
 */

public class OfficeHelper {


    public static boolean shouldOffice365Preview() {
        return BeeWorks.getInstance().config.isLite()
                || "OFFICE365".equalsIgnoreCase(DomainSettingsManager.getInstance().getFileOnlinePreviewFeature());
    }

    public static boolean isOnlineSupportType(FileData.FileType fileType) {
        switch (fileType) {
            case File_Pdf:
            case File_Word:
            case File_Excel:
            case File_Ppt:
                return true;

            default:
                return false;
        }

    }

    public static boolean isSupportType(String path) {
        FileData.FileType fileType = FileData.getFileType(path);
        switch (fileType) {
            case File_Pdf:
            case File_Word:
            case File_Excel:
            case File_Ppt:
            case File_Txt:
                return true;

            default:
                return false;
        }

    }

    public static boolean isOffice365OnlinePreviewSupportType(String path) {
        return isOffice365OnlinePreviewSupportType(FileData.getFileType(path));
    }

    public static boolean isOffice365OnlinePreviewSupportType(FileData.FileType fileType) {
        switch (fileType) {
            case File_Word:
            case File_Excel:
            case File_Ppt:
                return true;

            default:
                return false;
        }
    }

    public static void previewByX5(Context context, String path) {
        Intent intent = OfficeViewActivity.getIntent(context, path, null);
        if(!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        context.startActivity(intent);
    }
    public static void previewByX5(Context context, String path, String sessionId, FileTransferChatMessage fileTransferChatMessage, int intentType) {
        Intent intent = OfficeViewActivity.getIntent(context, path, null, sessionId, fileTransferChatMessage, intentType);
        if(!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        context.startActivity(intent);
    }
    public static void previewByX5(Context context, String filePath, String from, FileData fileData, int intentType) {
        Intent intent = OfficeViewActivity.getIntent(context, filePath, from, fileData, intentType);
        if(!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        context.startActivity(intent);
    }
}
