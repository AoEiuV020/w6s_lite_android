package com.foreveross.atwork.modules.newsSummary.adapter.holder

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.foreverht.db.service.repository.SessionRepository
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.clickStatistics.Type
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.*
import com.foreveross.atwork.modules.chat.util.ArticleItemHelper
import com.foreveross.atwork.modules.clickStatistics.ClickStatisticsManager.updateClick
import com.foreveross.atwork.modules.image.activity.ImageSwitchInChatActivity
import com.foreveross.atwork.modules.image.fragment.ImageSwitchFragment
import com.foreveross.atwork.modules.newsSummary.data.DataCallBack
import com.foreveross.atwork.modules.newsSummary.data.NewsSummaryRVData
import com.foreveross.atwork.modules.newsSummary.util.CheckUnReadUtil
import com.foreveross.atwork.modules.voip.utils.VoipHelper
import com.foreveross.atwork.utils.AtworkToast
import com.foreveross.atwork.utils.AvatarHelper
import com.foreveross.atwork.utils.TimeViewUtil
import com.foreveross.atwork.utils.WorkplusTextSizeChangeHelper

class SearchContentHolder (itemView: View) : RvBaseHolder(itemView){

    private var ivAvatar: ImageView = itemView.findViewById(R.id.chat_item_avatar)
    private var tvTitleName: TextView = itemView.findViewById(R.id.tvTitleName)
    private var tvContent: TextView = itemView.findViewById(R.id.tvContent)
    private var tvTitleNTime: TextView = itemView.findViewById(R.id.tvTitleNTime)
    private var tvContentTitle: TextView = itemView.findViewById(R.id.tvContentTitle)
    private var llItem: LinearLayout = itemView.findViewById(R.id.llItem)
    private lateinit var mContext: Context
    private var mFileClickListener: FileClickListener? = null

    init {
        WorkplusTextSizeChangeHelper.handleViewEnlargedTextSizeStatus(ivAvatar, 1.1f)
    }
    override fun bindHolder(rvData: NewsSummaryRVData, context: Context, dataCallBack: DataCallBack<NewsSummaryRVData>) {
        mContext = context
        if(!TextUtils.isEmpty(rvData.chatPostMessage!!.mDisplayName)){
            tvTitleName.text = rvData.chatPostMessage!!.mDisplayName
        }

        if (rvData.chatPostMessage is ArticleChatMessage) {
            val articleChatMessage = rvData.chatPostMessage as ArticleChatMessage
            var contextStr = ""
            for(i in 0 until articleChatMessage.articles.size){
                contextStr += articleChatMessage.articles[i].title
                if(i < articleChatMessage.articles.size-1){
                    contextStr += "|"
                }
            }
            tvContent.text = contextStr
        }
        if (rvData.chatPostMessage is FileTransferChatMessage) {
            val fileTransferChatMessage = rvData.chatPostMessage as FileTransferChatMessage
            tvContent.text = fileTransferChatMessage.name
        }
        if (rvData.chatPostMessage is TextChatMessage) {
            val textChatMessage = rvData.chatPostMessage as TextChatMessage
            tvContent.text = textChatMessage.text
        }
        if (rvData.chatPostMessage is ImageChatMessage) {
            tvContent.text = "["+mContext.getString(R.string.message_type_image)+"]"
        }
        if (rvData.chatPostMessage is MicroVideoChatMessage) {
            tvContent.text =  "["+mContext.getString(R.string.label_micro_video_chat_pop)+"]"
        }
        if (rvData.chatPostMessage is VoiceChatMessage) {
            tvContent.text = "["+mContext.getString(R.string.audio3)+"]"
        }
        if(rvData.showTitle){
            tvContentTitle.visibility = View.VISIBLE
        }else{
            tvContentTitle.visibility = View.GONE
        }
        tvTitleNTime.text = TimeViewUtil.getNewsSummaryItemViewTime(BaseApplicationLike.baseContext, rvData.chatPostMessage!!.deliveryTime)
        AvatarHelper.setAppAvatarById(ivAvatar, rvData.chatPostMessage!!.from, rvData.chatPostMessage!!.mOrgId, true, true)
        ivAvatar.setOnClickListener {
            dataCallBack.onResult(rvData)
        }
        llItem.setOnClickListener {
            itemClickJump(rvData,context)
        }
    }

    private fun itemClickJump(rvData: NewsSummaryRVData,context: Context){
        if (rvData.chatPostMessage is ArticleChatMessage) {
            val articleChatMessage = rvData.chatPostMessage as ArticleChatMessage
            val articleList = articleChatMessage.articles
            var articleItem: ArticleItem? = null
            //先搜索出服务号的id
            val sessionList = SessionRepository.getInstance().querySessions("select * from session_ where identifier_ = \"${rvData.getChatId()}\"")
            for(i in 0 until articleList.size){
                if(rvData.getSearchValue() != null && articleList[i].title.contains(rvData.getSearchValue()!!)){
                    articleItem = articleList[i]
                }
            }
            if(sessionList.size > 0) {
                val session = sessionList[0]
                ArticleItemHelper.startWebActivity(context, session, articleChatMessage, articleItem)

                CheckUnReadUtil.CompareTime(session.identifier,articleChatMessage.deliveryTime)
                //更新点击率
                updateClick(session.identifier, Type.NEWS_SUMMARY)

            }
        }
        if(rvData.chatPostMessage is ImageChatMessage){
            val imageChatMessage = rvData.chatPostMessage as ImageChatMessage
            //先搜索出服务号的id
            val sessionList = SessionRepository.getInstance().querySessions("select * from session_ where identifier_ = \"${rvData.getChatId()}\"")
            if(sessionList.size > 0) {
                val session = sessionList[0]
                val messageList = ArrayList<ChatPostMessage>()
                messageList.add(imageChatMessage)
                refreshImageChatMessageList(messageList)
                val count = ImageSwitchInChatActivity.sImageChatMessageList.indexOf(imageChatMessage)
                val intent = Intent()
                intent.putExtra(ImageSwitchFragment.INDEX_SWITCH_IMAGE, count)
                intent.setClass(BaseApplicationLike.baseContext, ImageSwitchInChatActivity::class.java)
                context.startActivity(intent)

                CheckUnReadUtil.CompareTime(session.identifier,imageChatMessage.deliveryTime)
                //更新点击率
                updateClick(session.identifier, Type.NEWS_SUMMARY)

            }
        }
        if(rvData.chatPostMessage is MicroVideoChatMessage) {
            val  microVideoChatMessage = rvData.chatPostMessage as MicroVideoChatMessage
            if (VoipHelper.isHandlingVoipCall()) {
                AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip)
                return
            }
            //先搜索出服务号的id
            val sessionList = SessionRepository.getInstance().querySessions("select * from session_ where identifier_ = \"${rvData.getChatId()}\"")
            if(sessionList.size > 0) {
                val session = sessionList[0]
                val messageList = ArrayList<ChatPostMessage>()
                messageList.clear()
                messageList.add(microVideoChatMessage)
                refreshImageChatMessageList(messageList)
                val count = ImageSwitchInChatActivity.sImageChatMessageList.indexOf(microVideoChatMessage)
                val intent = Intent()
                intent.putExtra(ImageSwitchFragment.INDEX_SWITCH_IMAGE, count)
                intent.setClass(BaseApplicationLike.baseContext, ImageSwitchInChatActivity::class.java)
                context.startActivity(intent)

                CheckUnReadUtil.CompareTime(session.identifier,microVideoChatMessage.deliveryTime)
                //更新点击率
                updateClick(session.identifier, Type.NEWS_SUMMARY)

            }
        }
        if(rvData.chatPostMessage is FileTransferChatMessage) {
            val mFileChatMessage = rvData.chatPostMessage as FileTransferChatMessage
            mFileClickListener?.onClick(mFileChatMessage,rvData.getChatId()!!)
        }
    }

    /**
     * 获取消息中的所有图片消息
     */
    private fun refreshImageChatMessageList(messageList: ArrayList<ChatPostMessage>) {
        ImageSwitchInChatActivity.sImageChatMessageList.clear()
        for (message in messageList) {
            if (message.isBurn || message.isUndo) {
                continue
            }
            if (message is ImageChatMessage || message is MicroVideoChatMessage || message is FileTransferChatMessage) {
                if (message is FileTransferChatMessage) {
                    if (message.isGifType || message.isStaticImgType) {
                        ImageSwitchInChatActivity.sImageChatMessageList.add(message)
                    }
                    continue
                }
                ImageSwitchInChatActivity.sImageChatMessageList.add(message)
            }
        }
    }

    interface FileClickListener{
        fun onClick(fileTransferChatMessage: FileTransferChatMessage,id: String)
    }

    fun setFileClickListener(clickListener: FileClickListener){
        mFileClickListener = clickListener
    }

}