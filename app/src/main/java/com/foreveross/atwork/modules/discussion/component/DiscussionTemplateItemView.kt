package com.foreveross.atwork.modules.discussion.component

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.discussion.template.DiscussionTemplate
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.utils.ScreenUtils
import com.foreveross.atwork.infrastructure.utils.ViewUtil
import com.foreveross.atwork.infrastructure.utils.extension.asType
import com.foreveross.atwork.infrastructure.utils.extension.ctxApp
import com.foreveross.atwork.utils.ImageCacheHelper
import kotlinx.android.synthetic.main.item_view_discussion_template.view.*


class DiscussionTemplateItemView : FrameLayout {


    constructor(context: Context) : super(context) {
        findViews()
    }


    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        findViews()
    }

    private fun findViews() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_view_discussion_template, this)

        ViewUtil.setWidth(clRoot, ScreenUtils.getScreenWidth(context) - clRoot.marginLeft - clRoot.marginRight)
    }

    fun refreshItem(discussionTemplate: DiscussionTemplate) {
        if (discussionTemplate.isCommonNoTemplate()) {
            clRoot.background
                    .asType<GradientDrawable>()
                    ?.run {
                        mutate()
                        setColor(Color.parseColor("#F4FAFF"))
                    }

            ivTemplateCover.setImageResource(R.mipmap.icon_common_discussion)

            tvTemplateName.text = context.getString(R.string.common_template_discussion)
            tvTemplateDesc.text = context.getString(R.string.common_discussion_create_notice, AtworkConfig.DISCUSSION_MEMBER_COUNT_MAX)


        } else {
            clRoot.background
                    .asType<GradientDrawable>()
                    ?.run {
                        mutate()
                        setColor(Color.parseColor("#FFF8F6"))
                    }

            ImageCacheHelper.displayImageByMediaId(discussionTemplate.icon, ivTemplateCover, ImageCacheHelper.getRectOptions(R.mipmap.fail_cover_square_size))

            tvTemplateName.text = discussionTemplate.getNameI18n(context)
            tvTemplateDesc.text = discussionTemplate.desc

        }


    }
}