package com.foreveross.translate.google;

import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.Nullable;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.language.LanguageSupport;
import com.foreveross.translate.OnResultListener;
import com.foreveross.translate.TranslateStrategy;
import com.foreveross.translate.google.model.ResultResponse;

/**
 * Created by dasunsy on 2017/6/4.
 */

public class GoogleTranslate implements TranslateStrategy {



    @Override
    public void init(Context context, String key) {
        RemoteTranslator.getInstance().setKey(key);
    }

    @Override
    public void translate(final String text, @Nullable final Integer fromLocale, final Integer toLocale, final OnResultListener listener) {
        new AsyncTask<Void, Void, ResultResponse>() {
            @Override
            protected ResultResponse doInBackground(Void... voids) {
                String fromLang = getLanguage(fromLocale);
                String toLang = getLanguage(toLocale);
                return RemoteTranslator.getInstance().translate(text, fromLang, toLang);
            }

            @Override
            protected void onPostExecute(ResultResponse resultResponse) {
                if(null != resultResponse) {
                    listener.onResult(resultResponse.getTranslatedText());

                } else {
                    listener.onResult(null);

                }
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }


    /**
     * 获取语言标记
     * @param locale 为空时, 则api 自动识别语言
     * */
    public String getLanguage(@Nullable Integer locale) {
        if(null == locale) {
            return StringUtils.EMPTY;
        }

        if(LanguageSupport.ENGLISH == locale) {
            return "en";

        } else if(LanguageSupport.SIMPLIFIED_CHINESE == locale) {
            return "zh-CN";

        } else if(LanguageSupport.TRADITIONAL_CHINESE == locale) {
            return "zh-TW";
        }

        return StringUtils.EMPTY;
    }

}
