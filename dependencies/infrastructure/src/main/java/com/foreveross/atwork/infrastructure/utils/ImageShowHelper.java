package com.foreveross.atwork.infrastructure.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import android.util.Log;

import com.foreveross.atwork.infrastructure.manager.FileAlbumService;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptCacheDisk;
import com.foreveross.atwork.infrastructure.utils.file.FileStreamHelper;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;


public class ImageShowHelper {

    public static final String ORIGINAL_SUFFIX = "original";

    public static final String THUMBNAIL_SUFFIX = "thumbnail";


    public static final int ORIGINAL_TARGET_WIDTH = 1200;

    public static final int ORIGINAL_TARGET_HEIGHT = 1600;

    public static final int ORIGINAL_TARGET_SIZE = AtworkConfig.CHAT_ORIGINAL_COMPRESS_SIZE;

    public static final int THUMBNAIL_TARGET_WIDTH = 200;

    public static final int THUMBNAIL_TARGET_HEIGHT = 400;

    public static final int THUMBNAIL_TARGET_SIZE = AtworkConfig.CHAT_THUMB_SIZE;

    private static final int QRCODE_RECOGNIZE_TARGET_WIDTH = 500;

    private static final int QRCODE_RECOGNIZE_TARGET_HEIGHT = 500;

    private static final int QRCODE_RECOGNIZE_TARGET_SIZE = 50 << 10;


    private static final int COLLEAGUE_CIRCLE_TARGET_WIDTH = 1136;

    private static final int COLLEAGUE_CIRCLE_TARGET_HEIGHT = 1136;

    private static final int COLLEAGUE_CIRCLE_TARGET_SIZE = AtworkConfig.COLLEAGUE_CIRCLE_COMPRESS_SIZE;


    /**
     * 根据uri从文件中缩放的获取图片
     */
    public static Bitmap getBitmap(Context context, Uri uri, int requiredWidth, int requiredHeight) {
        ContentResolver cr = context.getContentResolver();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = null;
        try {
            BitmapFactory.decodeStream(cr.openInputStream(uri), null, options);
            options.inSampleSize = BitmapUtil.calculateInSampleSize(options, requiredWidth, requiredHeight);
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri), null, options);
        } catch (FileNotFoundException e) {
            Log.e("error!", e.getMessage(), e);
        }
        return bitmap;

    }

    public static Bitmap getBitmapFromFile(String imagePath, int width, int height, int quality) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, opts);
        opts.inSampleSize = BitmapUtil.computeSampleSize(opts, -1, width * height);
        opts.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, opts);
        byte[] thus = BitmapUtil.compressImageForQuality(bitmap, quality);
        return BitmapUtil.Bytes2Bitmap(thus);
    }

    public static byte[] getByteFromFile(String imagePath, int width, int height, int quality) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, opts);
        opts.inSampleSize = BitmapUtil.computeSampleSize(opts, -1, width * height);
        opts.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, opts);
        return BitmapUtil.compressImageForQuality(bitmap, quality);
    }

    public static Bitmap getJustDecodeBounds(byte[] imageBytes, String imageFile, BitmapFactory.Options opts ) {
        if(null != imageBytes){
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, opts);
        }else if(!StringUtils.isEmpty(imageFile)){

            String handlePath = EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(imageFile, false);
            return BitmapFactory.decodeFile(handlePath, opts);
        }

        return null;
    }



    public static byte[] compressImageForQrcodeRecognize(byte[] imageBytes) {
        return compressImage(imageBytes, null, Bitmap.CompressFormat.JPEG,
                QRCODE_RECOGNIZE_TARGET_HEIGHT * QRCODE_RECOGNIZE_TARGET_WIDTH, QRCODE_RECOGNIZE_TARGET_SIZE);
    }

    /**
     * 按照缩略图大小去压缩图片
     ** @see #compressImage(byte[], String, Bitmap.CompressFormat, int, int)
     */
    public static byte[] compressImageForThumbnail(byte[] imageBytes) {
        return compressImage(imageBytes, null, Bitmap.CompressFormat.JPEG,
                THUMBNAIL_TARGET_HEIGHT * THUMBNAIL_TARGET_WIDTH, THUMBNAIL_TARGET_SIZE);
    }

    /**
     ** @see #compressImage(byte[], String, Bitmap.CompressFormat, int, int)
     */
    public static byte[] compressImageForThumbnail(String imgPath) {
        return compressImage(null, imgPath, Bitmap.CompressFormat.JPEG,
                THUMBNAIL_TARGET_HEIGHT * THUMBNAIL_TARGET_WIDTH, THUMBNAIL_TARGET_SIZE);
    }

    public static byte[] compressImageForDropboxThumbnail(String imagePath) {
        return compressImage(null, imagePath,Bitmap.CompressFormat.JPEG, 100*200, 3 << 10);
    }

    /**
     ** @see #compressImage(byte[], String, Bitmap.CompressFormat, int, int)
     */
    public static byte[] compressImageForCircle(String imgPath) {
        return compressImage(null, imgPath, Bitmap.CompressFormat.JPEG,
                COLLEAGUE_CIRCLE_TARGET_HEIGHT * COLLEAGUE_CIRCLE_TARGET_WIDTH, COLLEAGUE_CIRCLE_TARGET_SIZE);
    }

    /**
     * 接近原图大小地去压缩图片
     * @see #compressImage(byte[], String, Bitmap.CompressFormat, int, int)
     * */
    public static byte[] compressImageForOriginal(byte[] imageBytes){
        return compressImage(imageBytes, null, Bitmap.CompressFormat.JPEG,
                ORIGINAL_TARGET_HEIGHT * ORIGINAL_TARGET_WIDTH, ORIGINAL_TARGET_SIZE);
    }

    /**
     * 拍照后的图像进行压缩处理
     * @param imageFile
     * @return array of bytes
     */
    public static byte[] compressImageForCamera(String imageFile) {
        return compressImage(null, imageFile, Bitmap.CompressFormat.JPEG, 600 * 1200, ORIGINAL_TARGET_SIZE);
    }


    /**
     * 根据给定的像素与图片大小限制, 进行压缩图片
     * @return array of bytes
     */
    public static byte[] compressImage(byte[] imageBytes, String imageFilePath, Bitmap.CompressFormat format, int maxNumOfPixel, int maxSize) {

        //若大小达到了指定标准, 则不进行处理
        if(null != imageBytes && maxSize >= imageBytes.length){
            return imageBytes;
        } else {

            if (!StringUtils.isEmpty(imageFilePath)) {
                File imageFile = new File(imageFilePath);
                if (maxSize >= imageFile.length()) {
                    try {
                        return FileUtil.readFileByRAWay(imageFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return BitmapUtil.Bitmap2Bytes(BitmapFactory.decodeFile(imageFilePath));
                    }
                }
            }

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            getJustDecodeBounds(imageBytes, imageFilePath, opts);

            opts.inSampleSize = BitmapUtil.computeSampleSize(opts, -1, maxNumOfPixel);
            opts.inJustDecodeBounds = false;

            Bitmap bitmap = getJustDecodeBounds(imageBytes, imageFilePath, opts);
            return BitmapUtil.compressImageForQuality(bitmap, format, maxSize);
        }

    }


    /**
     * 保存原始图
     *
     * @param msgId
     * @param bitmap
     */
    public static String saveOriginalImage(Context context, String msgId, byte[] bitmap) {
        String filePath = AtWorkDirUtils.getInstance().getImageDir(context) + msgId + "-" + ORIGINAL_SUFFIX;
        FileStreamHelper.saveFile(filePath, bitmap);
        return filePath;
    }

    /**
     * 保存缩略图
     *
     * @param msgId
     * @param bitmap
     */
    public static String saveThumbnailImage(Context context, String msgId, byte[] bitmap) {
        String filePath = AtWorkDirUtils.getInstance().getImageDir(context) + msgId + "-" + THUMBNAIL_SUFFIX;
        FileStreamHelper.saveFile(filePath, bitmap);
        return filePath;
    }



    /**
     * 获取原图内容
     *
     * @param msgId
     * @return
     */
    public static byte[] getOriginalImage(Context context, String msgId) {
        String filePath = AtWorkDirUtils.getInstance().getImageDir(context) + msgId + "-" + ORIGINAL_SUFFIX;
        return FileStreamHelper.readFile(filePath);
    }

    /**
     * 获取缩回图内容
     *
     * @param msgId
     * @return
     */
    public static byte[] getThumbnailImage(Context context, String msgId) {
        String filePath = AtWorkDirUtils.getInstance().getImageDir(context) + msgId + "-" + THUMBNAIL_SUFFIX;
        return FileStreamHelper.readFile(filePath);
    }

    public static String getThumbnailPath(Context context, String msgId) {
        String filePath = AtWorkDirUtils.getInstance().getImageDir(context) + msgId + "-" + THUMBNAIL_SUFFIX;
        return filePath;
    }

    public static String getOriginalPath(Context context, String msgId) {
        String filePath = AtWorkDirUtils.getInstance().getImageDir(context) + msgId + "-" + ORIGINAL_SUFFIX;
        return filePath;
    }

    public static String getFullPath(Context context, String msgId) {
        String filePath = AtWorkDirUtils.getInstance().getImageDir(context) + msgId;
        return filePath;
    }

    public static String getFileImgPath(Context context, FileTransferChatMessage fileTransferChatMessage) {
        if(!StringUtils.isEmpty(fileTransferChatMessage.filePath)) {
            return fileTransferChatMessage.filePath;
        }

        return getOriginalPath(context, fileTransferChatMessage.deliveryId);
    }

    public static String getFileGifPath(Context context, FileTransferChatMessage fileTransferChatMessage) {
        if(!StringUtils.isEmpty(fileTransferChatMessage.filePath)) {
            return fileTransferChatMessage.filePath;
        }

        return getOriginalPath(context, fileTransferChatMessage.mediaId);
    }

    public static String getChatMsgImgMediaId(ChatPostMessage chatPostMessage) {
        if(chatPostMessage instanceof ImageChatMessage) {
            ImageChatMessage imageChatMessage = (ImageChatMessage) chatPostMessage;
            return imageChatMessage.mediaId;

        } else if(chatPostMessage instanceof FileTransferChatMessage) {
            FileTransferChatMessage fileTransferChatMessage = (FileTransferChatMessage) chatPostMessage;
            return fileTransferChatMessage.mediaId;
        }

        return StringUtils.EMPTY;
    }

    @Nullable
    public static byte[] getChatMsgImgThumbnail(ChatPostMessage chatPostMessage) {
        if(chatPostMessage instanceof ImageChatMessage) {
            ImageChatMessage imageChatMessage = (ImageChatMessage) chatPostMessage;
            return imageChatMessage.getThumbnails();
        }

        return null;
    }

    @Nullable
    public static String getChatMsgImgPath(Context context, ChatPostMessage chatPostMessage) {
        String path = null;
        if(chatPostMessage instanceof FileTransferChatMessage) {
            path = ImageShowHelper.getFileImgPath(context, (FileTransferChatMessage) chatPostMessage);

        } else if(chatPostMessage instanceof ImageChatMessage) {
            ImageChatMessage imageChatMessage = (ImageChatMessage) chatPostMessage;
            if(!StringUtils.isEmpty(imageChatMessage.filePath)) {
                path = imageChatMessage.filePath;
            } else {
                path = ImageShowHelper.getOriginalPath(context, chatPostMessage.deliveryId);
            }


        }

        return path;
    }

    @Nullable
    public static String getChatMsgGifPath(Context context, ChatPostMessage chatPostMessage) {
        String path = null;
        if(chatPostMessage instanceof FileTransferChatMessage) {
            path = ImageShowHelper.getFileGifPath(context, (FileTransferChatMessage) chatPostMessage);

        } else if(chatPostMessage instanceof ImageChatMessage) {
            ImageChatMessage imageChatMessage = (ImageChatMessage) chatPostMessage;
            if(!StringUtils.isEmpty(imageChatMessage.filePath)) {
                path = imageChatMessage.filePath;
            } else {
                path = ImageShowHelper.getOriginalPath(context, chatPostMessage.deliveryId);
            }

        }

        return path;
    }


    public static String getImageChatMsgPath(Context context, ImageChatMessage imageChatMessage) {
        if(imageChatMessage.isFullMode()) {
            return imageChatMessage.fullImgPath;
        } else {
            return getOriginalPath(context, imageChatMessage.deliveryId);
        }
    }

    public static boolean saveImageToGallery(Context context, byte[] content, String fileFromPath, boolean isGif) {
        return !StringUtils.isEmpty(saveImageToGalleryAndGetPath(context,content, fileFromPath, isGif));
    }

    public static String saveImageToGalleryAndGetPath(Context context, byte[] content, String fileFromPath, boolean isGif) {
        return saveImageToGalleryAndGetPath(context, content, fileFromPath, isGif, AtworkConfig.OPEN_DISK_ENCRYPTION);
    }

    public static String saveImageToGalleryAndGetPath(Context context, byte[] content, String fileFromPath, boolean isGif, boolean needEncrypt) {
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
        String fileName;

        if(isGif) {
            fileName =  System.currentTimeMillis() + ".gif";
        } else {
            fileName =  System.currentTimeMillis() + ".jpg";

        }
        String fileTargetPath = galleryPath + "/" + fileName;
        File file = new File(fileTargetPath);

        if (null != content) {
            isSuccessful = FileStreamHelper.saveFile(needEncrypt, fileTargetPath, content);

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
            String url = insertImage(context.getContentResolver(),
                    file, fileName, null);
            notifyMediaStore(context, url);

        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("cimc", "sdcard_first_step", e);
            Logger.e("cimc", file.getAbsolutePath());

        }
        if(isSuccessful) {
            return fileTargetPath;
        } else {
            return StringUtils.EMPTY;

        }
    }

    /**
     * 用于返回给cordova的图片压缩 不用保存本地
     * @param originalPath
     * return targetLocalUrl
     */
    public static String imagePluginCompress(Context context, String originalPath) {
        //压缩图路径
        String fileName = "images_plugin_compressed_" + UUID.randomUUID() + ".jpg";
        String targetLocalUrl = AtWorkDirUtils.getInstance().getCompressImageDir(LoginUserInfo.getInstance().getLoginUserUserName(context)) + fileName;

        byte[] compressedByte = compressImageForCircle(originalPath);
        AndPermission
                .with((Activity) context)
                .runtime()
                .permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                .onGranted(granted -> {
                    FileUtil.saveFile(targetLocalUrl, compressedByte);
                })
                .onDenied(denied ->  {})
                .start();

        return targetLocalUrl;
    }


    /**
     * 获取旋转后的图片
     *
     * @param originalImgPath 源图地址
     * @param isThumbImg 是否是旋转缩略图
     * */
    public static Bitmap getRotateImageBitmap(String originalImgPath, boolean isThumbImg) {
        if (isThumbImg) {
            //旋转缩略图
            Bitmap bm = getBitmapFromFile(originalImgPath, THUMBNAIL_TARGET_WIDTH, THUMBNAIL_TARGET_HEIGHT, THUMBNAIL_TARGET_SIZE);
            return BitmapUtil.adjustOrientation(bm, originalImgPath);
        }
        //旋转原图
        Bitmap bm = getBitmapFromFile(originalImgPath, ORIGINAL_TARGET_WIDTH, ORIGINAL_TARGET_HEIGHT, ORIGINAL_TARGET_SIZE);
        return BitmapUtil.adjustOrientation(bm, originalImgPath);
    }

    public static void notifyMediaStore(Context context, String url){
        if(!StringUtils.isEmpty(url)){
            ContentResolver resolver = context.getContentResolver();
            ContentValues cv = FileAlbumService.getLastInsertImageCv(context);

            //有些手机不能默认自动拿文件夹作为相册名, 有权限更改该字段
            cv.put(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, AtworkConfig.APP_FOLDER);
            resolver.update(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    cv,
                    MediaStore.Images.Media._ID + "=?",
                    new String[]{String.valueOf(cv.getAsInteger(MediaStore.Images.Media._ID))}
            );

            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(new File((String) cv.get(MediaStore.Images.Media.DATA)))));
        }
    }

    public static String insertImage(ContentResolver cr, File file,
                                     String title, String description) throws Exception {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        String stringUrl = null;
        Uri uri = null;
        try {
            uri = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            try {
                uri = cr.insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, values);
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

    public static int[] getImageWidthHeight(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();

        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
        /**
         *options.outHeight为原始图片的高
         */
        return new int[]{options.outWidth,options.outHeight};
    }


}
