package com.w6s.emoji

import android.content.Context
import androidx.viewpager.widget.ViewPager
import android.view.View
import android.util.SparseArray
import android.graphics.drawable.StateListDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.core.util.keyIterator
import androidx.core.util.valueIterator
import androidx.core.view.setMargins
import com.rockerhieu.emojicon.R


class EmojiLayout : LinearLayout, View.OnClickListener {

    companion object {
        val EMOJI_COLUMNS = 8
        val EMOJI_ROWS = 4
        val EMOJI_PER_PAGE = EMOJI_COLUMNS * EMOJI_ROWS//最后一个是删除键

        val STICKER_COLUMNS = 4
        val STICKER_ROWS = 2
        val STICKER_PER_PAGE = STICKER_COLUMNS * STICKER_ROWS
    }


    private var mMeasuredWidth: Int = 0
    private var mMeasuredHeight: Int = 0

    private var tabPosition = 0
    private var mContext: Context
    private var vpEmotioin: ViewPager? = null
    private var mLlPageNumber: LinearLayout? = null
    private var mLlTabContainer: LinearLayout? = null
    private var mRlEmotionAdd: RelativeLayout? = null

    private var mTabCount: Int = 0
    private val mTabViewArray = SparseArray<View>()
//    private var mSettingTab: EmojiTab? = null
    private var emotionSelectedListener: IEmojiSelectedListener? = null
    private var emotionExtClickListener: IEmojiExtClickListener? = null
    private var mEmotionAddVisiable = false
    private var mEmotionSettingVisiable = false
    private var showSticker = true


    constructor(context: Context) : super(context) {
        mContext = context
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        mContext = context
    }

    fun setEmotionSelectedListener(emotionSelectedListener: IEmojiSelectedListener?) {
        if (emotionSelectedListener != null) {
            this.emotionSelectedListener = emotionSelectedListener
        }
    }


    fun setEmotionExtClickListener(emotionExtClickListener: IEmojiExtClickListener?) {
        if (emotionExtClickListener != null) {
            this.emotionExtClickListener = emotionExtClickListener
        }
    }

    fun setShowStick(show: Boolean) {
        showSticker = show
    }

    var isBurn = false;
    fun setBurnMode(isBurn: Boolean) {
        this.isBurn = isBurn
        if (mLlTabContainer == null) {
            return;
        }
        if (isBurn) {
            mLlTabContainer!!.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.burn_mode))
            for (tab in mTabViewArray.valueIterator()) {
                tab!!.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.burn_mode))
            }
            return;
        }
        for (tab in mTabViewArray.valueIterator()) {
            tab!!.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white))
        }
        mLlTabContainer!!.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white))
    }


    /**
     * 设置表情添加按钮的显隐
     *
     * @param visiable
     */
    fun setEmotionAddVisiable(visiable: Boolean) {
        mEmotionAddVisiable = visiable
        if (mRlEmotionAdd != null) {
            mRlEmotionAdd!!.visibility = if (mEmotionAddVisiable) View.VISIBLE else View.GONE
        }
    }

    /**
     * 设置表情设置按钮的显隐
     *
     * @param visiable
     */
    fun setEmotionSettingVisiable(visiable: Boolean) {
        mEmotionSettingVisiable = visiable
//        if (mSettingTab != null) {
//            mSettingTab!!.setVisibility(if (mEmotionSettingVisiable) View.VISIBLE else View.GONE)
//        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mMeasuredWidth = measureWidth(widthMeasureSpec)
        mMeasuredHeight = measureHeight(heightMeasureSpec)
        setMeasuredDimension(mMeasuredWidth, mMeasuredHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        init()
        initListener()
    }


    private fun measureWidth(measureSpec: Int): Int {
        var result = 0
        val specMode = View.MeasureSpec.getMode(measureSpec)
        val specSize = View.MeasureSpec.getSize(measureSpec)

        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = EmojiKit.dip2px(200.toFloat())
            if (specMode == View.MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize)
            }
        }
        return result
    }

    private fun measureHeight(measureSpec: Int): Int {
        var result = 0
        val specMode = View.MeasureSpec.getMode(measureSpec)
        val specSize = View.MeasureSpec.getSize(measureSpec)

        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = EmojiKit.dip2px(200.toFloat())
            if (specMode == View.MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize)
            }
        }
        return result
    }

    private fun init() {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.emotion_layout, this)
        //        View.inflate(mContext, R.layout.emotion_layout, this);
        vpEmotioin = findViewById<View>(R.id.vpEmotioin) as ViewPager
        mLlPageNumber = findViewById<View>(R.id.llPageNumber) as LinearLayout
        mLlTabContainer = findViewById<View>(R.id.llTabContainer) as LinearLayout
        mRlEmotionAdd = findViewById<View>(R.id.rlEmotionAdd) as RelativeLayout
        setEmotionAddVisiable(mEmotionAddVisiable)

        initTabs()

    }

    private fun initTabs() {
        //默认添加一个表情tab
        val emojiTab = EmojiTab(mContext, R.drawable.ic_tab_emoji)
        mLlTabContainer!!.addView(emojiTab)
        mTabViewArray.put(0, emojiTab)

        if (showSticker) {
            //添加所有的贴图tab
            val stickerCategories = StickerManager.instance.getStickerCategories()
            var tabId = 1
            for ((k, v) in stickerCategories.toSortedMap()) {
                val category = v
                val tab = EmojiTab(mContext, category!!.getCoverImgPath())
                mLlTabContainer!!.addView(tab)
                mTabViewArray.put(tabId, tab)
                StickerManager.instance.stickerTabRecoder.put(tabId, k)
                tabId += 1
            }
        }


//        //最后添加一个表情设置Tab
//        val drawable = StateListDrawable()
//        val unSelected = mContext.getResources().getDrawable(android.R.color.white)
//        drawable.addState(intArrayOf(-android.R.attr.state_pressed), unSelected)
//        val selected = mContext.getResources().getDrawable(android.R.color.darker_gray)
//        drawable.addState(intArrayOf(android.R.attr.state_pressed), selected)
//        mSettingTab!!.setBackground(drawable)
//        mLlTabContainer!!.addView(mSettingTab)
//        mTabViewArray.put(mTabViewArray.size(), mSettingTab)
//        setEmotionSettingVisiable(mEmotionSettingVisiable)

        selectTab(0)
    }

    private fun initListener() {
        if (mLlTabContainer != null) {
            mTabCount = mLlTabContainer!!.childCount //不包含最后的设置按钮
            for (position in 0 until mTabCount) {
                val tab = mLlTabContainer!!.getChildAt(position)
                tab.tag = position
                tab.setOnClickListener(this)
            }
        }

        vpEmotioin!!.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                setCurPageCommon(position)
            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        mRlEmotionAdd!!.setOnClickListener(OnClickListener { v ->
            if (emotionExtClickListener != null) {
                emotionExtClickListener!!.onEmojiAddClick(v)
            }
        })

//        mSettingTab!!.setOnClickListener(OnClickListener { v ->
//            if (emotionExtClickListener != null) {
//                emotionExtClickListener!!.onEmojiSettingClick(v)
//            }
//        })
    }

    private fun setCurPageCommon(position: Int) {
        if (tabPosition === 0)
            setCurPage(position, Math.ceil((EmojiManager.instance.getDisplayCount() / EmojiLayout.EMOJI_PER_PAGE.toFloat()).toDouble()).toInt())
        else {
            val category = StickerManager.instance.getStickerCategories().get(StickerManager.instance.stickerTabRecoder.get(tabPosition))
            setCurPage(position, Math.ceil((category!!.getStickers().size / STICKER_PER_PAGE.toFloat()).toDouble()).toInt())
        }
    }


    override fun onClick(v: View) {
        tabPosition = v.tag as Int
        selectTab(tabPosition)
    }

    private fun selectTab(tabPosi: Int) {
        for (i in 0 until mTabCount) {
            val tab = mTabViewArray.get(i)
            if (!isBurn) {
                tab?.setBackgroundResource(R.drawable.shape_tab_normal)
            }

        }
        if (!isBurn) {
            val tab = mTabViewArray.get(tabPosi)
            tab?.setBackgroundResource(R.drawable.shape_tab_press)
        }

        //显示表情内容
        fillVpEmotioin(tabPosi)
    }

    private fun fillVpEmotioin(tabPosi: Int) {
        val adapter = EmojiPagerAdapter(mMeasuredWidth, mMeasuredHeight, tabPosi, emotionSelectedListener)
        vpEmotioin!!.setAdapter(adapter)
        mLlPageNumber!!.removeAllViews()
        setCurPageCommon(0)
    }

    private fun setCurPage(page: Int, pageCount: Int) {
        val hasCount = mLlPageNumber!!.getChildCount()
        val forMax = Math.max(hasCount, pageCount)

        var ivCur: ImageView? = null
        for (i in 0 until forMax) {
            if (pageCount <= hasCount) {
                if (i >= pageCount) {
                    mLlPageNumber!!.getChildAt(i).visibility = View.GONE
                    continue
                } else {
                    ivCur = mLlPageNumber!!.getChildAt(i) as ImageView
                }
                ivCur!!.setBackgroundResource(R.drawable.selector_view_pager_indicator)
            } else {
                if (i < hasCount) {
                    ivCur = mLlPageNumber!!.getChildAt(i) as ImageView
                } else {
                    ivCur = ImageView(mContext)
                    ivCur!!.setBackgroundResource(R.drawable.selector_view_pager_indicator)
                    val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    ivCur!!.setLayoutParams(params)
                    params.setMargins(10)
                    mLlPageNumber!!.addView(ivCur)
                }
            }

            ivCur!!.setId(i)
            ivCur!!.setSelected(i == page)
            ivCur!!.setVisibility(View.VISIBLE)
        }
    }

    var mMessageEditText: EditText? = null

    fun attachEditText(messageEditText: EditText) {
        mMessageEditText = messageEditText
    }


}