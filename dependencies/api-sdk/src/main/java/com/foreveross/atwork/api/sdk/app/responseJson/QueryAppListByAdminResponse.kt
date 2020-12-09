package com.foreveross.atwork.api.sdk.app.responseJson

import com.foreveross.atwork.api.sdk.model.BasicResponseJSON
import com.foreveross.atwork.infrastructure.model.app.App
import com.foreveross.atwork.infrastructure.model.app.AppBundles
import com.foreveross.atwork.infrastructure.model.app.admin.QueryAppItemResultEntry
import com.foreveross.atwork.infrastructure.model.app.appEnum.AppKind
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import java.util.*

class QueryAppListByAdminResponse(

        @SerializedName("result")
        val result: QueryAppListByAdminResponseResult? = null

) : BasicResponseJSON() {


}

class QueryAppListByAdminResponseResult(
        @SerializedName("total_count")
        val total_count: Int = 0,

        @SerializedName("records")
        val appJsonResults: List<JsonObject>? = null,

        var appList: List<App>? = null,

        var appResults: List<QueryAppItemResultByAdmin>? = null
)

class QueryAppItemResultByAdmin(

        @SerializedName("app_id")
        val appKey: QueryAppItemResultKey,

        @SerializedName("source")
        val source: QueryAppItemResultResultSource?,

        @SerializedName("name")
        val name: String,

        @SerializedName("tw_name")
        var twName: String,

        @SerializedName("en_name")
        var enName: String,

        @SerializedName("pinyin")
        val pinyin: String,

        @SerializedName("initial")
        val initial: String,

        @SerializedName("entries")
        val entries: List<QueryAppItemResultEntry>,

        @SerializedName("app_type")
        val appType: String,

        @SerializedName("sort")
        val sort: Int,

        @SerializedName("android_entry_id")
        var mainEntryId: String? = null,

        @SerializedName("scopes")
        var scopes: List<String>? = null,

        var rawResultData: JsonObject? = null
) {

    fun transfer(): App {
        val app = App().apply {
            this.mAppId = appKey.appId
            this.mDomainId = appKey.domainId
            this.mAppName = name
            this.mAppTwName = twName
            this.mAppEnName = enName
            this.mAppPinYin = pinyin
            this.mAppInitial = initial
            this.mBundles = entries.map {
                it.transfer()
            }

            this.mAppKind = when(appType.toLowerCase()) {
                "h5" -> AppKind.LightApp
                "bot" -> AppKind.ServeNo
                else -> AppKind.LightApp
            }

            this.mAppSort = sort
            this.mRealMainBundleId = mainEntryId

        }

        return app

    }

}



class QueryAppItemResultKey(
        @SerializedName("id")
        val appId: String,

        @SerializedName("domain_id")
        val domainId: String)

class QueryAppItemResultResultSource(
        @SerializedName("domain_id")
        val domainId: String,

        @SerializedName("org_id")
        val orgId: String
)

