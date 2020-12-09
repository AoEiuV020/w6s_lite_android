package com.foreveross.atwork.modules.chat.component.multipart.item.content.commonMedia

import android.content.Context
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.UrlConstantManager
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage
import com.foreveross.atwork.modules.chat.component.multipart.item.content.MultipartMessageDetailBasicContentView
import kotlinx.android.synthetic.main.item_favorite_share_location.view.favorite_location_addr
import kotlinx.android.synthetic.main.item_favorite_share_location.view.favorite_location_title
import kotlinx.android.synthetic.main.multipart_share_location.view.*

class MultipartMessageDetailShareLocationContentView(context: Context): MultipartMessageDetailBasicContentView<ShareChatMessage>(context) {

    val options = RequestOptions()
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .fallback(R.mipmap.loading_gray_holding)
            .error(R.mipmap.loading_gray_holding)
    override fun findViews(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.multipart_share_location, this)
    }

    override fun refreshUI(message: ShareChatMessage) {
        val locationChatBody = message.chatBody[ShareChatMessage.SHARE_MESSAGE] as ShareChatMessage.LocationBody
        val textLocation = locationChatBody.mLongitude.toString() + "," + locationChatBody.mLatitude.toString()
        val mapUrl = String.format(UrlConstantManager.getInstance().staticMapUrl, textLocation, "0d659feae96844db545d8d4fbedaa32c")
        Glide.with(context).load(mapUrl).apply(options).into(favorite_location_img!!);

        favorite_location_title.text = locationChatBody.mName
        favorite_location_addr.text = locationChatBody.mAddress
    }



}