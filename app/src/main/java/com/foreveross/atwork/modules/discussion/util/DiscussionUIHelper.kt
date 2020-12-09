@file: JvmName("DiscussionUIHelper")

package com.foreveross.atwork.modules.discussion.util

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.discussion.DiscussionAsyncNetService
import com.foreveross.atwork.infrastructure.model.discussion.Discussion
import com.foreveross.atwork.infrastructure.model.discussion.template.DiscussionFeature
import com.foreveross.atwork.infrastructure.model.discussion.template.DiscussionMemberTag
import com.foreveross.atwork.infrastructure.model.discussion.template.DiscussionTemplate
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.infrastructure.utils.ViewUtil
import com.foreveross.atwork.infrastructure.utils.extension.asType
import com.foreveross.atwork.infrastructure.utils.extension.dp2px
import com.foreveross.atwork.infrastructure.utils.extension.getColorCompat
import com.foreveross.atwork.infrastructure.utils.extension.toColor
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager
import com.foreveross.atwork.utils.ImageCacheHelper
import com.nostra13.universalimageloader.core.DisplayImageOptions


data class LabelViewWrapper(val parentLabel: View, val ivLabel: ImageView, val tvLabel: TextView)


fun refreshLabel(discussion: Discussion, labelViewWrapper: LabelViewWrapper, action: ((hasHandledIv: Boolean, hasHandledTv: Boolean) -> Unit)?) {

    if (discussion.hasLabel()) {
        if (discussion.isInternalDiscussion) {
            refreshDiscussionInternalIconLabelUI(labelViewWrapper)
            action?.invoke(true, false)

            return
        }
    }


    hideLabelInternalDiscussion(labelViewWrapper)
    action?.invoke(false, false)
}

fun refreshDiscussionIconLabelUI(icon: String, labelViewWrapper: LabelViewWrapper) {
    val builder = DisplayImageOptions.Builder()
    builder.resetViewBeforeLoading(true)
    builder.cacheOnDisk(true)
    builder.cacheInMemory(true)
    ImageCacheHelper.displayImageByMediaId(icon, labelViewWrapper.ivLabel, builder.build())

    labelViewWrapper.ivLabel.visibility = View.VISIBLE
    labelViewWrapper.tvLabel.visibility = View.GONE
    labelViewWrapper.parentLabel.visibility = View.VISIBLE
}


fun refreshDiscussionTextLabelUI(template: DiscussionTemplate, labelViewWrapper: LabelViewWrapper) {

    val content = template.getLabelContent()
    val color = template.getLabelColor()

    labelViewWrapper.tvLabel.text = content
    labelViewWrapper.tvLabel.setTextColor(color)
    labelViewWrapper.tvLabel.background.mutate()
            .asType<GradientDrawable>()
            ?.run {
                setColor(R.color.transparent.toColor(labelViewWrapper.parentLabel.context))
                setStroke(0.5f.dp2px, color)
            }


    labelViewWrapper.tvLabel.visibility = View.VISIBLE
    labelViewWrapper.ivLabel.visibility = View.GONE
    labelViewWrapper.parentLabel.visibility = View.VISIBLE
}



fun refreshDiscussionInternalIconLabelUI(labelViewWrapper: LabelViewWrapper) {
    ImageCacheHelper.setImageResource(labelViewWrapper.ivLabel, R.mipmap.icon_internal_discussion)

    labelViewWrapper.tvLabel.visibility = View.GONE
    labelViewWrapper.ivLabel.visibility = View.VISIBLE
    labelViewWrapper.parentLabel.visibility = View.VISIBLE

}

fun hideLabelInternalDiscussion(labelViewWrapper: LabelViewWrapper) {
    labelViewWrapper.parentLabel.visibility = View.GONE
}

fun refreshDiscussionFeatureIcon(ivIcon: ImageView, discussionFeature: DiscussionFeature) {
    if(discussionFeature.isOnlyChatDetailEntryFeatures()) {
        when(discussionFeature.feature) {
            DiscussionFeature.VOIP -> ImageCacheHelper.setImageResource(ivIcon, R.mipmap.icon_conf_on_chat_detail)
            DiscussionFeature.CONFERENCE -> ImageCacheHelper.setImageResource(ivIcon, R.mipmap.icon_start_meeting_on_chat_detail)
            DiscussionFeature.LOCATION -> ImageCacheHelper.setImageResource(ivIcon, R.mipmap.icon_chat_more_location_on_chat_detail)
            DiscussionFeature.FILE -> ImageCacheHelper.setImageResource(ivIcon, R.mipmap.icon_file_on_chat_detail)
            DiscussionFeature.VIDEO -> ImageCacheHelper.setImageResource(ivIcon, R.mipmap.icon_micro_video_on_chat_detail)

        }

        return
    }


    ImageCacheHelper.displayImageByMediaId(discussionFeature.icon, ivIcon, ImageCacheHelper.getRoundOptions(R.mipmap.fail_cover_square_size))
}

data class RefreshMemberTagViewCarrier(val keyId: String, val discussionId: String, val userId: String, val tvTagList: MutableList<TextView>)

fun refreshMemberTagView(tagLinerLayout: View, carrier: RefreshMemberTagViewCarrier, tagLinerLayoutHandlingInterceptor: (() -> Boolean)? = null) {

    val tagLinerLayoutHandling = tagLinerLayout

    val originalViewCarrierTag = tagLinerLayoutHandling.tag?.asType<RefreshMemberTagViewCarrier>()
    tagLinerLayoutHandling.tag = carrier


    if(true == tagLinerLayoutHandlingInterceptor?.invoke()) {
        return
    }



//    if (!shouldShowAvatarInDiscussionChat(message)) {
//        tagLinerLayoutHandling.isGone = true
//        return
//    }

    if(carrier.userId != originalViewCarrierTag?.userId) {
        tagLinerLayoutHandling.isGone = true
    }

    DiscussionManager.getInstance().queryDiscussion(tagLinerLayout.context, carrier.discussionId, object : DiscussionAsyncNetService.OnQueryDiscussionListener {
        override fun onSuccess(discussion: Discussion) {
            val tvTagList = carrier.tvTagList

            if(isIllegal()) {
                return
            }

            val tagSize =  0
            if (tagSize > tvTagList.size) {
                //补全 tag item view
                repeat(tagSize - tvTagList.size) {
                    val tvTagItemView = buildTvTagItemView(tagLinerLayout.context)

                    tvTagList.add(tvTagItemView)
                    tagLinerLayoutHandling.asType<ViewGroup>()?.addView(tvTagItemView)
                }

            }

            tagLinerLayoutHandling.isVisible = true

        }

        override fun networkFail(errorCode: Int, errorMsg: String?) {
            if(isIllegal()) {
                return
            }

            tagLinerLayoutHandling.isGone = true
        }

        private fun isIllegal() =
                carrier.keyId != tagLinerLayoutHandling.tag?.asType<RefreshMemberTagViewCarrier>()?.keyId

    })




}


private fun buildTvTagItemView(context: Context): TextView {
    return TextView(context).apply {
        layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
        setTextColor(this.context.getColorCompat(R.color.common_text_color_999))
        setBackgroundResource(R.drawable.shape_tag_gray_bg_in_chat_item)
        gravity = Gravity.CENTER
        isSingleLine = true
        ellipsize = TextUtils.TruncateAt.END
        setPadding(5f.dp2px, 1f.dp2px, 5f.dp2px, 1f.dp2px)
        ViewUtil.setRightMarginDp(this, 5)
    }
}
