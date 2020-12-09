package com.foreveross.atwork.infrastructure.plugin.zebra

import android.content.Context
import com.foreveross.atwork.infrastructure.model.zebra.DCSScannerBasicInfo
import com.foreveross.atwork.infrastructure.model.zebra.ZebraEventResult
import com.foreveross.atwork.infrastructure.plugin.WorkplusPlugin

interface IZebraPlugin: WorkplusPlugin {

    fun init(context: Context)

    fun initSetting(context: Context)

    fun generatePairBarcode(context: Context, bluetoothAddress: String): String?

    fun generatePairBarImage(context: Context, barCode: String): String?

    fun disconnect(scannerId: Int)

    fun connect(scannerId: Int)

    fun getScannerList(listener: OnZebraGetScannerListListener)

    fun bindEventListener(onZebraEventListener: OnZebraEventListener)

    fun release()
}

interface OnZebraGetScannerListListener {
    fun onResult(scannerBasicInfoList: List<DCSScannerBasicInfo>)
}

interface OnZebraEventListener {
    fun onEvent(zebraEventsResult: ZebraEventResult)
}


