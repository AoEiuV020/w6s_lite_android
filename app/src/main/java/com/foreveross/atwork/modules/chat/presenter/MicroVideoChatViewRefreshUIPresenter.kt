package com.foreveross.atwork.modules.chat.presenter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.provider.MediaStore
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MicroVideoChatMessage
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptCacheDisk
import com.foreveross.atwork.modules.chat.component.chat.definition.IMicroVideoChatView
import com.foreveross.atwork.utils.FileHelper

class MicroVideoChatViewRefreshUIPresenter(private val context: Context, private val microVideoChatView: IMicroVideoChatView): IChatViewRefreshUIPresenter<MicroVideoChatMessage>(microVideoChatView) {

    private var bitmap: Bitmap? = null

    override fun refreshItemView(chatPostMessage: MicroVideoChatMessage) {
        microVideoChatView.coverView().tag = chatPostMessage.deliveryId

        if (needCurrentRefresh(chatPostMessage)) {
            initImageContent(chatPostMessage)
        }
    }


    //codes down here was copied from #RightMicroVideoChatItemView
    private fun initImageContent(microVideoChatMessage: MicroVideoChatMessage) {
        bitmap = getBitmap(microVideoChatMessage)
        if (bitmap == null) {
            handleBitmap(microVideoChatMessage)
        }

        if (needCurrentRefresh(microVideoChatMessage) && null != bitmap) {
//            ImageChatHelper.scaleImageView(bitmap, microVideoChatView.coverView())
            microVideoChatView.coverView().setImageBitmap(bitmap)

        }
    }

    private fun needCurrentRefresh(message: ChatPostMessage): Boolean {
        return null != microVideoChatView.coverView().tag && microVideoChatView.coverView().tag == message.deliveryId

    }

    private fun handleBitmap(microVideoChatMessage: MicroVideoChatMessage) {

        //todo 线程
        val video = FileHelper.getMicroExistVideoFile(context, microVideoChatMessage)
        if (video.exists()) {
            val originalPath = EncryptCacheDisk.getInstance().getOriginalFilePathAndCheck(video.absolutePath, false)
            bitmap = ThumbnailUtils.createVideoThumbnail(originalPath, MediaStore.Images.Thumbnails.MINI_KIND)
            return
        }

        bitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.loading_gray_holding)
    }

    private fun getBitmap(microVideoChatMessage: MicroVideoChatMessage): Bitmap? {
        var bitmap: Bitmap? = null
        if (microVideoChatMessage.thumbnails != null) {
            bitmap = BitmapFactory.decodeByteArray(microVideoChatMessage.thumbnails, 0, microVideoChatMessage.thumbnails.size)
            if (bitmap != null) {
                return bitmap
            }
        }
        val thumbnailBytes = ImageShowHelper.getThumbnailImage(context, microVideoChatMessage.deliveryId)
        return if (thumbnailBytes.isNotEmpty()) {
            BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.size)
        } else null
    }

}