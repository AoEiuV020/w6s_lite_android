package com.foreveross.atwork.infrastructure.model.workbench.data

import android.os.Parcelable
import com.foreveross.atwork.infrastructure.model.Employee
import com.foreveross.atwork.infrastructure.model.orgization.Organization
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WorkbenchScopeRecord(
        @SerializedName("root_org")
        val rootOrg: Organization,

        @SerializedName("orgs")
        var orgs: List<Organization>?,

        @SerializedName("employees")
        var employees: List<Employee>?

): Parcelable