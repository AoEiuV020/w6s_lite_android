package com.foreveross.atwork.utils;

import android.app.Activity;
import android.view.View;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;

/**
 * Created by dasunsy on 2017/12/17.
 */

public class WorkplusTextSizeChangeHelper {

    private static final float DEFAULT_RATE = 1.1f;

    public static void setTextSizeTheme(Activity activity) {
        int level = PersonalShareInfo.getInstance().getTextSizeLevel(activity);
        switch (level) {
            case 0:
                activity.setTheme(R.style.theme_level_small);
                break;

            case 1:
                activity.setTheme(R.style.theme_level_standard);
                break;

            case 2:
                activity.setTheme(R.style.theme_level_Large);
                break;

            case 3:
                activity.setTheme(R.style.theme_level_Large2);
                break;

            case 4:
                activity.setTheme(R.style.theme_level_Large3);
                break;

            case 5:
                activity.setTheme(R.style.theme_level_Large4);
                break;
        }
    }

    public static void handleHeightEnlargedTextSizeStatus(View view) {
        handleHeightEnlargedTextSizeStatus(view, DEFAULT_RATE);
    }

    public static void handleViewEnlargedTextSizeStatus(View view) {
        handleViewEnlargedTextSizeStatus(view, DEFAULT_RATE);
    }

    public static void handleHeightEnlargedTextSizeStatus(View view, float rate) {
        handleHeightEnlargedTextSizeStatus(view, rate, 2);
    }


    public static void handleHeightEnlargedTextSizeStatus(View view, float rate, int afterLevel) {
        if(afterLevel < PersonalShareInfo.getInstance().getTextSizeLevel(BaseApplicationLike.baseContext)) {
            view.getLayoutParams().height = (int) (rate *  view.getLayoutParams().height);
        }
    }

    public static void handleViewEnlargedTextSizeStatus(View view, float rate) {
        if(2 < PersonalShareInfo.getInstance().getTextSizeLevel(BaseApplicationLike.baseContext)) {
            view.getLayoutParams().height = (int) (rate *  view.getLayoutParams().height);
            view.getLayoutParams().width = (int) (rate *  view.getLayoutParams().width);
        }
    }


}
