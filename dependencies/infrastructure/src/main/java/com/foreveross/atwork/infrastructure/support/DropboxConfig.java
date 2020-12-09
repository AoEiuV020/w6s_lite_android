package com.foreveross.atwork.infrastructure.support;

public class DropboxConfig {

    private String mForceAllRefreshTag = "_FORCE1";

    private boolean mForcedShowWaterMark = false;


    public String getForceAllRefreshTag() {
        return mForceAllRefreshTag;
    }

    public void setForceAllRefreshTag(String forceAllRefreshTag) {
        mForceAllRefreshTag = forceAllRefreshTag;
    }


    public boolean isForcedShowWaterMark() {
        return mForcedShowWaterMark;
    }

    public void setForcedShowWaterMark(boolean forcedShowWaterMark) {
        mForcedShowWaterMark = forcedShowWaterMark;
    }
}
