package com.foreveross.atwork.infrastructure.utils;

import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by dasunsy on 2017/2/9.
 */

public class ViewUtil {

    public static void setLeftMarginDp(View view, int margin) {

        setMargins(view, new int[]{DensityUtil.dip2px(margin), -1, -1, -1});
    }

    public static void setLeftMargin(View view, int margin) {
        setMargins(view, new int[]{margin, -1, -1, -1});
    }

    public static void setTopMarginDp(View view, int margin) {
        setMargins(view, new int[]{-1, DensityUtil.dip2px(margin), -1, -1});
    }

    public static void setTopMargin(View view, int margin) {
        setMargins(view, new int[]{-1, margin, -1, -1});
    }

    public static void setRightMarginDp(View view, int margin) {
        setMargins(view, new int[]{-1, -1, DensityUtil.dip2px(margin), -1});
    }

    public static void setRightMargin(View view, int margin) {
        setMargins(view, new int[]{-1, -1, margin, -1});
    }

    public static void setBottomMarginDp(View view, int margin) {
        setMargins(view, new int[]{-1, -1, -1, DensityUtil.dip2px(margin)});
    }

    public static void setBottomMargin(View view, int margin) {
        setMargins(view, new int[]{-1, -1, -1, margin});
    }

    public static void setMargins(View view, int[] margin) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) params;
            boolean changed = false;
            if (-1 != margin[0] && layoutParams.leftMargin !=  margin[0]) {
                layoutParams.leftMargin = margin[0];
                changed = true;
            }

            if (-1 != margin[1] && layoutParams.topMargin !=  margin[1]) {
                layoutParams.topMargin = margin[1];
                changed = true;

            }

            if (-1 != margin[2] && layoutParams.rightMargin !=  margin[2]) {
                layoutParams.rightMargin = margin[2];
                changed = true;

            }


            if (-1 != margin[3] && layoutParams.bottomMargin !=  margin[3]) {
                layoutParams.bottomMargin = margin[3];
                changed = true;

            }
            if (changed) {
                view.setLayoutParams(layoutParams);
            }

        }

    }


    public static void setSize(View view, int height, int width) {
        setHeight(view, height);
        setWidth(view, width);
    }
    public static void setHeight(View view, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams.height != height) {
            layoutParams.height = height;
            view.setLayoutParams(layoutParams);
        }
    }

    public static void setWidth(View view, int width) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams.width != width) {
            layoutParams.width = width;
            view.setLayoutParams(layoutParams);
        }
    }

    public static void setVisible(View view, boolean visible) {
        if (visible) {
            view.setVisibility(View.VISIBLE);

        } else {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * 获取 textview 内容的宽度
     *
     * @param textView
     */
    public static int getTextLength(TextView textView) {
        return getTextLength(textView, textView.getText().toString());
    }
    /**
     * 获取 textview 内容的宽度
     *
     * @param textView
     * @param text
     */
    public static int getTextLength(TextView textView, String text) {
        Rect bounds = new Rect();
        textView.getPaint().getTextBounds(text, 0, text.length(), bounds);

        return bounds.width();
    }


    /**
     * 获取 textview 内容的高度
     *
     * @param textView
     */
    public static int getTextHeight(TextView textView) {
        return getTextHeight(textView, textView.getText().toString());
    }

    /**
     * 获取 textview 内容的高度
     *
     * @param textView
     * @param text
     */
    public static int getTextHeight(TextView textView, String text) {
        Rect bounds = new Rect();
        textView.getPaint().getTextBounds(text, 0, text.length(), bounds);

        return bounds.height();
    }

    /**
     * 为 textview 提供下划线
     */
    public static void addUnderline(TextView textView) {
        textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }


}
