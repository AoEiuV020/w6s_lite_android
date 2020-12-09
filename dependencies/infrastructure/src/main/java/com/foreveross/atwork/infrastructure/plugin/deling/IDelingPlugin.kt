package com.foreveross.atwork.infrastructure.plugin.deling

import android.content.Context
import com.foreveross.atwork.infrastructure.model.deling.DelingDeleteRecordsAction
import com.foreveross.atwork.infrastructure.model.deling.DelingOpenDoorAction
import com.foreveross.atwork.infrastructure.model.deling.DelingReadRecordsAction
import com.foreveross.atwork.infrastructure.plugin.WorkplusPlugin

interface IDelingPlugin: WorkplusPlugin {
    fun openDoor(context: Context, delingOpenDoorAction: DelingOpenDoorAction, listener: WorkplusOpenDoorCallbackListener)

    fun scanDevices(context: Context, listener: WorkplusScanDevicesListener)

    fun getLockRecords(context: Context, delingReadRecordsRequest: DelingReadRecordsAction, listener: WorkplusGetLockRecordsListener)


    fun deleteLockRecords(context: Context, delingDeleteRecordsRequest: DelingDeleteRecordsAction, listener: WorkplusDeleteLockRecordsListener)

}

interface WorkplusOpenDoorCallbackListener {
    fun onOpened(status: Int, msg: String)
}


interface WorkplusScanDevicesListener {
    fun onScanned(deviceIds: Array<String>)
}

interface WorkplusGetLockRecordsListener {
    fun onRead(totalCount: Int, records: List<Any>?)
}


interface WorkplusDeleteLockRecordsListener {
    fun onDelete(result: Int)
}