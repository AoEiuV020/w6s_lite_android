package com.foreveross.atwork.modules.newsSummary.data

abstract class DataCallBack<T>{
    /**需要传输的整合数据*/
    abstract fun onResult(data: T?)
    /**接口调用错误时返回*/
    abstract fun onFailure(data: String)
}