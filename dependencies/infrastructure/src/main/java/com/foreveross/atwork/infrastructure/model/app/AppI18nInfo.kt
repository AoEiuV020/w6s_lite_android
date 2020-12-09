package com.foreveross.atwork.infrastructure.model.app

import com.foreveross.atwork.infrastructure.model.i18n.CommonI18nInfoData
import com.google.gson.annotations.SerializedName

class AppI18nInfo (

        @SerializedName("category_name")
        var categoryName: String? = null,

        @SerializedName("category_en_name")
        var categoryEnName: String? = null,

        @SerializedName("category_tw_name")
        var categoryTwName: String? = null

): CommonI18nInfoData()