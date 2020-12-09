package com.foreveross.atwork.cordova.plugin;

import android.os.Handler;

import com.foreveross.atwork.manager.FengMapManager;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FengMapPlugin extends CordovaPlugin {

    public static final String INIT = "init";

    public static final String STOP_LOC = "stopLocation";

    private double mX;
    private double mY;
    private int mFloorId;
    private float mDirection;

    Handler mHandler;
    Runnable mRunnable;


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (INIT.equalsIgnoreCase(action)) {
            JSONObject json = args.optJSONObject(0);
            String appKey = json.optString("appKey");
            String mapId = json.optString("mapId");
            int interval = json.optInt("interval");
            FengMapManager mapManager = FengMapManager.getInstance();

            mapManager.initFengMap(cordova.getActivity(), appKey, mapId, new com.foreveross.atwork.infrastructure.plugin.FengMapPlugin.OnFengMapLocationListener() {
                @Override
                public void onPositionChange(double x, double y, int floorId, float direction) {
                    mX = x;
                    mY = y;
                    mFloorId = floorId;
                    mDirection = direction;
                    if (mHandler == null) {
                        mHandler = new Handler();
                        mRunnable =new Runnable() {
                            @Override
                            public void run() {
                                loadJS("onPositionChange(" + mX + ", " + mY + ", "+ mFloorId + ", " + mDirection + ")");
                                mHandler.postDelayed(this, interval == 0 ? 5 * 60 * 60 : interval * 60 * 60);
                            }
                        };
                        mHandler.postDelayed(mRunnable, interval == 0 ? 5 * 60 * 60 : interval * 60 * 60);
                    }
                }

                @Override
                public void onPositionFail(int errorCode) {
                    callbackContext.error(errorCode);
                }
            });

            return true;
        }
        if (STOP_LOC.equalsIgnoreCase(action)) {
            if (mHandler != null) {
                mHandler.removeCallbacks(mRunnable);
            }
            FengMapManager.getInstance().stopLocation();
            return true;
        }

        return false;
    }

    public void loadJS(String js) {
        if (this.webView == null) {
            return;
        }

        js = makeCompatible(js);
        js = makeCallSafely(js);

        webView.loadUrl("javascript:" + js);
    }


    private String makeCallSafely(String js) {
        return "try { " + js + " } catch(e) {}";
    }

    private String makeCompatible(String js) {
        if(!js.contains("(") && !js.contains(")")) {
            js += "()";
        }
        return js;
    }
}
