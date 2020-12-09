package com.foreveross.atwork.infrastructure.support

import com.foreveross.atwork.infrastructure.model.chat.SyncMessageMode
import java.util.*

class ChatConfig : BaseConfig(){

    var commandFriendTalk = false

    var dismissFriendDeleteChatData = false

    var maxCountByTransferDiscussion = -1

    fun isTransferDiscussionHavingLimit(): Boolean = -1 < maxCountByTransferDiscussion

    var defaultFetchMessagesMode = SyncMessageMode.MODE_FETCH_ALL_MSGS_SINCE_LOGOUT

    var defaultOnlyFetchRecentMessagesDays = 7

    var isChatDetailViewNeedOrgPosition = false

    var cleanMessagesThreshold = 10000


    override fun parse(pro: Properties) {

        pro.getProperty("CHAT_MAX_COUNT_TRANSFER_DISCUSSION")?.apply {
            maxCountByTransferDiscussion = toInt()
        }


        pro.getProperty("CHAT_CONFIG_COMMAND_FRIEND_TALK")?.apply {
            commandFriendTalk = toBoolean()
        }

        pro.getProperty("CHAT_CONFIG_DISMISS_FRIEND_DELETE_CHAT_DATA")?.apply {
            dismissFriendDeleteChatData = toBoolean()
        }


        pro.getProperty("CHAT_CONFIG_DEFAULT_FETCH_MESSAGES_MODE")?.apply {
            defaultFetchMessagesMode = toInt()
        }


        pro.getProperty("CHAT_CONFIG_DEFAULT_ONLY_FETCH_RECENT_MESSAGES_DAYS")?.apply {
            defaultOnlyFetchRecentMessagesDays = toInt()
        }

        pro.getProperty("CHAT_CONFIG_CHAT_DETAIL_VIEW_NEED_ORG_POSITION")?.apply {
            isChatDetailViewNeedOrgPosition = toBoolean()
        }


        pro.getProperty("CHAT_CONFIG_CLEAN_MESSAGES_THRESHOLD")?.apply {
            cleanMessagesThreshold = toInt()
        }

    }
}