package com.foreveross.atwork.modules.discussion.activity

import android.content.Context
import android.content.Intent
import com.foreveross.atwork.infrastructure.model.discussion.template.DiscussionTemplate
import com.foreveross.atwork.infrastructure.utils.extension.putParcelableDirectly
import com.foreveross.atwork.modules.discussion.fragment.DiscussionCreateWithTemplateFragment
import com.foreveross.atwork.support.SingleFragmentActivity

class DiscussionCreateWithTemplateActivity: SingleFragmentActivity() {

    companion object {

        @JvmStatic
        fun startActivity(context: Context, discussionTemplate: DiscussionTemplate) {
            val intent = Intent(context, DiscussionCreateWithTemplateActivity::class.java)
            intent.putParcelableDirectly<DiscussionTemplate>(discussionTemplate)
            context.startActivity(intent)
        }
    }

    override fun createFragment() = DiscussionCreateWithTemplateFragment()


}