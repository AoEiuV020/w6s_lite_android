package com.foreveross.atwork.infrastructure.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.foreveross.atwork.infrastructure.manager.FileAlbumService;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MicroVideoChatMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.file.FileStreamHelper;

import java.io.File;
import java.io.IOException;

public class MicroVideoHelper {

    public static String saveVideoToGalleryAndGetPath(Context context, byte[] content, String fileFromPath) {
        boolean isSuccessful = true;
        // 首先保存
        String galleryPath = AtWorkDirUtils.getInstance().getGalleryDir(LoginUserInfo.getInstance().getLoginUserUserName(context));
        File appDir = new File(galleryPath);

        if (!appDir.exists()) {
            boolean success = appDir.mkdir();
            if(!success || !appDir.canRead() || !appDir.canWrite()){
                galleryPath = AtWorkDirUtils.getInstance().getDefaultGalleryDir(LoginUserInfo.getInstance().getLoginUserUserName(context));
                appDir = new File(galleryPath);
                appDir.mkdir();
            }
        }
        String fileName = System.currentTimeMillis() + ".mp4";

        String fileTargetPath = galleryPath + "/" + fileName;
        File file = new File(fileTargetPath);

        if (null != content) {
            isSuccessful = FileStreamHelper.saveFile(fileTargetPath, content);

        } else {
            try {
                FileUtil.copyFile(fileFromPath, fileTargetPath);
            } catch (IOException e) {
                e.printStackTrace();
                isSuccessful = false;
            }
        }
        // 其次把文件插入到系统图库
        try {
            /**
             * 该出使用已经保存的文件作为插入media 库的地址,
             * android 自带的 insertImage 会在 Camera 文件夹生成一张图片, 并索引它, 我们不需要
             * */
            String url = insertVideo(context,
                    file, fileName, null);
            notifyMediaStore(context, url);

        } catch (Exception e) {
            e.printStackTrace();

        }
        if(isSuccessful) {
            return fileTargetPath;
        } else {
            return StringUtils.EMPTY;

        }
    }

    public static Long getVideoDuration(Context context, File videoFile) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//use one of overloaded setDataSource() functions to set your data source
        retriever.setDataSource(context, Uri.fromFile(videoFile));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        try {
            long duration = Long.parseLong(time);
            retriever.release();
            return duration;

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return -1L;
    }


    public static String insertVideo(Context context, File file,
                                     String title, String description) throws Exception {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Video.Media.TITLE, title);
        values.put(MediaStore.Video.Media.DATA, file.getAbsolutePath());
        values.put(MediaStore.Video.Media.DESCRIPTION, description);
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.DURATION, getVideoDuration(context, file));

        String stringUrl = null;
        Uri uri = null;
        ContentResolver contentResolver = context.getContentResolver();

        try {
            uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            try {
                uri = contentResolver.insert(MediaStore.Video.Media.INTERNAL_CONTENT_URI, values);
            } catch (UnsupportedOperationException ex) {
                ex.printStackTrace();
                return null;
            }
        }

        if (null != uri) {
            stringUrl = uri.toString();
        }

        return stringUrl;
    }

    public static void notifyMediaStore(Context context, String url) {
        if (!StringUtils.isEmpty(url)) {
            ContentResolver resolver = context.getContentResolver();
            ContentValues cv = FileAlbumService.getLastInsertVideoCv(context);

            //有些手机不能默认自动拿文件夹作为相册名, 有权限更改该字段
            cv.put(MediaStore.Video.Media.BUCKET_DISPLAY_NAME, AtworkConfig.APP_FOLDER);
            resolver.update(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    cv,
                    MediaStore.Video.Media._ID + "=?",
                    new String[]{String.valueOf(cv.getAsInteger(MediaStore.Video.Media._ID))}
            );

            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(new File((String) cv.get("_data")))));
        }
    }

    public static Bitmap getVideoBitmap(Context context, MicroVideoChatMessage item) {
        Bitmap bitmap;
        if (item.thumbnails != null) {
            bitmap = BitmapFactory.decodeByteArray(item.thumbnails, 0, item.thumbnails.length);
            if (bitmap != null) {
                return bitmap;
            }
        }
        byte[] b = ImageShowHelper.getThumbnailImage(context, item.deliveryId);
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        return null;
    }

}
