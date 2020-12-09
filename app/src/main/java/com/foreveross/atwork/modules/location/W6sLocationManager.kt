@file: JvmName("W6sLocationManager")

package com.foreveross.atwork.modules.location

import android.Manifest
import android.content.Context
import com.foreverht.workplus.amap.WorkPlusLocationManager
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.location.GetLocationInfo
import com.foreveross.atwork.infrastructure.model.location.GetLocationRequest
import com.foreveross.atwork.infrastructure.model.location.LocationDataInfo
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo
import com.foreveross.atwork.utils.CordovaHelper

private var lastRequestLocationTime = -1L

private const val REQUEST_LOCATION_THRESHOLD = 24 * 60 * 60 * 1000L

fun requestLocationData(context: Context) {

//    if(REQUEST_LOCATION_THRESHOLD > System.currentTimeMillis() - lastRequestLocationTime) {
//        return
//    }

    if (!PermissionsManager.getInstance().hasPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
        return
    }

    if (!PermissionsManager.getInstance().hasPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)) {
        return
    }

    WorkPlusLocationManager.getInstance().getLocationFlash(context, GetLocationRequest.newRequest().setSource(GetLocationRequest.Source.AMAP.toString()), null) { getLocationResponse: GetLocationInfo ->
        lastRequestLocationTime = System.currentTimeMillis()


        if (getLocationResponse.isSuccess) {
            val locationDataInfo = LocationDataInfo(
                    longitude = getLocationResponse.mLongitude,
                    latitude = getLocationResponse.mLatitude,
                    country = getLocationResponse.mCountry,
                    province = getLocationResponse.mProvince,
                    city = getLocationResponse.mCity
            )

            CommonShareInfo.updateLocationDataInfo(context, locationDataInfo)

        } else {
//            callbackContext.error()
        }
    }

}