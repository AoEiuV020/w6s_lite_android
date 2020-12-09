package com.foreveross.atwork.cordova.plugin.audioVideo.model

import com.foreveross.atwork.modules.video.model.VideoRecordResponse
import com.google.gson.annotations.SerializedName

class VideoCordovaResponse (
        @SerializedName("code")
        val code: Int,

        @SerializedName("message")
        val message: String,

        @SerializedName("info")
        val info: VideoRecordResponse?

)