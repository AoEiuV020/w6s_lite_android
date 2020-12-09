package com.foreveross.atwork.modules.newsSummary.adapter.holder

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.foreverht.cache.BitmapCache
import com.foreverht.db.service.repository.SessionRepository
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.clickStatistics.Type
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleItem
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MicroVideoChatMessage
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper
import com.foreveross.atwork.infrastructure.utils.ScreenUtils
import com.foreveross.atwork.modules.clickStatistics.ClickStatisticsManager.updateClick
import com.foreveross.atwork.modules.image.activity.ImageSwitchInChatActivity
import com.foreveross.atwork.modules.image.fragment.ImageSwitchFragment
import com.foreveross.atwork.modules.newsSummary.data.DataCallBack
import com.foreveross.atwork.modules.newsSummary.data.NewsSummaryRVData
import com.foreveross.atwork.modules.newsSummary.util.CheckUnReadUtil
import com.foreveross.atwork.utils.AvatarHelper
import com.foreveross.atwork.utils.TimeViewUtil
import com.foreveross.atwork.utils.img.round.RoundedImageView

class ImgWordHolder(itemView: View) : RvBaseHolder(itemView){

    private var imageArticle: RoundedImageView = itemView.findViewById(R.id.multi_image_article_cover)
    private var tvTitleName: TextView = itemView.findViewById(R.id.tvTitleName)
    private var ivAvatar: ImageView = itemView.findViewById(R.id.chat_item_avatar)
    private var tvTitleNTime: TextView = itemView.findViewById(R.id.tvTitleNTime)
    private var articleList: ArrayList<ArticleItem> = ArrayList()
    private var imageChatMessage: ImageChatMessage? = null
    private val messageList = java.util.ArrayList<ChatPostMessage>()

    override fun bindHolder(rvData: NewsSummaryRVData, context: Context, dataCallBack: DataCallBack<NewsSummaryRVData>) {
        val margin = DensityUtil.dip2px(40f)
        val width = ScreenUtils.getScreenWidth(context) - margin
        imageArticle.layoutParams.width = width
        imageArticle.layoutParams.height = width * 5 / 9

        imageArticle.setOnClickListener {
            //先搜索出服务号的id
            val sessionList = SessionRepository.getInstance().querySessions("select * from session_ where identifier_ = \"${rvData.getChatId()}\"")
            if(sessionList.size > 0) {
                val session = sessionList[0]
                messageList.clear()
                messageList.add(imageChatMessage!!)
                refreshImageChatMessageList()
                val count = ImageSwitchInChatActivity.sImageChatMessageList.indexOf(imageChatMessage)
                val intent = Intent()
                intent.putExtra(ImageSwitchFragment.INDEX_SWITCH_IMAGE, count)
                intent.setClass(BaseApplicationLike.baseContext, ImageSwitchInChatActivity::class.java)
                context.startActivity(intent)

                CheckUnReadUtil.CompareTime(session.identifier,imageChatMessage!!.deliveryTime)
                //更新点击率
                updateClick(session.identifier, Type.NEWS_SUMMARY)

            }
        }
        if(!TextUtils.isEmpty(rvData.chatPostMessage!!.mDisplayName)){
            tvTitleName.text = rvData.chatPostMessage!!.mDisplayName
        }
        tvTitleNTime.text = TimeViewUtil.getNewsSummaryItemViewTime(BaseApplicationLike.baseContext, rvData.chatPostMessage!!.deliveryTime)
        AvatarHelper.setAppAvatarById(ivAvatar, rvData.chatPostMessage!!.from, rvData.chatPostMessage!!.mOrgId, true, true)
        if (rvData.chatPostMessage is ImageChatMessage) {
            imageChatMessage = rvData.chatPostMessage as ImageChatMessage
            val mediaId = ImageShowHelper.getChatMsgImgMediaId(imageChatMessage)
            val thumbnails = ImageShowHelper.getChatMsgImgThumbnail(imageChatMessage)
            val thumbBitmap = BitmapCache.getBitmapCache().getContentBitmap(context, imageChatMessage!!.deliveryId, mediaId, false, thumbnails)
            if (null != thumbBitmap) {
                imageArticle.setImageBitmap(thumbBitmap)
            }
//            ImageCacheHelper.displayImage(ArticleItemHelper.getCoverUrl(imageChatMessage!!.mediaId), imageArticle, getDisplayImageOptions())
        }
        ivAvatar.setOnClickListener {
            dataCallBack.onResult(rvData)
        }
    }

    /**
     * 获取消息中的所有图片消息
     */
    private fun refreshImageChatMessageList() {
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

}