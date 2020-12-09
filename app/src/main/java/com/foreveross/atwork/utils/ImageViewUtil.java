package com.foreveross.atwork.utils;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.utils.ViewCompat;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.theme.ninePatch.NinePatchChunk;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lingen on 16/1/21.
 */
public class ImageViewUtil {


    public static int getResourceInt(String resourceName) {
        if (TextUtils.isEmpty(resourceName)) {
            return -1;
        }
        Class<R.mipmap> cls = R.mipmap.class;
        try {
            int value = cls.getDeclaredField(resourceName).getInt(null);
            return value;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return -1;
    }


    public static Bitmap getImageFromAssetsFile(String fileName) {
        Bitmap image = null;
        AssetManager am = BaseApplicationLike.baseContext.getResources().getAssets();
        InputStream is = null;
        try {
            is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
//            LogUtil.e(e.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return image;

    }

    /**
     * 设置 view 的 background 属性, 读取.9的资源
     * */
    public static void setViewBgNineDrawable(View view, Bitmap bitmap) {
        ViewCompat.setBackground(view, NinePatchChunk.create9PatchDrawable(view.getContext(), bitmap, null));
    }


    public static void setMaxHeight(ImageView imageView, int maxHeight) {
        if(maxHeight < imageView.getHeight()) {
            ViewUtil.setHeight(imageView, maxHeight);
        }
    }

}
