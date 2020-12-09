package com.foreverht.workplus.amap;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.utils.StringUtils;

/**
 * Created by reyzhang22 on 17/9/19.
 */

public class AmapManager {

    public static final String TAG = AmapManager.class.getName();
    public static final AmapManager sInstance = new AmapManager();
    public AMapLocationClient mLocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;

    public double mLongitude = -1;
    public double mLatitude = -1;
    public String mAddress = StringUtils.EMPTY;
    public String mCity = StringUtils.EMPTY;
    public String mDistrict = StringUtils.EMPTY;
    public String mStreet = StringUtils.EMPTY;
    public String mAoiName = StringUtils.EMPTY;
    public int mErrorCode = -1;
    public String mErrorInfo = StringUtils.EMPTY;

    public static AmapManager getInstance() {
        return sInstance;
    }

    public void init(final Context context) {
        mLocationClient = new AMapLocationClient(context);
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        //定位成功回调信息，设置相关消息
                        aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                        mLatitude = aMapLocation.getLatitude();//获取纬度
                        mLongitude = aMapLocation.getLongitude();//获取经度
                        mAddress = aMapLocation.getAddress();//获得地址信息
                        mCity = aMapLocation.getCity();
                        mDistrict = aMapLocation.getDistrict();
                        mStreet = aMapLocation.getStreet();
                        mAoiName = aMapLocation.getAoiName();

                        aMapLocation.getAccuracy();//获取精度信息
                        return;
                    }
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    mErrorCode = aMapLocation.getErrorCode();
                    mErrorInfo = aMapLocation.getErrorInfo();
                    Log.e(TAG,"location Error, ErrCode:" + mErrorCode + ", errInfo:" + mErrorInfo);
                    return;
                }
                Log.e(TAG, "aMapLocation is null");
            }
        });
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setLocationCacheEnable(true);
        mLocationOption.setNeedAddress(true);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);

        if(null != BeeWorks.getInstance().config.beeWorksAmap
                && null != BeeWorks.getInstance().config.beeWorksAmap.getInfo()) {
            AMapLocationClient.setApiKey(BeeWorks.getInstance().config.beeWorksAmap.getInfo().getAppKey());
        }
        //设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
    }

    /**
     * 启动定位
     */
    public void startLocation(Context context, int accuracy) {
        if (mLocationClient == null) {
            init(context);
        }
        switch (accuracy) {
            case 2 :
                mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

                break;

            case 1:
                mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);

                break;

            case 0:
                mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);

                break;
        }

        mLocationClient.startLocation();
    }

    /**
     * 停止定位
     */
    public void stopLocation(Context context) {
        if (mLocationClient == null) {
            init(context);
        }
        mLocationClient.stopLocation();
        clearLocation();
    }

    private void clearLocation() {
        mLatitude = -1;
        mLongitude = -1;
        mAddress = StringUtils.EMPTY;
        mCity = StringUtils.EMPTY;
        mDistrict = StringUtils.EMPTY;
        mStreet = StringUtils.EMPTY;
        mAoiName = StringUtils.EMPTY;
        mErrorCode = -1;
        mErrorInfo =  StringUtils.EMPTY;

    }

    public boolean isLocated() {
        return mLatitude != -1 && mLongitude != -1;
    }


}
