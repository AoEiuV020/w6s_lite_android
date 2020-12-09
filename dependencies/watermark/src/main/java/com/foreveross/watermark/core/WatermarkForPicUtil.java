package com.foreveross.watermark.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.foreveross.atwork.infrastructure.utils.DensityUtil;

/**
 * Created by reyzhang22 on 2017/10/10.
 * 处理图片合成水印工具类
 */

public class WatermarkForPicUtil {

    //------------------------------------------图片与图片合成部分------------------------------------

    /**
     * 设置水印图片在左上角
     * @param context
     * @param src
     * @param watermark
     * @param paddingLeft
     * @param paddingTop
     * @return
     */
    public static Bitmap createWaterMaskLeftTop(Context context, Bitmap src, Bitmap watermark,
            int paddingLeft, int paddingTop) {
        return createWaterMaskBitmap(src, watermark,
                DensityUtil.dip2px(paddingLeft), DensityUtil.dip2px(paddingTop));
    }

    /**
     * 设置水印图片到右上角
     * @param context
     * @param src
     * @param watermark
     * @param paddingRight
     * @param paddingTop
     * @return
     */
    public static Bitmap createWaterMaskRightTop(Context context, Bitmap src, Bitmap watermark,
            int paddingRight, int paddingTop) {
        return createWaterMaskBitmap( src, watermark,
                src.getWidth() - watermark.getWidth() - DensityUtil.dip2px(paddingRight),
                DensityUtil.dip2px(paddingTop));
    }

    /**
     * 设置水印图片在右下角
     * @param context
     * @param src
     * @param watermark
     * @param paddingRight
     * @param paddingBottom
     * @return
     */
    public static Bitmap createWaterMaskRightBottom(Context context, Bitmap src, Bitmap watermark,
            int paddingRight, int paddingBottom) {
        return createWaterMaskBitmap(src, watermark,
                src.getWidth() - watermark.getWidth() - DensityUtil.dip2px(paddingRight),
                src.getHeight() - watermark.getHeight() - DensityUtil.dip2px(paddingBottom));
    }

    /**
     * 设置水印图片到左下角
     * @param context
     * @param src
     * @param watermark
     * @param paddingLeft
     * @param paddingBottom
     * @return
     */
    public static Bitmap createWaterMaskLeftBottom(Context context, Bitmap src, Bitmap watermark,
            int paddingLeft, int paddingBottom) {
        return createWaterMaskBitmap(src, watermark, DensityUtil.dip2px(paddingLeft),
                src.getHeight() - watermark.getHeight() - DensityUtil.dip2px(paddingBottom));
    }

    /**
     * 设置水印图片到中间
     * @param src
     * @param watermark
     * @return
     */
    public static Bitmap createWaterMaskCenter(Bitmap src, Bitmap watermark) {
        return createWaterMaskBitmap(src, watermark,
                (src.getWidth() - watermark.getWidth()) / 2,
                (src.getHeight() - watermark.getHeight()) / 2);
    }

    private static Bitmap createWaterMaskBitmap(Bitmap src, Bitmap watermark,
                                                int paddingLeft, int paddingTop) {
        if (src == null) {
            return null;
        }
        int width = src.getWidth();
        int height = src.getHeight();
        //创建一个bitmap
        Bitmap newb = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        //将该图片作为画布
        Canvas canvas = new Canvas(newb);
        //在画布 0，0坐标上开始绘制原始图片
        canvas.drawBitmap(src, 0, 0, null);
        //在画布上绘制水印图片
        canvas.drawBitmap(watermark, paddingLeft, paddingTop, null);
        // 保存
        canvas.save();
        // 存储
        canvas.restore();
        return newb;
    }

    //------------------------------------------图片与文字合成部分------------------------------------

    /**
     * 给图片添加文字到左上角
     * @param context
     * @param bitmap
     * @param text
     * @return
     */
    public static Bitmap drawTextToLeftTop(Context context, Bitmap bitmap, String text, DrawWaterMark drawWaterMark, int paddingLeft, int paddingTop) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(drawWaterMark.mTextColor);
        paint.setTextSize(DensityUtil.dip2px(drawWaterMark.mTextSize));
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(context, bitmap, text, paint, bounds,
                DensityUtil.dip2px(paddingLeft),
                DensityUtil.dip2px(paddingTop) + bounds.height());
    }

    /**
     * 绘制文字到右下角
     * @param context
     * @param bitmap
     * @param text
     * @return
     */
    public static Bitmap drawTextToRightBottom(Context context, Bitmap bitmap, String text, DrawWaterMark drawWaterMark, int paddingRight, int paddingBottom) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(drawWaterMark.mTextColor);
        paint.setTextSize(DensityUtil.dip2px(drawWaterMark.mTextSize));
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(context, bitmap, text, paint, bounds,
                bitmap.getWidth() - bounds.width() - DensityUtil.dip2px(paddingRight),
                bitmap.getHeight() - DensityUtil.dip2px(paddingBottom));
    }

    /**
     * 绘制文字到右上方
     * @param context
     * @param bitmap
     * @param text
     * @param paddingRight
     * @param paddingTop
     * @return
     */
    public static Bitmap drawTextToRightTop(Context context, Bitmap bitmap, String text, DrawWaterMark drawWaterMark, int paddingRight, int paddingTop) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(drawWaterMark.mTextColor);
        paint.setTextSize(DensityUtil.dip2px(drawWaterMark.mTextSize));
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(context, bitmap, text, paint, bounds,
                bitmap.getWidth() - bounds.width() - DensityUtil.dip2px(paddingRight),
                DensityUtil.dip2px(paddingTop) + bounds.height());
    }

    /**
     * 绘制文字到左下方
     * @param context
     * @param bitmap
     * @param text
     * @param paddingLeft
     * @param paddingBottom
     * @return
     */
    public static Bitmap drawTextToLeftBottom(Context context, Bitmap bitmap, String  text, DrawWaterMark drawWaterMark, int paddingLeft, int paddingBottom) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(drawWaterMark.mTextColor);

        paint.setTextSize(DensityUtil.sp2px(drawWaterMark.mTextSize));

        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(context, bitmap, text, paint, bounds,
                DensityUtil.dip2px(paddingLeft),
                bitmap.getHeight() - DensityUtil.dip2px(paddingBottom));
    }

    /**
     * 绘制文字到中间
     * @param context
     * @param bitmap
     * @param text
     * @return
     */
    public static Bitmap drawTextToCenter(Context context, Bitmap bitmap, String text, DrawWaterMark drawWaterMark) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(drawWaterMark.mTextColor);
        paint.setTextSize(DensityUtil.dip2px(drawWaterMark.mTextSize));
        Rect bounds = new Rect();
        return drawTextToBitmap(context, bitmap, text, paint, bounds,
                (bitmap.getWidth() - bounds.width()) / 2,
                (bitmap.getHeight() + bounds.height()) / 2);
    }

    //图片上绘制文字
    private static Bitmap drawTextToBitmap(Context context, Bitmap bitmap, String text,
                                           Paint paint, Rect bounds, int paddingLeft, int paddingTop) {
        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();

        paint.setDither(true); // 获取跟清晰的图像采样
        paint.setFilterBitmap(true);// 过滤一些
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);
        String[] lines = text.split("\n");

        int yoff = 0 - ((bounds.height() + 10) * (lines.length -1));
        for (int i = 0; i < lines.length; ++i) {
            paint.getTextBounds(lines[i], 0, lines[i].length(), bounds);
            canvas.drawText(lines[i], paddingLeft, paddingTop + yoff, paint);
            yoff += bounds.height() + 10;
        }
//        canvas.drawText(text, paddingLeft, paddingTop, paint);
        return bitmap;
    }


}
