package com.foreveross.atwork.manager.share

import android.app.Activity
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.tencent.mm.sdk.modelmsg.SendMessageToWX


class WXTimeLineShare @JvmOverloads constructor(activity: Activity, appId: String? = AtworkConfig.WX_APP_ID) : WXShare(activity, appId) {


    override fun getScene(): Int = SendMessageToWX.Req.WXSceneTimeline

}