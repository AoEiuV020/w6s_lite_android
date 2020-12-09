package com.foreveross.atwork.modules.discussion.model

import com.foreveross.atwork.infrastructure.model.discussion.template.DiscussionMemberTag

class DiscussionMemberTagItemInfo (
        var tag: DiscussionMemberTag,

        var title: String,

        var sumLabel: String
): IDiscussionMemberItemDisplay