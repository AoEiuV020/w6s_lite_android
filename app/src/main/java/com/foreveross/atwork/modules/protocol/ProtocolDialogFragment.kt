package com.foreveross.atwork.modules.protocol

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.foreveross.atwork.R

class ProtocolDialogFragment : DialogFragment(){

    private lateinit var mTvContentOther: TextView
    private lateinit var tvLeft: TextView
    private lateinit var tvRight: TextView
    private var mIsColse: Boolean = false
    private var mChangeLeftClick: Boolean = false
    private lateinit var mOnLeftBtnClickListener: OnLeftBtnClickListener
    private lateinit var mOnBrightBtnClickListener: OnBrightBtnClickListener

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
//        initView()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(0))
    }

    override fun onDetach() {
        super.onDetach()
        if(mIsColse)
            activity?.finish()
    }

    private fun findViews(view: View){
        mTvContentOther = view.findViewById(R.id.tv_content)
        tvLeft = view.findViewById(R.id.tv_left)
        tvRight = view.findViewById(R.id.tv_right)

//        webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        mTvContentOther.text = getString(R.string.user_agreement_and_privacy_policy_content)
    }

    //Web视图
    private class webViewClient : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
        }

        override fun shouldOverrideKeyEvent(view: WebView?, event: KeyEvent?): Boolean {
            return super.shouldOverrideKeyEvent(view, event)
        }

    }

    private fun registerListener(){
        tvLeft.setOnClickListener {
            mIsColse = false
            if(mChangeLeftClick){
                mOnLeftBtnClickListener.onClick("0")
            }else{
                dismiss()
            }
        }
        tvRight.setOnClickListener {
            mIsColse = false
            mOnBrightBtnClickListener.onClick("0")
            dismiss()
        }
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

    interface OnBrightBtnClickListener {
        fun onClick(item: String)
    }
    interface OnLeftBtnClickListener {
        fun onClick(item: String)
    }
}