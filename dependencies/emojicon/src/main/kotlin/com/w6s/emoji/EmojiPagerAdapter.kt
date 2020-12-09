package com.w6s.emoji

import android.content.Context
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.ViewGroup
import android.view.Gravity
import com.w6s.emoji.EmojiLayout.Companion.EMOJI_PER_PAGE
import com.w6s.emoji.EmojiLayout.Companion.STICKER_PER_PAGE
import android.view.LayoutInflater
import android.widget.*
import androidx.viewpager.widget.PagerAdapter
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils
import com.foreveross.atwork.infrastructure.utils.NetworkStatusUtil
import com.rockerhieu.emojicon.R
import java.io.File


class EmojiPagerAdapter : PagerAdapter {

    private var mPageCount = 0
    private var mTabPosi = 0

    private var mEmotionLayoutWidth: Int = 0
    private var mEmotionLayoutHeight: Int = 0

    private var listener: IEmojiSelectedListener? = null
    private var mMessageEditText: EditText? = null

    private var forceRefresh = false;

    private val handler = object: Handler() {
        override fun handleMessage(msg: Message?) {
            when(msg?.what) {
                MESSAGE_STICKER_DOWNLOADED -> {
                    forceRefresh = true
                    val stickerCategory = StickerManager.instance.getStickerCategories().get(StickerManager.instance.stickerTabRecoder.get(mTabPosi))
                    if (stickerCategory != null) {
                        mPageCount = Math.ceil((stickerCategory.getStickers().size / EmojiLayout.STICKER_PER_PAGE.toFloat()).toDouble()).toInt()
                    }
                    notifyDataSetChanged()
                }
                MESSAGE_STICKER_DOWNLOAD_FAIL -> {

                }
            }
        }
    }

    override fun getItemPosition(`object`: Any): Int {
        if (forceRefresh) {
            forceRefresh = false
            return POSITION_NONE

        }
        return return POSITION_UNCHANGED
    }

    private var emojiListener: AdapterView.OnItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
        if (listener != null) {
            listener!!.onEmojiSelected(EmojiManager.instance.defaultEmojiData[id.toInt()])
        }
    }
    private var stickerListener: AdapterView.OnItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
        val category = StickerManager.instance.getStickerCategories().get(StickerManager.instance.stickerTabRecoder.get(mTabPosi))
        val stickers = category?.getStickers()
        val index = position + parent.tag as Int * EmojiLayout.STICKER_PER_PAGE

        if (index >= stickers!!.size) {
            false
        }

        if (listener != null) {
            val sticker = stickers.get(index)
            if (listener != null)
                listener!!.onStickerSelected(category.getCategoryId(), sticker)
        }
    }


    constructor(emojiLayoutWidth: Int, emojiLayoutHeigh: Int, tabPos: Int, listener: IEmojiSelectedListener?) : super() {
        mEmotionLayoutHeight = emojiLayoutHeigh
        mEmotionLayoutWidth = emojiLayoutWidth
        mTabPosi = tabPos
        if (mTabPosi === 0)
            mPageCount = Math.ceil((EmojiManager.instance.getDisplayCount() / EMOJI_PER_PAGE.toFloat()).toDouble()).toInt()
        else {
            val stickerCategory = StickerManager.instance.getStickerCategories().get(StickerManager.instance.stickerTabRecoder.get(mTabPosi))
            if (stickerCategory != null) {
                mPageCount = Math.ceil((stickerCategory.getStickers().size / EmojiLayout.STICKER_PER_PAGE.toFloat()).toDouble()).toInt()
            }
        }


        this.listener = listener
    }

    fun attachEditText(messageEditText: EditText) {
        mMessageEditText = messageEditText
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return if (mPageCount === 0) 1 else mPageCount
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val context = container.context
        val rl = RelativeLayout(context)
        rl.gravity = Gravity.CENTER
        val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)

        if (mTabPosi === 0) {
            val gridView = GridView(context)
            gridView.layoutParams = params
            gridView.gravity = Gravity.CENTER
            gridView.tag = position//标记自己是第几页
            gridView.onItemClickListener = emojiListener
            gridView.adapter = EmojiAdapter(context, mEmotionLayoutWidth, mEmotionLayoutHeight, position * EMOJI_PER_PAGE)
            gridView.numColumns = EmojiLayout.EMOJI_COLUMNS
            rl.addView(gridView)
        } else {
            val stickerInstance = StickerManager.instance
            val category = stickerInstance.getCategory(stickerInstance.getStickerCategories().get(stickerInstance.stickerTabRecoder.get(mTabPosi))!!.getCategoryId())
            val file = File(AtWorkDirUtils.getInstance().getStickerDirByCategoryId(category?.getCategoryId()))
            if (!file.exists()) {
                val downloadView = initDownloadView(context, container, category)
                downloadView.layoutParams = params
                rl.addView(downloadView)
            } else {
                val gridView = GridView(context)
                gridView.layoutParams = params
                gridView.gravity = Gravity.CENTER
                gridView.tag = position//标记自己是第几页
                gridView.onItemClickListener = stickerListener
                gridView.adapter = StickerAdapter(context, category, mEmotionLayoutWidth, mEmotionLayoutHeight, position * STICKER_PER_PAGE)
                gridView.numColumns = EmojiLayout.STICKER_COLUMNS
                rl.addView(gridView)
            }

        }
        container.addView(rl)
        return rl
    }

    private fun initDownloadView(context: Context, container: ViewGroup, category: StickerCategory?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.emojicon_download_view, container, false)
        val viewHolder = DownloadViewHolder(category!!.getCategoryId(), view)
        if (category?.getStatus() == -1) {

            viewHolder.downloadingView.visibility = View.GONE
            viewHolder.stickerAlbumName.text = category?.getName()
            if (NetworkStatusUtil.isWifiConnectedOrConnecting(context)) {
                category.setStatus(1)
                downloadStickerAlbumZip(context, category, viewHolder, handler, false)
            } else {
                viewHolder.stickerDownloadBtn.setOnClickListener {
                    if(!NetworkStatusUtil.isNetworkAvailable(context)) {
                        Toast.makeText(context, context.getString(R.string.cannot_connet_to_network), Toast.LENGTH_SHORT).show()
                    } else {
                        category.setStatus(1)
                        downloadStickerAlbumZip(context, category, viewHolder, handler, false)
                    }

                }
            }

        } else {
            viewHolder.unDownloadView.visibility = View.GONE
            viewHolder.progressText.text = ""
            viewHolder.progressView.progress = category!!.getDownloadProgress()
            setCurDownloadHolder(viewHolder)
        }
        return view
    }


    override fun destroyItem(container: ViewGroup, position: Int, viewObject: Any) {
        container.removeView(viewObject as View)
    }

    inner class DownloadViewHolder(categoryId: String, view: View) {
        val id = categoryId
        val unDownloadView: View = view.findViewById(R.id.sticker_undownload_view)
        val downloadingView: View = view.findViewById(R.id.sticker_downloading_view)
        val stickerDownloadBtn: TextView = unDownloadView.findViewById(R.id.download_sticker)
        val stickerAlbumName: TextView = unDownloadView.findViewById(R.id.sticker_album_name)
        val progressView: ProgressBar = downloadingView.findViewById(R.id.sticker_loading_progress)
        val progressText: TextView = downloadingView.findViewById(R.id.sticker_loading_progress_num)
    }


}