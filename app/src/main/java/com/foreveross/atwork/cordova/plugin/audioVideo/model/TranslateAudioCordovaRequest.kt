package com.foreveross.atwork.cordova.plugin.audioVideo.model

import com.foreveross.atwork.infrastructure.utils.FileUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName

class TranslateAudioCordovaRequest (

    @SerializedName("file_path")
    val filePath:String = StringUtils.EMPTY,

    @SerializedName("media_id")
    val mediaId: String = StringUtils.EMPTY
) {


    fun legal(): Boolean {
        if(!StringUtils.isEmpty(mediaId)) {
            return true
        }

        if(!StringUtils.isEmpty(filePath) && FileUtil.isExist(filePath)) {
            return true
        }



        return false
    }
}