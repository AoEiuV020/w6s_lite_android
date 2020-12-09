package com.foreveross.atwork.modules.chat.component.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.UrlConstantManager
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage.SHARE_MESSAGE
import com.foreveross.atwork.modules.chat.component.ChatSendStatusView
import kotlinx.android.synthetic.main.chat_right_share_location.view.*

/**
 *  create by reyzhang22 at 2020-01-03
 */
internal class RightShareLocationView(context: Context?) : RightBasicUserChatItemView(context) {

    var rootview:       View?       = null
    var ivAvatar:       ImageView?  = null
    var tvAddress:      TextView?   = null
    var tvPoi:          TextView?   = null
    var contentView:    FrameLayout? = null

    private var llSomeStatusInfo: LinearLayout? = null
    private var tvTime: TextView? = null
    private var ivSomeStatus: ImageView? = null

    lateinit var sendStatus:    ChatSendStatusView
    lateinit var mapView:       ImageView

    private var shareChatMessage: ShareChatMessage? = null

    val options = RequestOptions()
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .fallback(R.mipmap.loading_gray_holding)
            .error(R.mipmap.loading_gray_holding)

    init {
        findView()
        initListener()
    }

    private fun findView() {
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.chat_right_share_location, this)
        rootview = view.findViewById(R.id.rl_root)
        ivAvatar = view.findViewById(R.id.chat_right_share_avatar_card)
        tvAddress = view.findViewById(R.id.tv_address_name)
        tvPoi = view.findViewById(R.id.tv_address_poi)
        mapView = view.findViewById(R.id.mapview_chat_right)
        contentView = view.findViewById(R.id.chat_right_share_location)
        sendStatus = view.findViewById(R.id.chat_right_share_send_status_card)

        llSomeStatusInfo = view.findViewById(R.id.ll_some_status_info)
        tvTime = view.findViewById(R.id.tv_time)
        ivSomeStatus = view.findViewById(R.id.iv_some_status)
    }

    private fun initListener() {
        mapView.setOnClickListener { v ->
            setSelect()
        }

        mapView.setOnLongClickListener { v ->
            val anchorInfo = anchorInfo
            if (!mSelectMode) {
                mChatItemLongClickListener.shareLongClick(shareChatMessage, anchorInfo)
                true
            }
            false
        }

        right_share_select_card.setOnClickListener {
            setSelect()
        }
    }

    private fun setSelect() {
        if (mSelectMode) {
            shareChatMessage!!.select = !shareChatMessage!!.select
            select(shareChatMessage!!.select)
            return
        }
        mChatItemClickListener.locationClick(shareChatMessage)
    }

    override fun getSomeStatusView(): SomeStatusView? {
        return SomeStatusView
                .newSomeStatusView()
                .setIvStatus(ivSomeStatus)
                .setIconDoubleTick(R.mipmap.icon_double_tick_white)
                .setIconOneTick(R.mipmap.icon_one_tick_white)
                .setTvTime(tvTime)
                .setLlSomeStatusInfo(llSomeStatusInfo)
                .setSomeStatusInfoAreaGrayBg(context)
    }


    override fun getMessageRootView(): View? {
        return rootview
    }

    override fun getMessageSourceView(): MessageSourceView? {
        return null
    }

    override fun getMessage(): ChatPostMessage? {
        return shareChatMessage
    }

    override fun getContentRootView(): View {
        return contentView!!
    }

    override fun getSelectView(): ImageView? {
        return right_share_select_card
    }


    override fun getChatSendStatusView(): ChatSendStatusView {
        return sendStatus
    }

    override fun getAvatarView(): ImageView? {
        return ivAvatar
    }


    override fun refreshItemView(message: ChatPostMessage?) {
        super.refreshItemView(message)
        if (message == null || !(message is ShareChatMessage)) {
            return
        }
        shareChatMessage = message
        val locationChatBody = shareChatMessage!!.chatBody[SHARE_MESSAGE] as ShareChatMessage.LocationBody
        tvAddress?.text = locationChatBody.mName
        tvPoi?.text = locationChatBody.mAddress
        val textLocation = locationChatBody.mLongitude.toString() + "," + locationChatBody.mLatitude.toString()
        val mapUrl = String.format(UrlConstantManager.getInstance().staticMapUrl, textLocation, "0d659feae96844db545d8d4fbedaa32c")
        Glide.with(context).load(mapUrl).apply(options).into(mapView)

    }


}