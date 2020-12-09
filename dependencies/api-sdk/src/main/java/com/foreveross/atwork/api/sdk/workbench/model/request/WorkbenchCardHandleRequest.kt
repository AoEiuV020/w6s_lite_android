package com.foreveross.atwork.api.sdk.workbench.model.request

import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDisplayDefinitionData
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.google.gson.annotations.SerializedName

data class WorkbenchCardHandleRequest (
        @SerializedName("name")
        val name : String,

        @SerializedName("en_name")
        val enName: String? = null,

        @SerializedName("tw_name")
        val twName: String? = null,

        @SerializedName("type")
        val type: String,

        @SerializedName("definitions")
        val definition: WorkbenchCardDisplayDefinitionData = WorkbenchCardDisplayDefinitionData(),

        @SerializedName("platforms")
        val platforms: Array<String>  = arrayOf("ANDROID", "IOS"),

        @SerializedName("disabled")
        val disable: Boolean = true,

        @SerializedName("domain")
        val domainId: String = AtworkConfig.DOMAIN_ID

) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WorkbenchCardHandleRequest

        if (name != other.name) return false
        if (enName != other.enName) return false
        if (twName != other.twName) return false
        if (type != other.type) return false
        if (!platforms.contentEquals(other.platforms)) return false
        if (disable != other.disable) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + enName.hashCode()
        result = 31 * result + twName.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + platforms.contentHashCode()
        result = 31 * result + disable.hashCode()
        return result
    }
}