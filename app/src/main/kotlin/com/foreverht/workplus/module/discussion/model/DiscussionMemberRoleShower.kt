package com.foreverht.workplus.module.discussion.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class DiscussionMemberRoleShower(
        var memberId: String = "",

        var memberAvatar: String = "",

        var memberName: String = "",

        var isMemberAdmin: Boolean = false,

        var isDeleteMode: Boolean = false
) : DiscussionMemberDataShower, Parcelable {
    override fun getId(): String? {
        return memberId
    }

    override fun getAvatar(): String? {
        return memberAvatar
    }

    override fun getName(): String? {
        return memberName
    }

    override fun isAdmin(): Boolean {
        return isMemberAdmin
    }

    override fun deleteMode(): Boolean {
        return isDeleteMode
    }

}