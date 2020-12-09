package com.foreveross.atwork.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.foreverht.threadGear.ImageThreadPoolExecutor;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.beeworks.BeeWorksNetService;
import com.foreveross.atwork.api.sdk.net.RequestRemoteInterceptor;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptCacheDisk;
import com.foreveross.atwork.utils.img.WorkPlusDownloader;
import com.foreveross.atwork.utils.img.WorkPlusLimitedAgeDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.L;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * 图片缓存处理帮助类
 * Created by ReyZhang on 2015/4/28.
 */
public class ImageCacheHelper {

    private static final String TAG = ImageCacheHelper.class.getSimpleName();

    private static ImageLoader sImageLoader = ImageLoader.getInstance();

    final static int MAX_MEMORY = (int) (Runtime.getRuntime().maxMemory());

    //内存缓存大小
    private static final int MEMORY_CACHE_SIZE = MAX_MEMORY / 7;
    //本地缓存大小 50M
    private static final int DISK_CACHE_SIZE = 50 * 1024 * 1024;
    //缓存保存一个月
    private static final long AGE_DISK_CACHE = 30 * 24 * 3600;
    //内存size
    private static final LruMemoryCache sMemoryCache = new LruMemoryCache(MEMORY_CACHE_SIZE);
    //文件路径与命名
    private static AtworkMD5FileNameGenerator sMd5FileNameGenerator = new AtworkMD5FileNameGenerator();
    private static WorkPlusLimitedAgeDiskCache sDiskCache;


    public static void refreshLoader(Context context) {
        initDiskCache(context);
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context);
        builder.threadPriority(Thread.NORM_PRIORITY);
        builder.memoryCache(sMemoryCache);
        builder.memoryCacheSize(MEMORY_CACHE_SIZE);
        builder.diskCacheSize(DISK_CACHE_SIZE);
        builder.diskCache(sDiskCache);
        builder.denyCacheImageMultipleSizesInMemory();
        builder.imageDownloader(new WorkPlusDownloader(context));
//        builder.imageDecoder(new WorkPlusDecoder(BuildConfig.DEBUG));
        builder.taskExecutor(ImageThreadPoolExecutor.getInstance());
        ImageLoaderConfiguration config = builder.build();
        sImageLoader = ImageLoader.getInstance();
        if (sImageLoader.isInited()) {
            sImageLoader.destroy();
        }

        sImageLoader.init(config);
    }

    private static void initDiskCache(Context context) {
        File cacheDir = new File(AtWorkDirUtils.getInstance().getImageDiskCacheDir(LoginUserInfo.getInstance().getLoginUserUserName(context)));
        File reserveCacheDir = new File(AtWorkDirUtils.getInstance().getImageReserveDiskCacheDir(LoginUserInfo.getInstance().getLoginUserUserName(context)));
        sDiskCache = new WorkPlusLimitedAgeDiskCache(cacheDir, reserveCacheDir, sMd5FileNameGenerator, AGE_DISK_CACHE);
    }



    public static ImageLoader getImageLoader() {
        return sImageLoader;
    }

    public static void displayImageSimply(String uri, ImageView imageView) {
        sImageLoader.displayImage(uri, imageView);
    }

    public static void displayImage(String uri, final ImageView imageView, final DisplayImageOptions options) {
        displayImage(uri, imageView, options, null);
    }

    /**
     * 显示图片
     *
     * @param uri       图片文件URI， 如http://WWW.BAIDU.COM/32443.JPG  OR file:///sdcard/qwertt.jpg
     * @param imageView 要显示图片的imageview控件
     * @param options   option控制，通过 ImageCacheHelper.getXXXImageOptions获得，不同的option使用方式不同
     */
    @SuppressLint("StaticFieldLeak")
    public static void displayImage(String uri, final ImageView imageView, final DisplayImageOptions options, final ImageLoadedListener listener) {
        if (StringUtils.isEmpty(uri) || uri.startsWith("http") && !RequestRemoteInterceptor.INSTANCE.checkLegal(uri)) {
            setImageDrawable(imageView, options.getImageOnFail(imageView.getResources()));
            return;
        }

        final String uriHandled = handleUri(uri);
        if(uriHandled.startsWith("file://")) {
            String tag = UUID.randomUUID().toString();

            imageView.setTag(tag);

            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    return EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(uri, false);
                }

                @Override
                protected void onPostExecute(String path) {
                    if(null != imageView.getTag() && imageView.getTag().equals(tag)) {
                        imageLoaderDisplayImage(imageView, "file://" + path, options, listener);

                    }
                }
            }.executeOnExecutor(ImageThreadPoolExecutor.getInstance());


        } else {
            imageLoaderDisplayImage(imageView, uriHandled, options, listener);

        }
    }

    public static void imageLoaderDisplayImage(ImageView imageView, String uriHandled, DisplayImageOptions options, final ImageLoadedListener listener) {
        sImageLoader.displayImage(uriHandled, imageView, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (null != listener) {
                    listener.onImageLoadedFail();
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (null != listener) {
                    listener.onImageLoadedComplete(loadedImage);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
            }
        });
    }


    public static Bitmap loadImageByMediaSync(String mediaId) {
        LoginUserInfo loginInfo = LoginUserInfo.getInstance();
        if (loginInfo == null) {
            return null;
        }

        String url = "";
        if (!TextUtils.isEmpty(mediaId)) {
            url = getMediaUrl(mediaId);

        }
        return loadImageSync(url);
    }

    /**
     * 同步获取图片bitmap对象
     * @param uri
     * @return
     */
    public static Bitmap loadImageSync(String uri) {
        uri = handleUri(uri);
        return sImageLoader.loadImageSync(uri);
    }

    /**
     * 同步获取图片bitmap对象
     * @param uri
     * @return
     */
    public static Bitmap loadImageSync(String uri, DisplayImageOptions options) {
        uri = handleUri(uri);
        return sImageLoader.loadImageSync(uri, options);
    }


    public static boolean isImageViewMediaIdLoaded(ImageView imageView, String mediaId) {

        if(StringUtils.isEmpty(mediaId)) {
            return false;
        }

        if(("mediaId://" + mediaId).equals(imageView.getTag(R.id.key_imageId_loaded))) {
            return true;
        }

        return false;

    }

    public static void setImageViewMediaIdLoaded(@NonNull ImageView imageView, @NonNull String mediaId) {
        imageView.setTag(R.id.key_imageId_loaded, "mediaId://" + mediaId);
    }

    public static void resetImageViewMediaIdLoaded(@NonNull ImageView imageView) {
        imageView.setTag(R.id.key_imageId_loaded, null);
    }


    /**
     * @see #displayImage(String, ImageView, DisplayImageOptions, ImageLoadedListener)
     */
    public static void displayImageByMediaId(String mediaId, ImageView imageView, DisplayImageOptions options) {
        displayImageByMediaId(mediaId, imageView, options, null);
    }

    /**
     * 通过媒体id获取图片
     *
     * @param mediaId
     * @param imageView
     * @param options
     */
    public static void displayImageByMediaId(String mediaId, ImageView imageView, DisplayImageOptions options, ImageLoadedListener listener) {
        if(StringUtils.isEmpty(mediaId)) {
            setImageDrawable(imageView, options.getImageOnFail(imageView.getResources()));
            resetImageViewMediaIdLoaded(imageView);

            return;
        }

        String url = ImageCacheHelper.getMediaUrl(mediaId);
        displayImage(url, imageView, options, new ImageLoadedListener() {
            @Override
            public void onImageLoadedComplete(Bitmap bitmap) {

                setImageViewMediaIdLoaded(imageView, mediaId);
                if(null != listener) {
                    listener.onImageLoadedComplete(bitmap);
                }
            }

            @Override
            public void onImageLoadedFail() {
                resetImageViewMediaIdLoaded(imageView);

                if(null != listener) {
                    listener.onImageLoadedFail();
                }
            }
        });
    }



    public static void displayImageByMediaIdCompress(String mediaId, ImageView imageView, DisplayImageOptions options, ImageLoadedListener listener) {
        String url = getCompressImageByMediaId(mediaId, 0.5, 80, 80);
        displayImage(url, imageView, options, listener);
    }


    public static String getCompressImageByMediaId(String mediaId, double quality, int width, int height) {
        LoginUserInfo loginInfo = LoginUserInfo.getInstance();
        if (loginInfo == null) {
            return mediaId;
        }

        String url = "";
        if (!TextUtils.isEmpty(mediaId)) {
            url = String.format(UrlConstantManager.getInstance().V2_getCompressImageUrl(), mediaId, quality, width, height, LoginUserInfo.getInstance().getLoginUserAccessToken(BaseApplicationLike.baseContext));
        }
        return url;
    }

    public static void displayImageByMediaRes(String res, ImageView imageView, DisplayImageOptions options, ImageLoadedListener listener) {
        if(res.startsWith("http")) {
            displayImage(res, imageView, options, listener);

        } else {
            displayImageByMediaIdNotNeedToken(res, imageView, options, listener);
        }
    }

    public static void displayImageByMediaIdNotNeedToken(String mediaId, ImageView imageView, DisplayImageOptions options, ImageLoadedListener listener) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(BeeWorksNetService.getInstance().getMediaServerUrl()).append(mediaId);
        displayImage(stringBuffer.toString(), imageView, options, listener);
    }

    public static void loadImageByMediaNotNeedToken(String mediaId, DisplayImageOptions options, ImageLoadedListener listener) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(BeeWorksNetService.getInstance().getMediaServerUrl()).append(mediaId);
        loadImage(stringBuffer.toString(), null, options, listener);
    }


    /**
     * 区别于{@link #displayImageByMediaId(String, ImageView, DisplayImageOptions, ImageLoadedListener)},
     * 刚方法通过 Imageloader 的 loadImage 配合回调来展示 UI
     *
     * @param mediaId
     * @param displayImageOptions
     * @param listener
     */
    public static void loadImageByMediaId(String mediaId, DisplayImageOptions displayImageOptions, final ImageLoadedListener listener) {
        LoginUserInfo loginInfo = LoginUserInfo.getInstance();
        if (loginInfo == null) {
            return;
        }

        String url = "";
        if (!TextUtils.isEmpty(mediaId)) {
            url = getMediaUrl(mediaId);

        }

        loadImage(url, null, displayImageOptions, listener);
    }



    @NotNull
    public static String getMediaUrl(String mediaId) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(UrlConstantManager.getInstance().verifyApiMediaUrl())
                .append("medias/")
                .append(mediaId)
                .append("?domain_id=")
                .append(AtworkConfig.DOMAIN_ID)
                .append("&access_token=")
                .append(LoginUserInfo.getInstance().getLoginUserAccessToken(BaseApplicationLike.baseContext));
        return urlBuilder.toString();
    }

    /**
     * 区别于{@link #displayImageByMediaId(String, ImageView, DisplayImageOptions, ImageLoadedListener)},
     * 刚方法通过 Imageloader 的 loadImage 配合回调来展示 UI
     *
     * @param mediaId
     * @param listener
     */
    public static void loadImageByMediaId(String mediaId, final ImageLoadedListener listener) {
        loadImageByMediaId(mediaId, null, listener);
    }

    @SuppressLint("StaticFieldLeak")
    public static void loadImage(String url, ImageSize imageSize, DisplayImageOptions displayImageOptions, ImageLoadedListener listener) {
        final String uriHandled = handleUri(url);
        if(uriHandled.startsWith("file://")) {

            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    return EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(url, false);
                }

                @Override
                protected void onPostExecute(String path) {
                    imageLoaderLoadImage(imageSize, displayImageOptions, "file://" + path, listener);

                }
            }.executeOnExecutor(ImageThreadPoolExecutor.getInstance());

        } else {
            imageLoaderLoadImage(imageSize, displayImageOptions, uriHandled, listener);

        }
    }

    public static void imageLoaderLoadImage(ImageSize imageSize, DisplayImageOptions displayImageOptions, String uriHandled, final ImageLoadedListener listener) {
        sImageLoader.loadImage(uriHandled, imageSize, displayImageOptions, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                if (null != listener) {
                    listener.onImageLoadedFail();
                }
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                if (null != listener) {
                    listener.onImageLoadedComplete(bitmap);
                }
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }

    /**
     * 用url从网络上拉取图片
     */
    public static void loadImageByUrl(String uri, final ImageLoadedListener listener) {
        sImageLoader.loadImage(uri, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                if (null != listener) {
                    listener.onImageLoadedFail();
                }
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                if (null != listener) {
                    listener.onImageLoadedComplete(bitmap);
                }
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }

    public static void setImageResource(ImageView imageView, @DrawableRes int resId) {
        sImageLoader.cancelDisplayTask(imageView);
        imageView.setImageResource(resId);
    }

    public static void setImageDrawable(ImageView imageView,  Drawable res) {
        sImageLoader.cancelDisplayTask(imageView);
        imageView.setImageDrawable(res);
    }

    /**
     * 用于拍照显示的 options, 带有检查旋转
     * */
    public static DisplayImageOptions getCameraImageOptions() {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.cacheInMemory(false);
        builder.cacheOnDisk(true);
        builder.resetViewBeforeLoading(true);
        builder.considerExifParams(true);
        builder.bitmapConfig(Bitmap.Config.RGB_565);
        builder.imageScaleType(ImageScaleType.IN_SAMPLE_INT);
        DisplayImageOptions options = builder.build();
        return options;
    }



    /**
     * 获取默认的options
     *
     * @return
     */
    public static DisplayImageOptions getDefaultImageOptions(boolean isRoundBitmap, boolean cacheMemory, boolean cacheDisk) {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.cacheInMemory(cacheMemory);
        builder.cacheOnDisk(cacheDisk);
        builder.resetViewBeforeLoading(true);
        builder.bitmapConfig(Bitmap.Config.RGB_565);
        builder.imageScaleType(ImageScaleType.IN_SAMPLE_INT);
        if (isRoundBitmap) {
            builder.displayer(new RoundedBitmapDisplayer(10));
        }
        DisplayImageOptions options = builder.build();
        return options;
    }

    /**
     * 获取相册界面(瀑布流)的 option
     */
    public static DisplayImageOptions getPinteresteImageitemOptions() {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.cacheInMemory(true);
        builder.cacheOnDisk(false);
        builder.resetViewBeforeLoading(true);
        builder.bitmapConfig(Bitmap.Config.RGB_565);
        builder.imageScaleType(ImageScaleType.IN_SAMPLE_INT);
        builder.considerExifParams(true);
        DisplayImageOptions options = builder.build();
        return options;
    }


    /**
     * 获取应用图标option
     *
     * @return
     */
    public static DisplayImageOptions getAppImageOptions() {
        return getRoundOptions(R.mipmap.default_app, R.mipmap.loading_icon_size);
    }

    public static DisplayImageOptions getOrgLogoOptions() {
        return getRoundOptions(R.mipmap.icon_org, R.mipmap.icon_org);
    }

    public static DisplayImageOptions getImageOptions() {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.cacheOnDisk(true);
        builder.cacheInMemory(true);
        builder.showImageForEmptyUri(R.mipmap.loading_gray_holding);
        builder.showImageOnLoading(R.mipmap.loading_chat_size);
        builder.showImageOnFail(R.mipmap.loading_gray_holding);
        return builder.build();
    }

    public static DisplayImageOptions getDropboxImageOptions() {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.cacheOnDisk(true);
        builder.cacheInMemory(true);
        builder.showImageForEmptyUri(R.mipmap.no_dropbox_photo);
        builder.showImageOnLoading(R.mipmap.loading_chat_size);
        builder.showImageOnFail(R.mipmap.no_dropbox_photo);
        return builder.build();
    }

    public static DisplayImageOptions getBeeworksDefaultIconOptions() {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.cacheOnDisk(true);
        builder.cacheInMemory(true);
        builder.showImageForEmptyUri(R.mipmap.beeworks_default_icon);
        builder.showImageOnLoading(R.mipmap.beeworks_default_icon);
        builder.showImageOnFail(R.mipmap.beeworks_default_icon);
        return builder.build();
    }

    public static DisplayImageOptions getBeeworksLoginIconOptions() {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.cacheOnDisk(true);
        builder.cacheInMemory(true);
        builder.showImageForEmptyUri(R.mipmap.about_workplus_logo);
        builder.showImageOnLoading(R.mipmap.about_workplus_logo);
        builder.showImageOnFail(R.mipmap.about_workplus_logo);
        return builder.build();
    }

    public static DisplayImageOptions getBeeworksDefaultImageOptions() {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.cacheOnDisk(true);
        builder.cacheInMemory(true);
        builder.showImageForEmptyUri(R.mipmap.beeworks_default_image);
        builder.showImageOnLoading(R.mipmap.beeworks_default_image);
        builder.showImageOnFail(R.mipmap.beeworks_default_image);
        return builder.build();
    }

    /**
     * 获取"我"的界面的头像, 这里不使用加载中的头像占位
     *
     * @return
     */
    public static DisplayImageOptions getMyAccountAvatarOptions() {
        return getRoundOptions(R.mipmap.default_photo, -1);
    }

    public static void clearCache() {
        sImageLoader.clearDiskCache();
        sImageLoader.clearMemoryCache();
    }

    public static void checkPool() {
        if(ImageThreadPoolExecutor.getInstance().getActiveCount() > ImageThreadPoolExecutor.MAXIMUM_POOL_SIZE / 2) {
            stopAll();
        }
    }

    public static void stopAll() {
        sImageLoader.stop();
    }

    /**
     * @see #getRoundOptions(int, int)
     */
    public static DisplayImageOptions getRoundOptions(int resId) {
        return getRoundOptions(resId, resId);
    }

    public static DisplayImageOptions getRoundOptions(int resId, int loadingId) {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.cacheOnDisk(true);
        builder.cacheInMemory(true);
        if (-1 == loadingId) {
            builder.showImageOnLoading(null);
        } else {
            builder.showImageOnLoading(loadingId);
        }

        if (-1 != resId) {
            builder.showImageForEmptyUri(resId);
            builder.showImageOnFail(resId);
        }
        builder.displayer(new RoundedBitmapDisplayer(360));
        return builder.build();
    }

    public static DisplayImageOptions getRectOptions(int resId) {
        return getRectOptions(resId, resId);
    }

    public static DisplayImageOptions getRectOptions(int resId, int loadingId) {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.cacheOnDisk(true);
        builder.cacheInMemory(true);
        if (-1 == loadingId) {
            builder.showImageOnLoading(null);
        } else {
            builder.showImageOnLoading(loadingId);
        }

        if (-1 != resId) {
            builder.showImageForEmptyUri(resId);
            builder.showImageOnFail(resId);
        }
        builder.displayer(new RoundedBitmapDisplayer(8));
        return builder.build();
    }

    /**
     * 获取圆形头像
     *
     * @return
     */
    public static DisplayImageOptions getRoundAvatarOptions() {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.cacheOnDisk(true);
        builder.cacheInMemory(true);
        builder.showImageForEmptyUri(R.mipmap.round_default_photo);
        builder.showImageOnLoading(null);
        builder.showImageOnFail(R.mipmap.round_default_photo);
        builder.displayer(new RoundedBitmapDisplayer(360));
        return builder.build();
    }

    public static DisplayImageOptions getDefaultOptions() {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.cacheOnDisk(true);
        builder.cacheInMemory(true);
        return builder.build();
    }

    public static DisplayImageOptions getNotCacheOptions() {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.cacheOnDisk(false);
        builder.cacheInMemory(false);
        return builder.build();
    }

    public static String handleUri(String uri) {
        if (TextUtils.isEmpty(uri)) {
            return uri;
        }
        if (!uri.startsWith("http") && !uri.startsWith("file:") && !uri.startsWith("assets:") && !uri.startsWith("drawable:")) {
            uri = "file://" + uri;
        }
        return uri;
    }

    public static String getImgName(String uri) {
        return sMd5FileNameGenerator.generate(uri);
    }

    private static class AtworkMD5FileNameGenerator implements FileNameGenerator {

        private static final String HASH_ALGORITHM = "MD5";
        private static final int RADIX = 36;

        public AtworkMD5FileNameGenerator() {
        }

        @Override
        public String generate(String imageUri) {
            int lastIndex = imageUri.lastIndexOf("?");
            String fileName = imageUri;
            if (-1 != lastIndex) {
                fileName = imageUri.substring(0, lastIndex);
            }

//            LogUtil.e("CDN", "fileName image  key ->" + fileName);

            byte[] md5 = this.getMD5(fileName.getBytes());
            BigInteger bi = (new BigInteger(md5)).abs();
            return bi.toString(RADIX);
        }

        private byte[] getMD5(byte[] data) {
            byte[] hash = null;

            try {
                MessageDigest e = MessageDigest.getInstance(HASH_ALGORITHM);
                e.update(data);
                hash = e.digest();
            } catch (NoSuchAlgorithmException var4) {
                L.e(var4);
            }

            return hash;
        }
    }


    public interface ImageLoadedListener {
        void onImageLoadedComplete(Bitmap bitmap);

        void onImageLoadedFail();
    }
}
