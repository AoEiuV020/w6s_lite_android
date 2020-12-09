package com.foreveross.atwork.api.sdk.ocr.model

import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName

class OcrApiRequest(


        /**
         * 1 默认是文本类型
         * */
        @SerializedName("type")
        val type: Int? = null,

        @SerializedName("suffix_path")
        val suffixPath: String? = StringUtils.EMPTY,

        @SerializedName("file")
        val file: String = StringUtils.EMPTY



) {
    companion object {

        /**
         * 文本类型
         * */
        const val TYPE_TEXT = 1

        const val TYPE_UKNOWN = -1
    }
}