package com.foreveross.atwork.cordova.plugin.model

import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.manager.share.model.WxShareType
import com.google.gson.annotations.SerializedName

class WxOrQQShareRequest(
        @SerializedName("app_id")
        var appId: String? = null,

        @SerializedName("title")
        var title: String? = null,

        @SerializedName("url")
        private var _url: String? = null,

        @SerializedName("description")
        var description: String? = null,

        @SerializedName("thumb")
        var thumb: String? = null,

        @SerializedName("thumb_media_id")
        var thumbMediaId: String? = null,

        @SerializedName("scene")
        var scene: Int = 0,

        @SerializedName("type")
        var type: String? = null,

        @SerializedName("data")
        var data: Data? = null

) {


    var url: String?
        set(value) {
            _url = value
        }
        get() {
            data?.run { url }?.run { return this }

            return _url
        }


    fun isUrlShare(): Boolean {
        if (StringUtils.isEmpty(type)) {
            return true
        }

        return WxShareType.WEBPAGE.toString().equals(type, ignoreCase = true)

    }

    fun isImageShare(): Boolean = WxShareType.IMAGE.toString().equals(type, ignoreCase = true)

    fun isTxtShare(): Boolean = WxShareType.TXT.toString().equals(type, ignoreCase = true)


    class Data(

            @SerializedName("url")
            var url: String? = null,

            @SerializedName("image")
            var image: String? = null

    )
}