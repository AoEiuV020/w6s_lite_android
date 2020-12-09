package com.foreveross.atwork.api.sdk.organization.requstModel

class QureyOrganizationViewRange {

    object QueryRangeConst {
        const val queryLimit : Int = 100
    }

    var orgLimit : Int = QueryRangeConst.queryLimit

    var orgSkip : Int = 0

    var employeeLimit : Int = QueryRangeConst.queryLimit

    var employeeSkip : Int = 0


    fun havingLocalFeature() = employeeLimit > employeeSkip  && orgLimit > orgSkip

}