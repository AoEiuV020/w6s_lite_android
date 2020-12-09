package com.foreveross.atwork.api.sdk.chat;

import android.annotation.SuppressLint;

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TranslateLanguageResponse extends BasicResponseJSON {
    @SerializedName("translation")
    public List<String> translation;
}
