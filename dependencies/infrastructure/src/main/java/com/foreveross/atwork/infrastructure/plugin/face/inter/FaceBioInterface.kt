package com.foreveross.atwork.infrastructure.plugin.face.inter

import com.foreveross.atwork.infrastructure.plugin.face.model.FaceBioSuccessResult

interface IAliyunFaceBioListener {
    fun onSuccess(successResult: FaceBioSuccessResult)
    fun onError(sdkErrorCode: Int, netErrorCode: Int, netErrorMsg: String?)
}

interface IW6sApiNetListener {
    fun onSuccess()
    fun onError(netErrorCode: Int, netErrorMsg: String?)
}



interface IW6sApiNetWithResultListener<T> {
    fun onSuccess(t: T)
    fun onError(netErrorCode: Int, netErrorMsg: String?)
}

