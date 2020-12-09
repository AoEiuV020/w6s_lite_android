package com.foreveross.atwork.infrastructure.model.domain

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 *  create by reyzhang22 at 2019-08-22
 */
@Parcelize
class PhoneAssistant(

        @SerializedName("show_job_title")
        var showJobTitle: Boolean = true,

        @SerializedName("show_directly_org")
        var showDirectlyOrg: Boolean = true,

        @SerializedName("show_directly_corp")
        var showDirectlyCorp: Boolean = true

) : Parcelable