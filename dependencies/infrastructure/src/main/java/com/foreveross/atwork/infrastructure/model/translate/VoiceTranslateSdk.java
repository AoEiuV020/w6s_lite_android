package com.foreveross.atwork.infrastructure.model.translate;

/**
 * Created by dasunsy on 2017/7/14.
 */

public class VoiceTranslateSdk {
    private VoiceTranslateSdkType mSdkType;

    private String mKey;

    public VoiceTranslateSdk(VoiceTranslateSdkType sdkType, String key) {
        this.mSdkType = sdkType;
        this.mKey = key;
    }

    public VoiceTranslateSdkType getSdkType() {
        return mSdkType;
    }

    public String getKey() {
        return mKey;
    }
}
