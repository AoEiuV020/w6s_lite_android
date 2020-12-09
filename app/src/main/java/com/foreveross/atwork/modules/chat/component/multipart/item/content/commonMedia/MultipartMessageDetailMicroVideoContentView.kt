package com.foreveross.atwork.modules.chat.component.multipart.item.content.commonMedia

import android.content.Context
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MicroVideoChatMessage
import com.foreveross.atwork.infrastructure.utils.StringUtils
import kotlinx.android.synthetic.main.item_multipart_message_detail_common_media.view.*

class MultipartMessageDetailMicroVideoContentView(context: Context): MultipartMessageDetailCommonMediaContentView<MicroVideoChatMessage>(context) {

    override fun refreshUI(message: MicroVideoChatMessage) {
        tvTitle.setText(R.string.video2)
        ivIconFlag.setImageResource(R.mipmap.icon_b_video)
        tvContent.text = StringUtils.EMPTY
    }


}