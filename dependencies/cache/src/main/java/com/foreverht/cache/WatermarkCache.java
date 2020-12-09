package com.foreverht.cache;

import android.util.LruCache;

import com.foreveross.atwork.infrastructure.model.Watermark;

/**
 * Created by reyzhang22 on 17/3/22.
 */

public class WatermarkCache extends BaseCache  {

    private static final WatermarkCache sInstance = new WatermarkCache();

    private LruCache<String, Boolean> mWatermarkConfigCache = new LruCache<>(mMaxMemory / 10);

    public static WatermarkCache getInstance() {
        return sInstance;
    }


    public void setWatermarkConfigCache(Watermark watermark, boolean config) {
        mWatermarkConfigCache.put(watermark.mSourceId + watermark.mType.name(), config);
    }

    public boolean getWatermarkConfigCache(Watermark watermark) {
        Boolean watermarkConfig = getWatermark(watermark);
        return null == watermarkConfig ?  false : watermarkConfig;
    }

    public Boolean getWatermark(Watermark watermark) {
        return mWatermarkConfigCache.get(watermark.mSourceId + watermark.mType.name());
    }

}
