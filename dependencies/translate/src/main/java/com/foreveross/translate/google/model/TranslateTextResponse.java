package com.foreveross.translate.google.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dasunsy on 2017/6/4.
 */

public class TranslateTextResponse {

    @SerializedName("detectedSourceLanguage")
    public String mDetectedSourceLanguage;

    @SerializedName("translatedText")
    public String mTranslatedText;
}
