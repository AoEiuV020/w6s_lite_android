package com.foreveross.atwork.modules.route.model

import android.annotation.SuppressLint
import android.net.Uri
import android.text.TextUtils
import com.foreveross.atwork.api.sdk.UrlConstantManager
import com.foreveross.atwork.infrastructure.model.Session
import com.foreveross.atwork.infrastructure.model.SessionType
import com.foreveross.atwork.infrastructure.newmessage.post.notify.FriendNotifyMessage.APPLYING
import com.foreveross.atwork.infrastructure.newmessage.post.notify.FriendNotifyMessage.FROM
import com.foreveross.atwork.infrastructure.newmessage.post.notify.OrgNotifyMessage
import com.foreveross.atwork.infrastructure.utils.StringUtils
import java.util.*

class RouteParams private constructor(builder: Builder) {

    private var from: String? = StringUtils.EMPTY

    private var to: String? = StringUtils.EMPTY

    private var type: String? = StringUtils.EMPTY

    private var displayName: String? = StringUtils.EMPTY

    private var displayAvatar: String? = StringUtils.EMPTY

    private var entryType: Session.EntryType? = null

    private var entryValue: String? = StringUtils.EMPTY

    private var operation: String? = StringUtils.EMPTY

    private var targetUrl: String? = StringUtils.EMPTY

    private var uri: Uri? = null

    private var additionalData: MutableMap<String, Objects>? = null

    init {
        this.from = builder.from
        this.to = builder.to
        this.type = builder.type
        this.displayName = builder.displayName
        this.displayAvatar = builder.displayAvatar
        this.entryType = builder.entryType
        this.entryValue = builder.entryValue
        this.operation = builder.operation
        this.targetUrl = builder.targetUrl
        this.uri = builder.uri
        this.additionalData = builder.additionalData
    }

    fun getEntryType(): Session.EntryType? {
        return entryType
    }

    fun getFrom(): String? {
        return from
    }

    fun getTo(): String? {
        return to
    }

    fun getType(): String? {
        return type
    }

    fun getDisplayName(): String? {
        return displayName
    }

    fun getDisplayAvatar(): String? {
        return displayAvatar
    }

    fun getEntryValue(): String? {
        return entryValue
    }

    fun getOperation(): String? {
        return operation
    }

    fun getTargetUrl(): String? {
        return targetUrl
    }

    fun getUri(): Uri? {
        return uri
    }

    override fun toString(): String {
        return "RouteParams(from=$from, to=$to, type=$type, displayName=$displayName, displayAvatar=$displayAvatar, entryType=$entryType, entryValue=$entryValue, operation=$operation, targetUrl=$targetUrl, uri=$uri, additionalData=$additionalData)"
    }

    class Builder() {
        var from: String? = StringUtils.EMPTY
        var to: String? = StringUtils.EMPTY
        var type: String? = StringUtils.EMPTY
        var displayName: String? = StringUtils.EMPTY
        var displayAvatar: String? = StringUtils.EMPTY
        var entryType: Session.EntryType? = null
        var entryValue: String? = StringUtils.EMPTY
        var operation: String? = StringUtils.EMPTY
        var targetUrl: String? = StringUtils.EMPTY
        var uri: Uri? = null
        var additionalData: MutableMap<String, Objects>? = null

        fun build(): RouteParams {
            makeEntryAction()
            return RouteParams(this)
        }

        fun from(from: String): Builder {
            this.from = from
            return this
        }

        fun to(to: String): Builder {
            this.to = to
            return this
        }

        fun type(type: String): Builder {
            this.type = type
            return this
        }

        fun displayName(displayName: String): Builder {
            this.displayName = displayName
            return this
        }

        fun displayAvatar(displayAvatar: String): Builder {
            this.displayAvatar = displayAvatar
            return this
        }

        fun entryType(entryType: Session.EntryType): Builder {
            this.entryType = entryType
            return this
        }

        fun entryValue(entryValue: String): Builder {
            this.entryValue = entryValue
            return this
        }

        fun operation(operation: String): Builder {
            this.operation = operation
            return this
        }

        fun targetUrl(targetUrl: String): Builder {
            this.targetUrl = targetUrl
            return this
        }

        fun uri(uri: Uri): Builder {
            this.uri = uri
            return this
        }

        fun uri(url: String): Builder {
            this.uri = Uri.parse(url)
            return this
        }

        fun additionalData(mapData: MutableMap<String, Objects>): Builder {
            this.additionalData = mapData
            return this
        }

        @SuppressLint("DefaultLocale")
        private fun makeEntryAction() {
            if (!TextUtils.isEmpty(targetUrl)) {
                entryType = Session.EntryType.To_URL
                entryValue = targetUrl
                return
            }
            when(type?.toLowerCase()) {
                SessionType.User.name.toLowerCase(),
                SessionType.Discussion.name.toLowerCase()     -> entryType = Session.EntryType.To_Chat_Detail
                SessionType.Notice.name.toLowerCase()         -> makeNoticeEntry()
                SessionType.LightApp.name.toLowerCase(),
                SessionType.Service.name.toLowerCase(),
                SessionType.NativeApp.name.toLowerCase(),
                SessionType.Local.name.toLowerCase(),
                SessionType.SystemApp.name.toLowerCase()      -> makeAppEntry()
            }
        }

        private fun makeNoticeEntry() {
            if (!TextUtils.isEmpty(operation)) {
                when (from) {
                    OrgNotifyMessage.FROM       -> makeOrgNoticeEntry()
                    FROM    -> makeFriendNoticeEntry()
                }
            }
        }

        private fun makeAppEntry() {
            entryType = Session.EntryType.To_Chat_Detail
        }

        private fun makeOrgNoticeEntry() {
            when(operation) {
                APPLYING -> {
                    entryType = Session.EntryType.To_ORG_APPLYING
                }
            }
        }

        private fun makeFriendNoticeEntry() {
            when(operation) {
                APPLYING -> {
                    entryType = Session.EntryType.To_URL
                    entryValue = UrlConstantManager.getInstance().friendApprovalFromMain
                }

            }
        }
    }

    companion object {

        @JvmStatic
        fun newRouteParams(): Builder {
            return Builder()
        }
    }


}
