package com.foreveross.atwork.utils;

import android.view.View;

import androidx.core.content.ContextCompat;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;

/**
 * Created by dasunsy on 2017/10/10.
 */

public class ViewHelper {

    public static void focusOnLine(View vLine, boolean hasFocus) {
        if(hasFocus) {
            vLine.setBackgroundColor(ContextCompat.getColor(BaseApplicationLike.baseContext, R.color.common_blue_bg));
            ViewUtil.setHeight(vLine, DensityUtil.dip2px(1));
        } else {
            vLine.setBackgroundColor(ContextCompat.getColor(BaseApplicationLike.baseContext, R.color.common_text_hint_gray));
            ViewUtil.setHeight(vLine, 1);

        }
    }

}
