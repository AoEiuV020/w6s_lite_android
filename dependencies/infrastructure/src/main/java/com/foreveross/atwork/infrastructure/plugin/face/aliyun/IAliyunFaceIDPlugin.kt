package com.foreveross.atwork.infrastructure.plugin.face.aliyun

import android.content.Context
import com.foreveross.atwork.infrastructure.model.face.aliyun.StartBioAuthAction
import com.foreveross.atwork.infrastructure.plugin.WorkplusPlugin
import com.foreveross.atwork.infrastructure.plugin.face.inter.IAliyunFaceBioListener


const val SDK_CODE_PASS = 2

const val SDK_CODE_FAIL = 1

const val SDK_CODE_IN_AUDIT = 0

const val SDK_CODE_NOT = -1

const val SDK_CODE_EXCEPTION = -2

const val SDK_CODE_NO_TOUCH = 10001

const val MAX_NEGATIVE_SIZE = 2 shl 20 //2mb


interface IAliyunFaceIDPlugin: WorkplusPlugin {
    fun init(context : Context)

    fun startBioAuth(context: Context, startBioAuthAction: StartBioAuthAction, onResultListener: IAliyunFaceBioListener)
}


