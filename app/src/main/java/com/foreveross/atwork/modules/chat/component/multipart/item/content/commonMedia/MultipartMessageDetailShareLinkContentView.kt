package com.foreveross.atwork.modules.chat.component.multipart.item.content.commonMedia

import android.content.Context
import android.text.TextUtils
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.modules.chat.util.ArticleItemHelper
import com.foreveross.atwork.utils.ImageCacheHelper
import kotlinx.android.synthetic.main.item_multipart_message_detail_common_media.view.*

class MultipartMessageDetailShareLinkContentView(context: Context): MultipartMessageDetailCommonMediaContentView<ShareChatMessage>(context) {

    override fun refreshUI(message: ShareChatMessage) {
        ImageCacheHelper.displayImage(ArticleItemHelper.getCoverUrl(message.content), ivIconFlag, ImageCacheHelper.getRectOptions(R.mipmap.default_link))


        if (!TextUtils.isEmpty(message.content.title)) {
            tvTitle.text = message.content.title
            tvContent.text = message.content.url
        } else {
            tvTitle.text = message.content.url
            tvContent.text = StringUtils.EMPTY
        }
    }


}