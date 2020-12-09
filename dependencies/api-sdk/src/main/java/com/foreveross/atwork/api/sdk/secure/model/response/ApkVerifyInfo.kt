package com.foreveross.atwork.api.sdk.secure.model.response

import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName

class ApkVerifyInfo (
        @SerializedName("pkg_name")
        var pkgName: String = StringUtils.EMPTY,

        @SerializedName("md5")
        var md5: String = StringUtils.EMPTY,

        @SerializedName("build_no")
        var buildNo: Long = -1


)

