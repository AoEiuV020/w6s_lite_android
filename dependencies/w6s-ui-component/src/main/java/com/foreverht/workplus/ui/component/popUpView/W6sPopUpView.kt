package com.foreverht.workplus.ui.component.popUpView

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RelativeLayout
import androidx.core.view.ViewCompat
import androidx.core.widget.PopupWindowCompat
import com.foreverht.workplus.ui.component.NewMessageView
import com.foreverht.workplus.ui.component.R
import com.foreveross.atwork.infrastructure.utils.ScreenUtils

/**
 * Created by wuzejie on 19/11/1.
 * Description:
 * 通用POP VIEW设计
 */
class W6sPopUpView: RelativeLayout{

    private lateinit var mPopItemOnClickListener: PopItemOnClickListener
    private lateinit var mPopupWindow: PopupWindow
    private lateinit var mPopContainerView: LinearLayout
    private var mContext: Context
    private var mWidth: Int = 0

    constructor(context: Context) : super(context){
        mContext = context
        initView()
        initPopUpView()
    }
    /**
     *Description:初始化PopupWindow
     */
    private fun initPopUpView() {

        mPopupWindow = PopupWindow(this, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT).apply {
            setBackgroundDrawable(BitmapDrawable())
            isOutsideTouchable = true
            isFocusable = true
            isTouchable = true
        }

    }

    fun getPopupWindow(): PopupWindow {
        return mPopupWindow
    }

    private fun initView() {
        val view = LayoutInflater.from(context).inflate(R.layout.w6s_popview, this)
        mPopContainerView = view.findViewById(R.id.pop_view_container)
//        mRlPopContainerLayout = view.findViewById(R.id.pop_container_layout)
        // mRlPopContainerLayout.setOnClickListener(OnClickListener { mPopupWindow.dismiss() })
        ViewCompat.setElevation(mPopContainerView, 15f)
    }

    fun popSpaceCompatible(moreView: View, event: MotionEvent) {
        val contentView = getPopupWindow().contentView
        contentView.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
        val measuredHeight = contentView.measuredHeight
        val context = contentView.context

        if( measuredHeight >= ScreenUtils.getScreenHeight(context) - event.rawY) {
            pop(moreView, event.x.toInt(), event.y.toInt() - measuredHeight)

        } else {
            pop(moreView, event.x.toInt(), event.y.toInt())
        }
    }

    fun dismiss() {
        mPopupWindow.dismiss()
    }
    /**
     * Description:添加PopItem子项
     * @param resId 图标 (-1:表示不需要图标)
     * @param titleResId 标题
     * @param pos 位置，用于确定子项点击事件
     * @param isLineVisiable 是否显示下划线
     */
    fun addPopItem(resId: Int, titleResId: Int, pos: Int, isLineVisiable: Boolean): W6sPopUpView {
        if (isLineVisiable)
            addPopItem(resId, mContext.getString(titleResId), pos, isLineVisiable)
        else
            addPopItem(resId, mContext.getString(titleResId), pos, false)
        return this
    }

    fun addPopItem(resId: Int, titleResId: Int, pos: Int): W6sPopUpView {
        addPopItem(resId, mContext.getString(titleResId), pos, false)
        return this
    }

    fun addPopItem(resId: Int, title: String, pos: Int): W6sPopUpView {
        addPopItem(resId, title, pos, false)
        return this
    }

    fun addPopItem(resId: Int, title: String, pos: Int, isLineVisiable: Boolean): W6sPopUpView {
        val popViewItem = W6sPopViewItemView(context)
        popViewItem.setPopUpViewWidth(mWidth)
        popViewItem.setItem(resId, title)
        if(isLineVisiable)
            popViewItem.setLine(isLineVisiable)
        popViewItem.setOnClickListener { v ->
            mPopItemOnClickListener.click(popViewItem.getTitle(), pos)
            dismiss()
        }
        mPopContainerView.addView(popViewItem)
        return this
    }

    fun addPopItem(resId: Int, titleResId: Int, pos: Int, isLineVisiable: Boolean, newMessageCount: Int): W6sPopUpView {
        val popViewItem = W6sPopViewItemView(context)
        popViewItem.setPopUpViewWidth(mWidth)
        popViewItem.setItem(resId, mContext.getString(titleResId), newMessageCount)
        if(isLineVisiable)
            popViewItem.setLine(isLineVisiable)
        popViewItem.setOnClickListener { v ->
            mPopItemOnClickListener.click(popViewItem.getTitle(), pos)
            dismiss()
        }
        mPopContainerView.addView(popViewItem)
        return this
    }

    /**
     * Description:设置弹窗的相对位置
     * @param moreView
     */
    @JvmOverloads
    fun pop(moreView: View, x: Int = 0, y: Int = 0, gravity: Int = Gravity.NO_GRAVITY): W6sPopUpView {
        if (mPopupWindow.isShowing) {
            mPopupWindow.dismiss()

        } else {
            PopupWindowCompat.setOverlapAnchor(mPopupWindow, true)
            PopupWindowCompat.showAsDropDown(mPopupWindow, moreView, x, y, gravity)
        }
        return this
    }

    /**
     * Description:设置弹窗的宽度
     */
    fun setPopUpViewWidth(width: Int): W6sPopUpView {
        mWidth = width
        return this
    }

    fun setPopItemOnClickListener(popItemOnClickListener: PopItemOnClickListener): W6sPopUpView {
        this.mPopItemOnClickListener = popItemOnClickListener
        return this
    }

    interface PopItemOnClickListener {
        fun click(title: String, pos: Int)
    }

}