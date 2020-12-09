package com.foreveross.translate.youdao;

import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.Nullable;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.api.sdk.NetWorkFailListener;
import com.foreveross.atwork.api.sdk.chat.TranslateLanguageResponse;
import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.encryption.MD5Utils;
import com.foreveross.atwork.infrastructure.utils.language.LanguageSupport;
import com.foreveross.translate.OnResultListener;
import com.foreveross.translate.TranslateStrategy;
import com.youdao.sdk.app.Language;
import com.youdao.sdk.app.YouDaoApplication;
import com.youdao.sdk.ydonlinetranslate.TranslateErrorCode;
import com.youdao.sdk.ydonlinetranslate.TranslateListener;
import com.youdao.sdk.ydonlinetranslate.TranslateParameters;
import com.youdao.sdk.ydonlinetranslate.Translator;
import com.youdao.sdk.ydtranslate.Translate;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.UUID;

/**
 * Created by dasunsy on 2017/6/4.
 */

public class YoudaoTranslate implements TranslateStrategy {

    @Override
    public void init(Context context, String key) {
        YouDaoApplication.init(context, key);
    }

    @Override
    public void translate(String text, @Nullable Integer fromLocale, Integer toLocale, final OnResultListener listener) {
        Language langFrom = getLanguage(fromLocale);
        Language langTo = getLanguage(toLocale);

        TranslateParameters tps = new TranslateParameters.Builder().from(langFrom).to(langTo).build();

        Translator.getInstance(tps).lookup(text, new TranslateListener() {
            @Override
            public void onError(TranslateErrorCode translateErrorCode) {
                listener.onResult(null);
            }

            @Override
            public void onResult(Translate translate, String s) {
                if(null != translate && !ListUtil.isEmpty(translate.getTranslations())) {
                    listener.onResult(translate.getTranslations().get(0));

                } else {
                    listener.onResult(null);

                }
            }
        });

    }
    /**
     * @param content 原文
     * @param locale 翻译的语种
     * @return 返回有道翻译api的链接
     * */
    public String translate(String content, String locale){
        String appKey = "11a6f2058d63ba47";
        String appSecret  = "hgOj60pSzY4dmwsSa5gGmIA4PynSl614";
        String salt = UUID.randomUUID().toString();
        String signMd5 = MD5Utils.encoderByMd5(appKey + content + salt  + appSecret);
        String sign = signMd5.toLowerCase();
        String encodedValue = new String();
        try{
            encodedValue = URLEncoder.encode(content,"UTF-8").replaceAll("\\+", "%20");
            encodedValue = encodedValue.replaceAll("%3A", ":").replaceAll("%2F", "/");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }

        //组成api接口
        String url = String.format("https://openapi.youdao.com/api?q=%s&from=auto&to=%s&appKey=%s&salt=%s&sign=%s", encodedValue, locale, appKey, salt, sign);

        return url;
    }

    /**Description：智能翻译
     * @param url
     * @param onTranslateListener
     */
    public void getTranlateLanguage(final String url, final OnTranslateListener onTranslateListener){

        new AsyncTask<Void, Void, HttpResult>() {
            @Override
            protected HttpResult doInBackground(Void... voids) {
                HttpResult httpResult = HttpURLConnectionComponent.getInstance().getHttp(url);

                return httpResult;
            }

            @Override
            protected void onPostExecute(HttpResult httpResult) {

                if(httpResult.isNetSuccess()) {
                    TranslateLanguageResponse translateLanguageResponse = (TranslateLanguageResponse) NetGsonHelper.fromNetJson(httpResult.result, TranslateLanguageResponse.class);
                    LogUtil.e("test",translateLanguageResponse.translation.get(0));
                    onTranslateListener.onSuccess(translateLanguageResponse);
                }else{
                    onTranslateListener.networkFail(httpResult.statusCode, httpResult.error);
                }

            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    public interface OnTranslateListener extends NetWorkFailListener {
        void onSuccess(TranslateLanguageResponse translateLanguageResponse);
    }


    private Language getLanguage(@Nullable Integer locale) {
        if(null == locale){
            return Language.AUTO;
        }

        if(LanguageSupport.TRADITIONAL_CHINESE == locale || LanguageSupport.SIMPLIFIED_CHINESE == locale) {
            return Language.CHINESE;

        } else if(LanguageSupport.ENGLISH == locale) {
            return Language.ENGLISH;
        }

        return Language.AUTO;
    }
}
