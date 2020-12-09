package com.foreverht.workplus.ui.component.dialogFragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.foreverht.workplus.ui.component.R
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView
import java.util.*


interface IWorkplusLoadingDialog {
    fun setGif(resourceId: Int)
    fun setTip(resourceId: Int)
    fun show(activity: FragmentActivity)
    fun dismiss()
}

class WorkplusLoadingDialog: BasicUIDialogFragment(), IWorkplusLoadingDialog {


    private lateinit var llRoot: LinearLayout
    private lateinit var ivGif: GifImageView
    private lateinit var tvTip: TextView

    private var resourceIdGif = -1
    private var resourceIdTip = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar)

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_loading_new, null)
        findViews(view)
        registerListener()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(0))

        val gifDrawable = GifDrawable(resources, resourceIdGif)
        ivGif.setImageDrawable(gifDrawable)
        tvTip.setText(resourceIdTip)
    }


    private fun findViews(view: View) {
        llRoot = view.findViewById(R.id.ll_root)
        ivGif = view.findViewById(R.id.iv_gif)
        tvTip = view.findViewById(R.id.tv_tip)
    }

    override fun setGif(resourceId: Int ) {
        resourceIdGif = resourceId

    }

    override fun setTip(resourceId: Int) {
        resourceIdTip = resourceId
    }

    override fun show(activity: FragmentActivity) {
        show(activity.supportFragmentManager, UUID.randomUUID().toString())
    }

    override fun dismiss() {
        super.dismiss()

    }

    override fun changeStatusBar(view: View) {
        StatusBarUtil.setColorNoTranslucent(view as ViewGroup, dialog?.window, ContextCompat.getColor(view.context, R.color.transparent))

    }

    private fun registerListener() {
        llRoot.setOnClickListener { dismiss() }
    }


}