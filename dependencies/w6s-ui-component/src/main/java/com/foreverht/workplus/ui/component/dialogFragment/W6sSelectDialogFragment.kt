package com.foreverht.workplus.ui.component.dialogFragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.foreverht.workplus.ui.component.R
import com.foreverht.workplus.ui.component.Util.Util
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil
import kotlinx.android.parcel.Parcelize

private const val DATA_CONTENT_DATA = "DATA_CONTENT_DATA"
/**
 * Created by wuzejie on 2019/10/29.
 */
class W6sSelectDialogFragment : BasicUIDialogFragment(){
    /**
     * 属性
     */
    private lateinit var tvTitle: TextView
    private lateinit var llRoot: LinearLayout
    private lateinit var llItemList: LinearLayout
    private lateinit var llDialog: LinearLayout

    private var commonPopSelectData: CommonPopSelectData? = null
    private var itemViewMap = LinkedHashMap<String, W6sDialogItemView?>()
    private var itemSelect: String? = null

    private var mTitle: String? = ""
    private var mColor: String? = ""
    private var mTextListChangeColor: List<String> = ArrayList()
    private var mIsTextContentCenter: Boolean = false
    private var mWidth: Int = 0

    var mOnClickItemListener: OnClickItemListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.w6s_dialog_fragment, null)
        findViews(view)
        registerListener()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(0))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()
    }

    override fun changeStatusBar(view: View) {
        StatusBarUtil.setTransparentFullScreen(dialog?.window)
    }

    private fun findViews(view: View) {
        tvTitle = view.findViewById(R.id.tv_title)
        llRoot = view.findViewById(R.id.ll_root)
        llItemList = view.findViewById(R.id.ll_item_list)
        llDialog = view.findViewById(R.id.ll_dialog)
        if(mWidth != 0){
            var layoutParams : LinearLayout.LayoutParams = LinearLayout.LayoutParams(Util.dp2px(context, mWidth), LinearLayout.LayoutParams.WRAP_CONTENT)
            llDialog.layoutParams = layoutParams
        }
    }

    private fun registerListener() {
        llRoot.setOnClickListener { dismiss() }
    }
    /**
     * 初始化数据
     */
    private fun initData() {
        val bundle = arguments
        bundle?.apply {
            val commonPopSelectData = bundle.getParcelable<CommonPopSelectData>(DATA_CONTENT_DATA)
            commonPopSelectData?.itemList?.forEach {
                itemViewMap[it] = null
            }
            itemSelect = commonPopSelectData?.itemSelect
        }
        var index = -1

        itemViewMap.keys.forEach { itItemKey ->
            index++
            val w6sDialogItemView = W6sDialogItemView(activity)
            //循环为子项设置显示文本
            w6sDialogItemView.setContent(itItemKey)

            //设置是否选中样式
            itemSelect?.apply {
                if(equals(itItemKey)){
                    w6sDialogItemView.select()
                }else{
                    w6sDialogItemView.unselect()
                }

            }

            //设置文本颜色
            if(!mColor.equals("")){
                for (i in 0 until mTextListChangeColor.size){
                    if (mTextListChangeColor[i].equals(itItemKey)){
                        w6sDialogItemView.setTextColor(mColor)
                    }
                }
            }

            //设置文本是否居中
            if(mIsTextContentCenter){
                w6sDialogItemView.setTextContentCenter()
            }

            //设置点击事件
            val position = index
            w6sDialogItemView.setOnClickListener {
                mOnClickItemListener?.apply {

                    onClick(position, value = itItemKey)
                }
                select(itemSelect = itItemKey)
                dismiss()
            }
            //添加到布局中
            itemViewMap[itItemKey] = w6sDialogItemView
            llItemList.addView(w6sDialogItemView)

        }
        //设置标题
        if(!mTitle.equals("")){
            tvTitle.visibility = View.VISIBLE
            tvTitle?.text = mTitle
        }
    }
    /**
     * Description:获取显示的数据以及选中项
     */
    fun setData(commonPopSelectData: CommonPopSelectData): W6sSelectDialogFragment{
        val bundle = Bundle()
        bundle.putParcelable(DATA_CONTENT_DATA, commonPopSelectData)
        arguments = bundle
        return this
    }
    /**
     * Description:设置文本是否居中
     */
    fun setTextContentCenter(isTextContentCenter: Boolean): W6sSelectDialogFragment {
       mIsTextContentCenter = isTextContentCenter
        return this
    }

    /**
     * Description:是否显示标题，默认不显示
     */
    fun setTitle(title: String): W6sSelectDialogFragment {
        mTitle = title
        return this
    }
    /**
     * Description:设置某个子项的颜色
     * @param: textListChangeColor 哪些子项
     * @param：color 所设置的颜色
     */
    fun setTextColor(textListChangeColor: List<String>, color: String): W6sSelectDialogFragment {
        mTextListChangeColor = textListChangeColor
        mColor = color
        return this
    }

    /**
     * Description:设置弹窗的宽度
     * @param width:宽度，单位是dp
     */

    fun setDialogWidth(width: Int): W6sSelectDialogFragment {
        mWidth = width
        return this
    }
    /**
     * Description:根据新的选中的数组，设置显示的数据以及选中项，以及子项的ＵＩ
     */
    fun select(itemSelect: String): W6sSelectDialogFragment {
        commonPopSelectData?.apply {
            this.itemSelect = itemSelect
            setData(this)

            itemViewMap.forEach { itEntry ->

                itEntry.value?.let { itCommonPopSelectItemView ->
                    if (itemSelect == itEntry.key) {
                        itCommonPopSelectItemView.select()

                    } else {
                        itCommonPopSelectItemView.unselect()
                    }
                }
            }
        }
        return this
    }

    fun setOnClickItemListener(onClickItemListener : OnClickItemListener): W6sSelectDialogFragment {
        mOnClickItemListener = onClickItemListener
        return this
    }
    fun getOnClickItemListener(): OnClickItemListener? {
       return mOnClickItemListener
    }

    interface OnClickItemListener {
        fun onClick(position: Int, value: String)
    }

}

@Parcelize
class CommonPopSelectData(
        //数据集
        var itemList: List<String> = ArrayList(),
        //选中值
        var itemSelect: String? = null

) : Parcelable



