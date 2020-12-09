package com.foreveross.atwork.modules.device.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager
import com.foreveross.atwork.support.BackHandledFragment
import kotlinx.android.synthetic.main.fragment_login_device_auth.*

class LoginDeviceAuthNoMobileWarnFragment: BackHandledFragment() {


    private lateinit var tvTitle: TextView
    private lateinit var ivBack: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_device_no_mobile_warn, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        registerListener()
    }

    private fun initUI() {
        tvTitle.text = getStrings(R.string.title_login_device_auth)
        ivBack.setImageResource(R.mipmap.icon_remove_back)

        tvTip1.text = DomainSettingsManager.getInstance().loginDeviceUnAuthPrompt

        tvTip2.text = DomainSettingsManager.getInstance().loginDeviceRefuseAuthPrompt
    }

    private fun registerListener() {
        ivBack.setOnClickListener { onBackPressed() }

    }

    override fun findViews(view: View) {
        tvTitle = view.findViewById(R.id.title_bar_common_title)
        ivBack = view.findViewById(R.id.title_bar_common_back)
    }

    override fun onBackPressed(): Boolean {
        finish()
        return false
    }

}