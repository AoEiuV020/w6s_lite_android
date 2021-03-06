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
     * ??????uri?????????????????????????????????
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
     * ????????????????????????????????????
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
     * ????????????????????????????????????
     * @see #compressImage(byte[], String, Bitmap.CompressFormat, int, int)
     * */
    public static byte[] compressImageForOriginal(byte[] imageBytes){
        return compressImage(imageBytes, null, Bitmap.CompressFormat.JPEG,
                ORIGINAL_TARGET_HEIGHT * ORIGINAL_TARGET_WIDTH, ORIGINAL_TARGET_SIZE);
    }

    /**
     * ????????????????????????????????????
     * @param imageFile
     * @return array of bytes
     */
    public static byte[] compressImageForCamera(String imageFile) {
        return compressImage(null, imageFile, Bitmap.CompressFormat.JPEG, 600 * 1200, ORIGINAL_TARGET_SIZE);
    }


    /**
     * ??????????????????????????????????????????, ??????????????????
     * @return array of bytes
     */
    public static byte[] compressImage(byte[] imageBytes, String imageFilePath, Bitmap.CompressFormat format, int maxNumOfPixel, int maxSize) {

        //??????????????????????????????, ??????????????????
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
     * ???????????????
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
     * ???????????????
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
     * ??????????????????
     *
     * @param msgId
     * @return
     */
    public static byte[] getOriginalImage(Context context, String msgId) {
        String filePath = AtWorkDirUtils.getInstance().getImageDir(context) + msgId + "-" + ORIGINAL_SUFFIX;
        return FileStreamHelper.readFile(filePath);
    }

    /**
     * ?????????????????????
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
        // ????????????
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
        // ????????????????????????????????????
        try {
            /**
             * ?????????????????????????????????????????????media ????????????,
             * android ????????? insertImage ?????? Camera ???????????????????????????, ????????????, ???????????????
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
     * ???????????????cordova??????????????? ??????????????????
     * @param originalPath
     * return targetLocalUrl
     */
    public static String imagePluginCompress(Context context, String originalPath) {
        //???????????????
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
     * ????????????????????????
     *
     * @param originalImgPath ????????????
     * @param isThumbImg ????????????????????????
     * */
    public static Bitmap getRotateImageBitmap(String originalImgPath, boolean isThumbImg) {
        if (isThumbImg) {
            //???????????????
            Bitmap bm = getBitmapFromFile(originalImgPath, THUMBNAIL_TARGET_WIDTH, THUMBNAIL_TARGET_HEIGHT, THUMBNAIL_TARGET_SIZE);
            return BitmapUtil.adjustOrientation(bm, originalImgPath);
        }
        //????????????
        Bitmap bm = getBitmapFromFile(originalImgPath, ORIGINAL_TARGET_WIDTH, ORIGINAL_TARGET_HEIGHT, ORIGINAL_TARGET_SIZE);
        return BitmapUtil.adjustOrientation(bm, originalImgPath);
    }

    public static void notifyMediaStore(Context context, String url){
        if(!StringUtils.isEmpty(url)){
            ContentResolver resolver = context.getContentResolver();
            ContentValues cv = FileAlbumService.getLastInsertImageCv(context);

            //?????????????????????????????????????????????????????????, ????????????????????????
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
         * ?????????????????????options.inJustDecodeBounds = true;
         * ?????????decodeFile()????????????bitmap????????????????????????options.outHeight????????????????????????????????????
         */
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // ???????????????bitmap???null
        /**
         *options.outHeight?????????????????????
         */
        return new int[]{options.outWidth,options.outHeight};
    }


}
