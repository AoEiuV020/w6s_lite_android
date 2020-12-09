package com.foreveross.atwork.modules.newsSummary.adapter.holder

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.modules.newsSummary.data.DataCallBack
import com.foreveross.atwork.modules.newsSummary.data.NewsSummaryRVData
import com.foreveross.atwork.utils.AvatarHelper
import com.foreveross.atwork.utils.WorkplusTextSizeChangeHelper

class SearchServiceHolder (itemView: View) : RvBaseHolder(itemView){

    private var ivAvatar: ImageView = itemView.findViewById(R.id.chat_item_avatar)
    private var tvTitleName: TextView = itemView.findViewById(R.id.tvTitleName)
    private var tvServiceTitle: TextView = itemView.findViewById(R.id.tvServiceTitle)

    init {
        WorkplusTextSizeChangeHelper.handleViewEnlargedTextSizeStatus(ivAvatar, 1.1f)
    }
    override fun bindHolder(rvData: NewsSummaryRVData, context: Context, dataCallBack: DataCallBack<NewsSummaryRVData>) {
        if(!TextUtils.isEmpty(rvData.chatPostMessage!!.mDisplayName)){
            tvTitleName.text = rvData.chatPostMessage!!.mDisplayName
        }

        if(rvData.showTitle){
            tvServiceTitle.visibility = View.VISIBLE
        }else{
            tvServiceTitle.visibility = View.GONE
        }
        AvatarHelper.setAppAvatarById(ivAvatar, rvData.getChatId(), rvData.chatPostMessage!!.mOrgId, true, true)
        ivAvatar.setOnClickListener {
            dataCallBack.onResult(rvData)
        }
    }
}