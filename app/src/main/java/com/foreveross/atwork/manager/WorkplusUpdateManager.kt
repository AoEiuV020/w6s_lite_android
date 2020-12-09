package com.foreveross.atwork.manager

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.utils.AtworkUtil

object WorkplusUpdateManager {

    const val ACTION_REFRESH_FLOAT_UPDATE_TIP_VIEW = "ACTION_REFRESH_FLOAT_UPDATE_TIP_VIEW"

    var isNeedShowUpdateTipFloat = false
    var showTipCount = 0


    fun initTipFloatStatus() {
        isNeedShowUpdateTipFloat = AtworkUtil.isFoundNewVersion(BaseApplicationLike.baseContext)

    }

    fun setTipFloatStatusAndRefresh(isNeedShowUpdateTipFloat: Boolean) {
        if(!isNeedShowUpdateTipFloat) {
            WorkplusUpdateManager.addShowTipCount()
        }


        if(this.isNeedShowUpdateTipFloat == isNeedShowUpdateTipFloat){
            return
        }



        this.isNeedShowUpdateTipFloat = isNeedShowUpdateTipFloat
        refreshFloatUpdateTipView()
    }

    fun refreshFloatUpdateTipView() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(Intent(ACTION_REFRESH_FLOAT_UPDATE_TIP_VIEW))

    }

    fun addShowTipCount() {
        showTipCount++
    }

    fun clear() {
        isNeedShowUpdateTipFloat = false
    }
}