@file: JvmName("XfTalkingRecognizerHelper")

package com.foreveross.xunfei

import android.content.Context
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.infrastructure.utils.language.LanguageUtil
import com.iflytek.cloud.*


const val TAG = "XF_TalkingRecognizerHelper"


class NewXfSpeechRecognizerResult(
        var errorCode: Int,

        var speechRecognizer: SpeechRecognizer
)


private val mInitListener = InitListener { code ->
    LogUtil.e(TAG, "SpeechRecognizer init() code = $code")

    if (code != ErrorCode.SUCCESS) {

    }
}


fun newXfSpeechRecognizer(context: Context, recognizerListener: RecognizerListener): NewXfSpeechRecognizerResult {

    val speechRecognizer = SpeechRecognizer.createRecognizer(context, mInitListener)
    return startXfSpeechRecognizer(context, speechRecognizer, recognizerListener)
}

fun startXfSpeechRecognizer(context: Context, speechRecognizer: SpeechRecognizer, recognizerListener: RecognizerListener): NewXfSpeechRecognizerResult {
    speechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD)
    // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
    // 根据文档 10000为最大的间隔
    speechRecognizer.setParameter(SpeechConstant.VAD_EOS, "10000")
    // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
    // 根据文档 10000为最大的间隔
    speechRecognizer.setParameter(SpeechConstant.VAD_BOS, "10000")

    // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
    speechRecognizer.setParameter(SpeechConstant.ASR_PTT, "0")

    val voiceTranslateTarget = if (LanguageUtil.isZhLocal(context)) {
        "zh_cn"
    } else {
        "en_us"
    }

    speechRecognizer.setParameter(SpeechConstant.LANGUAGE, voiceTranslateTarget)
    val result = speechRecognizer.startListening(recognizerListener)

    return NewXfSpeechRecognizerResult(result, speechRecognizer)
}

fun cancelXfSpeechRecognizer(speechRecognizer: SpeechRecognizer) {
    speechRecognizer.cancel()
}

fun destroyXfSpeechRecognizer(speechRecognizer: SpeechRecognizer) {
    speechRecognizer.destroy()
}




