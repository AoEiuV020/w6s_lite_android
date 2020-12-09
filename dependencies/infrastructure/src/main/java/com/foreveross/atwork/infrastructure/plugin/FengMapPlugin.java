package com.foreveross.atwork.infrastructure.plugin;

import android.content.Context;

public class FengMapPlugin {

    public interface IFengMapPlugin extends WorkplusPlugin {

        public void initFengMap(Context context, String appKey, String mapId, OnFengMapLocationListener listener);

        public void stopLocation();
    }

    public interface OnFengMapLocationListener {
        void onPositionChange(double x, double y, int floorId, float direction);

        void onPositionFail(int errorCode);
    }


}
