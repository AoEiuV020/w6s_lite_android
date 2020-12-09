package com.foreveross.atwork.modules.common.fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.foreveross.atwork.R
import com.foreveross.atwork.component.BasicDialogFragment
import com.foreveross.atwork.infrastructure.utils.statusbar.StatusBarUtil
import kotlinx.android.synthetic.main.fragment_image_guide_page.*

class ImageGuidePageDialogFragment: BasicDialogFragment() {

    private var resGuide = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //必须在 onCreateView 之前
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_image_guide_page, null)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(0))

        registerListener()

        if(-1 != resGuide) {
            ivGuide.setImageResource(resGuide)
            ivGuide.isVisible = true
        }
    }

    fun setGuideImage(res: Int) {
        resGuide = res
    }

    private fun registerListener() {
        ivGuide.setOnClickListener { dismiss() }
    }

    override fun changeStatusBar(view: View) {
        StatusBarUtil.setTransparentFullScreen(dialog?.window)
    }
}