package com.foreveross.atwork.cordova.plugin.audioVideo.model

import com.google.gson.annotations.SerializedName

class TranslateAudioCordovaResponse (

    @SerializedName("media_url")
    val mediaUrl:String? = null,

    @SerializedName("voice_path")
    val voicePath:String? = null
) {
    override fun toString(): String {
        return "TranslateAudioCordovaResponse(mediaUrl='$mediaUrl', voicePath='$voicePath')"
    }
}