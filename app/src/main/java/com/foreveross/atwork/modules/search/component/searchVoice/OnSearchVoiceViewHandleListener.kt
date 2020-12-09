package com.foreveross.atwork.modules.search.component.searchVoice

import com.foreveross.atwork.modules.search.model.TalkingRecognizeResult

interface OnSearchVoiceViewHandleListener {
    fun onStart()

    fun onTalking(result: TalkingRecognizeResult)

    fun onCancel()
}