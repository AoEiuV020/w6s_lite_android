package com.foreveross.atwork.infrastructure.model.face.aliyun.request

import com.google.gson.annotations.SerializedName

/**
 * 开启/关闭 人脸识别授权请求
 */
data class FaceBioAuthToggleRequest(

        @SerializedName("cloud_auth_enable")
        val cloudAuthEnable: Boolean
)