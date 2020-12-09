package com.foreveross.atwork.modules.video.model

import android.os.Parcelable
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
class VideoRecordResponse (

    @SerializedName("video_path")
    val videoPath: String = StringUtils.EMPTY,

    @SerializedName("video_duration")
    val videoDuration: String = StringUtils.EMPTY,

    @SerializedName("video_size")
    val videoSize: String = StringUtils.EMPTY,

    @SerializedName("video_thumbnail")
    var videoCover: String = StringUtils.EMPTY,


    @SerializedName("sync_system_album")
    var syncSystemAlbum: Boolean = false


): Parcelable