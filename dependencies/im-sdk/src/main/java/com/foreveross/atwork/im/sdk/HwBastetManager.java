package com.foreveross.atwork.im.sdk;

//import com.huawei.android.bastet.HwBastet;


/**
 * 华为高能效心跳托管
 * Created by reyzhang22 on 16/7/4.
 */
public class HwBastetManager {

//    private static final String TAG = HwBastetManager.class.getSimpleName();
//
//    private static HwBastetManager sInatance = new HwBastetManager();
//
//    private HwBastet mHwBastet = null;
//
//    private boolean mBastetHbInit = false;
//
//    private boolean mReconnEnabled = false;
//
//
//    public static HwBastetManager getInstance() {
//        synchronized (TAG) {
//            if (sInatance == null) {
//                sInatance = new HwBastetManager();
//            }
//            return sInatance;
//        }
//    }
//
//    public void init(Context context, Handler handler, Socket socket) {
//        if (isSupportHwBastet() && RomUtil.isHuawei()) {
//            mHwBastet = new HwBastet("BASTET", socket, handler, context);
//            if (mHwBastet.isBastetAvailable()) {
//                configAol(true);
//                configReconn(true);
//                bastetHbEnable(true);
//            }
//
//        }
//    }
//
//    /**
//     * 确认当前系统是否存在 Bastet 功能 JAVA 库
//     * @return
//     */
//    public boolean isSupportHwBastet() {
//        boolean isFound = false;
//        try {
//            Class.forName("com.huawei.android.bastet.HwBastet");
//            isFound = true;
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        return isFound;
//    }
//
//    public void configAol(boolean enable) {
//        if (mHwBastet != null) {
//            Logger.e(TAG, "configAolOnOff " + enable);
//            if (!enable) {
//                mBastetHbInit = false;
//                mReconnEnabled = false;
//            }
//        }
//    }
//
//    public void configReconn(boolean enable) {
//        if (mHwBastet != null) {
//            Logger.e(TAG, "configReconn " + enable);
//            try {
//                mHwBastet.reconnectSwitch(enable);
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            mReconnEnabled = enable;
//        }
//    }
//
//    public void bastetHbEnable(boolean enable) {
//        if (mHwBastet != null) {
//            Logger.e(TAG, "bastetHbEnable " + enable);
//            byte[] send = new byte[2];
//            byte[] reply = new byte[2];
//            send[0] = 'A';
//            send[1] = 'B';
//            reply[0] = 'C';
//            reply[1] = 'D';
//            try {
//                if (enable) {
//                    if (mBastetHbInit) {
//                        mHwBastet.resumeHeartbeat();
//                    } else {
//                        int interval =  5*60*1000;
//                        mHwBastet.setAolHeartbeat(interval, send, reply);
//                        mBastetHbInit = true;
//                    }
//                } else {
//                    mHwBastet.pauseHeartbeat();
//                }
//            } catch (RemoteException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//    }



}
