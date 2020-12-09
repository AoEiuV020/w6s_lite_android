package com.foreveross.atwork.modules.video.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class VideoRecordRequestAction (
        @SerializedName("duration")
        val duration: Int = 10,


        /**
         *  "0" > 高清; "1" > 一般; "2" > 流畅(默认为一般)
         * */
        @SerializedName("quality")
        val quality: Int = 1,

        @SerializedName("sync_system_album")
        val syncSystemAlbum: Boolean = false,


        @SerializedName("front")
        val front: Boolean = false


): Parcelable