/*
 *          Copyright (C) 2016 jarlen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package cn.jarlen.photoedit.operate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.View;

import com.foreveross.atwork.infrastructure.utils.ScreenUtils;

/**
 * 添加文字图片工具类
 */
public class OperateUtils {

    /**
     * 根据路径获取图片并且压缩，适应view
     *
     * @param filePath    图片路径
     * @param contentView 适应的view
     * @return Bitmap 压缩后的图片
     */
    public static Bitmap compressionFiller(Context context, String filePath, View contentView) {
        int screenWidth = ScreenUtils.getScreenWidth(context);

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, opt);
        int layoutHeight = contentView.getHeight();
        float scale = 0f;
        int bitmapHeight = bitmap.getHeight();
        int bitmapWidth = bitmap.getWidth();
        scale = bitmapHeight > bitmapWidth
                ? layoutHeight / (bitmapHeight * 1f)
                : screenWidth / (bitmapWidth * 1f);
        Bitmap resizeBmp;
        if (scale != 0) {
            int bitmapheight = bitmap.getHeight();
            int bitmapwidth = bitmap.getWidth();
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale); // 长和宽放大缩小的比例
            resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmapwidth,
                    bitmapheight, matrix, true);
        } else {
            resizeBmp = bitmap;
        }
        return resizeBmp;
    }

    /**
     * 根据压缩图片并且适应view
     *
     * @param bitmap      压缩图片
     * @param contentView 适应的view
     * @return 压缩后的图片
     */
    public static Bitmap compressionFiller(Context context, Bitmap bitmap, View contentView) {
        int screenWidth = ScreenUtils.getScreenWidth(context);

        int layoutHeight = contentView.getHeight();

        float scale = 0f;
        int bitmapHeight = bitmap.getHeight();
        int bitmapWidth = bitmap.getWidth();
        scale = bitmapHeight > bitmapWidth
                ? layoutHeight / (bitmapHeight * 1f)
                : screenWidth / (bitmapWidth * 1f);
        Bitmap resizeBmp;
        if (scale != 0) {
            int bitmapheight = bitmap.getHeight();
            int bitmapwidth = bitmap.getWidth();
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale); // 长和宽放大缩小的比例
            resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmapwidth,
                    bitmapheight, matrix, true);
        } else {
            resizeBmp = bitmap;
        }
        return resizeBmp;
    }

}
