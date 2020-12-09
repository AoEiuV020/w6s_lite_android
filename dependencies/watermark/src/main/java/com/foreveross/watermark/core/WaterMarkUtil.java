package com.foreveross.watermark.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.user.LoginUserBasic;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.AppUtil;
import com.foreveross.atwork.infrastructure.utils.ViewCompat;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.watermark.R;

/**
 * 水印设置工具类
 *
 * @author reyzhang22
 */
public class WaterMarkUtil {

    /**
     * 给view设置水印背景
     *
     * @param context
     * @param view
     */
    public static void setLoginUserWatermark(Context context, View view, String addValue) {
        setLoginUserWatermark(context, view, -1, -1, addValue);
    }

    /**
     * 给view设置水印背景
     *
     * @param context
     * @paramw
     */
    public static void setLoginUserWatermark(Context context, View view, int bgColor, int textColor, String addValue) {
        LoginUserBasic meUser = LoginUserInfo.getInstance().getLoginUserBasic(context);
        DrawWaterMark drawWaterMark = new DrawWaterMark(meUser.mName, meUser.mUsername, bgColor, textColor, -1, -1, 0, addValue);
        WaterMarkUtil.setWaterMark(context, view, drawWaterMark);
    }


    public static void setWaterMark(Context context, View view, DrawWaterMark drawWaterMark) {
        BitmapDrawable drawable = drawTextToBitmap(context, drawWaterMark);
        ViewCompat.setBackground(view, drawable);
    }


    /**
     * 生成水印文字图片
     *
     * @param context
     * @param drawWaterMark
     */
    private static BitmapDrawable drawTextToBitmap(Context context, DrawWaterMark drawWaterMark) {
        try {
            View view = View.inflate(context, R.layout.watermark_bg, null);
            //change bg color
            if (-1 != drawWaterMark.mBgColor) {
                view.setBackgroundColor(drawWaterMark.mBgColor);
            }
            initViews(context, view, drawWaterMark);
            view.setDrawingCacheEnabled(true);
            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            view.buildDrawingCache();
            Bitmap bitmap = null;
            if (view != null) {
                Bitmap fakeBitmap = view.getDrawingCache();
                if (fakeBitmap != null) {
                    bitmap = fakeBitmap;
                }
            }
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setColor(Color.GRAY);
            canvas.save();
            canvas.restore();
            BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
            drawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            drawable.setDither(true);
            return drawable;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static View drawTextToBitmap(Context context, int bgColor, int textColor, User user, String addValue) {
        try {
            View view = View.inflate(context, R.layout.watermark_bg, null);
            DrawWaterMark drawWaterMark = new DrawWaterMark(user.mUsername, user.mNickname, bgColor, textColor, -1, -1, 0, addValue);
            //change bg color
            if (-1 != drawWaterMark.mBgColor) {
                view.setBackgroundColor(drawWaterMark.mBgColor);
            }
            initViews(context, view, drawWaterMark);
            view.setDrawingCacheEnabled(true);
            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            view.buildDrawingCache();
            Bitmap bitmap = null;
            if (view != null) {
                Bitmap fakeBitmap = view.getDrawingCache();
                if (fakeBitmap != null) {
                    bitmap = fakeBitmap;
                }
            }
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setColor(Color.GRAY);
            canvas.save();
            canvas.restore();
            BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
            drawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            drawable.setDither(true);
            view.setBackground(drawable);
            return view;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


    private static void initViews(Context context, View view, DrawWaterMark drawWaterMark) {
        TextView nameView = view.findViewById(R.id.name);
        TextView numView = view.findViewById(R.id.account);
        TextView appNameView = view.findViewById(R.id.app_name);
        TextView addValueView = view.findViewById(R.id.additional_value);
        int textColor = getTextColor(context, drawWaterMark);

        nameView.setTextColor(textColor);
        numView.setTextColor(textColor);
        appNameView.setTextColor(textColor);
        addValueView.setTextColor(textColor);
        if (!TextUtils.isEmpty(drawWaterMark.mAddVaule)) {
            addValueView.setVisibility(View.VISIBLE);
            addValueView.setText(drawWaterMark.mAddVaule);
        }

        nameView.setText(drawWaterMark.mName);
        int nameTvWidth = ViewUtil.getTextLength(nameView);
        //通过角度计算出 nameView 的 marginTop, 使得旋转时有足够空间显示出名字
        int marginTop = (int) (Math.sin(Math.toRadians(17)) * (nameTvWidth / 2));

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) nameView.getLayoutParams();
        params.topMargin = marginTop;
        if (drawWaterMark.mPadding != -1) {
            params.bottomMargin = (drawWaterMark.mPadding);
        }

        nameView.setLayoutParams(params);
        numView.setText(drawWaterMark.mNum);

        appNameView.setText(AppUtil.getAppName(BaseApplicationLike.baseContext));
    }

    private static  int getTextColor (Context context, DrawWaterMark drawWaterMark) {
        int textColor = (-1 != drawWaterMark.mTextColor) ? drawWaterMark.mTextColor : ContextCompat.getColor(context, R.color.watermark_text_color);
        int alpha = (int)drawWaterMark.mAlpha * 100;
        return ColorUtils.setAlphaComponent(textColor, alpha <= 0 || alpha >= 1 ? 99 : alpha);
    }


}