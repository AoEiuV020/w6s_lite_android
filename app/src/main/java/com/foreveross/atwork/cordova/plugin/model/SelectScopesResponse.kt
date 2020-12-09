package com.foreveross.atwork.cordova.plugin.model

import com.foreveross.atwork.infrastructure.model.Employee
import com.foreveross.atwork.infrastructure.model.orgization.Organization
import com.foreveross.atwork.infrastructure.model.orgization.Scope
import com.foreveross.atwork.infrastructure.utils.extension.asType
import com.google.gson.annotations.SerializedName

class SelectScopesResponse(
        @SerializedName("employees")
        var employees: List<Employee>? = null,


        @SerializedName("organizations")
        var organizations: List<Organization>? = null
) {

    fun flatResult(scopeContactList: List<Scope>) {
        scopeContactList
                .mapNotNull {
                    it.employee?.asType<Employee>()
                }.let {
                    employees = it
                }

        scopeContactList
                .mapNotNull {
                    it.organization?.asType<Organization>()
                }.let {
                    organizations = it
                }
    }
}