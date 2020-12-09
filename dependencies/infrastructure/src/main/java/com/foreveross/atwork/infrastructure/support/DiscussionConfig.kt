package com.foreveross.atwork.infrastructure.support

import java.util.*

class DiscussionConfig(


        /**
         * 是否显示用户离开群通知
         * */
        var isUserLeaveNotify: Boolean = true,

        /**
         * 是否强制创建 session, 假如 session 不存在的话
         * */
        var isCommandCreateSessionFromNotify: Boolean = false,


        /**
         * 是否需要群聊相关入口(创建群聊, 我的群聊)
         * */
        var isNeedEntry: Boolean = true,


        /**
         * 屏蔽群聊助手
         * */
        var isNeedDiscussionHelper: Boolean  = true



) : BaseConfig() {


    override fun parse(pro: Properties) {
        pro.getProperty("DISCUSSION_USER_LEAVE_NOTIFY")?.apply {
            isUserLeaveNotify = toBoolean()
        }


        pro.getProperty("DISCUSSION_COMMAND_CREATE_SESSION_FROM_NOTIFY")?.apply {
            isCommandCreateSessionFromNotify = toBoolean()
        }


        pro.getProperty("DISCUSSION_NEED_ENTRY")?.apply {
            isNeedEntry = toBoolean()
        }


        pro.getProperty("DISCUSSION_NEED_DISCUSSION_HELPER")?.apply {
            isNeedDiscussionHelper = toBoolean()
        }
    }
}
