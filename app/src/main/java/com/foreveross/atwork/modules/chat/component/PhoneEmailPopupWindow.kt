package com.foreveross.atwork.modules.chat.component

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.foreverht.workplus.ui.component.dialogFragment.BasicUIDialogFragment
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil

class PhoneEmailPopupWindow : BasicUIDialogFragment {

    private var jumpType = 0
    private var type = 0
    private lateinit var itemClick: ItemClick
    private var textData = ""

    companion object{
        const val PHONE = 1
        const val EMAIL = 2
        const val PHONE_JUMP = 1
        const val EMAIL_JUMP = 2
        const val COPY = 3
        const val CANCEL = 4
    }

    constructor(context: Context,type: Int,textData: String,itemClick: ItemClick){
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.type = type
        this.itemClick = itemClick
        this.textData = textData
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(0))
    }

    override fun changeStatusBar(view: View) {
        StatusBarUtil.setTransparentFullScreen(dialog?.window)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, android.R.style.Theme_NoTitleBar)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val popupView: View = inflater.inflate(R.layout.component_phone_email_pop, null)
        val llPop: LinearLayout = popupView.findViewById<LinearLayout>(R.id.llPop)
        val tvContent = popupView.findViewById<TextView>(R.id.tvContent)
        val tvJump = popupView.findViewById<TextView>(R.id.tvJump)
        val tvCopy = popupView.findViewById<TextView>(R.id.tvCopy)
        val tvCancel = popupView.findViewById<TextView>(R.id.tvCancel)
        var rlRoot = popupView.findViewById<RelativeLayout>(R.id.rlRoot)
        when(type){
            PHONE -> {
                jumpType = PHONE_JUMP
                tvContent.text = textData + context!!.getString(R.string.pop_phone_content)
                tvJump.text = context!!.getString(R.string.pop_call_phone)
                tvCopy.text = context!!.getString(R.string.pop_copy_phone)
                tvCancel.text = context!!.getString(R.string.pop_cancel)
            }
            EMAIL -> {
                jumpType = EMAIL_JUMP
                tvContent.text = textData + context!!.getString(R.string.pop_email_content)
                tvJump.text = context!!.getString(R.string.pop_write_email)
                tvCopy.text = context!!.getString(R.string.pop_copy_email)
                tvCancel.text = context!!.getString(R.string.pop_cancel)
            }
        }
        tvJump.setOnClickListener {
            itemClick.onItemClick(jumpType)
        }
        tvCopy.setOnClickListener {
            itemClick.onItemClick(COPY)
        }
        tvCancel.setOnClickListener {
            itemClick.onItemClick(CANCEL)
        }
        rlRoot.setOnClickListener {
            itemClick.onItemClick(CANCEL)
        }
        return popupView
    }

    interface ItemClick{
        fun onItemClick(type: Int)
    }
}