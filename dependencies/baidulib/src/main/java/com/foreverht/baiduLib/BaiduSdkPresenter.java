package com.foreverht.baiduLib;

import android.content.Context;
import android.util.Log;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.PushMessage;
import com.foreveross.atwork.infrastructure.Presenter.BasePresenter;
import com.foreveross.atwork.infrastructure.plugin.BaiduSdkPlugin;
import com.foreveross.atwork.infrastructure.plugin.WorkplusPlugin;

import java.util.HashMap;
import java.util.Map;

public class BaiduSdkPresenter extends BasePresenter implements BaiduSdkPlugin.IBaiduSdkPlugin{

    private LBSTraceClient mTraceClient;
    private Trace mTrace;

    @Override
    public Map<Class<? extends WorkplusPlugin>, WorkplusPlugin> getWorkplusPlugin() {
        Map<Class<? extends WorkplusPlugin>, WorkplusPlugin> map = new HashMap<>();
        map.put(BaiduSdkPlugin.IBaiduSdkPlugin.class, this);
        return map;
    }




    /**
     *
     * @param context           上下文
     * @param serviceId         服务id
     * @param TraceName         设备标识
     * @param gatherInterval    定位周期 秒
     * @param packInterval      回传周期 秒
     */
    @Override
    public void initBaiduTrace(Context context, long serviceId, String TraceName, int gatherInterval, int packInterval) {
        // 是否需要对象存储服务，默认为：false，关闭对象存储服务。注：鹰眼 Android SDK v3.0以上版本支持随轨迹上传图像等对象数据，若需使用此功能，该参数需设为 true，且需导入bos-android-sdk-1.0.2.jar。
        boolean isNeedObjectStorage = false;
        // 初始化轨迹服务
        mTrace = new Trace(serviceId, TraceName, isNeedObjectStorage);
        // 初始化轨迹服务客户端
        mTraceClient = new LBSTraceClient(context);
        // 设置定位和打包周期
        mTraceClient.setInterval(gatherInterval, packInterval);

    }

    @Override
    public void startTrace() {
        // 开启服务
        mTraceClient.startTrace(mTrace, mTraceListener);
    }

    @Override
    public void stopTrace() {
        mTraceClient.stopTrace(mTrace, mTraceListener);
    }


    // 初始化轨迹服务监听器
    private OnTraceListener mTraceListener = new OnTraceListener() {
        @Override
        public void onBindServiceCallback(int i, String s) {

        }

        // 开启服务回调
        @Override
        public void onStartTraceCallback(int status, String message) {
            Log.e("baidu", "start trace " + message);
            if (status ==0) {
                // 开启采集
                mTraceClient.startGather(mTraceListener);
            }

        }
        // 停止服务回调
        @Override
        public void onStopTraceCallback(int status, String message) {
            Log.e("baidu", "stop trace " + message);

        }
        // 开启采集回调
        @Override
        public void onStartGatherCallback(int status, String message) {
            Log.e("baidu", "start  Gather trace " + message);
        }
        // 停止采集回调
        @Override
        public void onStopGatherCallback(int status, String message) {
            if (status == 0) {
                mTraceClient.stopGather(mTraceListener);
            }
        }
        // 推送回调
        @Override
        public void onPushCallback(byte messageNo, PushMessage message) {}

        @Override
        public void onInitBOSCallback(int i, String s) {
            Log.e("baidu", "init callback " + s);
        }
    };
}
