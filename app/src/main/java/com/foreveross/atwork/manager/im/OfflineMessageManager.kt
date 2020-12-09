package com.foreveross.atwork.manager.im

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.manager.ReceivingTitleQueueManager
import com.foreveross.atwork.modules.chat.util.SessionRefreshHelper

abstract class OfflineMessageManager {

    companion object {
        /**
         * 离线拉取完毕的标记
         */
        const val ACTION_END_PULL_OFFLINE_MESSAGES = "ACTION_END_PULL_OFFLINE_MESSAGES"

    }

    protected fun notifyRefreshSessionInOffline(context: Context) {
        ReceivingTitleQueueManager.getInstance().pull(context, ReceivingTitleQueueManager.TAG_GET_OFFLINE)
        SessionRefreshHelper.notifyRefreshSessionAndCount()
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(Intent(ACTION_END_PULL_OFFLINE_MESSAGES))
    }
}