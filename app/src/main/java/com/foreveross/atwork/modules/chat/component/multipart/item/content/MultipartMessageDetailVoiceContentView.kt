package com.foreveross.atwork.modules.chat.component.multipart.item.content

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage
import kotlinx.android.synthetic.main.item_multipart_message_detail_voice.view.*

class MultipartMessageDetailVoiceContentView(context: Context): MultipartMessageDetailBasicContentView<VoiceChatMessage>(context) {
    override fun findViews(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.item_multipart_message_detail_voice, this)

    }

    override fun refreshUI(message: VoiceChatMessage) {
        tvMessageVoice.text = "${message.duration}\""

        if (message.playing) {
            tvMessageVoice.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.mipmap.icon_bing_voice_stop), null, null, null)
        } else {
            tvMessageVoice.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.mipmap.icon_bing_voice_play), null, null, null)

        }
    }

    fun getTvMessageVoice(): TextView = tvMessageVoice
}