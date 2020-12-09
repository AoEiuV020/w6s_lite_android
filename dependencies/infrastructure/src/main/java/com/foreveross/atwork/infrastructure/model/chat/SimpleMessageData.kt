package com.foreveross.atwork.infrastructure.model.chat

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *  create by reyzhang22 at 2020-02-13
 */
@Parcelize
data class SimpleMessageData(var messageId: String = "",
                             var messageTime: Long = -1) : Parcelable