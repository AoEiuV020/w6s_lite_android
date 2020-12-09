package com.foreveross.atwork.api.sdk.faceBio.aliyun.model.request

import com.google.gson.annotations.SerializedName

/**
 * 人脸识别底片请求
 *
 */
data class FaceBioFilmRequest(

        @SerializedName("image")
        val image: String,

        @SerializedName("cloud_auth_enable")
        val cloudAuthEnable: Boolean


)