package com.foreveross.atwork.modules.dropbox.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.foreveross.atwork.R;
import com.foreveross.atwork.utils.ImageCacheHelper;

/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 * Created by reyzhang22 on 16/9/26.
 */

public class SortedImageItem extends LinearLayout {

    private ImageView mImageView;

    public SortedImageItem(Context context) {
        super(context);
        initViews(context);
    }

    private void initViews(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_sorted_image, this);
        mImageView = (ImageView)view.findViewById(R.id.sorted_imageview);
    }

    /**
     *
     * @param mediaId       原图id
     * @param thumbnailId   缩略图id
     */
    public void setImage(String mediaId, String thumbnailId) {
        ImageCacheHelper.displayImageByMediaId(thumbnailId, mImageView, ImageCacheHelper.getRectOptions(R.mipmap.loading_cover_size), new ImageCacheHelper.ImageLoadedListener() {
            @Override
            public void onImageLoadedComplete(Bitmap bitmap) {

            }

            @Override
            public void onImageLoadedFail() {
                if (TextUtils.isEmpty(thumbnailId)) {
                    return;
                }
                //尝试使用显示base64
                byte[] decode = Base64.decode(thumbnailId, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
                if (bitmap != null) {
                    mImageView.setImageBitmap(bitmap);
                } else {
                    ImageCacheHelper.displayImageByMediaId(mediaId, mImageView, ImageCacheHelper.getRectOptions(R.mipmap.loading_cover_size));
                }

            }
        });
    }
}
