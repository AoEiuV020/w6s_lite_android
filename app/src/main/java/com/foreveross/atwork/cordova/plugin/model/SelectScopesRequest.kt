package com.foreveross.atwork.cordova.plugin.model

import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.infrastructure.model.Employee
import com.foreveross.atwork.infrastructure.model.orgization.Organization
import com.foreveross.atwork.infrastructure.model.orgization.Scope
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.google.gson.annotations.SerializedName
import java.util.*

class SelectScopesRequest(

    @SerializedName("org_code")
    val orgCode: String = PersonalShareInfo.getInstance().getCurrentOrg(AtworkApplicationLike.baseContext),

    @SerializedName("selected_employees")
    val selectedEmployees: List<Employee>? = null,


    @SerializedName("selected_organizations")
    val selectedOrganizations: List<Organization>? = null
) {
    fun getSelectScopes(): Set<Scope> {
        val scopeDataList: MutableList<Scope> = ArrayList()

        selectedOrganizations
                ?.map {it.transfer(AtworkApplicationLike.baseContext)}
                ?.let { scopeDataList.addAll(it) }

        selectedEmployees
                ?.mapNotNull { it.transfer(scopeDataList) }
                ?.let { scopeDataList.addAll(it) }

        return scopeDataList.toSet()
    }
}