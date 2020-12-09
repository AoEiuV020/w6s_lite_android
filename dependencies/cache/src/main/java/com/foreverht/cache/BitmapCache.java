package com.foreverht.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.Nullable;
import android.util.LruCache;

import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.StickerChatMessage;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.BitmapUtil;
import com.foreveross.atwork.infrastructure.utils.GifShowHelper;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import pl.droidsonroids.gif.GifDrawable;


/**
 * Created by lingen on 15/6/17.
 * Description:
 */
public class BitmapCache extends BaseCache{

    private BitmapCache() {

    }

    private static BitmapCache bitmapCache = new BitmapCache();

    public static BitmapCache getBitmapCache() {
        return bitmapCache;
    }

    // Use 1/8th of the available memory for this memory cache.
    final int cacheSize = mMaxMemory / 8;

    private static Set<String> originalImgBlackList = new HashSet<>();

    private LruCache<String, Bitmap> mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
        @Override
        protected int sizeOf(String key, Bitmap bitmap) {
            return bitmap.getByteCount() / 1024;
        }
    };

    public int getCacheSize() {
        return cacheSize;
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (key == null || bitmap == null) {
            return;
        }
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        Bitmap bitmap = mMemoryCache.get(key);
        if (bitmap != null && bitmap.isRecycled()) {
            mMemoryCache.remove(key);
            return null;
        }

        return bitmap;
    }



    @Nullable
    public Bitmap getContentBitmapCache(ImageChatMessage imageChatMessage) {
        Bitmap bitmap = getBitmapFromMemCache(imageChatMessage.deliveryId + ImageChatMessage.ORIGINAL_SUFFIX);
        if(null == bitmap) {
            bitmap = getBitmapFromMemCache(imageChatMessage.deliveryId + ImageChatMessage.THUMBNAIL_SUFFIX);
        }

        return bitmap;
    }

    @Nullable
    public Bitmap getContentBitmap(Context context, ImageChatMessage imageChatMessage) {

        return getContentBitmap(context, imageChatMessage.deliveryId, imageChatMessage.mediaId, imageChatMessage.isGif, ImageShowHelper.getChatMsgImgThumbnail(imageChatMessage));
    }



    @Nullable
    public Bitmap getContentBitmap(Context context, String deliveryId, String mediaId, boolean isGif, byte[] thumbnails) {
        //首先首先找原图缓存
        Bitmap bitmap = getOriginalBitmap(context, deliveryId, mediaId, isGif);

        if(null != bitmap) {
            return bitmap;
        }

        bitmap = getThumbBitmap(context, deliveryId, thumbnails);


        return bitmap;
    }

    @Nullable
    public Bitmap getThumbBitmap(Context context, String deliveryId, byte[] thumbnails) {
        Bitmap bitmap;//通过图片id从内存找缩略图，如果找到则返回bitmap对象
        bitmap = getBitmapFromMemCache(deliveryId + ImageChatMessage.THUMBNAIL_SUFFIX);

        if (null == bitmap) {
            //如果没有缩略图值的情况下
            if (thumbnails != null && thumbnails.length > 0) {
                bitmap = BitmapFactory.decodeByteArray(thumbnails, 0, thumbnails.length);
                if (bitmap != null) {
                    addBitmapToMemoryCache(deliveryId + ImageChatMessage.THUMBNAIL_SUFFIX, bitmap);
                }
            }

        }

        if(null == bitmap) {
            //查找本地是否存在缩略图文件
            bitmap = BitmapUtil.Bytes2Bitmap(ImageShowHelper.getThumbnailImage(context, deliveryId));
            if (bitmap != null) {
                addBitmapToMemoryCache(deliveryId + ImageChatMessage.THUMBNAIL_SUFFIX, bitmap);
            }
        }
        return bitmap;
    }

    @Nullable
    private Bitmap getOriginalBitmap(Context context, String deliveryId, String mediaId, boolean isGif) {


        Bitmap bitmap = getBitmapFromMemCache(deliveryId + ImageChatMessage.ORIGINAL_SUFFIX);

        if (null == bitmap) {
            //如果本地查找不到，尝试找一次本地的原图
            if (isGif) {
                try {
                    GifDrawable gif = new GifDrawable(GifShowHelper.getGifByte(context, deliveryId, mediaId));
                    //the first frame is the cover
                    bitmap = gif.seekToFrameAndGet(0);
                    gif.recycle();
                    gif = null;

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {

                if(originalImgBlackList.contains(mediaId)) {
                    return null;
                }

                File originalFile = new File(ImageShowHelper.getOriginalPath(context, deliveryId));
                if(AtworkConfig.CHAT_IMG_SHOW_LIMIT >= originalFile.length()) {
                    bitmap = BitmapUtil.Bytes2Bitmap(ImageShowHelper.getOriginalImage(context, deliveryId));
                }

                if(null != bitmap) {
                    if(AtworkConfig.CHAT_IMG_SHOW_LIMIT < bitmap.getByteCount()) {
                        bitmap.recycle();
                        bitmap = null;

                        originalImgBlackList.add(mediaId);
                    }
                }

            }

            if (null != bitmap) {


                //存成缩略图的cache
                addBitmapToMemoryCache(deliveryId + ImageChatMessage.ORIGINAL_SUFFIX, bitmap);
            }
        }
        return bitmap;
    }
}
