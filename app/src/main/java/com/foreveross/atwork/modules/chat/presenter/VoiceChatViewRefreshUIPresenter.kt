package com.foreveross.atwork.modules.chat.presenter

import android.app.Activity
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.ViewUtil
import com.foreveross.atwork.modules.chat.component.chat.definition.IVoiceChatView
import com.foreveross.atwork.modules.chat.inter.VoicePlayingListener

class VoiceChatViewRefreshUIPresenter(private val context : Context, private val voiceChatView: IVoiceChatView): IChatViewRefreshUIPresenter<VoiceChatMessage>(voiceChatView), VoicePlayingListener {

    var voiceAnimationPlaying: Int = R.drawable.reference_message_voice_animation_blue
    var voiceImgStill: Int = R.mipmap.reference_voice_play_animation2_blue

    override fun refreshItemView(chatPostMessage: VoiceChatMessage) {

        calVoiceLength(chatPostMessage)
        voiceChatView.durationView().text = "${chatPostMessage.duration}\""

        if (chatPostMessage.playing) {
            playingAnimation()
        } else {
            stopPlayingAnimation()
        }

    }


    private fun calVoiceLength(message: VoiceChatMessage) {

        //动态计算声音长度
        var length = 3 + message.duration
        if (length > 12) {
            length = 12
        }
        ViewUtil.setWidth(voiceChatView.voiceBgFlView(), DensityUtil.DP_1O_TO_PX * length)

    }

    override fun playingAnimation() {
        val activity = context as Activity
        activity.runOnUiThread {
            voiceChatView.voiceView().setImageResource(voiceAnimationPlaying)
            val frameAnimation = voiceChatView.voiceView().drawable as AnimationDrawable
            // Start the animation (looped playback by default).
            frameAnimation.start()
        }
    }

    override fun stopPlayingAnimation() {
        val activity = context as Activity
        activity.runOnUiThread {
            if (voiceChatView.voiceView().drawable is AnimationDrawable) {
                val frameAnimation = voiceChatView.voiceView().drawable as AnimationDrawable
                frameAnimation.stop()
                voiceChatView.voiceView().setImageResource(voiceImgStill)
            }

        }
    }

}