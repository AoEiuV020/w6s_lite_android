package com.foreveross.atwork.infrastructure.plugin.deling

import android.content.Context
import com.foreveross.atwork.infrastructure.model.deling.UploadInfo
import com.foreveross.atwork.infrastructure.plugin.WorkplusPlugin

interface IDelingDataPlugin: WorkplusPlugin {

    fun init(context: Context)


}