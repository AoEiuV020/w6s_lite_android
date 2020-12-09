package com.foreveross.atwork.modules.chat.service

import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager

object ChatFilePermissionService {

    private fun commonCheck(sessionId: String?): Boolean {
        if (StringUtils.isEmpty(sessionId)) {
            return true
        }

        val discussion = DiscussionManager.getInstance().queryDiscussionSync(BaseApplicationLike.baseContext, sessionId)
                ?: return true


        if (discussion.isInternalDiscussion) {
            return true
        }

        if (DomainSettingsManager.getInstance().handleUserDiscussionEnabledFeature()) {
            return true
        }

        return false
    }
}