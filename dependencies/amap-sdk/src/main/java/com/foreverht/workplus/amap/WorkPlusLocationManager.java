package com.foreverht.workplus.amap;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.model.location.GetLocationInfo;
import com.foreveross.atwork.infrastructure.model.location.GetLocationRequest;
import com.foreveross.atwork.infrastructure.plugin.map.location.OnGetLocationListener;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;

import java.util.List;

public class WorkPlusLocationManager {


    private static final String TAG = "AMP_LOCATION";

    private static final Object sLock = new Object();

    private static WorkPlusLocationManager sInstance;

//    private AMapLocationClient mLocationClient;


    private WorkPlusLocationManager() {

    }

    public static WorkPlusLocationManager getInstance() {
        if(null == sInstance) {
            synchronized (sLock) {
                if(null == sInstance) {
                    sInstance = new WorkPlusLocationManager();
                }
            }
        }

        return sInstance;
    }

    public void init(Context context) {

        if(null != BeeWorks.getInstance().config.beeWorksAmap
                && null != BeeWorks.getInstance().config.beeWorksAmap.getInfo()) {
            AMapLocationClient.setApiKey(BeeWorks.getInstance().config.beeWorksAmap.getInfo().getAppKey());
        }


    }


    public void getLocationFlash(Context context, GetLocationRequest getLocationRequest, final List<String> illegalInstalledList, final OnGetLocationListener getLocationListener) {
        Logger.e(TAG, "location request -> " + getLocationListener.toString());

        final long startTime = System.currentTimeMillis();

        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        setLocationMode(getLocationRequest, option);

        option.setNeedAddress(true);

        final AMapLocationClient locationClient = new AMapLocationClient(context.getApplicationContext());
        locationClient.setLocationOption(option);
        locationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if(null != aMapLocation) {
                    long endTime = System.currentTimeMillis();


                    Logger.e(TAG, "location Error, ErrCode:" + aMapLocation.getErrorCode()
                            + ", errInfo:" + aMapLocation.getErrorInfo()
                            + " use time : " + (endTime - startTime));


                    GetLocationInfo getLocationInfo = new GetLocationInfo();

                    if(0 == aMapLocation.getErrorCode()) {
                        getLocationInfo.setLongitude(aMapLocation.getLongitude());
                        getLocationInfo.setLatitude(aMapLocation.getLatitude());
                        getLocationInfo.setCountry(aMapLocation.getCountry());
                        getLocationInfo.setProvince(aMapLocation.getProvince());
                        getLocationInfo.setCity(aMapLocation.getCity());
                        getLocationInfo.setAddress(aMapLocation.getAddress());
                        getLocationInfo.setDistrict(aMapLocation.getDistrict());
                        getLocationInfo.setAoiName(aMapLocation.getAoiName());
                        getLocationInfo.setStreet(aMapLocation.getStreet());
                        if (!ListUtil.isEmpty(illegalInstalledList)) {
                            getLocationInfo.setIllegalInstallList(illegalInstalledList);
                        }
                        getLocationInfo.setResult("OK");



                        Logger.e(TAG, "location info ->  " + getLocationInfo.toString());


                    } else {
                        getLocationInfo.setResult("FAIL");
                    }

                    locationClient.stopLocation();

                    getLocationListener.onResult(getLocationInfo);

                }
            }
        });

        //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
        locationClient.stopLocation();
        locationClient.startLocation();
    }

    private void setLocationMode(GetLocationRequest getLocationRequest, AMapLocationClientOption option) {
        switch (getLocationRequest.mAccuracy) {
            case 2 :
                option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

                break;

            case 1:
                option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);

                break;

            case 0:
                option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);

                break;
        }
    }

}
