package com.foreveross.atwork.modules.chat.component

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.Session
import com.foreveross.atwork.infrastructure.model.file.FileData
import com.foreveross.atwork.infrastructure.model.file.MediaItem
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.*
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoFileTransferChatMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoImageChatMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.reference.ReferenceMessage
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.infrastructure.utils.ViewUtil
import com.foreveross.atwork.modules.chat.component.anno.AnnoFileDataHoldingView
import com.foreveross.atwork.modules.chat.component.anno.AnnoImageDataHoldingView
import com.foreveross.atwork.modules.chat.component.reference.*
import com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment
import kotlinx.android.synthetic.main.component_message_data_holding.view.*

class PopChatDetailDataHoldingView : FrameLayout, IPopChatDataHolder {



    companion object {
        val sessionReferencingMessageInfo = HashMap<String, PopChatDataHolding?>()
    }

    var dataHolding: PopChatDataHolding? = null
    var currentSession: Session? = null
    var onPopVisibleListener: ((visible: Boolean) -> Unit)? = null

    constructor(context: Context) : super(context) {
        findViews()
        registerListener()


    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        findViews()
        registerListener()

    }

    private fun findViews() {
        val view = LayoutInflater.from(context).inflate(R.layout.component_message_data_holding, this)

    }

    private fun registerListener() {
        ivCancel.setOnClickListener {
            cancelDataHolding()
        }
    }

    override fun getPopDataHolding(): PopChatDataHolding? = dataHolding

    override fun onRemoveMediaItem(position: Int) {
        val mediaDataOriginal = dataHolding?.mediaData
        if(mediaDataOriginal is MutableList) {
            mediaDataOriginal.removeAt(position)
        }

        if(ListUtil.isEmpty(mediaDataOriginal)) {
            visibility = View.GONE
            refreshSendBtnStatus()

        }


    }

    fun initCurrentSession(session: Session?) {
        currentSession = session
        dataHolding = sessionReferencingMessageInfo[session?.identifier]
    }


    fun mediaDataHolding() = 0 < dataHolding?.mediaData?.size ?: -1

    fun fileDataHolding() = 0 < dataHolding?.fileData?.size ?: -1

    fun messageReferencing() = null != dataHolding?.message


    fun getCancelView(): ImageView {
        return ivCancel
    }

    fun refreshMediaData(mediaData: List<MediaItem>) {
        refreshDataHolding(PopChatDataHolding(mediaData = mediaData))

    }

    fun refreshFileData(fileData: FileData) {
        refreshDataHolding(PopChatDataHolding(fileData = ListUtil.makeSingleList(fileData)))

    }

    fun refreshMessageReference(message: ChatPostMessage) {
        refreshDataHolding(PopChatDataHolding(message = message))
    }


    fun refreshDataHolding(popChatDataHolding: PopChatDataHolding?) {
        dataHolding = popChatDataHolding

        if (!StringUtils.isEmpty(currentSession?.identifier)) {
            sessionReferencingMessageInfo[currentSession?.identifier!!] = dataHolding
        }

        if(null == popChatDataHolding) {
            visibility = View.GONE
            onPopVisibleListener?.invoke(false)
            return
        }


        doRefreshDataHolding()
    }

    fun doRefreshDataHolding() {


        if (null != dataHolding?.message) {
            doRefreshMessageReferencing()
            onPopVisibleListener?.invoke(isVisible)
            return
        }

        if (!ListUtil.isEmpty(dataHolding?.fileData)) {
            doRefreshFileDataHolding()
            onPopVisibleListener?.invoke(isVisible)

            return
        }


        if (!ListUtil.isEmpty(dataHolding?.mediaData)) {
            doRefreshImageDataHolding()
            onPopVisibleListener?.invoke(isVisible)

            return
        }
    }

    private fun doRefreshFileDataHolding() {
        val fileData = dataHolding?.fileData?.get(0)
        if(null == fileData) {
            visibility = View.GONE

        } else {
            val annoFileDataHoldingView = AnnoFileDataHoldingView(context)
            annoFileDataHoldingView.refreshUI(fileData)
            flContent.removeAllViews()
            flContent.addView(annoFileDataHoldingView)

            ViewUtil.setWidth(vDivider, 0)
            visibility = View.VISIBLE
        }


    }

    private fun doRefreshImageDataHolding() {
        val mediaData = dataHolding?.mediaData
        if(ListUtil.isEmpty(mediaData)) {
            visibility = View.GONE

        } else {
            val annoImageDataHoldingView = AnnoImageDataHoldingView(context, this)
            annoImageDataHoldingView.refreshUI(mediaData!!)
            flContent.removeAllViews()
            flContent.addView(annoImageDataHoldingView)

            ViewUtil.setWidth(vDivider, 0)
            visibility = View.VISIBLE
        }
    }

    private fun doRefreshMessageReferencing() {

        val messageReferencing = dataHolding?.message

        val messageReferencingView: IRefreshReferencingView<out ChatPostMessage>? = when (messageReferencing) {
            is TextChatMessage -> TextMessageReferencingView(context)
            is ReferenceMessage -> TextMessageReferencingView(context)
            is AnnoFileTransferChatMessage -> TextMessageReferencingView(context)
            is AnnoImageChatMessage -> TextMessageReferencingView(context)
            is VoiceChatMessage -> VoiceMessageReferencingView(context)
            is FileTransferChatMessage -> FileMessageReferencingView(context)
            is MicroVideoChatMessage -> MicroVideoMessageReferencingView(context)
            is MultipartChatMessage -> MultipartMessageReferencingView(context)
            is StickerChatMessage -> StickerMessageReferencingView(context)
            is ShareChatMessage -> {

                when {
                    ShareChatMessage.ShareType.BusinessCard.toString().equals(messageReferencing.shareType, ignoreCase = true) -> BusinessCardMessageReferencingView(context)
                    ShareChatMessage.ShareType.Link.toString().equals(messageReferencing.shareType, ignoreCase = true) -> ShareLinkMessageReferencingView(context)
                    ShareChatMessage.ShareType.Loc.toString().equals(messageReferencing.shareType, ignoreCase = true) -> LocationMessageReferencingView(context)
                    else -> null
                }

            }
            is ImageChatMessage -> {

                if (messageReferencing.isGif) {
                    GifMessageReferencingView(context)
                } else {
                    ImageMessageReferencingView(context)

                }
            }
            else -> null
        }
        if (null == messageReferencingView) {
            visibility = View.GONE
        } else {
            val messageReferencingViewHolder = messageReferencingView as IRefreshReferencingView<ChatPostMessage>
            messageReferencingViewHolder.refreshUI(messageReferencing!!)
            flContent.removeAllViews()
            flContent.addView(messageReferencingView as View)

            ViewUtil.setWidth(vDivider, DensityUtil.dip2px(2f))

            visibility = View.VISIBLE

        }


    }

    fun cancelDataHolding() {
        refreshDataHolding(null)
        refreshSendBtnStatus()
    }

    private fun refreshSendBtnStatus() {
        LocalBroadcastManager.getInstance(AtworkApplicationLike.baseContext).sendBroadcast(Intent(ChatDetailFragment.ACTION_REFRESH_SEND_BTN_STATUS))
    }


}


interface IPopChatDataHolder {
    fun getPopDataHolding(): PopChatDataHolding?

    fun onRemoveMediaItem(position: Int)
}

class PopChatDataHolding (

        val fileData: List<FileData>? = null,

        val mediaData: List<MediaItem>? = null,

        val message: ChatPostMessage? = null
)