package com.foreveross.atwork.modules.chat.component.multipart.item.content.commonMedia

import android.content.Context
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage
import com.foreveross.atwork.modules.file.util.FileMediaTypeUtil
import com.foreveross.atwork.utils.ChatMessageHelper
import kotlinx.android.synthetic.main.item_multipart_message_detail_common_media.view.*

class MultipartMessageDetailFileContentView(context: Context): MultipartMessageDetailCommonMediaContentView<FileTransferChatMessage>(context) {

    override fun refreshUI(message: FileTransferChatMessage) {
        ivIconFlag.setImageResource(FileMediaTypeUtil.getFileTypeIcon(message))
        tvTitle.text = message.name
        tvContent.text = ChatMessageHelper.getMBOrKBString(message.size)
    }


}