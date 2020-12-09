package com.foreveross.atwork.modules.chat.component.multipart.item.content.commonMedia

import android.content.Context
import android.text.TextUtils
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage
import com.foreveross.atwork.manager.EmployeeManager
import com.foreveross.atwork.utils.AvatarHelper
import kotlinx.android.synthetic.main.item_multipart_message_detail_common_media.view.*

class MultipartMessageDetailOrgInviteContentView(context: Context): MultipartMessageDetailCommonMediaContentView<ShareChatMessage>(context) {

    override fun refreshUI(message: ShareChatMessage) {
        AvatarHelper.setUserAvatarByAvaId(message.content.mOrgAvatar, ivIconFlag, false, false)

        if (!TextUtils.isEmpty(message.content.mOrgName)) {
            tvContent.text = message.content.mOrgName
        }

        EmployeeManager.getInstance().setEmployeeNameForShareMsg(message, tvTitle)


        if (!TextUtils.isEmpty(message.content.mOrgName)) {
            tvContent.text = context.getString(R.string.invite_you, message.content.mOrgName)
        }
    }


}