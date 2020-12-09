package com.foreveross.atwork.infrastructure.model.advertisement

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
class AdminAdvertisementConfig @JvmOverloads constructor(

        @SerializedName("id")
        var id: String? = null,

        @SerializedName("domain_id")
        var domainId: String,

        @SerializedName("org_id")
        var orgCode: String,

        @SerializedName("serial_no")
        var serialNo: String,

        @SerializedName("name")
        var name: String,

        @SerializedName("media_id")
        var mediaId: String,

        @SerializedName("media_type")
        var mediaType: String,

        @SerializedName("link_url")
        var linkUrl: String,

        @SerializedName("disabled")
        var disabled: Boolean = false,

        @SerializedName("sort_order")
        val sort: Int,

        @SerializedName("catalog")
        val catalog: String = "work_bench",

        @SerializedName("status")
        val status: String = "valid",

        @SerializedName("load_seconds")
        val loadSeconds: Int = 3,

        @SerializedName("scopes")
        var scopes: Array<String>? = null,

        @SerializedName("load_on_wifi_only")
        var loadOnWifiOnly: Boolean = false,

        @SerializedName("begin_time")
        var beginTime: Long = -1,

        @SerializedName("end_time")
        var endTime: Long = -1


): Parcelable {


    fun parse(): AdvertisementConfig = AdvertisementConfig().apply {
        mId = id
        mDomainId = domainId
        mMediaId = mediaId
        mName = name
        mType = mediaType
        mLinkUrl = linkUrl
        mSort = sort
        mBeginTime = beginTime
        mEndTime = endTime
        mOrgId = orgCode
        mSerialNo = serialNo
    }
}