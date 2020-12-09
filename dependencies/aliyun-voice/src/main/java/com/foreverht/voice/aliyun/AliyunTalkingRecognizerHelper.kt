@file: JvmName("AliyunTalkingRecognizerHelper")

package com.foreverht.voice.aliyun

import android.os.AsyncTask
import com.alibaba.idst.token.AccessToken
import com.alibaba.idst.util.NlsClient
import com.alibaba.idst.util.SpeechRecognizerWithRecorder
import com.alibaba.idst.util.SpeechRecognizerWithRecorderCallback
import com.foreverht.threadGear.AsyncTaskThreadPool
import com.foreverht.threadGear.HighPriorityCachedTreadPoolExecutor
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.aliyun.AccessTokenInfo
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.infrastructure.utils.TimeUtil

const val TAG = "ALIYUN_TalkingRecognizerHelper"


class NewAliyunSpeechRecognizerResult(
        var errorCode: Int,

        var speechRecognizer: SpeechRecognizerWithRecorder,

        var client: NlsClient
)

fun newAliyunNlsClient():NlsClient  {
    return NlsClient()
}

fun newAliyunSpeechRecognizer(client: NlsClient?, callback: SpeechRecognizerWithRecorderCallback, getResult: (NewAliyunSpeechRecognizerResult) -> Unit) {

    //第一步，创建client实例，client只需要创建一次，可以用它多次创建recognizer
    //需要在 ui 线程初始化
    val clientHandle: NlsClient = client ?: NlsClient()
    val speechRecognizer = clientHandle.createRecognizerWithRecorder(callback)

    object : AsyncTask<Void, Void, NewAliyunSpeechRecognizerResult>() {
        override fun doInBackground(vararg params: Void?): NewAliyunSpeechRecognizerResult {


            val accessToken = getAccessTokenSync()

            speechRecognizer.setToken(accessToken)
            speechRecognizer.setAppkey(AtworkConfig.VOICE_CONFIG.aliyunSdk?.appKey)

            // 设置返回中间结果，更多参数请参考官方文档
            // 开启ITN
            speechRecognizer.enableInverseTextNormalization(true)
            // 开启标点
            speechRecognizer.enablePunctuationPrediction(false)
            // 不返回中间结果
            speechRecognizer.enableIntermediateResult(true)
            // 设置打开服务端VAD
            speechRecognizer.enableVoiceDetection(true)
            speechRecognizer.setMaxStartSilence(10000)
            speechRecognizer.setMaxEndSilence(2000)


            return NewAliyunSpeechRecognizerResult(errorCode = 0, speechRecognizer = speechRecognizer, client = clientHandle)
        }

        override fun onPostExecute(result: NewAliyunSpeechRecognizerResult) {
            getResult(result)
        }

    }.executeOnExecutor(HighPriorityCachedTreadPoolExecutor.getInstance())


}

fun startAliyunSpeechRecognizer(speechRecognizer: SpeechRecognizerWithRecorder): Int {
    return speechRecognizer.start()
}

fun cancelAliyunSpeechRecognizer(speechRecognizer: SpeechRecognizerWithRecorder?) {
    speechRecognizer?.stop()
}

fun destroyAliyunSpeechRecognizer(client: NlsClient?, speechRecognizer: SpeechRecognizerWithRecorder?) {
    speechRecognizer?.stop()
    client?.release()
}

fun checkAccessToken() {
    AsyncTaskThreadPool.getInstance().execute {
        getAccessTokenSync()
    }
}

fun refreshAccessToken() {
    AsyncTaskThreadPool.getInstance().execute {
        refreshAccessTokenSync()
    }
}


fun getAccessTokenSync(): String? {
    val accessTokenInfo = CommonShareInfo.getAliyunAccessToken(BaseApplicationLike.baseContext)
    accessTokenInfo?.let {
        //未过期, 直接使用
        if(TimeUtil.getCurrentTimeInMillis() < it.expireTime * 1000L) {
            return it.accessToken
        }
    }
    val accessToken = refreshAccessTokenSync()



    return accessToken?.token
}

private fun refreshAccessTokenSync(): AccessToken? {
    if(!AtworkConfig.VOICE_CONFIG.isEnabled) {
        return null
    }

    // Token有有效期，请使用https://help.aliyun.com/document_detail/72153.html 动态生成token
    val accessToken = AccessToken(AtworkConfig.VOICE_CONFIG.aliyunSdk?.keyId, AtworkConfig.VOICE_CONFIG.aliyunSdk?.keySecret)

    LogUtil.e(TAG, "request accessToken")
    try {
        accessToken.apply()

    } catch (e: Exception) {
        e.printStackTrace()

    } finally {

        if (!StringUtils.isEmpty(accessToken.token)) {
            CommonShareInfo.setAliyunAccessToken(BaseApplicationLike.baseContext, AccessTokenInfo(accessToken.token, accessToken.expireTime))
        }


        LogUtil.e(TAG, "getAccessTokenSync -> accessToken.token:  ${accessToken.token}   + time: ${accessToken.expireTime}")

    }
    return accessToken
}