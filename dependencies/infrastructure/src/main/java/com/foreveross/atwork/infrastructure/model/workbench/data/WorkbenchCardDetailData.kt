package com.foreveross.atwork.infrastructure.model.workbench.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WorkbenchCardDetailData(

        @SerializedName("id")
        val id: Long,

        @SerializedName("domain_id")
        val domainId: String,

        @SerializedName("org_code")
        var orgCode: String,

        @SerializedName("type")
        val type: String,

        @SerializedName("name")
        var name: String?,

        @SerializedName("en_name")
        var enName: String?,

        @SerializedName("tw_name")
        var twName: String?,

        @SerializedName("platforms")
        val platforms: List<String>,

        @SerializedName("create_time")
        val createTime: Long? = -1,

        @SerializedName("disabled")
        var disabled: Boolean = true,

        @SerializedName("definitions")
        val workbenchCardDisplayDefinitions: WorkbenchCardDisplayDefinitionData?


): Parcelable {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as WorkbenchCardDetailData

                if (id != other.id) return false
                if (domainId != other.domainId) return false
                if (orgCode != other.orgCode) return false

                return true
        }

        override fun hashCode(): Int {
                var result = id.hashCode()
                result = 31 * result + domainId.hashCode()
                result = 31 * result + orgCode.hashCode()
                return result
        }
}