package com.foreveross.atwork.modules.search.component

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.modules.search.component.searchVoice.ISearchVoiceView
import com.foreveross.atwork.modules.search.component.searchVoice.OnSearchVoiceViewHandleListener
import com.foreveross.atwork.modules.search.model.TalkingRecognizeResult
import com.foreveross.atwork.utils.AtworkToast
import com.foreveross.atwork.utils.AtworkUtil
import com.foreveross.xunfei.JsonParser
import com.foreveross.xunfei.newXfSpeechRecognizer
import com.foreveross.xunfei.startXfSpeechRecognizer
import com.iflytek.cloud.*
import kotlinx.android.synthetic.main.component_search_voice_float_action.view.*

val TAG: String = SearchVoiceXunfeiFloatView::class.java.name




class SearchVoiceXunfeiFloatView: FrameLayout, ISearchVoiceView {


    var onSearchVoiceViewListener: OnSearchVoiceViewHandleListener? = null

    private var speechRecognizer: SpeechRecognizer? = null

    constructor(context: Context) : super(context) {
        findViews()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        findViews()
    }

    private fun findViews() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.component_search_voice_float_action, this)

        initWaveView()
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {

        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                startRecognize()
            }


            MotionEvent.ACTION_UP -> {
                speechRecognizer?.cancel()
                handleCancelAction()
            }
        }

        return true
    }

    private fun startRecognize() {

        if(PermissionsManager.getInstance().hasPermission(AtworkApplicationLike.baseContext, Manifest.permission.RECORD_AUDIO)) {
            doStartRecognize()
            return
        }


        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(context as Activity, arrayOf(Manifest.permission.RECORD_AUDIO), object : PermissionsResultAction() {

            override fun onGranted() {

            }

            override fun onDenied(permission: String) {
                AtworkUtil.popAuthSettingAlert(context, permission)
            }
        })
    }

    private fun doStartRecognize() {
        val newXfSpeechRecognizerResult = if (null == speechRecognizer) {
            newXfSpeechRecognizer(AtworkApplicationLike.baseContext, VoiceRecognizerListener(searchVoiceXunfeiFloatView = this))

        } else {
            startXfSpeechRecognizer(AtworkApplicationLike.baseContext, speechRecognizer!!, VoiceRecognizerListener(searchVoiceXunfeiFloatView = this))
        }

        if (ErrorCode.SUCCESS != newXfSpeechRecognizerResult.errorCode) {
            onSearchVoiceViewListener?.onCancel()
            return
        }

        speechRecognizer = newXfSpeechRecognizerResult.speechRecognizer

        talkingMode()
    }

    fun handleCancelAction() {
        stillMode()
        onSearchVoiceViewListener?.onCancel()
    }



    private fun initWaveView() {
        waveView.setMaxRadius(DensityUtil.dip2px(80F).toFloat())
        waveView.setInitialRadius(DensityUtil.dip2px(30F).toFloat())
        waveView.setColor(ContextCompat.getColor(BaseApplicationLike.baseContext, R.color.common_blue_bg))
        waveView.setAlpha(100)
        waveView.setSpeed(1000)
    }


    private fun stillMode() {
        tvVoiceStateGuide.setText(R.string.press_talking)
        waveView.stop()
    }

    private fun talkingMode() {
        tvVoiceStateGuide.setText(R.string.release_searching)
        waveView.start()
    }


    override fun handleInit() {
    }

    override fun handleDestroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
    }

    override fun setOnSearchVoiceViewHandleListener(listener: OnSearchVoiceViewHandleListener) {
        this.onSearchVoiceViewListener = listener
    }

}

class VoiceRecognizerListener(var searchVoiceXunfeiFloatView: SearchVoiceXunfeiFloatView?): RecognizerListener {



    override fun onVolumeChanged(volume: Int, data: ByteArray) {
        LogUtil.e(TAG, "返回音频数据：" + data.size)

    }

    override fun onResult(results: RecognizerResult, isLast: Boolean) {
        LogUtil.e(TAG, "onResult ->" + results.resultString)

        val result = JsonParser.parseIatResult(results.resultString)

        LogUtil.e(TAG, "onResult text ->$result")


        searchVoiceXunfeiFloatView?.onSearchVoiceViewListener?.onTalking(TalkingRecognizeResult(isLast, result, replaced = false))

    }

    override fun onBeginOfSpeech() {
        // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入

        LogUtil.e(TAG, "onBeginOfSpeech")

        searchVoiceXunfeiFloatView?.onSearchVoiceViewListener?.onStart()
    }

    override fun onEvent(eventType: Int, arg1: Int, arg2: Int, obj: Bundle?) {
    }

    override fun onEndOfSpeech() {
        // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
        LogUtil.e(TAG, "onEndOfSpeech")

        searchVoiceXunfeiFloatView?.handleCancelAction()
    }

    override fun onError(error: SpeechError) {
        LogUtil.e(TAG, "error -> $error.errorDescription   error_code ->   $error.errorCode")

        // Tips：
        // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
        // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
        if(10118 == error.errorCode) {

        } else {
            AtworkToast.showToast(error.toString())
        }

        searchVoiceXunfeiFloatView?.handleCancelAction()
    }

}