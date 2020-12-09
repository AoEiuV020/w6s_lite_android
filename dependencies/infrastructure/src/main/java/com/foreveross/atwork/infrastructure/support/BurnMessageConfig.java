package com.foreveross.atwork.infrastructure.support;

public class BurnMessageConfig {

    private boolean mCommandHideWatermark = false;

    public boolean isCommandHideWatermark() {
        return mCommandHideWatermark;
    }

    public BurnMessageConfig setCommandHideWatermark(boolean commandHideWatermark) {
        mCommandHideWatermark = commandHideWatermark;
        return this;
    }
}
