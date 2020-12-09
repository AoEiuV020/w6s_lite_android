package com.foreveross.atwork.infrastructure.model.domain

import android.os.Parcelable
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class ChatSettings(@SerializedName("max_undo_time")
                   var maxUndoTime: Long = 120000,

                   @SerializedName("connection_mode")
                   var connectionMode: ChatConnectionMode = ChatConnectionMode.UN_LIMIT,

                   @SerializedName("connection_nonsupport_prompt")
                   var connectionNonsupportPrompt: String? = StringUtils.EMPTY,

                   @SerializedName("connection_retain_days")
                   var connectionRetainDays: Int = -1,

                   @SerializedName("message_roaming_days")
                   var messageRoamingDays: Int = -1


) : Parcelable