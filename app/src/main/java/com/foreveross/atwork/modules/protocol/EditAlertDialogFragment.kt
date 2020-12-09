package com.foreveross.atwork.modules.protocol

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.foreveross.atwork.R

class EditAlertDialogFragment: DialogFragment(){

    private lateinit var mTvTitle: TextView
    private lateinit var mTvContent: TextView
    private lateinit var mTvTip: TextView
    private lateinit var mEtContent: EditText
    private lateinit var mAllDelBtn: ImageView
    private lateinit var mViewUnderLine: View
    private lateinit var mTvLeft: TextView
    private lateinit var mTvRight: TextView
    private lateinit var mTvProgressDesc: TextView
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mTvProgress: TextView
    private lateinit var mTvContentOther: TextView
    private lateinit var mTvContentThird: TextView

    private var mTitle: String? = ""
    private var mContent: String? = ""
    private var mTip: String? = ""
    private var mIsEditEnable = false
    private var mLineColor: Int = 0
    private var mLineHeight = 0
    private var mLeftContent: String? = ""
    private var mLeftTvColor: Int = 0
    private var mRightContent: String? = ""
    private var mRightTvColor: Int = 0
    private var mTvProgressDescContent: String? = ""
    private var mTvProgressDescColor: Int = 0
    private var mTvProgressContent: String? = ""
    private var mTvProgressMax: Int = 100
    private var mTvProgressCurrent: Int = 0
    private var mContentOther: String? = ""
    private var mContentThird: String? = ""
    private var mIsColse: Boolean = false


    private var mIsHideLeftBtn: Boolean = false
    private var mChangeLeftClick: Boolean = false
    private lateinit var mOnBrightBtnClickListener: OnBrightBtnClickListener
    private lateinit var mOnLeftBtnClickListener: OnLeftBtnClickListener
    private lateinit var mOnContentOtherClickListener: OnContentOtherClickListener
    private lateinit var mOnContentThirdClickListener: OnContentThirdClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //return super.onCreateView(inflater, container, savedInstaneState)
        val view = inflater.inflate(R.layout.protocol_dialog_fragment, null)
        findViews(view)
        registerListener()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(0))
    }

    override fun onDetach() {
        super.onDetach()
        if(mIsColse)
            activity?.finish()
    }

    private fun findViews(view: View) {
        mTvTitle = view.findViewById(R.id.tv_title)
        mTvContent = view.findViewById(R.id.tv_content)
        mTvTip = view.findViewById(R.id.tv_tip)
        mEtContent = view.findViewById(R.id.et_content)
        mAllDelBtn = view.findViewById(R.id.all_del_Btn)
        mViewUnderLine = view.findViewById(R.id.view_under_line)
        mTvLeft = view.findViewById(R.id.tv_left)
        mTvRight = view.findViewById(R.id.tv_right)
        mTvProgress = view.findViewById(R.id.tv_progress)
        mTvProgressDesc = view.findViewById(R.id.tv_desc)
        mProgressBar = view.findViewById(R.id.pb_loading)
        mTvContentOther = view.findViewById(R.id.tv_content_other)
        mTvContentThird = view.findViewById(R.id.tv_content_third)

    }

    private fun registerListener() {

        mTvLeft.setOnClickListener {
            mIsColse = false
            if(mChangeLeftClick){
                mOnLeftBtnClickListener.onClick("0")
            }else{
                dismiss()
            }

        }
        mTvRight.setOnClickListener {
            mIsColse = false
            mOnBrightBtnClickListener.onClick("0")
            dismiss()
        }
        mTvContentOther.setOnClickListener {
            mOnContentOtherClickListener.onClick("0")
        }
        mTvContentThird.setOnClickListener {
            mOnContentThirdClickListener.onClick("0")
        }
    }
    /**
     * 初始化数据
     */
    private fun initView() {
        if(!mTitle.equals("")){
            mTvTitle.visibility = View.VISIBLE
            mTvTitle.text = mTitle
        }
        if(!mContent.equals("")){
            mTvContent.visibility = View.VISIBLE
            mTvContent.text = mContent
        }
        if(!mTip.equals("")){
            mTvTip.visibility = View.VISIBLE
            mTvTip.text = mTip
        }
        if(mIsEditEnable){
            mEtContent.visibility = View.VISIBLE
            mAllDelBtn.visibility = View.VISIBLE
            mViewUnderLine.visibility = View.VISIBLE
        }
        if(mLineColor != 0){
            mViewUnderLine.setBackgroundColor( getResources().getColor(R.color.common_underline_color))
            var layoutParams : LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, MaxHeightSwitchOrgScrollView.dip2px(context!!, mLineHeight.toFloat()))
            mViewUnderLine.layoutParams = layoutParams
        }
        if(!mLeftContent.equals("")){
            mTvLeft.text = mLeftContent
            mTvLeft.setTextColor(this.getResources().getColor(mLeftTvColor))
        }
        if(!mRightContent.equals("")){
            mTvRight.text = mRightContent
            mTvRight.setTextColor(mRightTvColor)
            mTvRight.setTextColor(this.getResources().getColor(mRightTvColor))
        }
        if (mIsHideLeftBtn){
            mTvLeft.visibility = View.GONE
        }
        if (!mTvProgressDescContent.equals("")){
            mTvProgressDesc.visibility = View.VISIBLE
            mProgressBar.visibility = View.VISIBLE
            mTvProgress.visibility = View.VISIBLE
            mTvProgressDesc.text = mTvProgressDescContent
            mProgressBar.max = mTvProgressMax
            var progressCurrentNum: String =mTvProgressCurrent.toString() + "/" + mProgressBar.max
            mTvProgress.text = progressCurrentNum
            mProgressBar.progress = mTvProgressCurrent
        }
        if(!mContentOther.equals("")){
            mTvContentOther.visibility = View.VISIBLE
            mTvContentOther.text = mContentOther
        }
        if(!mContentThird.equals("")){
            mTvContentThird.visibility = View.VISIBLE
            mTvContentThird.text = mContentThird
        }


    }

    /**
     * Description:设置标题
     */
    fun setTitle(context: Context, title: Int) {
        mTitle = context.getString(title)
    }
    fun setTitle(title: String) {
        mTitle = title
    }
    /**
     * Description:设置文本
     */
    fun setTvContent(context: Context, content: Int) {
        mContent = context.getString(content)
    }
    fun setTvContent(content: String) {
        mContent = content
    }
    /**
     * Description:设置富文本
     */
    fun setTvContent(context: Context, content: Int, boolean: Boolean) {
        if(boolean)
            mContent = Html.fromHtml(context.getString(content)).toString()
        else
            setTvContent(context, content)
    }
    /**
     * Description:设置提示语
     */
    fun setTvTip(context: Context, tip: Int) {
        mTip = context.getString(tip)
    }
    fun setTvTip(tip: String) {
        mTip = tip
    }
    /**
     * Description:显示编辑框
     */
    fun setEditVisible(isEditEnable: Boolean) {
        mIsEditEnable = isEditEnable
    }
    /**
     * Description:自定义下划线的颜色和宽度
     */
    fun setViewUnderLine(color: Int, height: Int) {
        mLineColor = color
        mLineHeight = height
    }
    /**
     * Description:左边按钮的文本和颜色
     */
    fun setTvLeft(context: Context, leftContent: Int, color: Int) {
        mLeftContent = context.getString(leftContent)
        mLeftTvColor = color
    }
    fun setTvLeft(context: Context, leftContent: Int) {
        mLeftContent = context.getString(leftContent)
        mLeftTvColor = R.color.common_left_button_text_color
    }
    fun setTvLeft(leftContent: String) {
        mLeftContent = leftContent
        mLeftTvColor = R.color.common_left_button_text_color
    }
    /**
     * Description:右边按钮的文本和颜色
     */
    fun setTvRight(context: Context, RightContent: Int, color: Int) {
        mRightContent = context.getString(RightContent)
        mRightTvColor = color
    }

    fun setTvRight(context: Context, RightContent: Int) {
        mRightContent = context.getString(RightContent)
        mRightTvColor = R.color.common_right_button_text_color
    }
    fun setTvRight(RightContent: String) {
        mRightContent = RightContent
        mRightTvColor = R.color.common_right_button_text_color
    }
    /**
     * Description:进度条提示语的文本和颜色
     */
    fun setProgressDesc(context: Context, RightContent: Int, color: Int) {
        mTvProgressDescContent = context.getString(RightContent)
        mTvProgressDescColor = color
    }
    fun setProgressDesc(context: Context, RightContent: Int) {
        mTvProgressDescContent = context.getString(RightContent)
        mTvProgressDescColor = R.color.common_text_color
    }
    fun setProgressDesc(RightContent: String) {
        mTvProgressDescContent = RightContent
        mTvProgressDescColor = R.color.common_text_color
    }
    /**
     * Description:进度条的最大值
     */
    fun setMax(max: Int){
        mTvProgressMax = max
    }
    /**
     * Description:进度条进度
     */
    fun setProgress(progress: Int) {
        mTvProgressContent = progress.toString() + "/" + this.mTvProgressMax
        mTvProgressCurrent = progress
    }

    /**
     * Description:设置第二个的文本
     */
    fun setTvContentOther(context: Context, content: Int) {
        mContentOther = context.getString(content)
    }
    fun setTvContentOther(content: String) {
        mContentOther = content
    }
    /**
     * Description:设置第二个的富文本
     */
    fun setTvContentOther(context: Context, content: Int, boolean: Boolean) {
        if(boolean)
            mContentOther = Html.fromHtml(context.getString(content)).toString()
        else
            setTvContent(context, content)
    }
    /**
     * Description:设置第三个的文本
     */
    fun setTvContentThird(context: Context, content: Int) {
        mContentThird = context.getString(content)
    }
    fun setTvContentThird(content: String) {
        mContentThird = content
    }
    /**
     * Description:设置第三个的富文本
     */
    fun setTvContentThird(context: Context, content: Int, boolean: Boolean) {
        if(boolean)
            mContentThird = Html.fromHtml(context.getString(content)).toString()
        else
            setTvContent(context, content)
    }

    /**
     * Description:因此取消按钮
     */
    fun hideLeftBtn(){
        mIsHideLeftBtn = true
    }

    /**
     * Description:右边按钮的点击事件
     */
    fun setRightOnclick(onBrightBtnClickListener: OnBrightBtnClickListener) {
        mOnBrightBtnClickListener = onBrightBtnClickListener
    }

    /**
     * Description:左边按钮的点击事件
     */
    fun setLeftOnclick(onLeftBtnClickListener: OnLeftBtnClickListener) {
        mChangeLeftClick = true
        mOnLeftBtnClickListener = onLeftBtnClickListener
    }

    /**
     * Description:第二个文本的点击事件
     */
    fun setContentOtherOnclick(onContentOtherClickListener: OnContentOtherClickListener) {
        mOnContentOtherClickListener = onContentOtherClickListener
    }

    /**
     * Description:第三个文本的点击事件
     */
    fun setContentThirdOnclick(onContentThirdClickListener: OnContentThirdClickListener) {
        mOnContentThirdClickListener = onContentThirdClickListener
    }

    /**
     * Description:点击返回键按钮是否在退出DialogFragment的同时退出外部的Fragment/activity
     */
    fun isColseOutSideView(isColse: Boolean) {
        mIsColse = isColse
    }

    interface OnBrightBtnClickListener {
        fun onClick(item: String)
    }
    interface OnLeftBtnClickListener {
        fun onClick(item: String)
    }
    interface OnContentOtherClickListener {
        fun onClick(item: String)
    }
    interface OnContentThirdClickListener {
        fun onClick(item: String)
    }

}