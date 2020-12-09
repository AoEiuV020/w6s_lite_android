package com.foreveross.atwork.infrastructure.model.translate;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by dasunsy on 2017/6/7.
 */

public class VoiceTranslateStatus implements Serializable {


    @Expose
    public String mResult;

    @Expose
    public String mLanguage;

    @Expose
    public VoiceTranslateSdkType mTranslateSdk = VoiceTranslateSdkType.XUNFEI;

    public boolean mTranslating = false;

    @Expose
    public boolean mVisible = false;


    /**
     * 是否是普通话
     * */
    public boolean isMandarin() {
        return null == mLanguage || mLanguage.startsWith("zh");
    }

}
