package com.foreveross.atwork.utils;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;

import static com.foreveross.atwork.infrastructure.model.dropbox.Dropbox.DropboxFileType.Application;
import static com.foreveross.atwork.infrastructure.model.dropbox.Dropbox.DropboxFileType.Archive;
import static com.foreveross.atwork.infrastructure.model.dropbox.Dropbox.DropboxFileType.Audio;
import static com.foreveross.atwork.infrastructure.model.dropbox.Dropbox.DropboxFileType.File;
import static com.foreveross.atwork.infrastructure.model.dropbox.Dropbox.DropboxFileType.Image;
import static com.foreveross.atwork.infrastructure.model.dropbox.Dropbox.DropboxFileType.Other;
import static com.foreveross.atwork.infrastructure.model.dropbox.Dropbox.DropboxFileType.Video;



/**
 * Created by reyzhang22 on 17/3/15.
 */

public class DropboxUtils {

    public static String getChineseFromFileType(Dropbox.DropboxFileType fileType) {
        if (Other.equals(fileType)) {
            return AtworkApplicationLike.getResourceString(R.string.others);
        }
        if (File.equals(fileType)) {
            return AtworkApplicationLike.getResourceString(R.string.file_doc);
        }
        if (Archive.equals(fileType)) {
            return AtworkApplicationLike.getResourceString(R.string.archive);
        }
        if (Image.equals(fileType)) {
            return AtworkApplicationLike.getResourceString(R.string.image);
        }
        if (Video.equals(fileType)) {
            return AtworkApplicationLike.getResourceString(R.string.video2);
        }
        if (Audio.equals(fileType)) {
            return AtworkApplicationLike.getResourceString(R.string.audio2);
        }
        if (Application.equals(fileType)) {
            return AtworkApplicationLike.getResourceString(R.string.application);
        }
        return AtworkApplicationLike.getResourceString(R.string.others);
    }
}
