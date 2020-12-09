package com.foreverht.workplus.component

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.utils.DeviceUtil
import com.foreveross.atwork.utils.AtworkToast

class DeviceInfoDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_device_info, container)
        findView(view)
        return view
    }

    fun findView(view: View) {
        val deviceCodeTv = view.findViewById<TextView>(R.id.tv_device_code)
        val deviceNameTv = view.findViewById<TextView>(R.id.tv_device_name)
        val deviceTypeTv = view.findViewById<TextView>(R.id.tv_device_type)

        deviceCodeTv.text = context?.getString(R.string.device_code) + " ${AtworkConfig.DEVICE_ID}"
        deviceNameTv.text = context?.getString(R.string.device_name) + " ${DeviceUtil.getShowName()}"
        deviceTypeTv.text = context?.getString(R.string.device_type) + " Android ${Build.VERSION.RELEASE}"

        val copyTv = view.findViewById<TextView>(R.id.tv_copy)
        val cancelTv = view.findViewById<TextView>(R.id.tv_cancel)

        copyTv.setOnClickListener {
            val copyData: String =  "${deviceCodeTv.text}\n${deviceNameTv.text}\n${deviceTypeTv.text}"
            val cmb = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("newPlainTextLabel", copyData)
            cmb.setPrimaryClip(clipData)
            AtworkToast.showResToast(R.string.copy_success)
        }

        cancelTv.setOnClickListener {
            dismiss()
        }
    }

}