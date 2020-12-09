package com.foreveross.atwork.modules.ocr.component;/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.foreveross.atwork.R;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 */
public final class OcrViewfinderView extends View {
    private static final String TAG = "log";
    /**
     * 刷新界面的时间
     */
    private static final long ANIMATION_DELAY = 10L;
    private static final int OPAQUE = 0xFF;

    /**
     * 四个绿色边角对应的长度
     */
    private int connerLength;

    /**
     * 四个绿色边角对应的宽度
     */
    private static final int CORNER_WIDTH = 4;

    /**
     * 边界线
     */
    private static final int BORDER_LINE = 1;
    /**
     * 扫描框中的中间线的宽度
     */
    private static final int MIDDLE_LINE_WIDTH = 5;

    /**
     * 扫描框中的中间线的与扫描框左右的间隙
     */
    private static final int MIDDLE_LINE_PADDING = 5;

    /**
     * 中间那条线每次刷新移动的距离
     */
    private static final int DEFAULT_SCREEN_DISTANCE = 5;

    /**
     * 手机的屏幕密度
     */
    private static float density;
    /**
     * 字体大小
     */
    private static final int TEXT_SIZE = 13;
    /**
     * 字体距离扫描框下面的距离
     */
    private static final int TEXT_PADDING_TOP = 30;

    /**
     * 画笔对象的引用
     */
    private Paint paint;

    /**
     * 中间滑动线的最顶端位置
     */
    private int slideTop;

    /**
     * 中间滑动现的最右侧位置
     */
    private int slideRight;

    private int slideLeft;

    /**
     * 中间滑动线的最底端位置
     */
    private int slideBottom;

    /**
     * 将扫描的二维码拍下来，这里没有这个功能，暂时不考虑
     */
    private Bitmap resultBitmap;
    private final int maskColor;
    private final int resultColor;

    boolean mIsSquare = true;

    boolean isFirst;

    //中间的扫描框
    public Rect frame;


    private int mScreenDistance = DEFAULT_SCREEN_DISTANCE;

    public OcrViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        density = context.getResources().getDisplayMetrics().density;
        //将像素转换成dp
        connerLength = (int) (10 * density);

        paint = new Paint();
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.qrcode_viewfinder_mask);
        resultColor = resources.getColor(R.color.qrcode_result_view);


    }

    @Override
    public void onDraw(Canvas canvas) {


        //初始化中间线滑动的最上边和最下边
        if (!isFirst) {
            isFirst = true;
            if (mIsSquare) {
                slideTop = frame.top;
                slideBottom = frame.bottom;
            } else {
                slideRight = frame.right;
                slideLeft = frame.left;
            }

        }

        //获取屏幕的宽和高
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        paint.setColor(resultBitmap != null ? resultColor : maskColor);

        //画出扫描框外面的阴影部分，共四个部分，扫描框的上面到屏幕上面，扫描框的下面到屏幕下面
        //扫描框的左边面到屏幕左边，扫描框的右边到屏幕右边
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1,
                paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);


        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(OPAQUE);
            canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
        } else {

            //画扫描框边上的角，总共8个部分
            paint.setColor(getResources().getColor(R.color.white));
            canvas.drawRect(frame.left, frame.top, frame.left + connerLength, frame.top + CORNER_WIDTH, paint);
            canvas.drawRect(frame.left, frame.top, frame.left + CORNER_WIDTH, frame.top + connerLength, paint);
            canvas.drawRect(frame.right - connerLength, frame.top, frame.right, frame.top + CORNER_WIDTH, paint);
            canvas.drawRect(frame.right - CORNER_WIDTH, frame.top, frame.right, frame.top + connerLength, paint);
            canvas.drawRect(frame.left, frame.bottom - CORNER_WIDTH, frame.left + connerLength, frame.bottom, paint);
            canvas.drawRect(frame.left, frame.bottom - connerLength, frame.left + CORNER_WIDTH, frame.bottom, paint);
            canvas.drawRect(frame.right - connerLength, frame.bottom - CORNER_WIDTH, frame.right, frame.bottom, paint);
            canvas.drawRect(frame.right - CORNER_WIDTH, frame.bottom - connerLength, frame.right, frame.bottom, paint);

            //边界线
            canvas.drawRect(frame.left, frame.top, frame.right, frame.top + BORDER_LINE, paint);
            canvas.drawRect(frame.left, frame.top, frame.left + BORDER_LINE, frame.bottom, paint);
            canvas.drawRect(frame.left, frame.bottom - BORDER_LINE, frame.right, frame.bottom, paint);
            canvas.drawRect(frame.right - BORDER_LINE, frame.top, frame.right, frame.bottom, paint);

            //绘制中间的线,每次刷新界面，中间的线往下移动SCREEN_DISTANCE
            if (mIsSquare) {
                slideTop += mScreenDistance;
                if (slideTop >= frame.bottom) {
                    slideTop = frame.top;
                }
                canvas.drawRect(frame.left + MIDDLE_LINE_PADDING, slideTop - MIDDLE_LINE_WIDTH / 2, frame.right - MIDDLE_LINE_PADDING, slideTop + MIDDLE_LINE_WIDTH / 2, paint);

            } else {
                slideRight -= mScreenDistance;
                if (slideRight <= frame.left) {
                    slideRight = frame.right;
                }
                canvas.drawRect(slideRight + MIDDLE_LINE_WIDTH / 2,  frame.top + MIDDLE_LINE_PADDING, slideRight - MIDDLE_LINE_WIDTH / 2, frame.bottom - MIDDLE_LINE_PADDING, paint);
            }


            //画扫描框下面的字
            String text = getResources().getString(R.string.scan_text);
//            String[] textArray = text.split("\\n");
            paint.setColor(Color.WHITE);
            paint.setTextSize(TEXT_SIZE * density);
            paint.setTypeface(Typeface.create("System", Typeface.NORMAL));
//            canvas.drawText(text, frame.left, (float) (frame.bottom + (float) TEXT_PADDING_TOP * density), paint);
//            canvas.drawText(textArray[1], frame.left, (float) (frame.bottom + (float) TEXT_PADDING_TOP * 2 * density), paint);




            //只刷新扫描框的内容，其他地方不刷新
            postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top,
                    frame.right, frame.bottom);

        }
    }

    public void drawViewfinder() {
        //控制扫描框速率
        mScreenDistance = (int) (frame.height() / (1000 / ANIMATION_DELAY));
        if(0 == mScreenDistance) {
            mScreenDistance = 1;
        }

        int shortLength = frame.width() <= frame.height() ? frame.width(): frame.height();

        if(shortLength < connerLength) {
            connerLength = shortLength;
        }

        if(0 == shortLength) {
            connerLength = 1;
        }

        resultBitmap = null;
        invalidate();
    }

    public void setScreenDistance(int screenDistance) {
        this.mScreenDistance = screenDistance;
    }

}
