package com.foreveross.atwork.infrastructure.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Base64;
import android.util.Log;

import com.foreveross.atwork.infrastructure.utils.encryption.Base64Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


public class BitmapUtil {

    /**
     * bitmap转为base64
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    /**
     * @see #Bitmap2Bytes(Bitmap, boolean)
     */
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        return Bitmap2Bytes(bm, true);
    }

    /**
     * bitmap转成byte[]
     *
     * @param bm
     * @return
     */
    public static byte[] Bitmap2Bytes(Bitmap bm, boolean needRecycle) {
        long startTime = System.currentTimeMillis();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (bm != null) {
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos);

            if (needRecycle) {
                bm.recycle();
                bm = null;
            }

        }
        long endTime = System.currentTimeMillis();

        Log.e("test", "bitmap2byte time : " + (endTime - startTime));

        return baos.toByteArray();
    }

    public static byte[] Bitmap2JpgBytes(Bitmap bm, boolean needRecycle) {
        long startTime = System.currentTimeMillis();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (bm != null) {
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            if (needRecycle) {
                bm.recycle();
                bm = null;
            }

        }
        long endTime = System.currentTimeMillis();

        Log.e("test", "bitmap2byte time : " + (endTime - startTime));

        return baos.toByteArray();
    }

    /**
     * byte[] 转成BITMAP
     *
     * @param b
     * @return
     */
    public static Bitmap Bytes2Bitmap(byte[] b) {
        try {
            if (b != null && b.length != 0) {
                return BitmapFactory.decodeByteArray(b, 0, b.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 将base64转换成bitmap图片
     *
     * @param str base64字符串
     * @return bitmap
     */
    public static Bitmap strToBitmap(String str) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64Util.decode(str);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    /**
     * 图片压缩，按质量就行压缩
     *
     * @see #compressImageForQuality(Bitmap, int)
     */
    public static byte[] compressImageForQuality(Bitmap image, int size) {
        return compressImageForQuality(image, Bitmap.CompressFormat.JPEG, size);
    }

    /**
     * 图片压缩，按质量就行压缩
     *
     * @param image
     * @param format
     * @param size
     * @return
     */
    public static byte[] compressImageForQuality(Bitmap image, Bitmap.CompressFormat format, int size) {

        if (image == null) {
            return new byte[0];
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {

            // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            image.compress(format, 90, baos);
            // 循环判断如果压缩后图片是否大于指定大小,大于继续压缩
            int max = 90;
            while ((baos.toByteArray().length) > size) {

                if (max <= 0) {
                    break;
                }

                // 重置baos即清空baos
                baos.reset();


                if (10 < max) {
                    max -= 10;
                } else {
                    max -= 1;
                }
                image.compress(format, max, baos);
            }

        } finally {
            image.recycle();
            image = null;
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return baos.toByteArray();
    }


    public static Bitmap adjustOrientation(Bitmap bm, String path) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
            exif = null;
        }
        if (bm == null || exif == null) {
            return bm;
        }
        int digree = 0;
        // 读取图片中相机方向信息
        int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        // 计算旋转角度
        switch (ori) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                digree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                digree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                digree = 270;
                break;
            default:
                digree = 0;
                break;
        }
        if (digree != 0) {
            // 旋转图片
            bm = degree(bm, digree);
        }
        return bm;
    }

    public static Bitmap degree(Bitmap bm, int degree) {
        Matrix m = new Matrix();
        m.postRotate(degree);
        bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
        return bm;
    }


    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        int wScale = (int) Math.ceil(1.0 * width / reqWidth);
        int yScale = (int) Math.ceil(1.0 * height / reqHeight);
        inSampleSize = wScale > yScale ? wScale : yScale;

        if (inSampleSize < 1) {
            inSampleSize = 1;
        }
        return inSampleSize;
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }


    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }


    public static Bitmap setMaxHeight(Bitmap bitmap, int max) {
        if (null != bitmap) {
            if (max < bitmap.getHeight()) {
                float rate = (float) max / bitmap.getWidth();
                //获取想要缩放的matrix
                Matrix matrix = new Matrix();
                matrix.postScale(rate, rate);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            }
        }

        return bitmap;

    }


    /**
     * 通过不占内存的方式获取图片的信息
     */
    public static ImageInfo getImageInfo(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        ImageInfo info = new ImageInfo();
        info.height = options.outHeight;
        info.width = options.outWidth;
        info.size = new File(imagePath).length();
        info.type = options.outMimeType;
        options.inJustDecodeBounds = false;
        return info;
    }

    public static class ImageInfo {
        public int height;
        public int width;
        public long size;
        public String type;
    }

}
