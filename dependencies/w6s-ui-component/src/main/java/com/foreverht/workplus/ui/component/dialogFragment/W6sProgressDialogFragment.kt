package com.foreverht.workplus.ui.component.dialogFragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.foreverht.workplus.ui.component.R
/**
 * Created by wuzejie on 2019/11/4.
 * TODO:待使用；可能直接使用原项目的loading框，所以删掉。
 *  另外，合并到dev之后，对应的style最低支持api21，现项目到api19，已屏蔽相关代码，若要使用待完善
 */
class W6sProgressDialogFragment: DialogFragment(){

    private lateinit var mLlRoot: LinearLayout
    private lateinit var mLlWhiteProgress: LinearLayout
    private lateinit var mTvProgress: TextView
    private var mStrContent: String =""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.w6s_progress_dialog_fragment, null)
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

    private fun findViews(view: View) {
        mLlRoot = view.findViewById(R.id.ll_root)
        mTvProgress = view.findViewById(R.id.tv_progress)
        mLlWhiteProgress = view.findViewById(R.id.ll_white_progress)
    }

    private fun registerListener() {
        mLlRoot.setOnClickListener { dismiss() }
    }
    /**
     * Description:初始化数据
     */
    private fun initData() {
        if(mStrContent != ""){
                mTvProgress.text = mStrContent

        }
    }
    /**
     * Description:设置文本
     */
    fun setTvContent(strContent: String){
        mStrContent = strContent
    }

}