package com.foreveross.atwork.infrastructure.model.translate;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by dasunsy on 2017/6/5.
 */

public class TextTranslateStatus implements Serializable {

    @Expose
    public TextTranslateSdkType mTranslateSdk;

    //翻译所用的sdk
    @Expose
    public String mTranslateSdkLabel;

    //翻译的结果
    @Expose
    public String mResult;

    //是否正在翻译
    public boolean mTranslating = false;

    //是否可见
    @Expose
    public boolean mVisible = false;

    //翻译的语种
    @Expose
    public String mTranslationLanguage;

}
