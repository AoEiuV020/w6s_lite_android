package com.foreveross.atwork.api.sdk.workbench.model.request

import com.foreveross.atwork.infrastructure.model.orgization.Scope
import com.google.gson.annotations.SerializedName

data class WorkbenchHandleRequest (
        @SerializedName("name")
        val name: String,

        @SerializedName("en_name")
        val enName: String?,

        @SerializedName("tw_name")
        val twName: String?,

        @SerializedName("remarks")
        val remarks: String?,

        @SerializedName("scopes")
        val scopes: Array<String>,

        val scopesData: Array<Scope>? = null,

        @SerializedName("platforms")
        val platforms: Array<String>  = arrayOf("ANDROID", "IOS"),

        @SerializedName("disabled")
        val disable: Boolean = true

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WorkbenchHandleRequest

        if (name != other.name) return false
        if (enName != other.enName) return false
        if (twName != other.twName) return false
        if (remarks != other.remarks) return false
        if (!scopes.contentEquals(other.scopes)) return false
        if (!platforms.contentEquals(other.platforms)) return false
        if (disable != other.disable) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (enName?.hashCode() ?: 0)
        result = 31 * result + (twName?.hashCode() ?: 0)
        result = 31 * result + (remarks?.hashCode() ?: 0)
        result = 31 * result + scopes.contentHashCode()
        result = 31 * result + platforms.contentHashCode()
        result = 31 * result + disable.hashCode()
        return result
    }

}