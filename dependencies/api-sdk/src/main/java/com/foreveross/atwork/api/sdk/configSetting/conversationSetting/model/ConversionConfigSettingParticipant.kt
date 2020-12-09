package com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model

import com.foreveross.atwork.infrastructure.model.Session
import com.foreveross.atwork.infrastructure.model.SessionType
import com.foreveross.atwork.infrastructure.newmessage.post.notify.FriendNotifyMessage
import com.foreveross.atwork.infrastructure.newmessage.post.notify.OrgNotifyMessage
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName

const val CLIENT_ID_SYSTEM_NOTIFICATION = "system_notification"

const val CLIENT_ID_DISCUSSION_CONVERSATIONS_HELPER = "discussion_conversations_helper"

const val CLIENT_ID_SUMMARY_HELPER = "news_summary_helper"

const val CLIENT_ID_ANNOUNCE_APP = "internal_proclaim_notification"


const val CLIENT_ID_EMAIL_NOTIFICATION = "email_notification"

const val CLIENT_ID_MEETING_NOTIFICATION = "meeting_notification"

const val CLIENT_ID_TRANSACTION_NOTIFICATION = "transaction_notification"

const val CLIENT_ID_FRIEND_APPLY_NOTIFICATION = "friend_apply_notification"

const val CLIENT_ID_ORG_APPLY_NOTIFICATION = "org_apply_notification"

const val CLIENT_ID_DROPBOX_OVERDUE_REMIND = "dropbox_overdue_remind"

const val CLIENT_ID_APP_SUBSCRIPTION_NOTIFICATION = "app_subscription_notification"


data class ConversionConfigSettingParticipant(

        @SerializedName("client_id")
        var clientId: String? = null,

        @SerializedName("domain_id")
        var domainIid: String? = null,

        @SerializedName("type")
        var type: String? = null
) {

    constructor(session: Session) : this() {
        this.domainIid = session.domainId
        checkDomainId()

        mappingClientInfo(session.identifier, session.type)

    }

    private fun checkDomainId() {
        if (StringUtils.isEmpty(this.domainIid)) {
            this.domainIid = AtworkConfig.DOMAIN_ID
        }
    }

    constructor(sessionId: String, domainIid: String, sessionType: SessionType) : this() {
        this.domainIid = domainIid
        checkDomainId()

        mappingClientInfo(sessionId, sessionType)
    }

    private fun mappingClientInfo(sessionId: String, sessionType: SessionType) {
        type = when (sessionType) {
            SessionType.Service, SessionType.LightApp, SessionType.NativeApp, SessionType.Local, SessionType.SystemApp -> "APP"
            SessionType.Notice -> "SYSTEM"
            else -> sessionType.toString().toUpperCase()
        }

        clientId = getClientIdFromSessionId(sessionId)

        type = when (clientId) {
            CLIENT_ID_SYSTEM_NOTIFICATION
                , CLIENT_ID_ANNOUNCE_APP
                , CLIENT_ID_DISCUSSION_CONVERSATIONS_HELPER
                , CLIENT_ID_SUMMARY_HELPER
                , CLIENT_ID_TRANSACTION_NOTIFICATION
                , CLIENT_ID_MEETING_NOTIFICATION
                , CLIENT_ID_FRIEND_APPLY_NOTIFICATION
                , CLIENT_ID_EMAIL_NOTIFICATION
                , CLIENT_ID_DROPBOX_OVERDUE_REMIND
                , CLIENT_ID_APP_SUBSCRIPTION_NOTIFICATION
            -> "SYSTEM"
            else -> type
        }

    }


    fun getSessionIdFromClientId(): String? {
        return getSessionIdFromClientId(clientId)
    }


    companion object {

        @JvmStatic
        fun getSessionIdFromClientId(clientId: String?): String? {
            return when (clientId) {
                CLIENT_ID_SYSTEM_NOTIFICATION -> Session.WORKPLUS_SYSTEM
                CLIENT_ID_DISCUSSION_CONVERSATIONS_HELPER -> Session.WORKPLUS_DISCUSSION_HELPER
                CLIENT_ID_APP_SUBSCRIPTION_NOTIFICATION -> Session.WORKPLUS_SUMMARY_HELPER
                CLIENT_ID_ANNOUNCE_APP -> Session.COMPONENT_ANNOUNCE_APP
                CLIENT_ID_TRANSACTION_NOTIFICATION -> Session.ASSET_NOTIFY_SYSTEM
                CLIENT_ID_MEETING_NOTIFICATION -> Session.WORKPLUS_MEETING
                CLIENT_ID_FRIEND_APPLY_NOTIFICATION -> FriendNotifyMessage.FROM
                CLIENT_ID_ORG_APPLY_NOTIFICATION -> OrgNotifyMessage.FROM
                CLIENT_ID_EMAIL_NOTIFICATION -> Session.EMAIL_APP_ID
                CLIENT_ID_DROPBOX_OVERDUE_REMIND -> Session.DROPBOX_OVERDUE_REMIND
                else -> clientId
            }
        }


        @JvmStatic
        fun getClientIdFromSessionId(sessionId: String?): String? {
            return when (sessionId) {
                Session.WORKPLUS_SYSTEM -> CLIENT_ID_SYSTEM_NOTIFICATION
                Session.WORKPLUS_DISCUSSION_HELPER -> CLIENT_ID_DISCUSSION_CONVERSATIONS_HELPER
                Session.WORKPLUS_SUMMARY_HELPER -> CLIENT_ID_APP_SUBSCRIPTION_NOTIFICATION
                Session.COMPONENT_ANNOUNCE_APP -> CLIENT_ID_ANNOUNCE_APP
                Session.ASSET_NOTIFY_SYSTEM -> CLIENT_ID_TRANSACTION_NOTIFICATION
                Session.WORKPLUS_MEETING -> CLIENT_ID_MEETING_NOTIFICATION
                FriendNotifyMessage.FROM -> CLIENT_ID_FRIEND_APPLY_NOTIFICATION
                OrgNotifyMessage.FROM -> CLIENT_ID_ORG_APPLY_NOTIFICATION
                Session.EMAIL_APP_ID -> CLIENT_ID_EMAIL_NOTIFICATION
                Session.DROPBOX_OVERDUE_REMIND -> CLIENT_ID_DROPBOX_OVERDUE_REMIND
                else -> sessionId
            }
        }
    }
}