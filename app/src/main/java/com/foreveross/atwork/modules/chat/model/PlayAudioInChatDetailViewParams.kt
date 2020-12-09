package com.foreveross.atwork.modules.chat.model

import android.content.Context
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage
import com.foreveross.atwork.modules.chat.inter.VoicePlayingListener


class PlayAudioInChatDetailViewParams {

    lateinit var context: Context

    lateinit var voiceChatMessage: VoiceChatMessage

    //是否正在连续播放的过程中(不是开头的播放)
    var isInSuccession: Boolean = false

    //是否需要播放下一条
    var needTryPlayNext: Boolean = true

    var voicePlayingListener: VoicePlayingListener? = null

    var seekPosition = -1

}