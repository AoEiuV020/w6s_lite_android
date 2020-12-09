package com.foreveross.atwork.infrastructure.plugin.face

interface OnFaceBioResultListener<T> {
    fun onSuccess(t: T)
    fun onFailure(errorCode: Int, errorMsg: String)
}