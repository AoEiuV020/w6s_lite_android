package com.w6s.inter

import java.io.Serializable

interface OnPenalResultCallback : Serializable {
    fun onResult(resultStr: String)
    fun onCancel()
}