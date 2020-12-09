package com.foreveross.atwork.modules.ocr.model

import android.os.Parcelable
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class OcrRequestAction (
    @SerializedName("orientation")
    val orientation: Int = 0,

    @SerializedName("rate")
    val rate: Float = 1.0f,

    @SerializedName("padding")
    val padding: Int = 10,

    @SerializedName("recognize_type")
    val recognizeType: Int = -1,

    @SerializedName("recognize_suffix_path")
    val recognizeSuffixPath: String? = StringUtils.EMPTY

): Parcelable