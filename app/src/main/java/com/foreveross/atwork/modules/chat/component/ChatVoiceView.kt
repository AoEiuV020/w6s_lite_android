package com.foreveross.atwork.modules.chat.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.fragment.app.FragmentActivity
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.component.viewPager.XmlPagerAdapter
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.modules.chat.fragment.ChatRecordVoiceDialogFragment
import com.foreveross.atwork.modules.chat.inter.ChatDetailInputListener
import kotlinx.android.synthetic.main.chat_detail_voice_pager.view.*

class ChatVoiceView: RelativeLayout {

    val chatRecordVoiceDialogFragment = ChatRecordVoiceDialogFragment()
    var chatDetailInputListener: ChatDetailInputListener? = null

    constructor(context: Context) : super(context) {
        initView()
        registerListener()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
        registerListener()
    }


    private fun initView() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.chat_detail_voice_pager, this)


        vpVoice.adapter = XmlPagerAdapter(1)
    }

    private fun registerListener() {
//        ivRecord.setOnLongClickListener {
//
//            val location = IntArray(2)
//            ivRecord.getLocationInWindow(location)
//
//            LogUtil.e("ivRecord.x : ${location[0]}   ivRecord.y: ${location[1]}    ")
//
//            if(context is FragmentActivity) {
//                chatRecordVoiceDialogFragment.setData(location, ivRecord.height)
//                chatRecordVoiceDialogFragment.show((context as FragmentActivity).supportFragmentManager, "ChatRecordVoiceDialogFragment")
//            }
//
//
//            false
//        }

        ivRecord.setOnTouchListener { view, motionEvent ->
            if(!chatRecordVoiceDialogFragment.isDataReady()) {
                val location = IntArray(2)
                ivRecord.getLocationInWindow(location)

                LogUtil.e("ivRecord.x : ${location[0]}   ivRecord.y: ${location[1]}    ")
                chatRecordVoiceDialogFragment.setData(location, ivRecord.height)
                chatRecordVoiceDialogFragment.chatDetailInputListener = chatDetailInputListener
            }




            chatRecordVoiceDialogFragment.handleMotionEvent(context as FragmentActivity, view, motionEvent)
        }
    }

    fun dismissRecordingDialog() {
        try {
            AtworkApplicationLike.runOnMainThread {
                chatRecordVoiceDialogFragment.dismissAllowingStateLoss()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}