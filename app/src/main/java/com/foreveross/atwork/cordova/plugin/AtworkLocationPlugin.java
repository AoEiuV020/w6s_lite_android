package com.foreveross.atwork.cordova.plugin;

import android.Manifest;
import android.content.Context;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.Nullable;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreverht.workplus.amap.AmapManager;
import com.foreverht.workplus.amap.WorkPlusLocationManager;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.cordova.plugin.model.GetDeviceBasicInfoRequest;
import com.foreveross.atwork.cordova.plugin.model.PluginError;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.location.GetLocationRequest;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.NetworkStatusUtil;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.modules.map.location.GooglePlayServiceMapService;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.CordovaHelper;
import com.foreveross.atwork.utils.OutFieldPunchHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AtworkLocationPlugin extends WorkPlusCordovaPlugin {

    public static final String GET_LOCATION_SYNC = "getLocation";

    public static final String GET_DEVICE_BASIC_INFO = "getDeviceBasicInfo";

    public static final String OPEN_SIGN_IN = "enableOrgSignIn";

    public static final String CLOSE_SIGN_IN = "disableOrgSignIn";

    public static final String LOCATION_DISCONNECTED = "定位无法使用！";

    @Override
    public boolean execute(String action, JSONArray args,
                           final CallbackContext callbackContext) {
        if(!requestCordovaAuth()){
            callbackContext.error(CORDOVA_NOT_AUTH);
            return true;
        }

        if (action.equals(GET_LOCATION_SYNC)) {

            handleGetLocation(args, callbackContext);

            return true;
        }

        if (action.equalsIgnoreCase(OPEN_SIGN_IN)) {
            cordova.getThreadPool().execute(() -> {
                String orgId = PersonalShareInfo.getInstance().getCurrentOrg(cordova.getActivity());
                OutFieldPunchHelper.onOutFieldNetRequest(cordova.getActivity(), orgId, OutFieldPunchHelper.IntervalType.start, outFieldInterval -> {
                    PersonalShareInfo.getInstance().setOrgOutFieldPunchRequestCode(cordova.getActivity(), orgId);
                    int requestCode = PersonalShareInfo.getInstance().getOrgOutFieldPunchRequestCode(cordova.getActivity(), orgId);
                    OutFieldPunchHelper.startOutFieldIntervalPunch(cordova.getActivity(), orgId, requestCode, outFieldInterval);
                    callbackResult(callbackContext, outFieldInterval);
                });
            });

            return true;
        }

        if (action.equalsIgnoreCase(CLOSE_SIGN_IN)) {
            cordova.getThreadPool().execute(() -> {
                String orgId = PersonalShareInfo.getInstance().getCurrentOrg(cordova.getActivity());
                int requestCode = PersonalShareInfo.getInstance().getOrgOutFieldPunchRequestCode(cordova.getActivity(), orgId);
                OutFieldPunchHelper.onOutFieldNetRequest(cordova.getActivity(), orgId, OutFieldPunchHelper.IntervalType.end, outFieldInterval -> {
                    OutFieldPunchHelper.stopOutFieldIntervalPunch(cordova.getActivity(), orgId, requestCode);
                    OutFieldPunchHelper.removeShareInfo(cordova.getActivity(), orgId);
                    callbackResult(callbackContext, outFieldInterval);
                });

            });

            return true;
        }

        if (action.equalsIgnoreCase(GET_DEVICE_BASIC_INFO)) {
            GetDeviceBasicInfoRequest getCurrentUserRequestJson = NetGsonHelper.fromCordovaJson(args, GetDeviceBasicInfoRequest.class);
            int accuracy = GetLocationRequest.Accuracy.HIGH;
            if(null != getCurrentUserRequestJson) {
                accuracy = getCurrentUserRequestJson.mAccuracy;
            }

            JSONObject jsonObject = new JSONObject();
            try {
                Context context = cordova.getActivity();
                AmapManager.getInstance().startLocation(context, accuracy);
                jsonObject.put("deviceId", AtworkConfig.getDeviceId());
                jsonObject.put("userId", LoginUserInfo.getInstance().getLoginUserId(context));
                jsonObject.put("platform", "ANDROID");
                jsonObject.put("brand", Build.BOARD);
                jsonObject.put("model", Build.MODEL);
                jsonObject.put("manufacturer", Build.MANUFACTURER);
                jsonObject.put("system_version", Build.VERSION.RELEASE);
                String networkType = "NONE";
                if (NetworkStatusUtil.is2GNetWorkType(context)) {
                    networkType = "2G";
                }
                if (NetworkStatusUtil.is3GNetWorkType(context)) {
                    networkType = "3G";
                }
                if (NetworkStatusUtil.is4GNetWorkType(context)) {
                    networkType = "4G";
                }
                if (NetworkStatusUtil.isWifiConnectedOrConnecting(context)) {
                    networkType = "WIFI";
                }
                jsonObject.put("network_type", networkType);

                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                jsonObject.put("wifi_enable", wifiManager.isWifiEnabled());
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                jsonObject.put("wifi_mac", wifiInfo == null ? "" : wifiInfo.getBSSID());

                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                jsonObject.put("gps_enable", locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));

                jsonObject.put("current_latitude", AmapManager.getInstance().mLatitude) ;
                jsonObject.put("current_longitude", AmapManager.getInstance().mLongitude);
                AmapManager.getInstance().stopLocation(context);
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);
                callbackContext.sendPluginResult(pluginResult);
                callbackContext.success();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }

        return false;
    }

    private void handleGetLocation(JSONArray args, CallbackContext callbackContext) {

        GetLocationRequest getLocationRequest = NetGsonHelper.fromCordovaJson(args, GetLocationRequest.class);

        if(null == getLocationRequest) {
            getLocationRequest = GetLocationRequest.newRequest()
                    .setTimeout(3)
                    .setAccuracy(GetLocationRequest.Accuracy.HIGH);
        }

        GetLocationRequest finalGetLocationRequest = getLocationRequest;
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(cordova.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                //这个调用不能在threadPool里面做。。

                PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(cordova.getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, new PermissionsResultAction() {

                    @Override
                    public void onGranted() {
                        if (!ListUtil.isEmpty(finalGetLocationRequest.mVirtualDevices)) {
                            compareVirtualDevices(finalGetLocationRequest.mVirtualDevices, illegalInstallList -> {
                                getLocationMap(finalGetLocationRequest, illegalInstallList, callbackContext);
                            });
                            return;
                        }
                        getLocationMap(finalGetLocationRequest, null, callbackContext);
                    }

                    @Override
                    public void onDenied(String permission) {
                        AtworkUtil.popAuthSettingAlert(cordova.getActivity(), permission);
                        JSONObject jsonObject = new JSONObject();
                        try {
//                            jsonObject.put("code", 0);
                            jsonObject.put("message", LOCATION_DISCONNECTED);
                            PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, jsonObject);
                            callbackContext.sendPluginResult(pluginResult);
                            callbackContext.success();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }

            @Override
            public void onDenied(String permission) {
                AtworkUtil.popAuthSettingAlert(cordova.getActivity(), permission);
                JSONObject jsonObject = new JSONObject();
                try {
//                    jsonObject.put("code", 0);
                    jsonObject.put("message", LOCATION_DISCONNECTED);
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, jsonObject);
                    callbackContext.sendPluginResult(pluginResult);
                    callbackContext.success();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void compareVirtualDevices(final List<String> virtualDevices, OnVirtualCompareListener listener) {
        if (listener == null) {
            return;
        }
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                List<String> installedApps = AtworkApplicationLike.getInstalledApps();
                List<String> illegalInstalledList = new ArrayList<>();
                for (String virtualDevice : virtualDevices) {
                    if (installedApps.contains(virtualDevice)) {
                        illegalInstalledList.add(virtualDevice);
                    }
                }
                listener.onCompare(illegalInstalledList);
                return null;
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance());
    }

    private void getLocationMap(GetLocationRequest finalGetLocationRequest, @Nullable List<String> illegalInstalledList, CallbackContext callbackContext) {
        if (GetLocationRequest.Source.GOOGLE.toString().equalsIgnoreCase(finalGetLocationRequest.mSource)) {
            getGooglePlayServiceLocation(finalGetLocationRequest, illegalInstalledList, callbackContext);

        } else {
            getAmpLocation(finalGetLocationRequest, illegalInstalledList, callbackContext);

        }
    }

    private void getAmpLocation(GetLocationRequest finalGetLocationRequest, List<String> illegalInstalledList, CallbackContext callbackContext) {
        WorkPlusLocationManager.getInstance().getLocationFlash(BaseApplicationLike.baseContext, finalGetLocationRequest, illegalInstalledList, getLocationResponse -> {
            if(getLocationResponse.isSuccess()) {
                CordovaHelper.doSuccess(getLocationResponse, callbackContext);
            } else {
                callbackContext.error();
            }
        });
    }


    private void getGooglePlayServiceLocation(GetLocationRequest getLocationRequest, List<String> illegalInstalledList, CallbackContext callbackContext) {
        cordova.getThreadPool().execute(()->{
            GooglePlayServiceMapService.getInstance().startLocation(cordova.getActivity(), getLocationRequest.mTimeout * 1000, illegalInstalledList, getLocationInfo -> {
                if(getLocationInfo.isSuccess()) {
                    CordovaHelper.doSuccess(getLocationInfo, callbackContext);
                } else {
                    callbackContext.error();
                }
            });
        });
    }


    private void getAmapLocation(GetLocationRequest getLocationRequestJson, CallbackContext callbackContext) {
        AmapManager.getInstance().startLocation(cordova.getActivity(), getLocationRequestJson.mAccuracy);
//                            BaiduSDKApplication.startLocation();
        int finalBlockTimeout = getLocationRequestJson.mTimeout;
        cordova.getThreadPool().execute(() -> getLocationInfo(callbackContext, finalBlockTimeout * 1000));
    }

    private void callbackResult(CallbackContext callbackContext, int outFieldInterval) {
        cordova.getActivity().runOnUiThread(() -> {
            JSONObject result = new JSONObject();
            try {
                result.put("status", outFieldInterval);
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, result);
                callbackContext.sendPluginResult(pluginResult);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

    }


    private void getLocationInfo(CallbackContext callbackContext, int timeout) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        AmapManager amap = AmapManager.getInstance();
        try {
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, getLocation(timeout).toString());
            callbackContext.sendPluginResult(pluginResult);
            callbackContext.success();

            Logger.e("cordova返回地址信息", "longitude = " + amap.mLongitude
                    + " latitude = " + amap.mLatitude
                    + " address = " + amap.mAddress
                    + " time = " + TimeUtil.getStringForMillis(System.currentTimeMillis(), TimeUtil.getTimeFormat1(BaseApplicationLike.baseContext)));

        } catch (Exception e1) {
            Logger.e("error!", e1.getMessage(), e1);

            try {
                //可能已经超时了
                PluginError pluginError = PluginError.createInstance(e1.getMessage());
                callbackContext.error(gson.toJson(pluginError));
            } catch (Exception e2) {
                e2.printStackTrace();
            }

        } finally {
            amap.stopLocation(cordova.getActivity());
        }
    }

    /**
     * 获取地理位置
     */
    public JSONObject getLocation(int timeout) throws Exception {

        //获取地理位置
        AmapManager amap = AmapManager.getInstance();
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONObject jsObject = new JSONObject();
        try {
            if (amap.isLocated()) {
                jsObject.put("result", "OK");
                jsObject.put("longitude", amap.mLongitude);
                jsObject.put("latitude", amap.mLatitude);
                jsObject.put("address", amap.mAddress);
                jsObject.put("city", amap.mCity);
                jsObject.put("district", amap.mDistrict);
                jsObject.put("street", amap.mStreet);
                jsObject.put("aoiName", amap.mAoiName);
            } else {
                throw new Exception("获取定位失败，ErrCode:" + amap.mErrorCode + ", errInfo:" + amap.mErrorInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsObject;
    }

    interface OnVirtualCompareListener {
        void onCompare(List<String> illegalInstalledList);
    }

}
