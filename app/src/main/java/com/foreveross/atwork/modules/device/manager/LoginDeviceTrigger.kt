package com.foreveross.atwork.modules.device.manager

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.foreveross.atwork.infrastructure.model.device.LoginDeviceInfo

const val ACTION_REFRESH_DEVICE_INFO = "ACTION_REFRESH_DEVICE_INFO"

const val DATA_DEVICE_INFO = "DATA_DEVICE_INFO"

const val ACTION_REMOVE_DEVICE_INFO = "ACTION_REMOVE_DEVICE_INFO"



fun refreshLoginDeviceInfo(context: Context, loginDeviceInfo: LoginDeviceInfo) {
    val intent = Intent(ACTION_REFRESH_DEVICE_INFO)
    intent.putExtra(DATA_DEVICE_INFO, loginDeviceInfo)
    LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
}

fun removeLoginDeviceInfo(context: Context, loginDeviceInfo: LoginDeviceInfo) {
    val intent = Intent(ACTION_REMOVE_DEVICE_INFO)
    intent.putExtra(DATA_DEVICE_INFO, loginDeviceInfo)
    LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
}