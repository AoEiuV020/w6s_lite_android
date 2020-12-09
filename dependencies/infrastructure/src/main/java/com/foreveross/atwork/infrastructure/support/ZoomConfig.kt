package com.foreveross.atwork.infrastructure.support

import com.foreveross.atwork.infrastructure.beeworks.BeeWorks
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager
import com.foreveross.atwork.infrastructure.model.zoom.ZoomSdk
import com.foreveross.atwork.infrastructure.utils.EnumLookupUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.infrastructure.utils.UrlHandleHelper
import com.google.gson.annotations.SerializedName

class ZoomConfig: BaseConfig() {


    @SerializedName("appKey")
    var appKey: String = StringUtils.EMPTY

    @SerializedName("appSecret")
    var appSecret: String = StringUtils.EMPTY

    @SerializedName("webDomain")
    var webDomain: String = StringUtils.EMPTY

    @SerializedName("basicUrl")
    var basicUrl:String = StringUtils.EMPTY

    @SerializedName("url")
    var url: String? = StringUtils.EMPTY
        get() {
            if(!StringUtils.isEmpty(DomainSettingsManager.getInstance().zoomBasicUrl)) {
                return UrlHandleHelper.addPathInfo(DomainSettingsManager.getInstance().zoomBasicUrl, "video-meeting-reservation")
            }

            if (StringUtils.isEmpty(field)
                    && !StringUtils.isEmpty(basicUrl)) {
                return UrlHandleHelper.addPathInfo(basicUrl, "video-meeting-reservation")
            }

            return field
        }

    @SerializedName("inviteUrl")
    var inviteUrl: String = StringUtils.EMPTY
        get() {
            if(!StringUtils.isEmpty(DomainSettingsManager.getInstance().zoomBasicUrl)) {
                return UrlHandleHelper.addPathInfo(DomainSettingsManager.getInstance().zoomBasicUrl, "video-invite")
            }

            if (StringUtils.isEmpty(field)
                    && !StringUtils.isEmpty(basicUrl)) {
                return UrlHandleHelper.addPathInfo(basicUrl, "video-invite")
            }

            return field
        }

    var detailUrl: String = StringUtils.EMPTY
        get() {
            if(!StringUtils.isEmpty(DomainSettingsManager.getInstance().zoomBasicUrl)) {
                return UrlHandleHelper.addPathInfo(DomainSettingsManager.getInstance().zoomBasicUrl, "video-meeting-detail")
            }

            if (StringUtils.isEmpty(field)
                    && !StringUtils.isEmpty(basicUrl)) {
                return UrlHandleHelper.addPathInfo(basicUrl, "video-meeting-detail")
            }

            return field
        }

    @SerializedName("sdk")
    var sdk: ZoomSdk? = null

    @SerializedName("enabled")
    var enabled: Boolean = false

    get() {
        if(!field) {
            return false
        }

        if(StringUtils.isEmpty(appKey)) {
            return false
        }

        if(StringUtils.isEmpty(appSecret)) {
            return false
        }

        if(StringUtils.isEmpty(webDomain)) {
            return false
        }

        return true
    }

    override fun parse() {

        BeeWorks.getInstance().config.beeWorksZoom?.let {
            appKey = it.appKey
            appSecret = it.appSecret
            webDomain = it.webDomain
            basicUrl = it.basicUrl
            url = it.url
            inviteUrl = it.inviteUrl
            detailUrl = it.detailUrl
            sdk = EnumLookupUtil.lookup(ZoomSdk::class.java, it.sdk)
            enabled = it.enabled
        }

    }

    fun isUrlEnabled(): Boolean {
//        if(!this.enabled) {
//            return false
//        }

        return !StringUtils.isEmpty(url)
    }

}