package com.foreveross.atwork.infrastructure.plugin.ibeacon

abstract class IBeaconDataCallBack<T> {
    /**需要传输的整合数据*/
    abstract fun onResult(code: Int,message: String,data: T?)
}