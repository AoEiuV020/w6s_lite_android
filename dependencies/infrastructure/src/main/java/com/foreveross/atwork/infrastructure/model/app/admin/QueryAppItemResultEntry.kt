package com.foreveross.atwork.infrastructure.model.app.admin

import android.os.Parcelable
import com.foreveross.atwork.infrastructure.model.app.AppBundles
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.HashMap

@Parcelize
class QueryAppItemResultEntry(
        @SerializedName("app_id")
        var appId: String? = null,

        @SerializedName("domain_id")
        var domainId: String? = null,

        @SerializedName("entry_id")
        val entryId: String,

        @SerializedName("entry_type")
        val entry_type: String,

        @SerializedName("icon")
        var icon: String,

        @SerializedName("name")
        val name: String,

        @SerializedName("tw_name")
        var twName: String?,

        @SerializedName("en_name")
        var enName: String?,

        @SerializedName("pinyin")
        val pinyin: String?,

        @SerializedName("sort")
        val sort: Int,

        @SerializedName("settings")
        val settings: HashMap<String, String>?,

        @SerializedName("entry_point")
        val entryPoint: String?
): Parcelable {

    fun transfer(): AppBundles {

        return AppBundles().apply {
            this.mBundleId = entryId
            this.mIcon = icon
            this.mBundleName = name
            this.mBundleTwName = twName
            this.mBundleEnName = enName
            this.mBundlePy = pinyin
            this.mSort = sort
            this.mBundleParams = settings
            if (!StringUtils.isEmpty(entryPoint)) {
                this.mAccessEndPoints = hashMapOf("MOBILE" to entryPoint)
            }
        }
    }
}
