package com.foreveross.atwork.modules.search.model

import java.io.Serializable

/**
 * 搜索的行为
 */
enum class SearchAction : Serializable {
    /**
     * 默认打开 user 详情
     */
    DEFAULT,

    /**
     * 发起 voip
     */
    VOIP,


    /**
     * 选人
     * */
    SELECT

}