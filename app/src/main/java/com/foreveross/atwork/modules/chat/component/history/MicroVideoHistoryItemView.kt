package com.foreveross.atwork.modules.chat.component.history

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MicroVideoChatMessage
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper
import com.foreveross.atwork.utils.ImageCacheHelper
import com.foreveross.atwork.utils.ImageChatHelper
import com.nostra13.universalimageloader.core.DisplayImageOptions
import kotlinx.android.synthetic.main.item_message_hsitory_micro_video_view.view.*


class MicroVideoHistoryItemView : BasicHistoryItemView {

    private var bitmap: Bitmap? = null


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    override fun findViews(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.item_message_hsitory_micro_video_view, this)

    }

    override fun getTimeView(): TextView {
        return tvTime

    }


    override fun refreshView(message: ChatPostMessage) {
        super.refreshView(message)

        if (message is MicroVideoChatMessage) {
            initThumbnail(message, ivVideoThumbnail)
        }
    }


    private fun initThumbnail(message: MicroVideoChatMessage, ivVideoThumbnail: ImageView) {
        ivVideoThumbnail.tag = message.deliveryId
        //TODO: 做一个获取视频文件的缩略图获取方法。。。。。

        bitmap = getBitmap(message)
        if (bitmap == null) {
            if (!TextUtils.isEmpty(message.thumbnailMediaId)) {
                ImageCacheHelper.displayImageByMediaId(message.thumbnailMediaId, ivVideoThumbnail, getImageOptions())

            } else {
                bitmap = BitmapFactory.decodeResource(resources, R.mipmap.loading_history_micro_video_bg)
            }

        }
        if (null != ivVideoThumbnail.tag && ivVideoThumbnail.tag == message.deliveryId
                && null != bitmap) {
            ImageChatHelper.scaleImageView(bitmap, ivVideoThumbnail)
            ivVideoThumbnail.setImageBitmap(bitmap)

        }

    }

    private fun getBitmap(message: MicroVideoChatMessage): Bitmap? {
        var bitmap: Bitmap? = null
        if (message.thumbnails != null) {
            bitmap = BitmapFactory.decodeByteArray(message.thumbnails, 0, message.thumbnails.size)
            if (bitmap != null) {
                return bitmap
            }
        }
        val b = ImageShowHelper.getThumbnailImage(context, message.deliveryId)

        return if (b.isNotEmpty()) {
            BitmapFactory.decodeByteArray(b, 0, b.size)
        } else null
    }


    private fun getImageOptions(): DisplayImageOptions {
        val builder = DisplayImageOptions.Builder()
        builder.cacheOnDisk(true)
        builder.cacheInMemory(true)
        builder.showImageForEmptyUri(R.mipmap.loading_history_micro_video_bg)
        builder.showImageOnLoading(R.mipmap.loading_history_micro_video_bg)
        builder.showImageOnFail(R.mipmap.loading_history_micro_video_bg)
        return builder.build()
    }


    companion object {
        const val VIEW_TYPE_MICRO_VIDEO = 4
    }

}