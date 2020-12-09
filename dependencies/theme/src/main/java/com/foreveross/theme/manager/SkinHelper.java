package com.foreveross.theme.manager;

import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.foreveross.theme.model.BaseColor;
import com.foreveross.theme.model.Theme;

/**
 * 创建时间：2016年09月12日 下午2:28
 * 创建人：laizihan
 * 类名：ColorHelper
 * 用途：
 */
public class SkinHelper {


    @Nullable
    public static String getColorFromTag(String tag) {
        String color = null;
        Theme currentTheme = SkinMaster.getInstance().getCurrentTheme();

        if (null != currentTheme) {
            BaseColor baseColor = currentTheme.mBaseColor;

            switch (tag) {
                case "c1":
                    color = baseColor.c1;
                    break;
                case "c2":
                    color = baseColor.c2;
                    break;
                case "c3":
                    color = baseColor.c3;
                    break;
                case "c4":
                    color = baseColor.c4;
                    break;
                case "c5":
                    color = baseColor.c5;
                    break;
                case "c6":
                    color = baseColor.c6;
                    break;
                case "c7":
                    color = baseColor.c7;
                    break;
                case "c8":
                    color = baseColor.c8;
                    break;
                case "c9":
                    color = baseColor.c9;
                    break;
                case "c10":
                    color = baseColor.c10;
                    break;
                case "c11":
                    color = baseColor.c11;
                    break;
                case "c12":
                    color = baseColor.c12;
                    break;
                case "c13":
                    color = baseColor.c13;
                    break;
                case "c14":
                    color = baseColor.c14;
                    break;
                case "c15":
                    color = baseColor.c15;
                    break;
                case "c16":
                    color = baseColor.c16;
                    break;
                case "c17":
                    color = baseColor.c17;
                    break;
                case "c18":
                    color = baseColor.c18;
                    break;
                case "c19":
                    color = baseColor.c19;
                    break;
                case "c20":
                    color = baseColor.c20;
                    break;
                case "c21":
                    color = baseColor.c21;
                    break;

                default:
                    Log.e("from", tag + "");
    //                color = mBaseColor.c1;
                    break;

            }
        }
        return color;
    }


    public static boolean tagBelongsToBaseColor(String tag) {
        return getColorFromTag(tag) != null;
    }


    public static int parseColorFromTag(String tag) {
        return Color.parseColor(getColorFromTag(tag));
    }

    public static int getMainColor() {
        return parseColorFromTag("c1");
    }

    public static int getPrimaryTextColor() {
        return parseColorFromTag("c15");
    }

    public static int getSecondaryTextColor() {
        return parseColorFromTag("c16");
    }

    public static int getTipsColor() {
        return parseColorFromTag("c10");
    }

    public static int getBackgroundColor() {
        return parseColorFromTag("c20");
    }

    public static int getActiveColor() {
        return parseColorFromTag("c2");
    }

    public static int getInactiveColor() {
        return parseColorFromTag("c3");
    }

    public static int getTabActiveColor() {
        return parseColorFromTag("c4");
    }

    public static int getTabInactiveColor() {
        return parseColorFromTag("c5");
    }

    public static int getSideTextColor() {
        return parseColorFromTag("c6");
    }


    public static void clearSkinTag(View view) {
        view.setTag(null);
    }

}
