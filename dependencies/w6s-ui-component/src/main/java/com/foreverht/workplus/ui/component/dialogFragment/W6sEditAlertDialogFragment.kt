package com.foreverht.workplus.ui.component.dialogFragment
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.foreverht.workplus.ui.component.R
import com.foreverht.workplus.ui.component.Util.Util
import androidx.fragment.app.DialogFragment

/**
 * Created by wuzejie on 2019/10/30.
 */
class W6sEditAlertDialogFragment :DialogFragment(){

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


    private var mIsHideLeftBtn: Boolean = false
    private var mChangeLeftClick: Boolean = false
    private lateinit var mOnBrightBtnClickListener: OnBrightBtnClickListener
    private lateinit var mOnLeftBtnClickListener: OnLeftBtnClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //return super.onCreateView(inflater, container, savedInstaneState)
        val view = inflater.inflate(R.layout.w6s_edit_alert_dialog_fragment, null)
        findViews(view)
        registerListener()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(0))
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

    }

    private fun registerListener() {
        mTvLeft.setOnClickListener {
            if(mChangeLeftClick){
                mOnLeftBtnClickListener.onClick("0")
            }else{
                dismiss()
            }

        }
        mTvRight.setOnClickListener {
            mOnBrightBtnClickListener.onClick("0")
            dismiss()
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
            mViewUnderLine.setBackgroundColor( getResources().getColor(mLineColor))
            var layoutParams : LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Util.dp2px(context, mLineHeight))
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


    }

    /**
     * Description:设置标题
     */
    fun setTitle(context: Context, title: Int): W6sEditAlertDialogFragment {
        mTitle = context?.getString(title)
        return this
    }
    fun setTitle(title: String): W6sEditAlertDialogFragment {
        mTitle = title
        return this
    }
    /**
     * Description:设置文本
     */
    fun setTvContent(context: Context, content: Int): W6sEditAlertDialogFragment {
        mContent = context?.getString(content)
        return this
    }
    fun setTvContent(content: String): W6sEditAlertDialogFragment {
        mContent = content
        return this
    }
    /**
     * Description:设置提示语
     */
    fun setTvTip(context: Context, tip: Int): W6sEditAlertDialogFragment {
        mTip = context?.getString(tip)
        return this
    }
    fun setTvTip(tip: String): W6sEditAlertDialogFragment {
        mTip = tip
        return this
    }
    /**
     * Description:显示编辑框
     */
    fun setEditVisible(isEditEnable: Boolean): W6sEditAlertDialogFragment {
        mIsEditEnable = isEditEnable
        return this
    }
    /**
     * Description:自定义下划线的颜色和宽度
     */
    fun setViewUnderLine(color: Int, height: Int): W6sEditAlertDialogFragment {
        mLineColor = color
        mLineHeight = height
        return this
    }
    /**
     * Description:左边按钮的文本和颜色
     */
    fun setTvLeft(context: Context, leftContent: Int, color: Int): W6sEditAlertDialogFragment {
        mLeftContent = context?.getString(leftContent)
        mLeftTvColor = color
        return this
    }
    fun setTvLeft(context: Context, leftContent: Int): W6sEditAlertDialogFragment {
        mLeftContent = context?.getString(leftContent)
        mLeftTvColor = R.color.common_button_text_color
        return this
    }
    fun setTvLeft(leftContent: String): W6sEditAlertDialogFragment {
        mLeftContent = leftContent
        mLeftTvColor = R.color.common_button_text_color
        return this
    }
    /**
     * Description:右边按钮的文本和颜色
     */
    fun setTvRight(context: Context, RightContent: Int, color: Int): W6sEditAlertDialogFragment {
        mRightContent = context?.getString(RightContent)
        mRightTvColor = color
        return this
    }

    fun setTvRight(context: Context, RightContent: Int): W6sEditAlertDialogFragment {
        mRightContent = context?.getString(RightContent)
        mRightTvColor = R.color.common_button_text_color
        return this
    }
    fun setTvRight(RightContent: String): W6sEditAlertDialogFragment {
        mRightContent = RightContent
        mRightTvColor = R.color.common_button_text_color
        return this
    }
    /**
     * Description:进度条提示语的文本和颜色
     */
    fun setProgressDesc(context: Context, RightContent: Int, color: Int): W6sEditAlertDialogFragment {
        mTvProgressDescContent = context?.getString(RightContent)
        mTvProgressDescColor = color
        return this
    }
    fun setProgressDesc(context: Context, RightContent: Int): W6sEditAlertDialogFragment {
        mTvProgressDescContent = context?.getString(RightContent)
        mTvProgressDescColor = R.color.common_text_color
        return this
    }
    fun setProgressDesc(RightContent: String): W6sEditAlertDialogFragment {
        mTvProgressDescContent = RightContent
        mTvProgressDescColor = R.color.common_text_color
        return this
    }
    /**
     * Description:进度条的最大值
     */
    fun setMax(max: Int): W6sEditAlertDialogFragment{
        mTvProgressMax = max
        return this
    }
    /**
     * Description:进度条初始进度
     */
    fun setInitProgress(progress: Int): W6sEditAlertDialogFragment {
        mTvProgressCurrent = progress
        return this
    }

    /**
     * Description:实时变更进度条的进度
     */
    fun setCurrentProgresss(progress: Int): W6sEditAlertDialogFragment {

        var progressCurrentNum: String =progress.toString() + "/" + mProgressBar.max
        mTvProgress.text = progressCurrentNum
        return this
    }

    /**
     * Description:因此取消按钮
     */
    fun hideLeftBtn(): W6sEditAlertDialogFragment{
        mIsHideLeftBtn = true
        return this
    }

    /**
     * Description:右边按钮的点击事件
     */
    fun setRightOnclick(onBrightBtnClickListener: OnBrightBtnClickListener): W6sEditAlertDialogFragment {
        mOnBrightBtnClickListener = onBrightBtnClickListener
        return this
    }

    /**
     * Description:左边按钮的点击事件
     */
    fun setLeftOnclick(onLeftBtnClickListener: OnLeftBtnClickListener): W6sEditAlertDialogFragment {
        mChangeLeftClick = true
        mOnLeftBtnClickListener = onLeftBtnClickListener
        return this
    }


    interface OnBrightBtnClickListener {
        fun onClick(item: String)
    }
    interface OnLeftBtnClickListener {
        fun onClick(item: String)
    }

}