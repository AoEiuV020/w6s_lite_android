package com.foreveross.atwork.infrastructure.model.translate;

/**
 * Created by dasunsy on 2017/6/5.
 */

public class TextTranslateSdk {

    private TextTranslateSdkType mSdkType;

    private String mKey;

    public TextTranslateSdk(TextTranslateSdkType sdkType, String key) {
        this.mSdkType = sdkType;
        this.mKey = key;
    }

    public TextTranslateSdkType getSdkType() {
        return mSdkType;
    }

    public String getKey() {
        return mKey;
    }
}
