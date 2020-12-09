package com.foreveross.translate.google.model;

import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dasunsy on 2017/6/4.
 */

public class ResultResponse {
    @SerializedName("data")
    public TranslateTextResponseList mResultList;

    class TranslateTextResponseList {

        @SerializedName("translations")
        public List<TranslateTextResponse> mTextResponseList;
    }

    public boolean isSuccess() {
        return null != mResultList && !ListUtil.isEmpty(mResultList.mTextResponseList);
    }

    public String getTranslatedText() {
        if(isSuccess()) {
            return mResultList.mTextResponseList.get(0).mTranslatedText;
        }

        return StringUtils.EMPTY;
    }
}
