package com.foreveross.atwork.modules.log.service

import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.api.sdk.net.MediaCenterHttpURLConnectionUtil
import com.foreveross.atwork.api.sdk.net.model.UploadFileParamsMaker
import com.foreveross.atwork.api.sdk.setting.FeedbackNetService
import com.foreveross.atwork.api.sdk.setting.model.FeedbackJson
import com.foreveross.atwork.api.sdk.upload.model.MediaUploadResultResponseJson
import com.foreveross.atwork.infrastructure.utils.Logger
import com.foreveross.atwork.infrastructure.utils.TimeUtil
import com.foreveross.atwork.infrastructure.utils.encryption.MD5Utils
import java.util.*

object LoggerManager {

    fun uploadLog(beginTime: Long, endTime: Long) {
        Logger.zipLogs(beginTime, endTime, object : Logger.OnZipResultListener {
            override fun success(path: String?) {


                if(null == path) {
                    postLogNothing(beginTime, endTime)
                    return
                }

                Logger.getExecutorService().run {

                    val checkSum = MD5Utils.getDigest(path)


                    val uploadFileParamsMaker = UploadFileParamsMaker.newRequest()
                            .setMsgId(UUID.randomUUID().toString())
                            .setFileDigest(checkSum)
                            .setFilePath(path)
                            .setExpireLimit(-1)

                    val context = AtworkApplicationLike.baseContext
                    val httpResult = MediaCenterHttpURLConnectionUtil.getInstance().uploadCommonFile(context, uploadFileParamsMaker)
                    if(httpResult.isRequestSuccess) {
                        val basicResponseJSON = httpResult.resultResponse
                        //get mediaId name
                        if (null != basicResponseJSON) {
                            if (basicResponseJSON is MediaUploadResultResponseJson) {
                                postLogSync(basicResponseJSON, beginTime, endTime)

                            }

                        }


                    }
                }
            }

            override fun fail() {

            }

        })
    }

    private fun postLogSync(basicResponseJSON: MediaUploadResultResponseJson, beginTime: Long, endTime: Long) {
        val context = AtworkApplicationLike.baseContext

        val mediaId = basicResponseJSON.mediaId

        val logFeedback = FeedbackJson().apply {
            mType = "log"
            mAttachment = mediaId
            val feedBackBeginTime = TimeUtil.getStringForMillis(beginTime, TimeUtil.getTimeFormat3(context))
            val feedBackEndTime = TimeUtil.getStringForMillis(endTime, TimeUtil.getTimeFormat3(context))

            mFeedBack = if(feedBackBeginTime == feedBackEndTime) {
                "LOG【$feedBackBeginTime】"

            } else {
                "LOG【$feedBackBeginTime-$feedBackEndTime】"

            }

            mStartInterval = beginTime
            mEndInterval = endTime

        }

        FeedbackNetService.postFeedbackSync(context, logFeedback)
    }

    private fun postLogNothing(beginTime: Long, endTime: Long) {
        val context = AtworkApplicationLike.baseContext

        Logger.getExecutorService().run {
            val logFeedback = FeedbackJson().apply {
                mType = "log"
                mFeedBack = "LOG【暂无日志】"
                mStartInterval = beginTime
                mEndInterval = endTime

            }

            FeedbackNetService.postFeedbackSync(context, logFeedback)
        }
    }
}