package com.foreveross.atwork.modules.newsSummary.adapter.holder

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage
import com.foreveross.atwork.modules.newsSummary.data.DataCallBack
import com.foreveross.atwork.modules.newsSummary.data.NewsSummaryRVData
import com.foreveross.atwork.utils.AvatarHelper
import com.foreveross.atwork.utils.TimeViewUtil
import com.foreveross.atwork.utils.WorkplusTextSizeChangeHelper

class WordsHodler(itemView: View) : RvBaseHolder(itemView){

    private var tvTitleName: TextView = itemView.findViewById(R.id.tvTitleName)
    private var ivAvatar: ImageView = itemView.findViewById(R.id.chat_item_avatar)
    private var tvSecondTitleName: TextView = itemView.findViewById(R.id.tvSecondTitleName)
    private var tvContent: TextView = itemView.findViewById(R.id.tvContent)
    private var tvTitleNTime: TextView = itemView.findViewById(R.id.tvTitleNTime)

    init {
        WorkplusTextSizeChangeHelper.handleViewEnlargedTextSizeStatus(ivAvatar, 1.1f)
    }
    override fun bindHolder(rvData: NewsSummaryRVData, context: Context, dataCallBack: DataCallBack<NewsSummaryRVData>) {
        if(!TextUtils.isEmpty(rvData.chatPostMessage!!.mDisplayName)){
            tvTitleName.text = rvData.chatPostMessage!!.mDisplayName
        }
        if (rvData.chatPostMessage is TextChatMessage) {
            val textChatMessage = rvData.chatPostMessage as TextChatMessage
            tvContent.text = textChatMessage.text
        }
        tvTitleNTime.text = TimeViewUtil.getNewsSummaryItemViewTime(BaseApplicationLike.baseContext, rvData.chatPostMessage!!.deliveryTime)
        AvatarHelper.setAppAvatarById(ivAvatar, rvData.chatPostMessage!!.from, rvData.chatPostMessage!!.mOrgId, true, true)
        ivAvatar.setOnClickListener {
            dataCallBack.onResult(rvData)
        }
    }
}