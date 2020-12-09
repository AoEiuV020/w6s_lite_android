package com.foreveross.atwork.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.ServiceCompat;
import com.foreveross.atwork.services.ImSocketService;
import com.foreveross.atwork.utils.AtworkUtil;

import java.util.Date;

/**
 * Created by lingen on 15/4/18.
 * Description:
 */

public class NetworkBroadcastReceiver extends BroadcastReceiver {


    public static final String ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    private NetworkChangedListener mNetworkChangedListener;

    public NetworkBroadcastReceiver(NetworkChangedListener networkChangedListener) {
        this.mNetworkChangedListener = networkChangedListener;
    }

    private static long mLastAutoPunchTime;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION.equals(intent.getAction())) {
            //获取管理网络连接相关操作的对象
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //判断网络连接
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected()) {
                Logger.v(ACTION, "network is disconnected.... at " + new Date().toLocaleString());
                mNetworkChangedListener.networkChanged(NetWorkType.NO_NETWORK);
            } else {
//                AdvertisementManager.getInstance().getRemoteBootAdvertisementsByOrgId(context, PersonalShareInfo.getInstance().getCurrentOrg(context));
                if (ConnectivityManager.TYPE_WIFI == networkInfo.getType()) {
                    mNetworkChangedListener.networkChanged(NetWorkType.WIFI);
                    if (System.currentTimeMillis() - mLastAutoPunchTime < 3000) {
                        return;
                    }

                    mLastAutoPunchTime = System.currentTimeMillis();

                    new Handler().postDelayed(() -> AtworkUtil.autoPunchInWifi(context), 3000);

                    Logger.v(ACTION, "network connected on WIFI.... at " + new Date().toLocaleString());
                } else if (ConnectivityManager.TYPE_MOBILE == networkInfo.getType()) {
                    mNetworkChangedListener.networkChanged(NetWorkType.MOBILE_NETWORK);
                    Logger.v(ACTION, "network connected on MOBILE_NETWORK.... at " + new Date().toLocaleString());
                }
            }

            ServiceCompat.startServiceCompat(context, ImSocketService.class);
        }
    }

    public enum NetWorkType {
        WIFI {
            @Override
            public boolean hasNetwork() {
                return true;
            }
        },

        NO_NETWORK {
            @Override
            public boolean hasNetwork() {
                return false;
            }
        },

        MOBILE_NETWORK {
            @Override
            public boolean hasNetwork() {
                return true;
            }
        };

        public abstract boolean hasNetwork();
    }

    public interface NetworkChangedListener {

        void networkChanged(NetWorkType networkType);

    }

}
