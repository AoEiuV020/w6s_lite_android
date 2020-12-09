package com.foreverht.workplus.ui.component.popUpView

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.foreverht.workplus.ui.component.NewMessageView
import com.foreverht.workplus.ui.component.R
import com.foreveross.atwork.infrastructure.utils.explosion.ExplosionUtils

/**
 * Created by wuzejie on 19/11/1.
 * Description:
 * Pop View Item
 */

class W6sPopViewItemView : LinearLayout{
    //属性
    private lateinit var mIvIcon: ImageView
    private lateinit var mTvTitle: TextView
    private lateinit var mVLine: View
    private lateinit var mNewMessageView: NewMessageView
    private lateinit var mLlContainer: View

    private var mIconResId: Int = -1
    private var mTitleResId: Int = -1

    //构造函数
    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
        initAttrs(context, attrs)
    }

    private fun initView(){
        val view = LayoutInflater.from(context).inflate(R.layout.w6s_popview_item, this)
        mIvIcon = view.findViewById(R.id.pop_view_icon)
        mTvTitle = view.findViewById(R.id.pop_view_title)
        mVLine = view.findViewById(R.id.line_pop_view)
        mNewMessageView = view.findViewById(R.id.newMessageView)
        mLlContainer = view.findViewById(R.id.ll_container)
    }

    /**
     * Description:设置控件的资源和文本
     * @param context
     * @param attrs
     */
    private fun initAttrs(context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.PopViewItemView)
        mIconResId = typedArray.getResourceId(R.styleable.PopViewItemView_popIcon, 0)
        mTitleResId = typedArray.getResourceId(R.styleable.PopViewItemView_popTitle, 0)

        if (mIconResId != 0 && mTitleResId != 0) {
            setItem(mIconResId, mTitleResId)
        }

        typedArray.recycle()
    }

    /**
     * Description:设置控件的资源和文本
     * @param iconResId 图标控件的资源
     * @param titleStr 标题控件的文本
     */
    fun setItem(iconResId: Int, titleResId: Int) {
        if(-1 == iconResId){
            mIvIcon.visibility = View.GONE
        }else{
            mIvIcon.visibility = View.VISIBLE
            mIvIcon.setImageResource(iconResId)
        }
        val str = resources.getString(titleResId)
        mTvTitle.text = str

    }
    fun setItem(iconResId: Int, titleStr: String) {
        if(-1 == iconResId){
            mIvIcon.visibility = View.GONE
        }else{
            mIvIcon.visibility = View.VISIBLE
            mIvIcon.setImageResource(iconResId)
        }
        mTvTitle.text = titleStr
    }
    fun setItem(iconResId: Int, titleStr: String, newMessageCount: Int) {
        if(-1 == iconResId){
            mIvIcon.visibility = View.GONE
        }else{
            mIvIcon.visibility = View.VISIBLE
            mIvIcon.setImageResource(iconResId)
        }
        mNewMessageView.visibility  = if (newMessageCount > 0) View.VISIBLE else View.GONE
        mNewMessageView.setNum(newMessageCount)

        mTvTitle.text = titleStr
    }
    fun setIcon(bitmap: Bitmap) {
        mIvIcon.visibility = View.VISIBLE
        mIvIcon.setImageBitmap(bitmap)
    }

    fun setTitle(titleStr: String) {
        mTvTitle.text = titleStr
    }

    fun getTitle(): String {
        return mTvTitle.text.toString()
    }

    fun setLine(isLineVisiable: Boolean){
        if (isLineVisiable)
            mVLine.visibility = View.VISIBLE
        else
            mVLine.visibility = View.GONE
    }
    /**
     * Description:设置弹窗的宽度
     */
    fun setPopUpViewWidth(width: Int) {
        if(width != 0){
            var layoutParams : LayoutParams = LayoutParams(ExplosionUtils.dp2Px(width), LayoutParams.WRAP_CONTENT)
            mLlContainer.layoutParams = layoutParams
        }

    }
}