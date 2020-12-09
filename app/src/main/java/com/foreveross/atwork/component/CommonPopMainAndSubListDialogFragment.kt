package com.foreveross.atwork.component

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil
import kotlinx.android.parcel.Parcelize

private const val DATA_CONTENT_DATA = "DATA_CONTENT_DATA"

class CommonPopMainAndSubListDialogFragment : BasicDialogFragment() {

    private lateinit var llRoot: LinearLayout
    private lateinit var llItemList: LinearLayout


    var onClickItemListener: OnClickItemListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //必须在 onCreateView 之前
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_common_pop_main_or_sub_list, null)
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

    fun setData(commonPopMainAndSubData: CommonPopMainAndSubData) {
        val bundle = Bundle()
        bundle.putParcelable(DATA_CONTENT_DATA, commonPopMainAndSubData)
        arguments = bundle


    }




    private fun findViews(view: View) {
        llRoot = view.findViewById(R.id.ll_root)
        llItemList = view.findViewById(R.id.ll_item_list)
    }

    private fun registerListener() {
        llRoot.setOnClickListener { dismiss() }
    }


    private fun initData() {
        val bundle = arguments
        bundle?.apply {
            val commonPopMainAndSubData = bundle.getParcelable<CommonPopMainAndSubData>(DATA_CONTENT_DATA)
            commonPopMainAndSubData?.itemMainContentList?.forEachIndexed { index, mainContent ->
                val commonPopMainAndSubItemView = CommonPopMainAndSubItemView(activity)
                commonPopMainAndSubItemView.setMainContent(mainContent)
                commonPopMainAndSubItemView.setSubContent(commonPopMainAndSubData.itemSubContentList[index])

                commonPopMainAndSubItemView.setOnClickListener {
                    onClickItemListener?.onClick(index, mainContent)
                }


                llItemList.addView(commonPopMainAndSubItemView)
            }


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
class CommonPopMainAndSubData(


        var itemMainContentList: List<String> = ArrayList(),

        var itemSubContentList: List<String> = ArrayList()


) : Parcelable