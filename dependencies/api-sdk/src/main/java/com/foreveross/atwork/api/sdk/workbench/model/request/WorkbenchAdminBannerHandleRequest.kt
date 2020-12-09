package com.foreveross.atwork.api.sdk.workbench.model.request

import com.foreveross.atwork.infrastructure.model.advertisement.AdvertisementConfig
import com.google.gson.annotations.SerializedName

class WorkbenchAdminBannerHandleRequest(


        @SerializedName("id")
        var id: String,

        @SerializedName("domain_id")

        var domainId: String,

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
        var loadOnWifiOnly: Boolean = false


) {
    companion object {
        fun parse(advertisementConfig: AdvertisementConfig): WorkbenchAdminBannerHandleRequest {
            return WorkbenchAdminBannerHandleRequest(
                    id = advertisementConfig.mId,
                    domainId = advertisementConfig.mDomainId,
                    name = advertisementConfig.mName,
                    mediaId = advertisementConfig.mMediaId,
                    mediaType = advertisementConfig.mType,
                    linkUrl = advertisementConfig.mLinkUrl,
                    sort = advertisementConfig.mSort
            )
        }

    }
}