package com.foreveross.atwork.modules.newsSummary.adapter.holder

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileStatus
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage
import com.foreveross.atwork.modules.chat.component.chat.ChatFileItemView
import com.foreveross.atwork.modules.newsSummary.data.DataCallBack
import com.foreveross.atwork.modules.newsSummary.data.NewsSummaryRVData
import com.foreveross.atwork.utils.AvatarHelper
import com.foreveross.atwork.utils.TimeViewUtil

class FileHolder(itemView: View) : RvBaseHolder(itemView){

    private var tvTitleName: TextView = itemView.findViewById(R.id.tvTitleName)
    private var ivAvatar: ImageView = itemView.findViewById(R.id.chat_item_avatar)
    private var tvTitleNTime: TextView = itemView.findViewById(R.id.tvTitleNTime)
    private var mChatFileItemView: ChatFileItemView = itemView.findViewById(R.id.chat_file_line)
    private var mContext: Context? = null
    private var mFileChatMessage: FileTransferChatMessage? = null
    private var mClickListener: ClickListener? = null

    override fun bindHolder(rvData: NewsSummaryRVData, context: Context, dataCallBack: DataCallBack<NewsSummaryRVData>) {
        mContext = context
        if(!TextUtils.isEmpty(rvData.chatPostMessage!!.mDisplayName)){
            tvTitleName.text = rvData.chatPostMessage!!.mDisplayName
        }
        tvTitleNTime.text = TimeViewUtil.getNewsSummaryItemViewTime(BaseApplicationLike.baseContext, rvData.chatPostMessage!!.deliveryTime)
        AvatarHelper.setAppAvatarById(ivAvatar, rvData.chatPostMessage!!.from, rvData.chatPostMessage!!.mOrgId, true, true)
        if (rvData.chatPostMessage is FileTransferChatMessage) {
            mFileChatMessage = rvData.chatPostMessage as FileTransferChatMessage

            if (mFileChatMessage!!.fileStatus == null) {
                mFileChatMessage!!.fileStatus = FileStatus.NOT_DOWNLOAD
            }

            mChatFileItemView.refreshFileItem(mFileChatMessage)
            mChatFileItemView.setOnClickListener { v: View? ->
                mClickListener?.onClick(mFileChatMessage!!,rvData.getChatId()!!)
            }
        }
        ivAvatar.setOnClickListener {
            dataCallBack.onResult(rvData)
        }
    }

    interface ClickListener{
        fun onClick(fileTransferChatMessage: FileTransferChatMessage,id: String)
    }

    fun setClickListener(clickListener: ClickListener){
        mClickListener = clickListener
    }

    private fun showFileStatusFragment(fileTransferChatMessage: FileTransferChatMessage) {

    }
}