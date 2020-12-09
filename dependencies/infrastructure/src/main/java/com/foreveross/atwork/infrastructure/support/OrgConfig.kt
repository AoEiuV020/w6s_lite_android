package com.foreveross.atwork.infrastructure.support

import java.util.*

class OrgConfig: BaseConfig() {

    var isShowHeaderInAppFragment = true

    var isShowHeaderOneOrgInAppFragment = true

    var isShowSelect = true

    var isShowDisplay = true




    override fun parse(pro: Properties) {
        pro.getProperty("ORG_SHOW_HEADER_IN_APP_FRAGMENT")?.apply {
            isShowHeaderInAppFragment = toBoolean()
        }

        pro.getProperty("ORG_SHOW_HEADER_ONE_ORG_IN_APP_FRAGMENT")?.apply {
            isShowHeaderOneOrgInAppFragment = toBoolean()
        }

        pro.getProperty("SHOW_ORG_SELECT")?.apply {
            isShowSelect = toBoolean()
        }


        pro.getProperty("SHOW_ORG_DISPLAY")?.apply {
            isShowDisplay = toBoolean()
        }
    }
}
