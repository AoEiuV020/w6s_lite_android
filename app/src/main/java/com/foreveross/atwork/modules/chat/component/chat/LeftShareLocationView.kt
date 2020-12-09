package com.foreveross.atwork.modules.chat.component.chat

import android.content.Context
import android.os.Bundle
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
import com.foreveross.atwork.modules.chat.inter.HasChatItemClickListener
import com.foreveross.atwork.modules.chat.inter.HasChatItemLongClickListener

/**
 *  create by reyzhang22 at 2020-01-03
 */
internal class LeftShareLocationView(context: Context?, savedInstanceState: Bundle?) : LeftBasicUserChatItemView(context),
         HasChatItemClickListener, HasChatItemLongClickListener {

    val saveBundle = savedInstanceState

    var rootview:       View?       = null
    var ivSelectView:   ImageView?  = null
    var ivAvatar:       ImageView?  = null
    var tvName:         TextView?   = null
    var tvSubTitle:     TextView?   = null
    var tvAddress:      TextView?   = null
    var tvPoi:          TextView?   = null
    var mapView:        ImageView?  = null
    var source:         MessageSourceV2View? = null
    var contentView:    FrameLayout? = null
    var llTags:         LinearLayout? = null

    private var llSomeStatusInfo: LinearLayout? = null
    private var tvTime: TextView? = null
    private var ivSomeStatus: ImageView? = null

    var shareChatMessage: ShareChatMessage? = null


    val options = RequestOptions()
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .fallback(R.mipmap.loading_gray_holding)
            .error(R.mipmap.loading_gray_holding)


    init {
        findView()
        initListener()
    }

    private fun initListener() {
        mapView?.setOnClickListener { v ->
            if (mSelectMode) {
                shareChatMessage!!.select = !shareChatMessage!!.select
                select(shareChatMessage!!.select)
                return@setOnClickListener
            }
            mChatItemClickListener.locationClick(shareChatMessage)
        }

        mapView?.setOnLongClickListener { v ->
            val anchorInfo = anchorInfo
            if (!mSelectMode) {
                mChatItemLongClickListener.shareLongClick(shareChatMessage, anchorInfo)
                true
            }
            false
        }
    }


    private fun findView() {
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.chat_left_share_location, this)
        rootview = view.findViewById(R.id.rl_root)
        ivSelectView = view.findViewById(R.id.left_share_select_card)
        ivAvatar = view.findViewById(R.id.chat_left_share_avatar_card)
        tvName = view.findViewById(R.id.chat_left_share_username_card)
        tvSubTitle = view.findViewById(R.id.chat_left_share_sub_title)
        tvAddress = view.findViewById(R.id.tv_address_name)
        tvPoi = view.findViewById(R.id.tv_address_poi)
        mapView = view.findViewById(R.id.mapview_chat_left)
        source = view.findViewById(R.id.message_srouce_from)
        contentView = view.findViewById(R.id.chat_left_share_location)

        llSomeStatusInfo = view.findViewById(R.id.ll_some_status_info)
        tvTime = view.findViewById(R.id.tv_time)
        ivSomeStatus = view.findViewById(R.id.iv_some_status)

        llTags = view.findViewById(R.id.ll_tags)
    }

    override fun getTagLinerLayout(): View = llTags!!

    override fun getSomeStatusView(): SomeStatusView? {
        return SomeStatusView
                .newSomeStatusView()
                .setIvStatus(ivSomeStatus)
                .setTvTime(tvTime)
                .setIconDoubleTick(R.mipmap.icon_double_tick_white)
                .setIconOneTick(R.mipmap.icon_one_tick_white)
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
        return ivSelectView
    }

    override fun getNameView(): TextView? {
        return tvName
    }

    override fun getSubTitleView(): TextView? {
        return tvSubTitle
    }

    override fun getConfirmEmergencyView(): TextView? {
        return null
    }

    override fun getAvatarView(): ImageView? {
        return ivAvatar
    }

    override fun registerListener() {
        super.registerListener()
        mapView?.setOnClickListener { v ->
            setSelect()
        }

        mapView?.setOnLongClickListener { v ->
            val anchorInfo = anchorInfo
            if (!mSelectMode) {
                mChatItemLongClickListener.shareLongClick(shareChatMessage, anchorInfo)
                true
            }
            false
        }

        ivSelectView?.setOnClickListener {
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

    override fun refreshItemView(message: ChatPostMessage?) {
        super.refreshItemView(message)
        this.shareChatMessage = message as ShareChatMessage
        val locationChatBody = shareChatMessage!!.chatBody[ShareChatMessage.SHARE_MESSAGE] as ShareChatMessage.LocationBody
        val textLocation = locationChatBody.mLongitude.toString() + "," + locationChatBody.mLatitude.toString()
        val mapUrl = String.format(UrlConstantManager.getInstance().staticMapUrl, textLocation, "0d659feae96844db545d8d4fbedaa32c")
        Glide.with(context).load(mapUrl).apply(options).into(mapView!!);
        tvAddress?.text = locationChatBody.mName
        tvPoi?.text = locationChatBody.mAddress
    }

}