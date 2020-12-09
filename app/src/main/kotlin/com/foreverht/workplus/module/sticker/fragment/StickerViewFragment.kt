package com.foreverht.workplus.module.sticker.activity.fragment

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.foreverht.workplus.module.sticker.activity.StickerViewActivity
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.UrlConstantManager
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType
import com.foreveross.atwork.infrastructure.newmessage.post.chat.StickerChatMessage
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils
import com.foreveross.atwork.support.BackHandledFragment

class StickerViewFragment: BackHandledFragment() {

    var stickerView: ImageView? = null
    var activity: Activity? = null
    var stickerChatMessage: StickerChatMessage? = null

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        stickerChatMessage = arguments!![StickerViewActivity.STICKER_MESSAGE] as StickerChatMessage?
        return inflater.inflate(R.layout.fragment_sticker_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnClickListener { onBackPressed() }
    }

    override fun onBackPressed(): Boolean {
        activity!!.finish()
        return false
    }

    override fun findViews(view: View?) {
        stickerView = view!!.findViewById(R.id.sticker_view)
        if (stickerChatMessage == null) {
            return;
        }
        var loadUrl: String? = ""
        if (BodyType.Sticker == stickerChatMessage?.mBodyType) {
            var name = stickerChatMessage?.stickerName
            if (!TextUtils.isEmpty(name) && name!!.contains(".")) {
                name = name.split(".")[0]
            }
            loadUrl = AtWorkDirUtils.getInstance().getAssetStickerUri(stickerChatMessage?.themeName, name)
        } else {
            loadUrl = stickerChatMessage?.getChatStickerUrl(mActivity, UrlConstantManager.getInstance().stickerImageUrl)
        }
        val options = RequestOptions()
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .fallback(R.mipmap.loading_gray_holding)
                .error(R.mipmap.loading_gray_holding)
//        ImageCacheHelper.displayImage(loadUrl, stickerView, ImageCacheHelper.getImageOptions())
        Glide.with(mActivity).load(loadUrl).apply(options).into(stickerView!!)
    }



}