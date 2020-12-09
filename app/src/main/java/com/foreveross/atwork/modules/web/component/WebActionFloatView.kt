package com.foreveross.atwork.modules.web.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView

import com.foreveross.atwork.R
import com.foreveross.atwork.modules.web.view.DonutProgress

private const val BEING_CLOSE_DURATION = 600L

private const val LONG_CLICK_CRITICAL_TIME = 400L


class WebActionFloatView : FrameLayout {

    private lateinit var vCircleProgress: DonutProgress
    private lateinit var ivAction: ImageView
    private lateinit var vCircleBg: View

    private var status: Status = Status.BACK

    private var lastDownTime = -1L

    var onClickEventListener: OnClickEventListener? = null

    constructor(context: Context) : super(context) {
        findViews()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        findViews()

    }

    private fun findViews() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.layout_workplus_float_web_action_btn, this)
        vCircleProgress = view.findViewById(R.id.v_circle_progress)
        ivAction = view.findViewById(R.id.iv_action)
        vCircleBg = view.findViewById(R.id.v_circle_bg)

    }

    fun refreshView(status: Status) {
        this.status = status
        when(status) {
            Status.BACK -> {
                ivAction.setImageResource(R.mipmap.webview_float_back)
                vCircleBg.setBackgroundResource(R.drawable.round_webview_float_action_back_bg)

            }

            Status.BEING_CLOSE-> {
                ivAction.setImageResource(R.mipmap.webview_float_close)
                vCircleBg.setBackgroundResource(R.drawable.round_webview_float_action_being_close_bg)
            }

            Status.CLOSE-> {
                vCircleProgress.cancelProgressAnimate()
                vCircleProgress.progress = 0f

                ivAction.setImageResource(R.mipmap.webview_float_close)
                vCircleBg.setBackgroundResource(R.drawable.round_webview_float_action_close_bg)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (MotionEvent.ACTION_DOWN == event.action) {
            lastDownTime = System.currentTimeMillis()

            if (Status.CLOSE != status) {
                vCircleProgress.postDelayed(startBeingCloseRunnable, LONG_CLICK_CRITICAL_TIME)
            }

        }



        if (MotionEvent.ACTION_UP == event.action) {
            vCircleProgress.removeCallbacks(startBeingCloseRunnable)
            vCircleProgress.removeCallbacks(beingCloseRunnable)


            val gap = System.currentTimeMillis() - lastDownTime
            if(LONG_CLICK_CRITICAL_TIME >= gap) {
                //handle click event
                onClickEventListener?.onClick(status)

            } else if(LONG_CLICK_CRITICAL_TIME + BEING_CLOSE_DURATION > gap) {
                if (Status.CLOSE != status) {
                    //cancel being close
                    vCircleProgress.cancelProgressAnimate()
                    vCircleProgress.progress = 0f

                    refreshView(Status.BACK)
                }

            } else {
                //is close status
            }
        }


        return true
    }

    private val beingCloseRunnable = Runnable {
        refreshView(Status.CLOSE)

        //handle click event
        onClickEventListener?.onClick(Status.CLOSE)
    }

    private val startBeingCloseRunnable = Runnable {
        refreshView(Status.BEING_CLOSE)
        //start circle progress
        vCircleProgress.setProgressAnimate(BEING_CLOSE_DURATION, 100)
        vCircleProgress.postDelayed(beingCloseRunnable, BEING_CLOSE_DURATION)
    }



}

enum class Status {

    /**
     * 点击返回
     */
    BACK,

    /**
     * 松手后变成"点击关闭"状态
     */
    BEING_CLOSE,


    /**
     * 点击关闭
     */
    CLOSE

}

interface OnClickEventListener {
    fun onClick(status: Status)
}



