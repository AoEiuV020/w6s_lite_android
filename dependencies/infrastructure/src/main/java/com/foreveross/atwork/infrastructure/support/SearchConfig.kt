package com.foreveross.atwork.infrastructure.support

import java.util.*

/**
 * Created by dasunsy on 2018/2/1.
 */

class SearchConfig : BaseConfig(){



    /**
     * 雇员搜索是否需要试图权限
     */
    var isEmployeeViewAcl = true


    var isAutoSearchInMainBusiness = true

    var isShowSearchBtnInMainBusiness = false

    var isSearchUsersLocalFirst = false

    var isMinjieSearch = false

    var muteList: List<String>? = null

    override fun parse(pro: Properties) {
        pro.getProperty("SEARCH_EMPLOYEE_VIEW_ACL")?.apply {
            isEmployeeViewAcl = toBoolean()
        }


        pro.getProperty("SEARCH_AUTO_SEARCH_IN_MAIN_BUSINESS")?.apply {
            isAutoSearchInMainBusiness = toBoolean()
        }


        pro.getProperty("SEARCH_SHOW_SEARCH_BTN_IN_MAIN_BUSINESS")?.apply {
            isShowSearchBtnInMainBusiness = toBoolean()
        }


        pro.getProperty("SEARCH_USERS_LOCAL_FIRST")?.apply {
            isSearchUsersLocalFirst = toBoolean()
        }


        pro.getProperty("SEARCH_IS_MINJIE")?.apply {
            isMinjieSearch = toBoolean()
        }

    }
}
