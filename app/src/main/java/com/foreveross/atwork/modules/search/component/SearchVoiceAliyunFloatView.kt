package com.foreveross.atwork.modules.search.component

import android.Manifest
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.alibaba.idst.util.NlsClient
import com.alibaba.idst.util.SpeechRecognizerWithRecorder
import com.alibaba.idst.util.SpeechRecognizerWithRecorderCallback
import com.foreverht.voice.aliyun.*
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction
import com.foreveross.atwork.infrastructure.utils.*
import com.foreveross.atwork.modules.search.component.searchVoice.ISearchVoiceView
import com.foreveross.atwork.modules.search.component.searchVoice.OnSearchVoiceViewHandleListener
import com.foreveross.atwork.modules.search.model.TalkingRecognizeResult
import com.foreveross.atwork.utils.AtworkToast
import com.foreveross.atwork.utils.AtworkUtil
import kotlinx.android.synthetic.main.component_search_voice_float_action.view.*
import org.json.JSONObject


class SearchVoiceAliyunFloatView : FrameLayout, ISearchVoiceView {


    private var onSearchVoiceViewListener: OnSearchVoiceViewHandleListener? = null

    private var speechRecognizer: SpeechRecognizerWithRecorder? = null
    private var nlsClient: NlsClient? = null

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

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startRecognize()
            }


            MotionEvent.ACTION_UP -> {
                cancelAliyunSpeechRecognizer(speechRecognizer)
                handleCancelAction()
            }
        }

        return true
    }

    private fun startRecognize() {
        if(!NetworkStatusUtil.isNetworkAvailable(AtworkApplicationLike.baseContext)) {
            AtworkToast.showResToast(R.string.network_error)
            return
        }


        if (PermissionsManager.getInstance().hasPermission(AtworkApplicationLike.baseContext, Manifest.permission.RECORD_AUDIO)) {
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

//        talkingMode()

        getAliyunSpeechRecognizer {
            val result = startAliyunSpeechRecognizer(it)
            LogUtil.e(TAG, "startAliyunSpeechRecognizer result -> $result")
        }
    }



    private fun getAliyunSpeechRecognizer(getResult: ((SpeechRecognizerWithRecorder) -> Unit)? = null) {


        newAliyunSpeechRecognizer(nlsClient, VoiceAliyunRecognizerListener(searchVoiceFloatView = this)) {
            nlsClient = it.client
            speechRecognizer = it.speechRecognizer

            getResult?.invoke(speechRecognizer!!)


        }

    }

    fun handleCancelAction() {
        stillMode()
        onSearchVoiceViewListener?.onCancel()
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
    }

    override fun setOnSearchVoiceViewHandleListener(listener: OnSearchVoiceViewHandleListener) {
        this.onSearchVoiceViewListener = listener
    }

    fun getOnSearchVoiceViewHandleListener(): OnSearchVoiceViewHandleListener? {
        return onSearchVoiceViewListener
    }

    override fun handleDestroy() {
        destroyAliyunSpeechRecognizer(nlsClient, speechRecognizer)
    }

    override fun handleInit() {
        nlsClient = newAliyunNlsClient()
        checkAccessToken()
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

    public fun talkingMode() {
        tvVoiceStateGuide.setText(R.string.release_searching)
        waveView.start()
    }

}


class VoiceAliyunRecognizerListener(var searchVoiceFloatView: SearchVoiceAliyunFloatView?) : SpeechRecognizerWithRecorderCallback {
    override fun onRecognizedResultChanged(message: String, code: Int) {
        LogUtil.e(TAG, "onRecognizedResultChanged ->  message: $message    code: $code")


        onTalking(message, false)
    }


    override fun onVoiceVolume(i: Int) {
        LogUtil.e(TAG, "onVoiceVolume ->  i: $i")

    }

    override fun onRecognizedCompleted(message: String, code: Int) {
        LogUtil.e(TAG, "onRecognizedCompleted ->  message: $message    code: $code")

        onTalking(message, true)

    }

    override fun onTaskFailed(message: String, code: Int) {
        LogUtil.e(TAG, "onTaskFailed ->  message: $message    code: $code")

        if(403 == code) {
            refreshAccessToken()
        }

        AtworkApplicationLike.runOnMainThread {

            if(!CommonUtil.isFastClick(1000)) {
                AtworkToast.showToast( "${AtworkApplicationLike.getResourceString(R.string.network_not_avaluable)} code: $code")
            }
            searchVoiceFloatView?.handleCancelAction()

        }
    }

    override fun onChannelClosed(message: String, code: Int) {
        LogUtil.e(TAG, "onChannelClosed ->  message: $message    code: $code")
        AtworkApplicationLike.runOnMainThread {
            searchVoiceFloatView?.handleCancelAction()

        }

    }

    override fun onVoiceData(data: ByteArray, i: Int) {
        LogUtil.e(TAG, "onVoiceData ->  i: $i")

    }

    override fun onRecognizedStarted(message: String, code: Int) {
        LogUtil.e(TAG, "onRecognizedStarted ->  message: $message    code: $code")

        AtworkApplicationLike.runOnMainThread {
            searchVoiceFloatView?.talkingMode()
            searchVoiceFloatView?.getOnSearchVoiceViewHandleListener()?.onStart()
        }
    }


    private fun onTalking(message: String, isLast: Boolean) {
        if (!StringUtils.isEmpty(message)) {
            val jsonObject = JSONObject(message)
            if (jsonObject.has("payload")) {
                val result = jsonObject.getJSONObject("payload").getString("result")
                AtworkApplicationLike.runOnMainThread {
                    searchVoiceFloatView?.getOnSearchVoiceViewHandleListener()?.onTalking(TalkingRecognizeResult(isLast, message = result))

                }
            }


        }
    }


}

