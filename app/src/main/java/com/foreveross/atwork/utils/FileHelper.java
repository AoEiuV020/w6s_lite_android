package com.foreveross.atwork.utils;

import android.content.Context;

import com.foreveross.atwork.infrastructure.newmessage.post.chat.MicroVideoChatMessage;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.chat.util.MessageChatViewBuild;

import java.io.File;

/**
 * Created by ReyZhang on 2015/4/24.
 */
public class FileHelper {


    /**
     * 转换文件大小
     *
     * @param fileSize
     * @return
     */
    public static String getFileSizeStr(long fileSize) {
        String str = "0KB";
        double sizeB = fileSize;
        if (sizeB <= 0) {
            return str;
        }
        double sizeK = 1.0 * sizeB / 1024;
        double sizeM = sizeK / 1024;
        double sizeG = sizeM / 1024;
        double sizeT = sizeG / 1024;
        if (sizeT >= 1) {
            str = String.format("%.2f", sizeT) + "T";
        } else if (sizeG >= 1) {
            str = String.format("%.2f", sizeG) + "G";
        } else if (sizeM >= 1) {
            str = String.format("%.2f", sizeM) + "M";
        } else if (sizeK >= 1) {
            str = String.format("%.2f", sizeK) + "KB";
        } else {
            str = String.format("%.2f", sizeB) + "B";
        }

        if(str.contains(".00")) {
            str = str.replace(".00", "");
        }

        return str;
    }

    public static String getMicroExistVideoFilePath(Context context, MicroVideoChatMessage microVideoChatMessage) {
        return getMicroExistVideoFile(context, microVideoChatMessage).getAbsolutePath();
    }

    public static File getMicroExistVideoFile(Context context, MicroVideoChatMessage microVideoChatMessage) {
        if (!StringUtils.isEmpty(microVideoChatMessage.filePath)) {
            return new File(microVideoChatMessage.filePath);

        } else {
            //兼容旧版本
            return getMicroVideoFile_oldVersion(context, microVideoChatMessage);
        }
    }

    public static String getMicroNewFilePath(Context context, MicroVideoChatMessage microVideoChatMessage) {
        return getMicroNewFile(context, microVideoChatMessage).getAbsolutePath();
    }

    public static File getMicroNewFile(Context context, MicroVideoChatMessage microVideoChatMessage) {
        File newFile;
        if (MessageChatViewBuild.isLeftView(microVideoChatMessage)) {
            newFile = getMicroVideoFileReceiveById(context, microVideoChatMessage.mediaId);

        } else {
            newFile = getMicroVideoFileSendById(context, microVideoChatMessage.deliveryId);

        }
        microVideoChatMessage.filePath = newFile.getAbsolutePath();
        return newFile;

    }

    public static File getMicroVideoFile_oldVersion(Context context, MicroVideoChatMessage microVideoChatMessage) {
        if (MessageChatViewBuild.isLeftView(microVideoChatMessage)) {
            return getMicroVideoFileReceiveById(context, microVideoChatMessage.mediaId);

        } else {
            File file = getMicroVideoFileSendById(context, microVideoChatMessage.deliveryId);
            if (file.exists()) {
                return file;
            }
            return getMicroVideoFileSendById(context, microVideoChatMessage.mediaId);

        }
    }



    public static File getMicroVideoFileReceiveById(Context context, String deliverId) {
        return new File(AtWorkDirUtils.getInstance().getMicroVideoDir(context), deliverId + ".mp4");
    }

    public static String getMicroVideoFileSendPathById(Context context, String deliverId) {
        return getMicroVideoFileSendById(context, deliverId).getAbsolutePath();
    }

    public static File getMicroVideoFileSendById(Context context, String deliverId) {
        return new File(AtWorkDirUtils.getInstance().getMicroVideoHistoryDir(context), deliverId + ".mp4");
    }

}
