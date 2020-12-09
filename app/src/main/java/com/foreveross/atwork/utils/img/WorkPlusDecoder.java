package com.foreveross.atwork.utils.img;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.decode.ImageDecodingInfo;
import com.nostra13.universalimageloader.utils.IoUtils;
import com.nostra13.universalimageloader.utils.L;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by dasunsy on 2017/2/23.
 */

public class WorkPlusDecoder extends BaseImageDecoder {

    public WorkPlusDecoder(boolean loggingEnabled) {
        super(loggingEnabled);
    }

    /**
     * Decodes image from URI into {@link Bitmap}. Image is scaled close to incoming {@linkplain ImageSize target size}
     * during decoding (depend on incoming parameters).
     *
     * @param decodingInfo Needed data for decoding image
     * @return Decoded bitmap
     * @throws IOException                   if some I/O exception occurs during image reading
     * @throws UnsupportedOperationException if image URI has unsupported scheme(protocol)
     */
    @Override
    public Bitmap decode(ImageDecodingInfo decodingInfo) throws IOException {
        Bitmap decodedBitmap;
        ImageFileInfo imageInfo;

        InputStream imageStream = getImageStream(decodingInfo);
        if (imageStream == null) {
            L.e(ERROR_NO_IMAGE_STREAM, decodingInfo.getImageKey());
            return null;
        }
        try {
            imageInfo = defineImageSizeAndRotation(imageStream, decodingInfo);
            imageStream = resetStream(imageStream, decodingInfo);
            BitmapFactory.Options decodingOptions = prepareDecodingOptions(imageInfo.imageSize, decodingInfo);
            decodedBitmap = BitmapFactory.decodeStream(imageStream, null, decodingOptions);
        } finally {
            IoUtils.closeSilently(imageStream);
        }

        if (decodedBitmap == null) {
            L.e(ERROR_CANT_DECODE_IMAGE, decodingInfo.getImageKey());
        } else {
            decodedBitmap = considerExactScaleAndOrientatiton(decodedBitmap, decodingInfo, imageInfo.exif.rotation,
                    imageInfo.exif.flipHorizontal);
        }
        return decodedBitmap;
    }

}
