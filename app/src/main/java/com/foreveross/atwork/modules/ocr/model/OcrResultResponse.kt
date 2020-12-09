package com.foreveross.atwork.modules.ocr.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class OcrResultResponse(

        @SerializedName("crop_path")
        var cropPath: String,

        @SerializedName("crop_img")
        var cropImg: String? = null


) : Parcelable