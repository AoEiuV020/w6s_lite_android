package com.foreveross.atwork.infrastructure.model.workbench

enum class WorkbenchCardType {


    /**
     * 自定义卡片
     * */
    SEARCH,

    /**
     * banner 卡片
     * */
    BANNER,


    /**
     * 常用应用
     * */
    COMMON_APP,


    /**
     * 快捷入口卡片1
     * */
    SHORTCUT_0,


    /**
     * 快捷入口卡片2
     * */
    SHORTCUT_1,

    /**
     * 列表卡片1
     * */
    LIST_0,


    /**
     * 列表卡片2
     * */
    LIST_1,

    /**
     * 新闻卡片1
     * */
    NEWS_0,

    /**
     * 新闻卡片2
     * */
    NEWS_1,


    /**
     * 新闻卡片3
     * */
    NEWS_2,


    /**
     * 新闻卡片4
     * */
    NEWS_3,

    /**
     * 分类卡片
     * */
    CATEGORY,


    /**
     * 自定义卡片
     * */
    CUSTOM,

    /**
     * 消息汇总
     */
    APP_MESSAGES,

    /**
     * 管理员卡片
     * */
    ADMIN,


    /**
     * 容器型卡片(九宫格)
     * */
    APP_CONTAINER_0,

    /**
     * 容器型卡片(列表)
     * */
    APP_CONTAINER_1,

    /**
     * 未知
     * */
    UNKNOWN;

    companion object {
        fun parse(workbenchCardTypeStr: String): WorkbenchCardType = when (workbenchCardTypeStr.toUpperCase()) {
            "BANNER" -> BANNER
            "SHORTCUT" -> COMMON_APP
            "SEARCH" -> SEARCH
            "SHORTCUT_1" -> SHORTCUT_0
            "SHORTCUT_2" -> SHORTCUT_1
            "LIST_1" -> LIST_0
            "LIST_2" -> LIST_1
            "NEWS_1" -> NEWS_0
            "NEWS_2" -> NEWS_1
            "NEWS_3" -> NEWS_2
            "NEWS_4" -> NEWS_3
            "CATEGORY_1" -> CATEGORY
            "CUSTOM" -> CUSTOM
            "APP_MESSAGES" -> APP_MESSAGES
            "ADMIN" -> ADMIN
            "APP_CONTAINER_1" -> APP_CONTAINER_0
            "APP_CONTAINER_2" -> APP_CONTAINER_1
            else -> UNKNOWN

        }

        fun parse(type: WorkbenchCardType): String = when(type) {
            SEARCH -> "SEARCH"
            BANNER -> "BANNER"
            COMMON_APP -> "SHORTCUT"
            SHORTCUT_0 -> "SHORTCUT_1"
            SHORTCUT_1 -> "SHORTCUT_2"
            LIST_0 -> "LIST_1"
            LIST_1 -> "LIST_2"
            NEWS_0 -> "NEWS_1"
            NEWS_1 -> "NEWS_2"
            NEWS_2 -> "NEWS_3"
            NEWS_3 -> "NEWS_4"
            CATEGORY -> "CATEGORY_1"
            CUSTOM -> "CUSTOM"
            APP_MESSAGES -> "APP_MESSAGES"
            ADMIN -> "ADMIN"
            APP_CONTAINER_0 -> "APP_CONTAINER_1"
            APP_CONTAINER_1 -> "APP_CONTAINER_2"
            else -> "UNKNOWN"
        }


    }


}