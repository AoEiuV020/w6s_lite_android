package com.foreveross.atwork.modules.discussion.model

import com.foreveross.atwork.infrastructure.model.ShowListItem
import com.foreveross.atwork.infrastructure.model.discussion.template.DiscussionMemberTag

open class DiscussionMemberContactItemInfo (

        var type: DiscussionMemberItemInfoType,

        var tag: DiscussionMemberTag? = null,

        var contact: ShowListItem? = null
): IDiscussionMemberItemDisplay


enum class DiscussionMemberItemInfoType {
    CONTACT,

    ADD,

    REMOVE
}