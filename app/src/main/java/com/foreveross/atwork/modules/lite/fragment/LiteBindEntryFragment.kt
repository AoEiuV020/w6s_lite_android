package com.foreveross.atwork.modules.lite.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foreveross.atwork.R
import com.foreveross.atwork.modules.qrcode.activity.QrcodeScanActivity
import com.foreveross.atwork.support.BackHandledFragment
import com.foreveross.atwork.utils.AtworkUtil
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import kotlinx.android.synthetic.main.fragment_lite_bind_entry.*

class LiteBindEntryFragment: BackHandledFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_lite_bind_entry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerListener()
    }

    override fun findViews(view: View) {

    }

    private fun registerListener() {

        tvEntry.setOnClickListener {
            AndPermission
                    .with(activity)
                    .runtime()
                    .permission(Permission.CAMERA)
                    .onGranted {
                        val intent = QrcodeScanActivity.getIntent(context)
                        startActivity(intent)
                    }
                    .onDenied { data: List<String?> -> AtworkUtil.popAuthSettingAlert(context, data[0], false) }
                    .start()
        }
    }

    override fun onBackPressed(): Boolean {
        finish()
        return false
    }
}