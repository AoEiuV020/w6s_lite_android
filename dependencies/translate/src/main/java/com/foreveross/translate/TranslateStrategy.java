package com.foreveross.translate;

import android.content.Context;
import androidx.annotation.Nullable;

import java.util.Locale;

/**
 * Created by dasunsy on 2017/6/4.
 */

public interface TranslateStrategy {

    void init(Context context, String key);

    /**
     * @param text
     * @param fromLocale 当为 null 时, 为 auto,即自动识别语言
     * @param toLocale
     * @param listener
     * */
    void translate(String text, @Nullable Integer fromLocale, Integer toLocale, OnResultListener listener);


}
