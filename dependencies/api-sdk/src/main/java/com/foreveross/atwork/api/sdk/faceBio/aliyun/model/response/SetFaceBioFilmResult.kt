package com.foreveross.atwork.api.sdk.faceBio.aliyun.model.response

import com.foreveross.atwork.api.sdk.net.HttpResult
import com.foreveross.atwork.infrastructure.utils.StringUtils

/**
 * 设置人脸识别底片返回结果
 */
class SetFaceBioFilmResult (


    var httpResult: HttpResult? = null,

    var mediaIdSetSuccessfully: String = StringUtils.EMPTY
) {


    fun success(): Boolean {
        if(StringUtils.isEmpty(mediaIdSetSuccessfully)) {
            return false
        }

        if(true == httpResult?.isRequestSuccess) {
            return true

        }

        return false
    }
}