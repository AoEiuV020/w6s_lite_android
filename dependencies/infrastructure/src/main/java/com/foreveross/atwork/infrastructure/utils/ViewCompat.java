package com.foreveross.atwork.infrastructure.utils;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

/**
 * Created by dasunsy on 2016/9/28.
 */

public class ViewCompat extends androidx.core.view.ViewCompat{

    public static void setBackground(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        }  else {
            view.setBackgroundDrawable(drawable);
        }
    }
}
