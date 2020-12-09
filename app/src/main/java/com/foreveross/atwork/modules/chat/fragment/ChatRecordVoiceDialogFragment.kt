package com.foreveross.atwork.modules.chat.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.marginBottom
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.component.IRootViewListener
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.infrastructure.utils.TimeUtil
import com.foreveross.atwork.infrastructure.utils.ViewUtil
import com.foreveross.atwork.modules.chat.component.voice.WaveMode
import com.foreveross.atwork.modules.chat.inter.ChatDetailInputListener
import com.foreveross.atwork.modules.chat.util.AudioRecord
import com.foreveross.atwork.modules.voip.support.qsy.utils.VibratorUtil
import com.foreveross.atwork.modules.voip.utils.VoipHelper
import com.foreveross.atwork.utils.AtworkToast
import kotlinx.android.synthetic.main.fragment_dialog_chat_record_voice.*
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue


class ChatRecordVoiceDialogFragment: DialogFragment() {

    private val DATA_RECORD_LOGO_LOCATION = "DATA_RECORD_LOGO_LOCATION"
    private val DATA_RECORD_LOGO_SIZE = "DATA_RECORD_LOGO_SIZE"

    private lateinit var locationData: IntArray
    private var ivRecordHeight: Int  = 0

    private var timeIntervalTooShort = false
    private var lastEndTime: Long = -1

    private var initRlLooseSend_Y = -1F

    private var dX: Float = 0.toFloat()
    private var dY: Float = 0.toFloat()


    private val mCheckTouchThreadPool = Executors.newScheduledThreadPool(2)
    private var scheduledFuture: ScheduledFuture<*>? = null

    private var mLastCollectedPositionSize = 0
    private val mWatchingPositionList: ArrayList<Float> = ArrayList()
    private val mCollectedPositionList: ArrayList<Float> = ArrayList()


    private var legalMoveSpace = -1F
    private var redLineTouch = -1F

    private var amplitudeHandling = 0.0

    private var mMode: Mode = Mode.STILL

    var chatDetailInputListener: ChatDetailInputListener? = null

    var recordDuration = 0

    private var originalTrashWidth = -1
    get() {
        if(-1 == field) {
            field = flTrash.width
        }

        return field
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //必须在 onCreateView 之前
        setStyle(STYLE_NO_FRAME, 0)

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dialog_chat_record_voice, null)
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Light)

        dialog?.window?.let {
            it.setBackgroundDrawable(ColorDrawable(0))
            it.decorView.setPadding(0, 0, 0, 0) //消除边距

            val params: ViewGroup.LayoutParams = it.attributes
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            it.attributes = params as WindowManager.LayoutParams

            it.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)

        }



        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(null == chatDetailInputListener) {
            dismissAllowingStateLoss()
            return
        }

        initData()


        vInputVolume.apply {
            showGravity = Gravity.BOTTOM or Gravity.CENTER
            waveMode = WaveMode.LEFT_RIGHT

            lineColor = ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.white)
            lineWidth = DensityUtil.dip2px(3.7F).toFloat()
            lineSpace = DensityUtil.dip2px(3.7F).toFloat()

            updateBodyList(getAmplitudeWaveList(0.0))

            start()
        }

        handleModeRefresh()
        refreshUI()
        if (Mode.RECORDING == mMode) {
            startDurationClock()
            startAmplitudePullTask()
        }

        ViewUtil.setHeight(flFunctionArea, CommonShareInfo.getKeyBoardHeight(context))
        vChatDetailInputInclude.hideAll()

        val ivRecordX = locationData[0].toFloat()
        val ivRecordY = locationData[1].toFloat()

//        ViewUtil.setTopMargin(ivRecord, ivRecordY.toInt())
        if(activity is IRootViewListener) {

            val bottom = ((activity as IRootViewListener).getRootHeight() - ivRecordY - ivRecordHeight).toInt()

            ViewUtil.setBottomMargin(ivRecord, bottom)

        }

        rlContentTop.doOnPreDraw {
            redLineTouch = (flTrash.marginBottom / 2).toFloat()
            legalMoveSpace = (redLineTouch + flTrash.height / 2)
            initRlLooseSend_Y = rlHandleLooseSendArea.y
        }


        VibratorUtil.Vibrate(context, 100)


    }

    fun isDataReady() = ::locationData.isInitialized || 0 < ivRecordHeight

    fun handleMotionEvent(activity: FragmentActivity, view: View, motionEvent: MotionEvent): Boolean {


        LogUtil.e("handleMotionEvent motionEvent -> ${motionEvent}" )

        val newX: Float
        val newY: Float

        when(motionEvent.action) {



            MotionEvent.ACTION_DOWN -> {
                dX = view.x - motionEvent.rawX
                dY = view.y - motionEvent.rawY

                timeIntervalTooShort = false

                val current = TimeUtil.getCurrentTimeInMillis()

                if (VoipHelper.isHandlingVoipCall()) {
                    AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip)
                    return true
                }

                if (current - lastEndTime < 400) {
                    LogUtil.e("Audio", "time interval down ")
                    timeIntervalTooShort = true
                    return true
                }

                mMode = Mode.RECORDING


                this.show(activity.supportFragmentManager, "ChatRecordVoiceDialogFragment")


                chatDetailInputListener?.record()
                checkTouchFunction()


            }

            MotionEvent.ACTION_UP -> {
                lastEndTime = TimeUtil.getCurrentTimeInMillis()

                cancelAuthCheckSchedule()
                cancelDurationClock()
                cancelAmplitudePullTask()

                //时间间隔太短
                if (timeIntervalTooShort) {
                    LogUtil.e("Audio", "time interval up ")
                    return true
                }

                if(Mode.RECORDING == mMode) {
                    chatDetailInputListener?.recordEnd()
                } else {
                    chatDetailInputListener?.recordCancel()

                }

            }

            MotionEvent.ACTION_MOVE -> {

                if(!isAdded) {
                    return true
                }

                if (timeIntervalTooShort) {
                    return true
                }

                if(0 > motionEvent.y ) {

                    var moveY = motionEvent.y
                    if(legalMoveSpace < moveY.absoluteValue) {
                        moveY = -legalMoveSpace
                    }

                    rlHandleLooseSendArea.translationY = moveY
                    flTrash.translationY = moveY.absoluteValue


                    //变红色, 松开取消发送的模式
                    if(redLineTouch <= moveY.absoluteValue) {
                        mMode = Mode.CANCEL

                        val range = (moveY.absoluteValue - redLineTouch) / (legalMoveSpace - redLineTouch)
                        LogUtil.e("range : $range")

                        val targetWidth = (rlHandleLooseSendArea.width - originalTrashWidth) * range + originalTrashWidth

                        ViewUtil.setWidth(flTrash, targetWidth.toInt())

                        handleModeRefresh(cancelRange = range)

                    } else {
                        mMode = Mode.RECORDING

                        ViewUtil.setWidth(flTrash, originalTrashWidth)

                        handleModeRefresh()

                    }




                    LogUtil.e("rlLooseSend  ${rlHandleLooseSendArea.y} flTrash  ${flTrash.y}   ")
                } else {
                    rlHandleLooseSendArea.translationY = 0F
                    flTrash.translationY = 0F
                }
            }


            MotionEvent.ACTION_CANCEL -> {
                chatDetailInputListener?.onSystemCancel()
                lastEndTime = TimeUtil.getCurrentTimeInMillis()
            }
        }


        return true
    }

    private fun initData() {
        arguments?.let {
            locationData = it.getIntArray(DATA_RECORD_LOGO_LOCATION)!!
            ivRecordHeight = it.getInt(DATA_RECORD_LOGO_SIZE, 0)
        }

    }


    fun setData(locationData : IntArray, ivRecordHeight: Int) {
        val bundle = Bundle()
        bundle.putIntArray(DATA_RECORD_LOGO_LOCATION, locationData)
        bundle.putInt(DATA_RECORD_LOGO_SIZE, ivRecordHeight)
        arguments = bundle
    }

    private fun handleModeRefresh(mode: Mode = mMode, cancelRange: Float = 0F) {
        when(mode) {
            Mode.RECORDING -> {
                refreshUIStill()
            }
            Mode.CANCEL -> {
                ivRecord.setBackgroundResource(R.drawable.round_chat_voice_record_loose_bg)
                rlHandleLooseSendArea.setBackgroundResource(R.drawable.round_chat_voice_record_loose_send_loose_bg)
                ivLooseSendCoverBg.setImageResource(R.mipmap.icon_chat_voice_loose_to_cancel)

                if(flTrash.background is GradientDrawable) {
                    (flTrash.background as GradientDrawable).setColor(Color.parseColor("#E84D48"))
                    (flTrash.background as GradientDrawable).alpha = (127 * (1 - cancelRange)).toInt()

                }


                tvTip.setText(R.string.recording_cancel_tip)

            }
            Mode.TOO_SHORT -> {

            }
            Mode.STILL -> {
                refreshUIStill()
            }
        }
    }

    private fun refreshUIStill() {
        ivRecord.setBackgroundResource(R.drawable.round_chat_voice_record_slide_bg)
        rlHandleLooseSendArea.setBackgroundResource(R.drawable.round_chat_voice_record_loose_send_slide_bg)
        ivLooseSendCoverBg.setImageResource(R.mipmap.icon_chat_voice_loose_to_send)

        if(flTrash.background is GradientDrawable) {
            (flTrash.background as GradientDrawable).alpha = 255
            (flTrash.background as GradientDrawable).setColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.transparent_70))
        }

        tvTip.setText(R.string.recording_slide_up_tip)
    }

    private fun refreshUI() {
        if (isAdded) {
            tvDuration.text = "$recordDuration\""
        }
    }


    private fun startDurationClock() {
        recordDuration = 0
        refreshUI()
        doStartDurationClock()
    }

    private val durationClock = Runnable {
        recordDuration++
        refreshUI()
        doStartDurationClock()
    }

    private fun doStartDurationClock() {
        tvDuration?.postDelayed(durationClock, 1000)
    }

    private fun cancelDurationClock() {
        tvDuration?.removeCallbacks(durationClock)
    }


    private fun startAmplitudePullTask() {
        doStartAmplitudePullTask()
    }


    private val amplitudePullTask = Runnable {
        val amplitude = AudioRecord.getAmplitude()

        LogUtil.e("amplitudeHandling -> ${amplitudeHandling}  amplitude -> ${amplitude}  ")

        if (getAmplitudeWave(amplitudeHandling.toInt()) != getAmplitudeWave(amplitude.toInt())) {
            val waveList = getAmplitudeWaveList(amplitude)

            vInputVolume?.updateBodyList(waveList)
        }


        doStartAmplitudePullTask()
    }

    private fun getAmplitudeWaveList(amplitude: Double): LinkedList<Int> {


        amplitudeHandling = amplitude
        val amplitudeWave = getAmplitudeWave(amplitudeHandling.toInt())


        val waveList = LinkedList<Int>()
        for (i in 1..6) {
            waveList.add(((amplitudeWave / 6) * i))
        }


        waveList.addAll(waveList.reversed())

        LogUtil.e(waveList.toString())

        return waveList
    }

    private fun getAmplitudeWave(amplitude: Int): Int {
        var amplitudeHandled = amplitude
        if (0 > amplitudeHandled) {
            amplitudeHandled = 0
        }

        var amplitudeWave = amplitudeHandled * 10
        if (0 >= amplitudeWave || 28 > amplitudeWave) {
            amplitudeWave = 28
        }
        return amplitudeWave
    }

    private fun doStartAmplitudePullTask() {
        vInputVolume?.postDelayed(amplitudePullTask, 300)
    }

    private fun cancelAmplitudePullTask() {
        vInputVolume?.removeCallbacks(amplitudePullTask)
    }


    /**
     * 用于判断是否失去了点触事件(down 事件还没触发), 借此用于判断系统是否弹出了权限选择框
     */
    private fun checkTouchFunction() {
        if (!mCheckTouchThreadPool.isShutdown) {
            run {
                mWatchingPositionList.clear()
                mCollectedPositionList.clear()
                mLastCollectedPositionSize = 0
                scheduledFuture = mCheckTouchThreadPool.scheduleAtFixedRate(object : Runnable {
                    var durationFly = 0
                    override fun run() {
                        durationFly += 500
                        if (0 == mLastCollectedPositionSize) {
                            mLastCollectedPositionSize = mCollectedPositionList.size
                        }
                        if (mLastCollectedPositionSize != mCollectedPositionList.size) {
                            mLastCollectedPositionSize = mCollectedPositionList.size
                            mWatchingPositionList.add(mCollectedPositionList[mLastCollectedPositionSize - 1])
                        } else {
                            mWatchingPositionList.add(-1f)
                        }
                        if (3000 <= durationFly) {
                            cancelAuthCheckSchedule()
                            if (touchFunctionHasError()) {
                                chatDetailInputListener?.recordKill()
                            }
                        }
                    }
                }, 500, 500, TimeUnit.MILLISECONDS)
            }
        }
    }

    fun cancelAuthCheckSchedule() {
        scheduledFuture?.cancel(true)
    }

    private fun touchFunctionHasError(): Boolean {
        val isRecordViewVisible: Boolean = this.isVisible
        var errorTimes = 0
        for (position in mWatchingPositionList) {
            if (-1f == position) {
                errorTimes++
            } else {
                errorTimes = 0 //若失去 down 事件会为连续-1, 所以此处要重新刷新次数
            }
        }
        //        LogUtil.e("error Times = " + errorTimes);
//        LogUtil.e("error status = " + isRecordViewVisible);
//        LogUtil.e("error collect position  = " + mCollectedPositionList.size());
        return !isRecordViewVisible && 5 < errorTimes
    }

}

enum class Mode {
    RECORDING, CANCEL, TOO_SHORT, STILL
}