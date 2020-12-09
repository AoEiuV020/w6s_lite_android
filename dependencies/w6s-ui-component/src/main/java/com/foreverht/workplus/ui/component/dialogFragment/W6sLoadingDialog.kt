package com.foreverht.workplus.ui.component.dialogFragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.foreverht.workplus.ui.component.R
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView
import java.util.*


class W6sLoadingDialog: BasicUIDialogFragment() {


    private lateinit var llRoot: LinearLayout
    private lateinit var ivGif: GifImageView
    private lateinit var tvTip: TextView

    private var resourceIdGif = -1
    private var resourceIdTipStr = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_NoTitleBar)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.w6s_dialog_loading, null)
        findViews(view)
        registerListener()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(0))

        val gifDrawable = GifDrawable(resources, resourceIdGif)
        ivGif.setImageDrawable(gifDrawable)
        if(resourceIdTipStr.isNotEmpty()){
            tvTip.visibility = View.VISIBLE
            tvTip.text = resourceIdTipStr
        }else{
            tvTip.visibility = View.GONE
        }

    }

    private fun findViews(view: View) {
        llRoot = view.findViewById(R.id.ll_root)
        ivGif = view.findViewById(R.id.iv_gif)
        tvTip = view.findViewById(R.id.tv_tip)
    }

    fun setGif(resourceId: Int ): W6sLoadingDialog {
        resourceIdGif = resourceId
        return this
    }

    fun setTip(resourceId: Int): W6sLoadingDialog {
        setTip(getString(resourceId))
        return this
    }

    fun setTip(resourceIdTip: String): W6sLoadingDialog {
        this.resourceIdTipStr = resourceIdTip
        return this
    }

    fun show(activity: FragmentActivity?) {
        activity?.let { show(it.supportFragmentManager, UUID.randomUUID().toString()) }
    }

    override fun dismiss() {
        super.dismiss()

    }

    override fun setCancelable(cancelable: Boolean) {
        super.setCancelable(cancelable)
    }

    override fun changeStatusBar(view: View) {
        StatusBarUtil.setColorNoTranslucent(view as ViewGroup, dialog?.window, ContextCompat.getColor(view.context, R.color.transparent))

    }

    private fun registerListener() {
        if (isCancelable) {

            llRoot.setOnClickListener {
                dismiss()
            }
        }
    }


}