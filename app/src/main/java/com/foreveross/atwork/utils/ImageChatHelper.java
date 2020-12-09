package com.foreveross.atwork.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.foreverht.cache.BitmapCache;
import com.foreverht.threadGear.ImageThreadPoolExecutor;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.MediaCenterHttpURLConnectionUtil;
import com.foreveross.atwork.api.sdk.net.RequestRemoteInterceptor;
import com.foreveross.atwork.api.sdk.upload.MediaCenterSyncNetService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageContentInfo;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.StickerChatMessage;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.BitmapUtil;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.infrastructure.utils.ScreenUtils;
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptCacheDisk;

/**
 * Created by dasunsy on 15/9/24.
 */
public class ImageChatHelper {
    private static int MAX_IMG_LENGTH;
    private static int MIN_IMG_LENGTH;

    static {
        MAX_IMG_LENGTH = ScreenUtils.getScreenWidth(BaseApplicationLike.baseContext) / 3;
        if (200 > MAX_IMG_LENGTH) { //俗称最大的最小值 :)
            MAX_IMG_LENGTH = 200;
        }

        MIN_IMG_LENGTH = DensityUtil.dip2px(20);

    }

    public static void initStickerContent(StickerChatMessage stickerChatMessage, ImageView ivContent) {
        Context context = ivContent.getContext();
        String imageUrl = "";
        if (BodyType.Sticker.equals( stickerChatMessage.mBodyType)) {
            String name = stickerChatMessage.getStickerName();
            if (name.contains(".")) {
                name = name.split("\\.")[0];
            }
            imageUrl = AtWorkDirUtils.getInstance().getAssetStickerUri(stickerChatMessage.getThemeName(), name);
        } else {
            imageUrl = stickerChatMessage.getChatStickerUrl(context, UrlConstantManager.getInstance().getStickerImageUrl());
        }
        RequestOptions options = new RequestOptions()
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .fallback(R.mipmap.loading_gray_holding)
                .error(R.mipmap.loading_gray_holding);
        Glide.with(context).load(imageUrl).apply(options).into(ivContent);
    }

    private static void scaleStickerView(Context context, ImageView ivContent, StickerChatMessage stickerChatMessage) {
        int targetWidth = DensityUtil.dip2px(stickerChatMessage.getWidth());
        int targetHeight = DensityUtil.dip2px(stickerChatMessage.getHeight());

        if(ivContent.getLayoutParams().width != targetWidth || ivContent.getLayoutParams().height != targetHeight) {
            ivContent.getLayoutParams().width = targetWidth;
            ivContent.getLayoutParams().height = targetHeight;
            ivContent.requestLayout();
        }

    }

    public static void initImageContent(ImageChatMessage imageChatMessage, ImageView ivContent) {
        initImageContent(imageChatMessage, ivContent, null, true);
    }

    public static void initImageContent(ImageChatMessage imageChatMessage, ImageView ivContent, @DrawableRes Integer loadingImgRes, boolean needScale) {
        ivContent.setTag(imageChatMessage.deliveryId);

        Context context = ivContent.getContext();

        Bitmap bitmapCache = BitmapCache.getBitmapCache().getContentBitmapCache(imageChatMessage);
        if (null != bitmapCache) {
            setImageContentCheck(ivContent, bitmapCache, imageChatMessage, needScale);
            return;
        }

        if (null != loadingImgRes) {
            ImageCacheHelper.setImageResource(ivContent, loadingImgRes);

            if (needScale) {
                ImageChatHelper.scaleImageView(imageChatMessage, ivContent);
            }
        }

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... voids) {
                Bitmap bitmap = BitmapCache.getBitmapCache().getContentBitmap(context, imageChatMessage);

                if (null == bitmap
                        && TextUtils.isEmpty(imageChatMessage.thumbnailMediaId)
                        && TextUtils.isEmpty(imageChatMessage.mediaId)) {
                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.loading_gray_holding);
                }

                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (null != bitmap) {
                    setImageContentCheck(ivContent, bitmap, imageChatMessage, needScale);
                    return;
                }


                if (!TextUtils.isEmpty(imageChatMessage.thumbnailMediaId)) {
//                    setImageByThumbnailMediaId(context, ivContent, imageChatMessage, needScale);
                    setImageByMediaId(context, ivContent, imageChatMessage, false, needScale);

                    return;
                }

                if (!TextUtils.isEmpty(imageChatMessage.mediaId)) {
                    setImageByMediaId(context, ivContent, imageChatMessage, true, needScale);
                    return;
                }


            }
        }.executeOnExecutor(ImageThreadPoolExecutor.getInstance());

    }

    private static void setImageContentCheck(ImageView ivContent, Bitmap bitmapCache, ImageChatMessage imageChatMessage, boolean needScale) {
        if(needCurrentRefresh(imageChatMessage, ivContent)){
            ivContent.setImageBitmap(bitmapCache);

            if (needScale) {
                ImageChatHelper.scaleImageViewCompat(imageChatMessage, bitmapCache, ivContent);
            }

        }
    }

    private static void setImageByMediaId(Context context, ImageView ivContent, ImageChatMessage imageChatMessage, boolean original, boolean needScale) {
        ImageChatHelper.showImg(context, imageChatMessage, original, new OnReloadImageListener() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                if (bitmap != null) {
                    setImageContentCheck(ivContent, bitmap, imageChatMessage, needScale);
                }
            }

            @Override
            public void onFail() {
                Bitmap failBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.loading_gray_holding);
                ivContent.setImageBitmap(failBitmap);
            }
        });
    }

    private static void setImageByThumbnailMediaId(Context context, ImageView ivContent, ImageChatMessage imageChatMessage, boolean needScale) {
        ImageCacheHelper.displayImageByMediaId(imageChatMessage.thumbnailMediaId, ivContent, ImageCacheHelper.getImageOptions(), new ImageCacheHelper.ImageLoadedListener() {
            @Override
            public void onImageLoadedComplete(Bitmap bitmap) {
                if (bitmap != null) {
                    setImageContentCheck(ivContent, bitmap, imageChatMessage, needScale);
                }

            }

            @Override
            public void onImageLoadedFail() {
                Bitmap failBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.loading_gray_holding);
                ivContent.setImageBitmap(failBitmap);
            }
        });
    }

    private static void setImageByThumbnailMediaId(Context context, ImageView ivContent, String mediId) {
        ImageCacheHelper.displayImageByMediaId(mediId, ivContent, ImageCacheHelper.getImageOptions(), new ImageCacheHelper.ImageLoadedListener() {
            @Override
            public void onImageLoadedComplete(Bitmap bitmap) {
                if (bitmap != null) {
                    ivContent.setImageBitmap(bitmap);
                }

            }

            @Override
            public void onImageLoadedFail() {
                Bitmap failBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.loading_gray_holding);
                ivContent.setImageBitmap(failBitmap);
            }
        });
    }

    /**
     * 当前视图是否合适刷新
     *
     * @param message
     * @return boolean
     * */
    private static boolean needCurrentRefresh(ChatPostMessage message, ImageView ivContent){
        return (null != ivContent.getTag() && ivContent.getTag().equals(message.deliveryId));

    }

    /**
     * 展示静态图
     */
    @SuppressLint("StaticFieldLeak")
    public static void showImg(final Context context, final ImageChatMessage imageChatMessage, boolean original, final OnReloadImageListener listener) {
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {

                String mediaId;
                if(original) {
                    mediaId = imageChatMessage.mediaId;
                } else {
                    mediaId = imageChatMessage.thumbnailMediaId;

                }

                if (!RequestRemoteInterceptor.INSTANCE.checkLegal(mediaId)) {
                    return null;
                }
                MediaCenterSyncNetService mediaCenterSyncNetService = MediaCenterSyncNetService.getInstance();

                String filePath = null;
                if (original) {
                    filePath = ImageShowHelper.getOriginalPath(context, imageChatMessage.deliveryId);
                } else {
                    filePath = ImageShowHelper.getThumbnailPath(context, imageChatMessage.deliveryId);
                }

                HttpResult result = mediaCenterSyncNetService.getMediaContent(BaseApplicationLike.baseContext, mediaId, filePath, MediaCenterHttpURLConnectionUtil.DOWNLOAD_TYPE.ORIGINAL);
                Bitmap bitmap = null;
                if (0 == result.status) {
                    byte[] thumbnails = ImageShowHelper.compressImageForThumbnail(filePath);
                    ImageShowHelper.saveThumbnailImage(context, imageChatMessage.deliveryId, thumbnails);
                    bitmap = BitmapUtil.Bytes2Bitmap(ImageShowHelper.getThumbnailImage(context, imageChatMessage.deliveryId));

                }

                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (null != bitmap) {
                    listener.onSuccess(bitmap);
                } else {
                    listener.onFail();
                }
            }
        }.executeOnExecutor(ImageThreadPoolExecutor.getInstance());

    }

    public static void showGif(final Context context, final ImageView gifShownView, final ImageView gifTagView, final ImageChatMessage imageChatMessage) {
        showGif(context, gifShownView, gifTagView, imageChatMessage, true);
    }

    public static void showGif(final Context context, final ImageView gifShownView, final ImageView gifTagView, final ImageChatMessage imageChatMessage, boolean needScale) {
        GifChatHelper.show(context, gifShownView, imageChatMessage, new GifChatHelper.OnLoadGifListener() {
            @Override
            public void settingThumb(Bitmap bitmap) {
                if (needScale) {
                    ImageChatHelper.scaleImageViewCompat(imageChatMessage, bitmap, gifShownView);
                }

            }

            @Override
            public void overSize(boolean isOverSize) {
                if (isOverSize) {
                    gifTagView.setVisibility(View.VISIBLE);

                } else {
                    gifTagView.setVisibility(View.GONE);

                }

            }

            @Override
            public void success(Bitmap bitmap) {
                if (needScale) {
                    ImageChatHelper.scaleImageViewCompat(imageChatMessage, bitmap, gifShownView);
                }

            }

            @Override
            public void failed() {

            }
        });
    }


    /**
     * 根据屏幕调整聊天图片的大小
     *
     * @param height
     * @param width
     * @param view
     */
    private static void scaleImageView(int height, int width, ImageView view) {
        float scaleSize = 1;

        if (MAX_IMG_LENGTH < width || MAX_IMG_LENGTH < height) {
            if (width > height) {
                scaleSize = (float) MAX_IMG_LENGTH / width;

            } else {
                scaleSize = (float) MAX_IMG_LENGTH / height;

            }

        } else if (MAX_IMG_LENGTH > width && MAX_IMG_LENGTH > height) {

            if (width > height) {
                scaleSize = ((float) MAX_IMG_LENGTH) / width;

            } else {
                scaleSize = ((float) MAX_IMG_LENGTH) / height;

            }

        }

        int scaledWidth = (int) (width * scaleSize);
        int scaledHeight = (int) (height * scaleSize);

        if(MIN_IMG_LENGTH > scaledWidth) {
            scaledWidth = MIN_IMG_LENGTH;
        }

        if(MIN_IMG_LENGTH > scaledHeight) {
            scaledHeight = MIN_IMG_LENGTH;
        }

        view.getLayoutParams().width = scaledWidth;
        view.getLayoutParams().height = scaledHeight;
    }

    public static void scaleImageViewCompat(ImageChatMessage imageChatMessage, Bitmap bitmap, ImageView view) {
        final boolean isScaled = ImageChatHelper.scaleImageView(imageChatMessage, view);
        if (!isScaled) {
            ImageChatHelper.scaleImageView(bitmap, view);
        }
    }

    /**
     * @see #scaleImageView(int, int, ImageView)
     */
    public static boolean scaleImageView(ImageChatMessage imageChatMessage, ImageView view) {
        int width = imageChatMessage.info.width;
        int height = imageChatMessage.info.height;

        boolean hasScaleInfo = false;
        if (0 < width && 0 < height) {
            scaleImageView(height, width, view);
            hasScaleInfo = true;
        }

        return hasScaleInfo;
    }


    /**
     * @see #scaleImageView(int, int, ImageView)
     */
    public static void scaleImageView(Bitmap bitmap, ImageView view) {
        scaleImageView(bitmap.getHeight(), bitmap.getWidth(), view);
    }

    /**
     * set the info of the img in the imageChatMessage
     */
    public static void setImageMessageInfo(ImageChatMessage imageChatMessage, String path) {
        String originalPath = EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(path, false);

        BitmapUtil.ImageInfo imageInfo = BitmapUtil.getImageInfo(originalPath);
        imageChatMessage.info.size = imageInfo.size;
        imageChatMessage.info.height = imageInfo.height;
        imageChatMessage.info.width = imageInfo.width;
        imageChatMessage.info.type = imageInfo.type;

    }


    /**
     * set the info of the img in the imageContentInfo
     */
    public static void setImageMessageInfo(ImageContentInfo imageContentInfo, String path) {
        String originalPath = EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(path, false);

        BitmapUtil.ImageInfo imageInfo = BitmapUtil.getImageInfo(originalPath);
        imageContentInfo.info.size = imageInfo.size;
        imageContentInfo.info.height = imageInfo.height;
        imageContentInfo.info.width = imageInfo.width;
        imageContentInfo.info.type = imageInfo.type;

    }

    public interface OnReloadImageListener {
        void onSuccess(Bitmap bitmap);

        void onFail();
    }
}
