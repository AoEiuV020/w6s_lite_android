@file: JvmName("LocationHelper")

package com.foreveross.atwork.modules.location.manager

import com.foreverht.threadGear.AsyncTaskThreadPool
import com.foreverht.workplus.amap.WorkPlusLocationManager
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.location.GetLocationRequest
import com.foreveross.atwork.infrastructure.plugin.map.location.OnGetLocationListener
import com.foreveross.atwork.modules.map.location.GooglePlayServiceMapService

fun getOneShotLocation(getLocationRequest: GetLocationRequest, getLocationListener: OnGetLocationListener) {
    when(getLocationRequest.mSource) {
        GetLocationRequest.Source.GOOGLE.toString() -> {
            getGooglePlayServiceLocation(getLocationRequest, getLocationListener)
        }


        GetLocationRequest.Source.AMAP.toString() -> {
            getAmpLocation(getLocationRequest, getLocationListener)
        }

    }
}


fun getAmpLocation(finalGetLocationRequest: GetLocationRequest, getLocationListener: OnGetLocationListener) {
    WorkPlusLocationManager.getInstance().getLocationFlash(BaseApplicationLike.baseContext, finalGetLocationRequest, null, getLocationListener)
}

private fun getGooglePlayServiceLocation(getLocationRequest: GetLocationRequest, getLocationListener: OnGetLocationListener) {
    AsyncTaskThreadPool.getInstance().run {
        GooglePlayServiceMapService.getInstance().startLocation(BaseApplicationLike.baseContext, (getLocationRequest.mTimeout * 1000).toLong(), null, getLocationListener)
    }
}