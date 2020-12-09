package com.foreveross.atwork.api.sdk.workbench.model.response

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.foreveross.atwork.infrastructure.model.advertisement.AdminAdvertisementConfig
import com.foreveross.atwork.infrastructure.model.advertisement.AdvertisementConfig
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName

class WorkbenchAdminQueryBannerListResponse (

        @SerializedName("result")
        val result: WorkbenchAdminQueryBannerListResult

): BasicResponseJSON()

class WorkbenchAdminQueryBannerListResult (
        @SerializedName("total_count")
        val totalCount: Int,

        @SerializedName("records")
        val records: List<WorkbenchAdminQueryBannerItem>


) {
        fun parseAdvertisementConfigs(): List<AdvertisementConfig> = records.map { it.parseAdvertisementConfig() }

        fun parseAdminAdvertisementConfigs(orgCode: String): List<AdminAdvertisementConfig> = records.map { it.parseAdminAdvertisementConfig(orgCode) }

}

class WorkbenchAdminQueryBannerItem(
        @SerializedName("id")
        val id: WorkbenchAdminQueryBannerItemKey,

        @SerializedName("owner")
        val owner: WorkbenchAdminQueryBannerItemOwner,

        @SerializedName("name")
        val name: String,

        @SerializedName("media")
        val media: WorkbenchAdminQueryBannerItemMedia,

        @SerializedName("settings")
        val settings: WorkbenchAdminQueryBannerItemSetting,

        @SerializedName("duration")
        val duration: WorkbenchAdminQueryBannerItemDuration,

        @SerializedName("disabled")
        val disabled: Boolean,

        @SerializedName("deleted")
        val deleted: Boolean,

        @SerializedName("sort_order")
        val sort: Int,

        @SerializedName("catalog")
        val catalog: String


) {
        fun parseAdvertisementConfig(): AdvertisementConfig = AdvertisementConfig().apply {
                mId = id.id
                mDomainId = id.domainId
                mType = media.mediaType
                mMediaId = media.mediaId
                mLinkUrl = media.linkUrl
                mWifiLoading = settings.loadOnWifiOnly
                mDisplaySeconds = settings.loadSeconds
                mBeginTime = duration.beginTime
                mEndTime = duration.endTime
                mSort = sort
                mKind = when (catalog) {
                        "WORK_BENCH" -> "widget"
                        else -> StringUtils.EMPTY
                }

        }


        fun parseAdminAdvertisementConfig(orgCode: String): AdminAdvertisementConfig = AdminAdvertisementConfig(
                id = id.id,
                domainId = id.domainId,
                orgCode = orgCode,
                serialNo = owner.ownerId,
                name = name,
                mediaType = media.mediaType,
                mediaId = media.mediaId,
                linkUrl = media.linkUrl,
                loadOnWifiOnly = settings.loadOnWifiOnly,
                disabled = disabled,
                sort = sort,
                beginTime = duration.beginTime,
                endTime = duration.endTime
        )

}



class WorkbenchAdminQueryBannerItemKey(
        @SerializedName("id")
        val id: String,

        @SerializedName("domain_id")
        val domainId: String
)

class WorkbenchAdminQueryBannerItemOwner(
        @SerializedName("owner_id")
        val ownerId: String,

        @SerializedName("domain_id")
        val domainId: String
)



class WorkbenchAdminQueryBannerItemMedia(
        @SerializedName("media_type")
        val mediaType: String,

        @SerializedName("media_id")
        val mediaId: String,

        @SerializedName("link_url")
        val linkUrl: String
)


class WorkbenchAdminQueryBannerItemSetting(
        @SerializedName("load_seconds")
        val loadSeconds: Int,

        @SerializedName("load_on_wifi_only")
        val loadOnWifiOnly: Boolean
)


class WorkbenchAdminQueryBannerItemDuration(
        @SerializedName("begin_time")
        val beginTime: Long,

        @SerializedName("end_time")
        val endTime: Long
)


