package com.foreveross.atwork.modules.organization.manager

import com.foreveross.atwork.api.sdk.organization.responseModel.OrganizationResult

interface IDeptManager {
    fun updateDeptsDbSync(parentId: String, filterSenior: Boolean, rankView: Boolean, results: List<OrganizationResult>)

}