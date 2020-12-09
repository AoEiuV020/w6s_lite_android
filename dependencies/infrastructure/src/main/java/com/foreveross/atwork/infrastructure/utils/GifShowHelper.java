package com.foreveross.atwork.infrastructure.utils;

import android.content.Context;

/**
 * Created by dasunsy on 2017/7/25.
 */

public class GifShowHelper {

    public static byte[] getGifByte(Context context, String deliveryId, String mediaId) {
        byte[] gifByte = ImageShowHelper.getOriginalImage(context, mediaId);
        if(ArrayUtil.isEmpty(gifByte)) {
            gifByte = ImageShowHelper.getOriginalImage(context, deliveryId);
        }

        return gifByte;
    }

}
