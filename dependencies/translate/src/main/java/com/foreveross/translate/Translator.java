package com.foreveross.translate;

import android.content.Context;

import com.foreveross.atwork.infrastructure.model.translate.TextTranslateSdkType;
import com.foreveross.atwork.infrastructure.utils.language.LanguageUtil;
import com.foreveross.translate.google.GoogleTranslate;
import com.foreveross.translate.youdao.YoudaoTranslate;

import java.util.Locale;

/**
 * Created by dasunsy on 2017/6/4.
 */

public class Translator {

    private static Translator sInstance;

    private Translator() {

    }

    public static Translator getInstance() {

        if(null == sInstance) {
            sInstance = new Translator();
        }

        return sInstance;
    }


    private TranslateStrategy mTranslatorImpl;

    public TranslateStrategy getTranslator() {
        return mTranslatorImpl;
    }

    public void setSdk(TextTranslateSdkType translateSdk) {
        if(TextTranslateSdkType.YOUDAO == translateSdk) {
            mTranslatorImpl = new YoudaoTranslate();

        } else if(TextTranslateSdkType.GOOGLE == translateSdk) {
            mTranslatorImpl = new GoogleTranslate();

        }
    }

    public void translate(Context context, String text, final OnResultListener listener) {
        getTranslator().translate(text, null, LanguageUtil.getLanguageSupport(context), listener);
    }

}
