package com.foreverht.workplus.fengmap;

import android.content.Context;

import com.foreveross.atwork.infrastructure.Presenter.BasePresenter;
import com.foreveross.atwork.infrastructure.plugin.FengMapPlugin;
import com.foreveross.atwork.infrastructure.plugin.WorkplusPlugin;
import com.mediasoc.locationlib.LocationUtils;
import com.mediasoc.locationlib.ble.PositionListener;

import java.util.HashMap;
import java.util.Map;

public class FengMapPresenter extends BasePresenter implements FengMapPlugin.IFengMapPlugin {

    private LocationUtils mLocationUtils;

    @Override
    public Map<Class<? extends WorkplusPlugin>, WorkplusPlugin> getWorkplusPlugin() {
        Map<Class<? extends WorkplusPlugin>, WorkplusPlugin> map = new HashMap<>();
        map.put(FengMapPlugin.IFengMapPlugin.class, this);
        return map;
    }

    @Override
    public void initFengMap(Context context, String appKey, String mapId, final FengMapPlugin.OnFengMapLocationListener listener) {
        mLocationUtils = new LocationUtils(context, appKey, mapId, new PositionListener() {
            @Override
            public void onPositionChange(double x, double y, int floorId, float direction, int code) {
                if (listener == null) {
                    return;
                }
                if (code == 0) {
                    listener.onPositionChange(x, y, floorId, direction);
                    return;
                }
                listener.onPositionFail(code);

            }
        });
        mLocationUtils.init();
    }



    @Override
    public void stopLocation() {
        if (mLocationUtils == null) {
            return;
        }
        mLocationUtils.destory();
    }
}
