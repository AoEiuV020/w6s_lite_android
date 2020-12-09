package com.foreveross.atwork.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;

import com.foreveross.atwork.infrastructure.utils.LogUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 创建时间：2016年08月30日 下午2:32
 * 创建人：laizihan
 * 类名：TypefaceUtil
 * 用途：
 */
public class TypefaceUtil {


    private static final String TAG = "TypefaceUtil";

    private TypefaceUtil() {

    }

    public static void overrideFont(Context context, String defaultFontNameToOverride, String customFontFileNameInAssets) {
        final Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), customFontFileNameInAssets);
        LogUtil.e(TAG, "typeface utils");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Map<String, Typeface> newMap = new HashMap<String, Typeface>();
            newMap.put("sans-serif", customFontTypeface);
            try {
                final Field staticField = Typeface.class
                        .getDeclaredField("sSystemFontMap");
                staticField.setAccessible(true);
                staticField.set(null, newMap);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {

            try {
                final Field defaultFontTypefaceField = Typeface.class.getDeclaredField(defaultFontNameToOverride);
                defaultFontTypefaceField.setAccessible(true);
                defaultFontTypefaceField.set(null, customFontTypeface);
            } catch (Exception e) {
                LogUtil.e(TAG, "Can not set custom font " + customFontFileNameInAssets + " instead of " + defaultFontNameToOverride);
            }

        }


    }
}
