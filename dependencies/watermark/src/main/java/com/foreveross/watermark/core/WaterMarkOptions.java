package com.foreveross.watermark.core;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;

/**
 * 水印配置类
 * Created by reyzhang22 on 17/3/8.
 */

public final class WaterMarkOptions {

    private int mCanvasBgColor;

    private int mPaintColor;

    private int mPaintAlpha;

    private boolean mAntiAlias;

    private int mTextSize;

    private Paint.Align mAlign;

    private Shader.TileMode mTitleModeX;

    private Shader.TileMode mTitleModeY;

    private MoveTo mMoveTo;

    private TextOnPath mTextOnPath;

    public int getCanvasBgColor() {
        return mCanvasBgColor;
    }

    public int getPaintColor() {
        return mPaintColor;
    }

    public int getPaintAlpha() {
        return mPaintAlpha;
    }

    public boolean isAntiAlias() {
        return mAntiAlias;
    }

    public int getTextSize() {
        return mTextSize;
    }

    public Paint.Align getAlign() {
        return mAlign;
    }

    public Shader.TileMode getTitleModeX() {
        return mTitleModeX;
    }

    public Shader.TileMode getTitleModeY() {
        return mTitleModeY;
    }

    public MoveTo getMoveTo() {
        return mMoveTo;
    }

    public TextOnPath getTextOnPath() {
        return mTextOnPath;
    }

    private WaterMarkOptions(Builder builder) {
        mCanvasBgColor = builder.mCanvasBgColor;
        mPaintColor = builder.mPaintColor;
        mPaintAlpha = builder.mPaintAlpha;
        mAntiAlias = builder.mAntiAlias;
        mTextSize = builder.mTextSize;
        mAlign = builder.mAlign;
        mTitleModeX = builder.mTitleModeX;
        mTitleModeY = builder.mTitleModeY;
        mMoveTo = builder.mMoveTo;
        mTextOnPath = builder.mTextOnPath;
    }

    /**
     * 创建默认水印
     * @return
     */
    public static WaterMarkOptions createSimpleOptions() {
        return new WaterMarkOptions.Builder().build();
    }

    /**
     * 创建用户定义的水印
     * @param builder
     * @return
     */
    public static WaterMarkOptions createCustomerOptions(Builder builder) {
        return new WaterMarkOptions(builder);
    }

    public static class Builder {
        private int mCanvasBgColor;
        private int mPaintColor;
        private int mPaintAlpha;
        private boolean mAntiAlias;
        private int mTextSize;
        private Paint.Align mAlign;
        private Shader.TileMode mTitleModeX;
        private Shader.TileMode mTitleModeY;
        private MoveTo mMoveTo;
        private TextOnPath mTextOnPath;

        public Builder() {
            mCanvasBgColor = Color.WHITE;
            mPaintColor = Color.GRAY;
            mPaintAlpha = 80;
            mAntiAlias = true;
            mAlign = Paint.Align.CENTER;
            mTextSize = 30;
            mTitleModeX = Shader.TileMode.REPEAT;
            mTitleModeY = Shader.TileMode.REPEAT;
            mMoveTo = new MoveTo(0, 150);
            mTextOnPath = new TextOnPath(0, 20);
        }

        /**
         * 设置画布背景色
         * @param color
         * @return
         */
        public WaterMarkOptions.Builder setCanvasBgColor(int color) {
            mCanvasBgColor = color;
            return this;
        }

        /**
         * 设置画笔颜色
         * @param color
         * @return
         */
        public WaterMarkOptions.Builder setPaintColor(int color) {
            mPaintColor = color;
            return this;
        }

        /**
         * 设置画笔透明度，范围为0~255
         * @param alpha
         * @return
         */
        public WaterMarkOptions.Builder setPaintAlpha(int alpha) {
            mPaintAlpha = alpha;
            return this;
        }

        /**
         * 设置是否抗锯齿
         * @param antiAlias
         * @return
         */
        public WaterMarkOptions.Builder setAntiAlias(boolean antiAlias) {
            mAntiAlias = antiAlias;
            return this;
        }

        /**
         * 设置画笔大小
         * @param textSize
         * @return
         */
        public WaterMarkOptions.Builder setTextSize(int textSize) {
            mTextSize = textSize;
            return this;
        }

        /**
         * 设置对齐方式
         * @param align
         * @return
         */
        public WaterMarkOptions.Builder setAlign(Paint.Align align) {
            mAlign = align;
            return this;
        }

        /**
         * 设置平铺方式
         * @param titleModeX    横向的平铺方式
         * @param titleModeY    纵向的平铺方式
         * @return
         */
        public WaterMarkOptions.Builder setTitleMode(Shader.TileMode titleModeX, Shader.TileMode titleModeY) {
            mTitleModeX = titleModeX;
            mTitleModeY = titleModeY;
            return this;
        }

        /**
         * 设置画笔起点
         * @param moveToX   画笔起点x
         * @param moveToY   画笔起点y
         * @return
         */
        public WaterMarkOptions.Builder setMoveTo(int moveToX, int moveToY) {
            MoveTo moveTo = new MoveTo(moveToX, moveToY);
            mMoveTo = moveTo;
            return this;
        }

        /**
         * 设置画笔在沿着path方向上的偏移量
         * @param hOffset   水平偏移量
         * @param vOffset   竖直偏移量
         * @return
         */
        public WaterMarkOptions.Builder setTextOnPath(int hOffset, int vOffset) {
            TextOnPath textOnPath = new TextOnPath(hOffset, vOffset);
            mTextOnPath = textOnPath;
            return this;
        }


        public WaterMarkOptions build() {
            return new WaterMarkOptions(this);
        }
    }


    /**
     * 移动画笔起点
     */
    protected static final class MoveTo {

        private int mMoveX;

        private int mMoveY;

        public MoveTo(int moveX, int moveY) {
            mMoveX = moveX;
            mMoveY = moveY;
        }

        public int getMoveX() {
            return mMoveX;
        }

        public int getMoveY() {
            return mMoveY;
        }
    }


    /**
     * 沿着Path路径绘制文本偏移量
     */
    protected static final class TextOnPath {

        private int mHOffset;

        private int mVOffset;

        public TextOnPath(int hOffset, int vOffset) {
            mHOffset = hOffset;
            mVOffset = vOffset;
        }

        public int getHOffset() {
            return mHOffset;
        }

        public int getVOffset() {
            return mVOffset;
        }
    }

}
