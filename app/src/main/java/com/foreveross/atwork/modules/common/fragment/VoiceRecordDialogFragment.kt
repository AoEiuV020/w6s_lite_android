package com.foreveross.atwork.modules.common.fragment

import android.Manifest
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog
import com.foreverht.workplus.ui.component.dialogFragment.WorkplusLoadingDialog
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.component.BasicDialogFragment
import com.foreveross.atwork.component.WaveView
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction
import com.foreveross.atwork.infrastructure.utils.*
import com.foreveross.atwork.infrastructure.utils.language.LanguageUtil
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil
import com.foreveross.atwork.modules.chat.inter.VoicePlayingListener
import com.foreveross.atwork.modules.chat.util.AudioRecord
import com.foreveross.atwork.modules.voip.utils.VoipHelper
import com.foreveross.atwork.utils.AtworkToast
import com.foreveross.atwork.utils.AtworkUtil
import com.foreveross.xunfei.JsonParser
import com.iflytek.cloud.*
import io.kvh.media.amr.AmrFileDecoder
import java.util.concurrent.Executors


class VoiceRecordDialogFragment : BasicDialogFragment() {
    private val TAG: String = VoiceRecordDialogFragment::class.java.name

    private val lock = Any()
    private lateinit var rlRoot: RelativeLayout
    private lateinit var rlContentArea: RelativeLayout
    private lateinit var tvCancel: TextView
    private lateinit var tvPublish: TextView
    private lateinit var waveView: WaveView
    private lateinit var ivVoiceState: ImageView
    private lateinit var tvVoiceStateGuide: TextView
    private lateinit var tvVoiceProgress: TextView

    private lateinit var loadingDialog: WorkplusLoadingDialog

    private var audioRecord: AudioRecord? = null
    private var audioRecordPath: String = StringUtils.EMPTY

    private var state = State.STILL

    private var needRecordingTo60sRed = false

    private var voiceDuration = 0L

    private var voiceProgress = 0L

    private var voiceResult: String? = null

    private var mIat: SpeechRecognizer? = null
    private var mVoiceTranslateTarget = "zh_ch"

    private var action: ((String?) -> Unit)? = null

    /**是否为只录制，不进行语音识别*/
    private var isOnlyRecord = false

    fun setOnlyRecord(isOnlyRecordBool: Boolean){
        isOnlyRecord = isOnlyRecordBool
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //必须在 onCreateView 之前
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_pop_voice_record, null)
        findViews(view)
        registerListener()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(0))

        initViews()
    }

    override fun onStop() {
        super.onStop()

        AudioRecord.stopPlaying()
    }

    override fun onDestroy() {
        super.onDestroy()

        Executors.newSingleThreadExecutor().execute {
            if (State.RECORDING == state) {
                audioRecord?.cancelRecord()
            }

        }
    }

    private fun findViews(view: View) {
        rlRoot = view.findViewById(R.id.rl_root)
        rlContentArea = view.findViewById(R.id.rl_content_area)
        tvCancel = view.findViewById(R.id.tv_cancel)
        tvPublish = view.findViewById(R.id.tv_publish)
        waveView = view.findViewById(R.id.waveview)
        ivVoiceState = view.findViewById(R.id.iv_voice_state)
        tvVoiceStateGuide = view.findViewById(R.id.tv_voice_state_guide)
        tvVoiceProgress = view.findViewById(R.id.tv_voice_progress)

        loadingDialog = WorkplusLoadingDialog().apply {
            setGif(R.mipmap.voice_translating)
            setTip(R.string.translating_voice_to_text)

        }
    }

    private fun initViews() {

        tvVoiceProgress.text = getTimeShow(0)

        initWaveView()

        refreshStateAndVoiceView(state)
    }

    private fun initWaveView() {
        waveView.setMaxRadius(DensityUtil.dip2px(70F).toFloat())
        waveView.setInitialRadius(DensityUtil.dip2px(40F).toFloat())
        waveView.setColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.common_blue_bg))
        waveView.setAlpha(100)
        waveView.setSpeed(1000)
    }

    private fun registerListener() {
        tvCancel.setOnClickListener { dismiss() }

//        rlRoot.setOnClickListener { dismiss() }

//        rlContentArea.setOnClickListener{ handleVoiceStateClick() }

        ivVoiceState.setOnClickListener { handleVoiceStateClick() }

        tvPublish.setOnClickListener {

            if (State.STILL == state) {
                return@setOnClickListener
            }

            if (State.RECORDING == state) {
                if (CommonUtil.isFastClick(500)) {
                    return@setOnClickListener
                }

                audioRecord?.stopRecord()

                tvPublish.postDelayed({
                    if(!isOnlyRecord) {
                        startVoiceTranslate()
                    } else {
                        action?.invoke(audioRecordPath)
                    }
                }, 300)




                return@setOnClickListener
            }

            if(!isOnlyRecord) {
                startVoiceTranslate()
            }else {
                action?.invoke(audioRecordPath)
            }

        }
    }

    private fun handleVoiceStateClick() {
        if (CommonUtil.isFastClick(1000)) {
            return
        }

        if (VoipHelper.isHandlingVoipCall()) {
            AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip)
            return

        }

        when (state) {
            State.STILL -> handleStartRecord()
            State.RECORDING -> audioRecord?.stopRecord()
            State.DONE -> handleStartPlay()
            State.PLAYING -> handleStopPlay()
        }
    }

    private fun handleStopPlay() {
        stopPlayUI()
        AudioRecord.stopPlaying()
    }

    private fun handleStartPlay() {
        startPlayUI()
        AudioRecord.playAudio(activity, audioRecordPath, object : VoicePlayingListener {
            override fun playingAnimation() {

            }

            override fun stopPlayingAnimation() {
                activity?.runOnUiThread { stopPlayUI() }
            }
        })
    }

    private fun handleStartRecord() {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(this, arrayOf(Manifest.permission.RECORD_AUDIO), object : PermissionsResultAction() {
            override fun onGranted() {
                refreshStateAndVoiceView(State.RECORDING)

                startRecordCore()
                startRecordUI()

            }

            override fun onDenied(permission: String) {
                AtworkUtil.popAuthSettingAlert(context, permission)
            }
        })
    }

    override fun changeStatusBar(view: View) {
        StatusBarUtil.setColorNoTranslucent(view as ViewGroup, dialog?.window, ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.transparent_70))

    }

    private fun getTimeShow(time: Long): String {
        return (time / 1000).toString() + "\""
    }

    private fun startRecordCore() {
        audioRecord = AudioRecord()
        audioRecord!!.apply {
            bindRecordListener(this)
            startRecord()
        }

    }

    private fun bindRecordListener(audioRecord: AudioRecord) {
        audioRecord.setRecordListener(object : AudioRecord.RecordListener {

            override fun timeout() {

            }

            override fun recordFinished(audioId: String, playtime: Int) {
                activity?.runOnUiThread {
                    audioRecordPath = VoiceChatMessage.getAudioPath(AtworkApplicationLike.baseContext, audioId)
                    stopRecordUI(playtime)
                }
            }

            override fun tooShort() {
                activity?.runOnUiThread {
                    tooShortRecordUI()
                    AtworkToast.showResToast(R.string.recored_too_short)

                }
            }

            override fun recordFail() {
                audioRecord?.apply {
                    cancelAuthCheckSchedule()
                    cancelRecord()
                }

                activity?.runOnUiThread {
                    failRecordUI()

                    AtworkAlertDialog(activity, AtworkAlertDialog.Type.SIMPLE)
                            .setContent(getString(R.string.tip_record_fail_no_auth, getString(R.string.app_name)))
                            .hideBrightBtn()
                            .setDeadBtnText(R.string.i_known)
                            .show()
                }

            }

        })
    }

    private fun startPlayUI() {
        refreshStateAndVoiceView(State.PLAYING)

        voiceProgress = 0
        tvVoiceProgress.text = getTimeShow(voiceProgress)
        startCountDownPlay()

    }

    private fun stopPlayUI() {
        refreshStateAndVoiceView(State.DONE)
        stopCountDownPlay()

        voiceProgress = 0
        tvVoiceProgress.text = getTimeShow(voiceDuration)


    }


    private fun startRecordUI() {
        refreshStateAndVoiceView(State.RECORDING)

        voiceDuration = 0
        needRecordingTo60sRed = false
        tvVoiceProgress.text = getTimeShow(voiceDuration)
        startCountDownRecord()

        waveView.start()

    }

    private fun stopRecordUI(voiceDuration: Int) {
        waveView.stop()

        if(60 == voiceDuration) {
            needRecordingTo60sRed = true
        }

        refreshStateAndVoiceView(State.DONE)

        stopCountDownRecord()

        this.voiceDuration = voiceDuration * 1000L
        tvVoiceProgress.text = getTimeShow(this.voiceDuration)

    }


    private fun tooShortRecordUI() {
        waveView.stop()
        refreshStateAndVoiceView(State.STILL)
        stopCountDownRecord()

    }

    private fun failRecordUI() {
        waveView.stop()
        refreshStateAndVoiceView(State.STILL)
        stopCountDownRecord()
    }


    private var countDownRecordRunnable: Runnable = Runnable {
        synchronized(lock) {
            voiceDuration += 1000
            tvVoiceProgress.text = getTimeShow(voiceDuration)


            startCountDownRecord()

            LogUtil.e("recording ~~~  $voiceDuration")
        }

    }


    private fun grayTvVoiceStateGuide() {
        tvVoiceStateGuide.setTextColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.common_text_color_999))
    }

    private fun redTvVoiceStateGuide() {
        tvVoiceStateGuide.setTextColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.red))
    }


    private fun startCountDownRecord() {
        tvVoiceProgress.postDelayed(countDownRecordRunnable, 1000)
    }

    private fun stopCountDownRecord() {
        synchronized(lock) {
            tvVoiceProgress.removeCallbacks(countDownRecordRunnable)
        }

    }


    private var countDownPlayRunnable: Runnable = Runnable {
        synchronized(lock) {
            voiceProgress += 1000
            tvVoiceProgress.text = getTimeShow(voiceProgress)

            startCountDownPlay()

            LogUtil.e("play ~~~  $voiceProgress")
        }

    }


    private fun startCountDownPlay() {
        tvVoiceProgress.postDelayed(countDownPlayRunnable, 1000)
    }

    private fun stopCountDownPlay() {
        synchronized(lock) {
            tvVoiceProgress.removeCallbacks(countDownPlayRunnable)
        }

    }


    private fun refreshStateAndVoiceView(state: State) {
        this.state = state
        when (state) {
            State.STILL -> {
                ivVoiceState.setImageResource(R.mipmap.icon_pop_record_voice_record_state)
                tvVoiceStateGuide.setText(R.string.click_to_start)
                grayTvVoiceStateGuide()


            }

            State.RECORDING -> {
                ivVoiceState.setImageResource(R.mipmap.icon_pop_record_voice_record_state)
                tvVoiceStateGuide.setText(R.string.click_to_finish)
                grayTvVoiceStateGuide()



            }

            State.DONE -> {
                ivVoiceState.setImageResource(R.mipmap.icon_pop_record_voice_play_state)

                if(needRecordingTo60sRed) {
                    tvVoiceStateGuide.setText(R.string.pop_voice_record_max_tip)
                    redTvVoiceStateGuide()

                    needRecordingTo60sRed = false

                } else {
                    tvVoiceStateGuide.setText(R.string.click_to_listen)
                    grayTvVoiceStateGuide()

                }


            }


            State.PLAYING -> {
                ivVoiceState.setImageResource(R.mipmap.icon_pop_record_voice_playing_state)
                tvVoiceStateGuide.setText(R.string.click_to_stop)
                grayTvVoiceStateGuide()



            }
        }

    }


    fun setPublishAction(action: (String?) -> Unit) {
        this.action = action
    }


    private fun startVoiceTranslate() {
        voiceResult = StringUtils.EMPTY
        mVoiceTranslateTarget = if (LanguageUtil.isZhLocal(activity)) {
            "zh_cn"

        } else {
            "en_us"
        }

        loadingDialog.show(this@VoiceRecordDialogFragment.activity!!)


        doXunfeiVoiceRecognize()
    }


    private fun doXunfeiVoiceRecognize() {
        mIat = SpeechRecognizer.createRecognizer(activity, mInitListener)
        mIat?.apply {
            setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD)
            // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
            // 根据文档 10000为最大的间隔
            setParameter(SpeechConstant.VAD_EOS, "10000")
            // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
            // 根据文档 10000为最大的间隔
            setParameter(SpeechConstant.VAD_BOS, "10000")
            //禁止静音抑制
            //        mIat.setParameter(SpeechConstant.VAD_ENABLE, "0");


            setParameter(SpeechConstant.AUDIO_SOURCE, "-1")
            setParameter(SpeechConstant.SAMPLE_RATE, "8000")

            setParameter(SpeechConstant.LANGUAGE, mVoiceTranslateTarget)


            val ret = startListening(mRecognizerListener)

            if (ErrorCode.SUCCESS == ret) {

                if (FileUtil.isExist(audioRecordPath)) {
                    val amrFileDecoder = AmrFileDecoder()

                    val armResult = amrFileDecoder.amr2Pcm(audioRecordPath)
                    LogUtil.e("armResult -> " + armResult.size)

                    writeAudio(armResult, 0, armResult.size)

                } else {
                    loadingDialog.dismiss()
                    AtworkToast.showToast("发生异常")

                }


                stopListening()


            } else {
                LogUtil.e("识别失败,错误码：$ret")

            }
        }

    }


    /**
     * 初始化监听器。
     */
    private val mInitListener = { code: Int ->
        LogUtil.e(TAG, "SpeechRecognizer init() code = $code")

        if (code != ErrorCode.SUCCESS) {

        }
    }

    /**
     * 听写监听器。
     */
    private val mRecognizerListener = object : RecognizerListener {

        override fun onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入

            LogUtil.e(TAG, "onBeginOfSpeech")
        }

        override fun onError(error: SpeechError) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。

            LogUtil.e(TAG, "error -> $error.errorDescription   error_code ->   $error.errorCode")

            if (10118 == error.errorCode) {
                actionResult()
            } else {
                loadingDialog.dismiss()
                AtworkToast.showToast("$error.errorDescription: $error.errorCode")
            }


        }

        override fun onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            LogUtil.e(TAG, "onEndOfSpeech")
        }

        override fun onResult(results: RecognizerResult, isLast: Boolean) {
            LogUtil.e(TAG, "onResult ->" + results.resultString)

            val result = JsonParser.parseIatResult(results.resultString)

            LogUtil.e(TAG, "onResult text ->$result")

            voiceResult += result

            if (isLast) {

                actionResult()

            }
        }

        override fun onVolumeChanged(volume: Int, data: ByteArray) {
            LogUtil.e(TAG, "返回音频数据：" + data.size)

        }

        override fun onEvent(eventType: Int, arg1: Int, arg2: Int, obj: Bundle?) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		LogUtil.e(TAG, "session id =" + sid);
            //	}
        }
    }

    private fun actionResult() {
        loadingDialog.dismiss()
        action?.invoke(voiceResult)

        dismiss()
    }


    enum class State {
        /**
         * 静止状态, 还没进行录音
         */
        STILL,

        /**
         * 录制中状态
         */
        RECORDING,


        /**
         * 录制完成的状态
         */
        DONE,

        /**
         * 播放中状态
         */
        PLAYING


    }


}