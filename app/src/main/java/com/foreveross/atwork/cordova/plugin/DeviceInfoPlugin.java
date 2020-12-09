package com.foreveross.atwork.cordova.plugin;/**
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 __           __
 .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 |________|_____|__|  |__|__|   __||__||_____|_____|
 |__|
 */


import android.Manifest;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;

import com.foreverht.threadGear.AsyncTaskThreadPool;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.api.sdk.util.NetGsonHelper;
import com.foreveross.atwork.cordova.plugin.model.GetBluetoothAddress;
import com.foreveross.atwork.cordova.plugin.model.GetRunningAppsResponse;
import com.foreveross.atwork.cordova.plugin.model.GetStepCountResponse;
import com.foreveross.atwork.cordova.plugin.model.PluginError;
import com.foreveross.atwork.infrastructure.model.location.GetLocationRequest;
import com.foreveross.atwork.infrastructure.model.step.TodayStepData;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AppUtil;
import com.foreveross.atwork.infrastructure.utils.DeviceUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.NetworkStatusUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.rom.RomUtil;
import com.foreveross.atwork.manager.BaiduSdkManager;
import com.foreveross.atwork.modules.step.StepCounterManager;
import com.foreveross.atwork.utils.AtworkUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;

/**
 * Created by reyzhang22 on 15/11/29.
 */
public class DeviceInfoPlugin extends WorkPlusCordovaPlugin {

    public static final String GET_IP_ADDRESS = "getIpAddress";

    public static final String GET_DEVICE_INFO = "getDeviceInfo";

    public static final String GET_RUNNING_APPS = "getRunningApps";

    public static final String START_BAIDU_TRACE = "startBaiduTrace";

    public static final String STOP_BAIDU_TRACE = "stopBaiduTrace";

    public static final String GET_PEDOMETER_DATA = "getPedometerData";

    public static final String GET_BLUETOOTH_ADDRESS = "getBluetoothAddress";

    public static final String CHECK_VIRTUAL_APPS = "checkVirtualApps";


    @Override
    public boolean execute(String action, JSONArray args,
                           final CallbackContext callbackContext) throws JSONException {
        if(!requestCordovaAuth()){
            callbackContext.error(CORDOVA_NOT_AUTH);
            return true;
        }

        if (action.equals(GET_IP_ADDRESS)) {
            cordova.getThreadPool().execute(() -> getGetIpAddress(callbackContext));
            return true;
        }

        if (action.equalsIgnoreCase(GET_DEVICE_INFO)) {
            cordova.getThreadPool().execute(() -> getDeviceInfo(callbackContext));
            return true;
        }

        if (GET_RUNNING_APPS.equalsIgnoreCase(action)) {
            getRunningApps(callbackContext);
            return true;
        }

        if (START_BAIDU_TRACE.equalsIgnoreCase(action)) {
            startBaiduTrace(callbackContext, args);
            return true;
        }

        if (STOP_BAIDU_TRACE.equalsIgnoreCase(action)) {
            stopBaiduTrace();
            return true;
        }

        if (GET_PEDOMETER_DATA.equalsIgnoreCase(action)) {
            getTodaySportStep(callbackContext);
            return true;
        }

        if(GET_BLUETOOTH_ADDRESS.equalsIgnoreCase(action)) {
            getBluetoothAddress(callbackContext);
            return true;
        }

        if (CHECK_VIRTUAL_APPS.equalsIgnoreCase(action)) {
            cordova.getThreadPool().execute(() -> {
                checkVirtualApps(callbackContext, args.toString());
            });
            return true;
        }

        return false;
    }

    private void getTodaySportStep(CallbackContext callbackContext) {
        StepCounterManager.INSTANCE.getTodaySportStepArray(cordova.getActivity(), todayStepDatas -> {

            GetStepCountResponse getStepCountResponse = new GetStepCountResponse();

            if(ListUtil.isEmpty(todayStepDatas)) {
                getStepCountResponse.setSteps(0);
                getStepCountResponse.setDistances(StringUtils.EMPTY);

            } else {
                TodayStepData lastData = todayStepDatas.get(todayStepDatas.size() - 1);
                getStepCountResponse.setSteps(lastData.getStepNum());
                getStepCountResponse.setDistances(lastData.getKm());
            }




            callbackContext.success(getStepCountResponse);

            return Unit.INSTANCE;
        });
    }

    private void startBaiduTrace(CallbackContext callbackContext, JSONArray args) {
        try {
            String username = LoginUserInfo.getInstance().getLoginUserName(cordova.getActivity());
            JSONObject jsonObject = args.getJSONObject(0);
            long serviceId = jsonObject.optLong("serviceId");
            int gatherInterval = jsonObject.optInt("gatherInterval");
            int packInterval = jsonObject.optInt("packInterval");
            if (serviceId < 0 || gatherInterval < 0 || packInterval < 0) {
                callbackContext.error("error values");
                return;
            }
            BaiduSdkManager manager = BaiduSdkManager.getInstance();

            manager.initBaiduTraceSdk(cordova.getActivity().getApplicationContext(), serviceId, username, gatherInterval, packInterval);
            manager.startBaiduTrace();

        } catch (JSONException e) {
            e.printStackTrace();
            callbackContext.error(e.getMessage());
        }
    }

    private void stopBaiduTrace() {
        BaiduSdkManager.getInstance().stopBaiduTrace();
    }

    private void getRunningApps(CallbackContext callbackContext) {
        GetRunningAppsResponse getRunningAppsResponse = new GetRunningAppsResponse();
        List<ActivityManager.RunningAppProcessInfo> runningAppsInfo = new ArrayList<ActivityManager.RunningAppProcessInfo>();

        PackageManager pm = cordova.getActivity().getPackageManager();
        ActivityManager am = (ActivityManager) cordova.getActivity()
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = am
                .getRunningServices(Integer.MAX_VALUE);


        for (ActivityManager.RunningServiceInfo service : runningServices) {

            String pkgName = service.process.split(":")[0];
            getRunningAppsResponse.mPackageNameList.add(pkgName);

            ActivityManager.RunningAppProcessInfo item = new ActivityManager.RunningAppProcessInfo();
            item.pkgList = new String[] { pkgName };
            item.pid = service.pid;
            item.processName = service.process;
            item.uid = service.uid;
            runningAppsInfo.add(item);

        }
        callbackContext.success(getRunningAppsResponse);
    }

    private void getDeviceInfo(CallbackContext callbackContext) {
        JSONObject json = new JSONObject();
        try {
            json.put("device_id", AtworkConfig.getDeviceId());
            json.put("platform", AtworkConfig.ANDROID_PLATFORM);
            json.put("domain_id", AtworkConfig.DOMAIN_ID);
            json.put("product_version", AppUtil.getVersionName(cordova.getActivity()));
            json.put("system_version", Build.VERSION.RELEASE);
            json.put("system_model",android.os.Build.MODEL);
            json.put("channel_vendor", RomUtil.getRomChannel());
            json.put("channel_id", AppUtil.getPackageName(cordova.getActivity()));
            json.put("device_name", DeviceUtil.getShowName());
            json.put("device_system_info", "Android " + Build.VERSION.RELEASE);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        PluginResult pr = new PluginResult(PluginResult.Status.OK, json);
        callbackContext.sendPluginResult(pr);
        callbackContext.success();
    }

    private void getGetIpAddress(CallbackContext callbackContext) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        PluginResult pr = null;
        try {
            pr = new PluginResult(PluginResult.Status.OK, getIpAddress());
            callbackContext.sendPluginResult(pr);
            callbackContext.success();
        } catch (Exception e) {
            e.printStackTrace();
            PluginError pluginError = PluginError.createInstance(e.getMessage());
            callbackContext.error(gson.toJson(pluginError));
        }

    }

    private void getBluetoothAddress(CallbackContext callbackContext) {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(cordova.getActivity(), new String[]{Manifest.permission.BLUETOOTH}, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                String address = BluetoothAdapter.getDefaultAdapter().getAddress();
                if(isBluetoothAddressIllegal(address)) {
                    address = android.provider.Settings.Secure.getString(AtworkApplicationLike.baseContext.getContentResolver(), "bluetooth_address");
                }

                if(null == address) {
                    address = StringUtils.EMPTY;
                }

                GetBluetoothAddress getBluetoothAddress = new GetBluetoothAddress();
                getBluetoothAddress.setAddress(address);

                callbackContext.success(getBluetoothAddress);
            }

            @Override
            public void onDenied(String permission) {
                AtworkUtil.popAuthSettingAlert(cordova.getActivity(), permission);
            }
        });


    }

    private void checkVirtualApps(CallbackContext callbackContext, String args) {
        //借用location里面的deviceList
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        GetLocationRequest request = NetGsonHelper.fromCordovaJson(args, GetLocationRequest.class);
        if (request == null || ListUtil.isEmpty(request.mVirtualDevices)) {
            PluginError pluginError = PluginError.createInstance("empty compare list");
            callbackContext.error(gson.toJson(pluginError));
            return;
        }
        compareVirtualDevices(request.mVirtualDevices, illegalInstallList -> {
            JSONObject json = new JSONObject();
            try {
                json.put("existIllegalApps", !ListUtil.isEmpty(illegalInstallList));
                JSONArray jsonArray = new JSONArray();
                for (String pkgId : illegalInstallList) {
                    JSONObject appJson = new JSONObject();
                    appJson.put("packageId", pkgId);
                    appJson.put("appName", AppUtil.getAppName(AtworkApplicationLike.baseContext, pkgId));
                    jsonArray.put(appJson);
                }
                json.put("illegalApps", jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            callbackContext.success(json.toString());
        });

    }

    private void compareVirtualDevices(final List<String> virtualDevices, AtworkLocationPlugin.OnVirtualCompareListener listener) {
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

    private boolean isBluetoothAddressIllegal(String address) {
        return StringUtils.isEmpty(address) || "02:00:00:00:00:00".equals(address);
    }

    //todo 不返回 json str 格式
    private String getIpAddress() throws JSONException, IllegalArgumentException {
        String ipAddr = NetworkStatusUtil.getIpAddress(true);
        if (TextUtils.isEmpty(ipAddr)) {
            throw  new IllegalArgumentException("cannot get wifi ip address");
        }
        JSONObject jsObject = new JSONObject();
        jsObject.put("result", "OK");
        jsObject.put("ipAddress", ipAddr);

        return jsObject.toString();
    }
}
