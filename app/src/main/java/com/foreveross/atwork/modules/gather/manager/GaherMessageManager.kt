package com.foreveross.atwork.modules.gather.manager

import com.foreverht.threadGear.AsyncTaskThreadPool
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.api.sdk.gather.postData
import com.foreveross.atwork.infrastructure.model.location.GetLocationRequest
import com.foreveross.atwork.infrastructure.newmessage.post.gather.CmdGatherMessage
import com.foreveross.atwork.infrastructure.newmessage.post.gather.GatherDataType
import com.foreveross.atwork.infrastructure.plugin.map.location.OnGetLocationListener
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.utils.JsonUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.modules.gather.model.GatherLocationRequestData
import com.foreveross.atwork.modules.location.manager.getOneShotLocation
import com.foreveross.atwork.modules.web.model.ReplaceResult
import com.foreveross.atwork.modules.web.util.UrlReplaceHelper

object GaherMessageManager {

    val offlinePullingMessages = HashMap<GatherDataType, CmdGatherMessage>()

    fun updateOfflinePullingData(message: CmdGatherMessage) {
        offlinePullingMessages[message.mGatherDataType] = message
    }

    fun replayOfflinePullingData() {
        offlinePullingMessages.forEach {
            handleGatherMessage(it.value)
        }

        offlinePullingMessages.clear()
    }

    fun handleGatherMessage(message: CmdGatherMessage) {
        if (StringUtils.isEmpty(message.mDataUrl)) {
            return
        }

        UrlReplaceHelper.replaceKeyParams(message.mDataUrl) { replaceResult ->

            when (message.mGatherDataType) {

                GatherDataType.LOCATION -> doPostLocationData(replaceResult)
            }


        }

    }

    private fun doPostLocationData(replaceResult: ReplaceResult) {
        AsyncTaskThreadPool.getInstance().run {


            val getLocationRequest = GetLocationRequest().apply {
                mSource = GetLocationRequest.Source.AMAP.toString()
            }

            getOneShotLocation(getLocationRequest, OnGetLocationListener {
                //assemble post data
                if (it.isSuccess) {
                    val gatherLocationRequestData = GatherLocationRequestData(
                            longitude = it.mLongitude,
                            latitude = it.mLatitude,
                            userId = LoginUserInfo.getInstance().getLoginUserId(AtworkApplicationLike.baseContext),
                            username = LoginUserInfo.getInstance().getLoginUserUserName(AtworkApplicationLike.baseContext))


                    postData(AtworkApplicationLike.baseContext, replaceResult.url!!, JsonUtil.toJson(gatherLocationRequestData))

                }

            })
        }
    }

}

