package com.w6s.emoji

import android.content.Context
import android.graphics.Paint
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.foreveross.atwork.api.sdk.UrlConstantManager
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.rockerhieu.emojicon.R
import java.io.File


class StickerAdapter : BaseAdapter {

    private var mContext: Context? = null
    private var mCategory: StickerCategory? = null
    private var startIndex: Int = 0

    private var mEmotionLayoutWidth: Int = 0
    private var mEmotionLayoutHeight: Int = 0
    private var mPerWidth: Float = 0.toFloat()
    private var mPerHeight: Float = 0.toFloat()

    constructor(context: Context, category: StickerCategory?, emotionLayoutWidth: Int, emotionLayoutHeight: Int, startIndex: Int) {
        mContext = context
        mCategory = category
        this.startIndex = startIndex

        mEmotionLayoutWidth = emotionLayoutWidth
        mEmotionLayoutHeight = emotionLayoutHeight - EmojiKit.dip2px((35 + 26 + 70).toFloat())//减去底部的tab高度、小圆点的高度才是viewpager的高度，再减少30dp是让表情整体的顶部和底部有个外间距
        mPerWidth = mEmotionLayoutWidth * 1f / EmojiLayout.STICKER_COLUMNS
        mPerHeight = mEmotionLayoutHeight * 1f / (EmojiLayout.STICKER_ROWS + 1)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val viewHolder: StickerViewHolder
        var retView: View? = null
        if (convertView == null) {
            retView = convertView
            val rl = LinearLayout(mContext)
            rl.layoutParams = AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, mPerHeight.toInt())
            rl.orientation = LinearLayout.VERTICAL
            rl.setHorizontalGravity(Gravity.CENTER_HORIZONTAL)
            rl.setVerticalGravity(Gravity.CENTER)

            val ivStickerIcon = ImageView(mContext)
            val tvStickerName = TextView(mContext)

            val iconParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
            iconParams.width = DensityUtil.dip2px(63.toFloat())
            iconParams.height = DensityUtil.dip2px(63.toFloat())
            iconParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
            ivStickerIcon.layoutParams = iconParams

            val textParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            tvStickerName.textSize = 12.toFloat()
            tvStickerName.setTextColor(mContext!!.resources.getColor(R.color.gray))
            tvStickerName.gravity = Gravity.CENTER_HORIZONTAL
            tvStickerName.maxLines = 1
            tvStickerName.ellipsize = TextUtils.TruncateAt.END
            tvStickerName.layoutParams = textParams

            rl.addView(ivStickerIcon)
            rl.addView(tvStickerName)

            viewHolder = StickerViewHolder()
            viewHolder.mImageView = ivStickerIcon
            viewHolder.mTextView = tvStickerName
            retView = rl
            retView.tag = viewHolder
        } else {
            retView = convertView
            viewHolder = retView.tag as StickerViewHolder
        }

        val index = startIndex + position
        if (index >= mCategory!!.getStickers().size) {
            return retView
        }

        val sticker = mCategory!!.getStickers()[index] ?: return retView

        val stickerThumbnailUri = AtWorkDirUtils.getInstance().getStickerThumbnailPath(mCategory!!.getCategoryId(), sticker.mThumbName);
        if (File(stickerThumbnailUri).exists()) {
            ImageLoader.getInstance().displayImage("file://$stickerThumbnailUri", viewHolder.mImageView)
        } else {
            val builder = DisplayImageOptions.Builder();
            builder.cacheOnDisk(true)
            builder.cacheInMemory(true)
            builder.showImageOnFail(R.drawable.ic_tab_emoji)
            builder.showImageForEmptyUri(R.drawable.ic_tab_emoji)
            val displayOptions = builder.build();
            val stickerThumbnailUrl = AtWorkDirUtils.getInstance().getStickerThumbnailUrl(convertView?.context, UrlConstantManager.getInstance().stickerImageUrl, mCategory!!.getCategoryId(), sticker.mId)
            displayImage(stickerThumbnailUrl, viewHolder.mImageView!!, displayOptions)
        }

        viewHolder.mTextView!!.text = sticker.getShowName(convertView?.context)
        return retView
    }

    override fun getItem(position: Int): Any {
        return mCategory!!.getStickers().get(startIndex + position)
    }

    override fun getItemId(position: Int): Long {
        return (startIndex + position).toLong()
    }

    override fun getCount(): Int {
        var count = mCategory!!.getStickers().size - startIndex
        count = Math.min(count, EmojiLayout.STICKER_PER_PAGE)
        return count
    }

    internal inner class StickerViewHolder {
        var mImageView: ImageView? = null
        var mTextView: TextView? = null
    }

}