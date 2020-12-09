package com.foreveross.atwork.modules.lite.module

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LiteBindConfig (

        @SerializedName("domain_id")
        val domainId: String,

        @SerializedName("domain_name")
        val  domainName: String,

        @SerializedName("base_url")
        val baseUrl: String,

        @SerializedName("api_url")
        val apiUrl: String,

        @SerializedName("data")
        val data: String
) : Parcelable {

        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as LiteBindConfig

                if (domainId != other.domainId) return false

                return true
        }

        override fun hashCode(): Int {
                return domainId.hashCode()
        }
}