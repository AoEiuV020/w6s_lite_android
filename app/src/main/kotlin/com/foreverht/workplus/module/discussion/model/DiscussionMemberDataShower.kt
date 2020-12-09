package com.foreverht.workplus.module.discussion.model

interface DiscussionMemberDataShower {

    fun getId(): String?

    fun getAvatar(): String?

    fun getName(): String?

    fun isAdmin(): Boolean

    fun deleteMode(): Boolean

}