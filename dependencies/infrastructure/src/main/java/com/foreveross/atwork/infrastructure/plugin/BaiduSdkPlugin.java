package com.foreveross.atwork.infrastructure.plugin;

import android.content.Context;

public class BaiduSdkPlugin {

    public interface IBaiduSdkPlugin extends WorkplusPlugin {

        public void initBaiduTrace(Context context, long serviceId, String TraceName, int gatherInterval, int packInterval);

        public void startTrace();

        public void stopTrace();
    }
}
