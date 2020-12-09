package com.foreveross.atwork.api.sdk.robot

import android.content.Context
import com.foreveross.atwork.api.sdk.UrlConstantManager
import com.foreveross.atwork.api.sdk.net.HttpResult
import com.foreveross.atwork.api.sdk.net.HttpURLConnectionComponent
import com.foreveross.atwork.api.sdk.robot.response.GetInstructResponse
import com.foreveross.atwork.api.sdk.util.NetGsonHelper
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo

class RobotSyncNetService {
    companion object{
        val sInstance = RobotSyncNetService()
        const val SKIP_DEFAULT = 0
        const val LIMIT_DEFAULT = 20
    }

    /**
     * 获取语音指令接口
     * @param context
     * @param discussionId
     * @return
     */
    fun queryRobotInstructionsBasicInfo(context: Context,refreshTime: Long,skip: Int,limit: Int): HttpResult {
        val url = String.format(UrlConstantManager.getInstance().robotInstructionsUrl,
                LoginUserInfo.getInstance().getLoginUserAccessToken(context),skip,limit,refreshTime)
        val httpResult = HttpURLConnectionComponent.getInstance().getHttp(url)
        if (httpResult.isNetSuccess) {
            httpResult.result(NetGsonHelper.fromNetJson(httpResult.result, GetInstructResponse::class.java))
        }

        return httpResult
    }
}