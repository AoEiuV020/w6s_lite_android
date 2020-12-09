package com.foreveross.atwork.infrastructure.plugin.face

import com.foreveross.atwork.infrastructure.plugin.face.model.FaceBioDetectResult

interface OnFaceBioDetectListener {
    fun onDetectSuccess(detectResult: FaceBioDetectResult)
    fun onDetectFailure(errorCode: Int, errorMsg: String)
}