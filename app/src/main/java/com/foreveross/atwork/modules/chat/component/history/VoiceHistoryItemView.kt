package com.foreveross.atwork.modules.chat.component.history

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage
import kotlinx.android.synthetic.main.item_message_hsitory_voice_view.view.*

class VoiceHistoryItemView : BasicHistoryItemView {


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    override fun findViews(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.item_message_hsitory_voice_view, this)

    }

    override fun getTimeView(): TextView {
        return tvTime

    }


    override fun refreshView(message: ChatPostMessage) {
        super.refreshView(message)

        if (message is VoiceChatMessage) {
            tvVoice.text = "${message.duration}\""

            if (message.playing) {
                tvVoice.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.mipmap.icon_history_item_voice_stop), null, null, null)
            } else {
                tvVoice.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.mipmap.icon_history_item_voice_play), null, null, null)

            }
        }
    }


    companion object {
        const val VIEW_TYPE_VOICE = 6
    }

}