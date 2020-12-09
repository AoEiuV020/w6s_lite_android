package com.foreveross.atwork.api.sdk.organization.requstModel

import com.foreveross.atwork.infrastructure.utils.StringUtils

class QueryOrganizationViewRequest {
    var orgCode: String = StringUtils.EMPTY

    var orgId: String = StringUtils.EMPTY

    var filterSenior: Boolean = false

    var filterRank: Boolean = true

    var rankView: Boolean = true

    var queryOrgViewRange : QureyOrganizationViewRange = QureyOrganizationViewRange();

    override fun toString(): String {
        return "QueryOrganizationViewRequest(orgCode='$orgCode', orgId='$orgId', filterSenior=$filterSenior, filterRank=$filterRank, rankView=$rankView, queryOrgViewRange=$queryOrgViewRange)"
    }


}