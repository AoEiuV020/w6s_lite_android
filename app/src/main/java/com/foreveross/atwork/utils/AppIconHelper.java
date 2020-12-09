package com.foreveross.atwork.utils;

import android.content.Context;
import android.widget.ImageView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.upload.MediaCenterSyncNetService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * Created by dasunsy on 15/8/25.
 * 专门应用市场的图片加载, 使用 imageLoader
 */
public class AppIconHelper {
    public static void setAppIcon(final Context context, final AppBundles appBundle, final ImageView imageView, boolean needLoading) {
        if (null != appBundle ) {
            MediaCenterSyncNetService mediaCenterSyncNetService = MediaCenterSyncNetService.getInstance();

            String downloadUrl = mediaCenterSyncNetService.getMediaUrl(context, appBundle.mIcon);
            DisplayImageOptions displayImageOptions;
            if (needLoading) {
                displayImageOptions = getRoundOptions(R.mipmap.appstore_loading_icon_size, R.mipmap.appstore_loading_icon_size);
            } else {
                displayImageOptions = getRoundOptions(R.mipmap.appstore_loading_icon_size, -1);

            }
            ImageCacheHelper.displayImage(downloadUrl, imageView, displayImageOptions);
        } else {
            imageView.setImageResource(R.mipmap.appstore_loading_icon_size);
        }
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
        builder.displayer(new RoundedBitmapDisplayer(DensityUtil.dip2px(8)));
        return builder.build();
    }
}
