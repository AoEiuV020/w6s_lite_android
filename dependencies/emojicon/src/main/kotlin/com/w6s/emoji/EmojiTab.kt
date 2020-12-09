package com.w6s.emoji

import android.content.Context
import android.graphics.Bitmap
import android.text.TextUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.view.LayoutInflater
import android.view.View
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.rockerhieu.emojicon.R


class EmojiTab: RelativeLayout {
    private var mIvIcon: ImageView? = null
    private var mCoverId: String? = ""
    private var mIconSrc = -1
    private var mContext = context

    constructor(context: Context, iconSrc: Int) : super(context) {
        mContext = context
        mIconSrc = iconSrc
        init(context)
    }

    constructor(context: Context, coverId: String?): super(context) {
        mContext = context
        mCoverId = coverId
        init(context)
    }

    private fun init(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.emotion_tab, this)

        mIvIcon = findViewById<View>(R.id.ivIcon) as ImageView

        if (TextUtils.isEmpty(mCoverId)) {
            mIvIcon!!.setImageResource(mIconSrc)
        } else {
            val builder = DisplayImageOptions.Builder();
            builder.cacheOnDisk(true)
            builder.cacheInMemory(true)
            builder.showImageOnFail(R.drawable.ic_tab_emoji)
            builder.showImageForEmptyUri(R.drawable.ic_tab_emoji)
            val displayOptions = builder.build();
            displayImageByMediaId(context, mCoverId!!, mIvIcon!!, displayOptions)
        }

    }


}