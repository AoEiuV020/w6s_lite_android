package com.w6s.emoji

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.Gravity
import android.widget.*
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.ScreenUtils
import com.rockerhieu.emojicon.EmojiconTextView
import com.rockerhieu.emojicon.emoji.Emojicon
import com.rockerhieu.emojicon.emoji.People


class EmojiAdapter : BaseAdapter {


    private var mContext: Context? = null
    private var mStartIndex: Int = 0
    private var mEmojiLayoutWidth: Int = 0
    private var mEmojiLayoutHeight: Int = 0
    private var mPerWidth: Float = 0.toFloat()
    private var mPerHeight: Float = 0.toFloat()
    private var mIvSize: Float = 0.toFloat()



    constructor(context: Context, emojiLayoutWidth: Int, emojiLayoutHeight: Int, startIndex: Int) :super() {
        mEmojiLayoutHeight = emojiLayoutHeight - EmojiKit.dip2px((35 ).toFloat())
        mEmojiLayoutWidth = emojiLayoutWidth
        mContext = context
        mStartIndex = startIndex


        mPerWidth = mEmojiLayoutWidth * 1f / EmojiLayout.EMOJI_COLUMNS
        mPerHeight = mEmojiLayoutHeight * 1f / (EmojiLayout.EMOJI_ROWS + 1) - 20
        val ivWidth = mPerWidth * .8f
        val ivHeight = mPerHeight * .8f
        mIvSize = Math.min(ivWidth, ivHeight)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: EmojiViewHolder
        var retView: View? = null
        if (convertView == null) {
            retView = convertView
            val rl = RelativeLayout(mContext)
            rl.layoutParams = AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, mPerHeight.toInt())
            rl.gravity = Gravity.BOTTOM

            val emojiconView = EmojiconTextView(mContext)
            emojiconView.setEmojiconSize(DensityUtil.dip2px(24.toFloat()))

            val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
            layoutParams.width = mIvSize.toInt()
            layoutParams.height = mIvSize.toInt()
            emojiconView.gravity = Gravity.CENTER_HORIZONTAL
            emojiconView.setLayoutParams(layoutParams)

            rl.addView(emojiconView)

            viewHolder = EmojiViewHolder()
            viewHolder.mEmojicon = emojiconView
            retView = rl
            retView.tag = viewHolder
        } else {
            retView = convertView
            viewHolder = retView.tag as EmojiViewHolder
        }
        val emoji = getItem(position) as Emojicon
        viewHolder.mEmojicon!!.setText(emoji.emoji)

        return retView
    }

    override fun getItem(position: Int): Any {
        return EmojiManager.instance.defaultEmojiData.get(getItemId(position).toInt())
    }

    override fun getItemId(position: Int): Long {
        return (mStartIndex + position).toLong();
    }

    override fun getCount(): Int {
        var count = EmojiManager.instance.getDisplayCount() - mStartIndex
        count = Math.min(count, EmojiLayout.EMOJI_PER_PAGE)
        return count
    }

    internal inner class EmojiViewHolder {
        var mEmojicon: EmojiconTextView? = null
    }

}