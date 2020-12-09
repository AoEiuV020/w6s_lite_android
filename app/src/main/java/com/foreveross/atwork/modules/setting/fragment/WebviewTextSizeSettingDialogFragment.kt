package com.foreveross.atwork.modules.setting.fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.foreverht.workplus.ui.component.dialogFragment.BasicUIDialogFragment
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.infrastructure.webview.AtworkWebView
import com.foreveross.atwork.modules.setting.util.getWebviewTextSizeLevel
import com.foreveross.atwork.modules.setting.util.getWebviewTextSizeSetNative
import com.foreveross.atwork.utils.AtworkToast
import kotlinx.android.synthetic.main.fragment_webview_textsize_setting.*

class WebviewTextSizeSettingDialogFragment: BasicUIDialogFragment() {

    private var currentTextSizeLevel: Int = -1

    var atworkWebView: AtworkWebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //必须在 onCreateView 之前
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_webview_textsize_setting, null)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(0))

        currentTextSizeLevel = getWebviewTextSizeLevel()
        vTextsizeSettingBar.setThumbIndices(currentTextSizeLevel, currentTextSizeLevel)

        registerListener()
    }

    private fun registerListener() {
        rlRoot.setOnClickListener { dismiss() }

        vTextsizeSettingBar.setOnTouchListener { v, event ->

            if(PersonalShareInfo.getInstance().getCommonTextSizeSyncWebview(AtworkApplicationLike.baseContext)) {
                AtworkToast.showResToast(R.string.webview_text_size_modify_warn_tip)
                return@setOnTouchListener true

            }


            return@setOnTouchListener false

        }


        vTextsizeSettingBar.setOnRangeBarChangeListener { rangeBar, leftThumbIndex, rightThumbIndex ->



            currentTextSizeLevel = leftThumbIndex
            vTextsizeSettingBar.setThumbIndices(currentTextSizeLevel, currentTextSizeLevel)

            PersonalShareInfo.getInstance().setWebviewTextSizeLevel(AtworkApplicationLike.baseContext, currentTextSizeLevel)

            atworkWebView?.changeTextSize(getWebviewTextSizeSetNative())
        }
    }

}