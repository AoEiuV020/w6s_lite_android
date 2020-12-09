package com.foreveross.atwork.modules.newsSummary.adapter.holder

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.foreverht.db.service.repository.SessionRepository
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.clickStatistics.Type
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MicroVideoChatMessage
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper
import com.foreveross.atwork.modules.clickStatistics.ClickStatisticsManager
import com.foreveross.atwork.modules.image.activity.ImageSwitchInChatActivity
import com.foreveross.atwork.modules.image.fragment.ImageSwitchFragment
import com.foreveross.atwork.modules.newsSummary.data.DataCallBack
import com.foreveross.atwork.modules.newsSummary.data.NewsSummaryRVData
import com.foreveross.atwork.modules.newsSummary.util.CheckUnReadUtil
import com.foreveross.atwork.modules.voip.utils.VoipHelper
import com.foreveross.atwork.utils.*
import java.util.*

class VideoHolder(itemView: View) : RvBaseHolder(itemView){

    private var tvTitleName: TextView = itemView.findViewById(R.id.tvTitleName)
    private var ivAvatar: ImageView = itemView.findViewById(R.id.chat_item_avatar)
    private var tvTitleNTime: TextView = itemView.findViewById(R.id.tvTitleNTime)
    private var mViewVideoParent: View = itemView.findViewById(R.id.micro_video_content)
    private var mIvVideoThumbnail: ImageView = itemView.findViewById(R.id.chat_thumbnail)
    private var microVideoChatMessage: MicroVideoChatMessage? = null
    private val messageList = ArrayList<ChatPostMessage>()
    private var mBitmap: Bitmap? = null
    private lateinit var mContext: Context

    override fun bindHolder(rvData: NewsSummaryRVData, context: Context, dataCallBack: DataCallBack<NewsSummaryRVData>) {
        mContext = context
        if(!TextUtils.isEmpty(rvData.chatPostMessage!!.mDisplayName)){
            tvTitleName.text = rvData.chatPostMessage!!.mDisplayName
        }
        tvTitleNTime.text = TimeViewUtil.getNewsSummaryItemViewTime(BaseApplicationLike.baseContext, rvData.chatPostMessage!!.deliveryTime)
        AvatarHelper.setAppAvatarById(ivAvatar, rvData.chatPostMessage!!.from, rvData.chatPostMessage!!.mOrgId, true, true)

        if (rvData.chatPostMessage is MicroVideoChatMessage) {
            microVideoChatMessage = rvData.chatPostMessage as MicroVideoChatMessage
            initThumbnail()
        }

        mViewVideoParent.setOnClickListener {
            if (VoipHelper.isHandlingVoipCall()) {
                AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip)
                return@setOnClickListener
            }
            //先搜索出服务号的id
            val sessionList = SessionRepository.getInstance().querySessions("select * from session_ where identifier_ = \"${rvData.getChatId()}\"")
            if(sessionList.size > 0) {
                val session = sessionList[0]
                messageList.clear()
                messageList.add(microVideoChatMessage!!)
                refreshImageChatMessageList()
                val count = ImageSwitchInChatActivity.sImageChatMessageList.indexOf(microVideoChatMessage)
                val intent = Intent()
                intent.putExtra(ImageSwitchFragment.INDEX_SWITCH_IMAGE, count)
                intent.setClass(BaseApplicationLike.baseContext, ImageSwitchInChatActivity::class.java)
                context.startActivity(intent)

                CheckUnReadUtil.CompareTime(session.identifier,microVideoChatMessage!!.deliveryTime)
                //更新点击率
                ClickStatisticsManager.updateClick(session.identifier, Type.NEWS_SUMMARY)

            }
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
                    val fileTransferChatMessage = message as FileTransferChatMessage
                    if (fileTransferChatMessage.isGifType || fileTransferChatMessage.isStaticImgType) {
                        ImageSwitchInChatActivity.sImageChatMessageList.add(message)
                    }
                    continue
                }
                ImageSwitchInChatActivity.sImageChatMessageList.add(message)
            }
        }
    }

    private fun initThumbnail() {
        mIvVideoThumbnail.setTag(microVideoChatMessage!!.deliveryId)
        //TODO: 做一个获取视频文件的缩略图获取方法。。。。。
        mBitmap = getBitmap()
        if (mBitmap == null) {
            if (!TextUtils.isEmpty(microVideoChatMessage!!.thumbnailMediaId)) {
                ImageCacheHelper.displayImageByMediaId(microVideoChatMessage!!.thumbnailMediaId, mIvVideoThumbnail, ImageCacheHelper.getImageOptions())
            } else {
                mBitmap = BitmapFactory.decodeResource(mContext.resources, R.mipmap.loading_gray_holding)
            }
        }
        if (null != mIvVideoThumbnail.getTag() && mIvVideoThumbnail.getTag() == microVideoChatMessage!!.deliveryId && null != mBitmap) {
            ImageChatHelper.scaleImageView(mBitmap, mIvVideoThumbnail)
            mIvVideoThumbnail.setImageBitmap(mBitmap)
        }
    }

    private fun getBitmap(): Bitmap? {
        var bitmap: Bitmap? = null
        if (microVideoChatMessage!!.thumbnails != null) {
            bitmap = BitmapFactory.decodeByteArray(microVideoChatMessage!!.thumbnails, 0, microVideoChatMessage!!.thumbnails.size)
            if (bitmap != null) {
                return bitmap
            }
        }
        val b = ImageShowHelper.getThumbnailImage(mContext, microVideoChatMessage!!.deliveryId)
        return if (b.size != 0) {
            BitmapFactory.decodeByteArray(b, 0, b.size)
        } else null
    }

}