package com.w6s.beeworks

import android.os.Parcelable
import com.foreveross.atwork.infrastructure.utils.JsonUtil
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject


const val HUAWEI_ID_META_DATA_NAME = "com.huawei.hms.client.appid"
const val VIVO_ID_META_DATA_NAME = "com.vivo.push.app_id"
const val VIVO_KEY_META_DATA_NAME = "com.vivo.push.api_key"
@Parcelize
class BeeworksApn(
        @SerializedName("xiaomiInfo")
        var xiaomiInfo: XiaomiInfo = XiaomiInfo(),

        @SerializedName("huaweiInfo")
        var huaweiInfo: HuaweiInfo = HuaweiInfo(),

        @SerializedName("meizuInfo")
        var meizuInfo: MeizuInfo = MeizuInfo(),

        @SerializedName("vivoInfo")
        var vivoInfo: VivoInfo = VivoInfo(),

        @SerializedName("oppoInfo")
        var oppoInfo: OppoInfo = OppoInfo()

) : Parcelable {

    companion object {

        @JvmStatic
        fun createInstance(jsonObject: JSONObject?) : BeeworksApn? {
            if(null == jsonObject) {
                return null
            }

            var beeworksApn : BeeworksApn = JsonUtil.fromJson(jsonObject.toString(), BeeworksApn::class.java)
            return beeworksApn
        }
    }

}


abstract class ApnInfo(
        @SerializedName("appId")
        var appId: String? = "",

        @SerializedName("appKey")
        var appKey: String? = ""
)

@Parcelize
class HuaweiInfo: ApnInfo(), Parcelable

@Parcelize
class XiaomiInfo: ApnInfo(), Parcelable

@Parcelize
class MeizuInfo: ApnInfo(), Parcelable

@Parcelize
class OppoInfo: ApnInfo(), Parcelable

@Parcelize
class VivoInfo: ApnInfo(), Parcelable
