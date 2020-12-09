package com.foreveross.atwork.infrastructure.plugin.ibeacon

import android.app.Activity
import android.content.Context
import com.foreveross.atwork.infrastructure.model.ibeacon.*
import com.foreveross.atwork.infrastructure.plugin.WorkplusPlugin

interface IBeaconCounterPlugin : WorkplusPlugin {
    /**扫描所有设备*/
    fun scanPeripherals(context: Context,scanPeripheralsResult: ScanPeripheralsResult,callbacks: IBeaconDataCallBack<List<ScanPeripheralsData>>)
    /**获取当前设备的特征*/
    fun searchCharacteristics(context: Context,searchCharacteristicsResult: SearchCharacteristicsResult,callbacks: IBeaconDataCallBack<SearchCharacteristicsData>)
    /**读取设备sn值*/
    fun readSNValue(context: Context,readSnResult: ReadSnResult,callbacks: IBeaconDataCallBack<ReadSnData>)
    /**写入设备Sn值*/
    fun writeSn(context: Context,readSnResult: ReadSnResult,callbacks: IBeaconDataCallBack<WriteStateData>)
    /**写入设备Maijor*/
    fun writeMajor(context: Context,writeMajorResult: WriteMajorResult,callbacks: IBeaconDataCallBack<WriteStateData>)
    /**写入设备Minor*/
    fun writeMinor(context: Context,writeMinorResult: WriteMinorResult,callbacks: IBeaconDataCallBack<WriteStateData>)
    /**注册iBeacon设备
     * @param permission 0权限正常；-2无蓝牙权限；-3无定位权限
     * */
    fun registerIBeacon(context: Activity,permission: Int,registerIbeaconResult: RegisterIbeaconResult,callbacks: IBeaconDataCallBack<List<ScanPeripheralsData>>)
    /**后台扫描iBeacon设备（考勤用）*/
    fun registerScanIBeacon(context: Activity)
    /**开启iBeacon扫描*/
    fun startScan(context: Context,callbacks: IBeaconDataCallBack<List<ScanPeripheralsData>>)
    /**关闭iBeacon扫描*/
    fun stopScan()

    fun init(context: Context)
    fun release()
}

interface OnIBeaconListener {
    fun onResult(scanPeripheralsDataList: List<ScanPeripheralsData>)
}