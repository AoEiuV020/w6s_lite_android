package com.foreveross.atwork.listener;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.broadcast.NetworkBroadcastReceiver;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.services.ImSocketService;

/**
 * Created by lingen on 15/6/9.
 * Description:
 */
public class IMNetworkChangedListener implements NetworkBroadcastReceiver.NetworkChangedListener {
    
    @Override
    public void networkChanged(NetworkBroadcastReceiver.NetWorkType networkType) {
        if (AtworkApplicationLike.sNetWorkType == null) {
            AtworkApplicationLike.sNetWorkType = networkType;
        }

        if (!AtworkApplicationLike.sNetWorkType.equals(networkType)) {
            AtworkApplicationLike.sNetWorkType = networkType;
            ImSocketService.checkConnection(BaseApplicationLike.baseContext);
        }

    }
}
