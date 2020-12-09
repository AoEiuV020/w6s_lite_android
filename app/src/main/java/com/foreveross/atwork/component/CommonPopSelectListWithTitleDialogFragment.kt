package com.foreveross.atwork.component

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil
import kotlinx.android.parcel.Parcelize

private const val DATA_CONTENT_DATA = "DATA_CONTENT_DATA"

class CommonPopSelectListWithTitleDialogFragment : BasicDialogFragment() {

    private lateinit var llRoot: LinearLayout
    private lateinit var llItemList: LinearLayout
    private lateinit var tvTitle: TextView

    private var commonPopSelectData: CommonPopSelectWithTitleData? = null
    private var itemViewMap = LinkedHashMap<String, CommonPopSelectWithTitleItemView?>()
    private var itemSelect: String? = null
    private var title: String? = null

    var onClickItemListener: OnClickItemListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //必须在 onCreateView 之前
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_common_pop_select_list_with_title, null)
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

    fun setData(commonPopSelectData: CommonPopSelectWithTitleData) {
        val bundle = Bundle()
        bundle.putParcelable(DATA_CONTENT_DATA, commonPopSelectData)
        arguments = bundle


    }


    fun select(itemSelect: String) {
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


    }


    private fun findViews(view: View) {
        llRoot = view.findViewById(R.id.ll_root)
        llItemList = view.findViewById(R.id.ll_item_list)
        tvTitle = view.findViewById(R.id.tv_title)
    }

    private fun registerListener() {
        llRoot.setOnClickListener { dismiss() }
    }


    private fun initData() {
        val bundle = arguments
        bundle?.apply {
            val commonPopSelectData = bundle.getParcelable<CommonPopSelectWithTitleData>(DATA_CONTENT_DATA)
            commonPopSelectData?.itemList?.forEach {
                itemViewMap[it] = null
            }

            itemSelect = commonPopSelectData?.itemSelect
            title = commonPopSelectData?.title

        }


        title?.apply {
            tvTitle.text = this
            tvTitle.visibility = View.VISIBLE
        }


        var index = -1
        itemViewMap.keys.forEach { itItemKey ->
            index++

            val commonPopSelectItemView = CommonPopSelectWithTitleItemView(activity)
            commonPopSelectItemView.setContent(itItemKey)

            itemSelect?.apply {

                if (equals(itItemKey)) {
                    commonPopSelectItemView.select()
                } else {
                    commonPopSelectItemView.unselect()

                }
            }
            val position = index

            commonPopSelectItemView.setOnClickListener {
                onClickItemListener?.apply { onClick(position, value = itItemKey) }

                select(itemSelect = itItemKey)
                dismiss()
            }

            itemViewMap[itItemKey] = commonPopSelectItemView

            llItemList.addView(commonPopSelectItemView)


        }


    }

    override fun changeStatusBar(view: View) {
        StatusBarUtil.setColorNoTranslucent(view as ViewGroup, dialog?.window, ContextCompat.getColor(BaseApplicationLike.baseContext, R.color.transparent_70))

    }

    interface OnClickItemListener {
        fun onClick(position: Int, value: String)
    }

}




@Parcelize
class CommonPopSelectWithTitleData(

        var itemList: List<String> = ArrayList(),

        var itemSelect: String? = null,

        var title: String? = null

) : Parcelable