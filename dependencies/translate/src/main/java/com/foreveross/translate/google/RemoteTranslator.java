package com.foreveross.translate.google;

import com.foreveross.atwork.api.sdk.net.HttpResult;
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent;
import com.foreveross.atwork.infrastructure.utils.JsonUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.translate.google.model.ResultResponse;
import com.foreveross.translate.google.model.TranslateRequest;

/**
 * Created by dasunsy on 2017/6/5.
 */

public class RemoteTranslator {
    private static RemoteTranslator sInstance;

    private String mKey;

    public static RemoteTranslator getInstance() {
        if(null == sInstance) {
            sInstance = new RemoteTranslator();
        }
        return sInstance;
    }


    public void setKey(String key) {
        this.mKey = key;
    }

    public ResultResponse translate(String text, String from, String target) {
        TranslateRequest request = new TranslateRequest();
        request.mRequestText = text;

        if (!StringUtils.isEmpty(from)) {
            request.mSource = from;
        }
        request.mTarget = target;

        String url = "https://translation.googleapis.com/language/translate/v2?key=" + this.mKey;

        HttpResult httpResult = HttpURLConnectionComponent.getInstance().postHttp(url, JsonUtil.toJson(request));
        ResultResponse response = null;
        if(httpResult.isNetSuccess()) {
            response = JsonUtil.fromJson(httpResult.result, ResultResponse.class);
        }

        return response;
    }
}
