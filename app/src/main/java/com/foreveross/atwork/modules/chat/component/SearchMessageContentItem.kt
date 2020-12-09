package com.foreveross.atwork.modules.chat.component

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType.FILE
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType.TEXT
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.modules.chat.data.SearchMessageItemData
import com.foreveross.atwork.modules.chat.util.ArticleItemHelper
import com.foreveross.atwork.modules.file.util.FileMediaTypeUtil
import com.foreveross.atwork.utils.AvatarHelper
import com.foreveross.atwork.utils.ChatMessageHelper
import com.foreveross.atwork.utils.ImageCacheHelper
import com.foreveross.atwork.utils.TimeViewUtil
import com.nostra13.universalimageloader.core.DisplayImageOptions
import kotlinx.android.synthetic.main.item_search_message_content.view.*

/**
 *  create by reyzhang22 at 2019-11-12
 */
class SearchMessageContentItem(context: Context?): LinearLayout(context) {

    init {
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.item_search_message_content, this)
    }


    @SuppressLint("SetTextI18n")
    fun setSearchItem(searchItem: SearchMessageItemData, keywords: String) {
        AvatarHelper.setUserAvatarById(iv_avatar, searchItem.mMessage.from, searchItem.mMessage.mFromDomain, true, true)
        tv_name.text = searchItem.mName
        tv_time.text = TimeViewUtil.getSimpleUserCanViewTime(context, searchItem.mMessage.deliveryTime)
        if (searchItem.mType == TEXT) {
            showTextContent(searchItem, keywords)
            return
        }

        if (searchItem.mType == FILE) {
            showFileContent(searchItem, keywords)
            return
        }

        showLinkContent(searchItem, keywords)

    }

    private fun showTextContent(searchItem: SearchMessageItemData, keywords: String) {
        tv_message_content.apply {
            visibility = View.VISIBLE
            text = searchItem.mMessage.showableString ?: searchItem.mMessage.searchAbleString
            highlightKey(this, keywords)
        }
        group_file_message_content.visibility = View.GONE
        group_link_message_content.visibility = View.GONE

    }

    private fun showFileContent(searchItem: SearchMessageItemData, keywords: String) {
        val fileTransferChatMessage = searchItem.mMessage as FileTransferChatMessage
        tv_message_content.visibility = View.GONE
        group_file_message_content.visibility = View.VISIBLE
        group_link_message_content.visibility = View.GONE

        tv_file_name.text = fileTransferChatMessage.name
        highlightKey(tv_file_name, keywords)
        tv_file_size.text = ChatMessageHelper.getMBOrKBString(fileTransferChatMessage.size)
        iv_message_file_type.setImageResource(FileMediaTypeUtil.getFileTypeIcon(fileTransferChatMessage))
    }

    private fun showLinkContent(searchItem: SearchMessageItemData, keywords: String) {
        val shareChatPostMessage = searchItem.mMessage as ShareChatMessage
        tv_message_content.visibility = View.GONE
        group_file_message_content.visibility = View.GONE
        group_link_message_content.visibility = View.VISIBLE

        ImageCacheHelper.displayImage(ArticleItemHelper.getCoverUrl(shareChatPostMessage.content), iv_link_img, getRectOptions())
        tv_link_title.apply {
            text = if(!TextUtils.isEmpty(shareChatPostMessage.content.title)) {
                shareChatPostMessage.content.title
            } else {
                shareChatPostMessage.content.url
            }
            highlightKey(this, keywords)
        }
        tv_link_summary.apply {
            text = if (TextUtils.isEmpty(shareChatPostMessage.mArticleItem.summary)) {
                shareChatPostMessage.content.url
            } else {
                shareChatPostMessage.mArticleItem.summary
            }
            highlightKey(this, keywords)
        }

    }

    private fun highlightKey(textView: TextView, keywords: String) {

        val color = context.resources.getColor(R.color.blue_lock)

        val text = textView.text.toString()
        if (!StringUtils.isEmpty(text)) {
            val start = text.toLowerCase().indexOf(keywords.toLowerCase())
            var end = -1
            if (start != -1) {
                end = start + keywords.length
                val spannableString = SpannableString(text)
                spannableString.setSpan(ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                textView.text = spannableString
            }
        }
    }

    private fun getRectOptions(): DisplayImageOptions {
        val builder = DisplayImageOptions.Builder()
        builder.cacheOnDisk(true)
        builder.cacheInMemory(true)
        builder.showImageOnLoading(R.mipmap.default_link)
        builder.showImageForEmptyUri(R.mipmap.default_link)
        builder.showImageOnFail(R.mipmap.default_link)

        return builder.build()
    }

}