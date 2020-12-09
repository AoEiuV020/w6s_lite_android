package com.foreveross.atwork.infrastructure.model.domain

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
class ChatFileSettings(

        @SerializedName("read_only")
        var readOnly: Boolean = false,

        @SerializedName("mobile_show_watermark_enabled")
        var showWatermark: Boolean = false,

        @SerializedName("mobile_online_view_enabled")
        private var _onlineViewEnabled: Boolean? = null,

        @SerializedName("mobile_retransmission_enabled")
        private var _transferEnabled: Boolean? = null,

        @SerializedName("mobile_pan_enabled")
        private var _panEnabled: Boolean? = null,

        @SerializedName("mobile_download_enabled")
        private var _downloadEnabled: Boolean? = null,

        @SerializedName("mobile_external_open_enabled")
        var externalOpenEnabled: Boolean = false,


        @SerializedName("enable_expired")
        var enableExpired: Boolean = false,

        @SerializedName("retention_days")
        var retentionDays: Int = 7
) : Parcelable {

    /**
     * make the compatibility to pre-server
     * */
    @IgnoredOnParcel
    var onlineViewEnabled: Boolean = false
        get() {
            if (null == _onlineViewEnabled) {
                return !readOnly
//                return false
            }
            return _onlineViewEnabled!!
        }


    /**
     * make the compatibility to pre-server
     * */
    @IgnoredOnParcel
    var transferEnabled: Boolean = false
    get() {
        if(null == _transferEnabled) {
            return !readOnly
//            return false
        }

        return _transferEnabled!!
    }

    /**
     * make the compatibility to pre-server
     * */
    @IgnoredOnParcel
    var downloadEnabled: Boolean = false
    get() {
        if(null == _downloadEnabled) {
            return !readOnly
//            return false
        }

        return _downloadEnabled!!
    }


}