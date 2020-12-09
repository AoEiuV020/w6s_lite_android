package com.foreveross.atwork.modules.lite.fragment

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.utils.extension.asType
import com.foreveross.atwork.infrastructure.utils.extension.getParcelableDirectly
import com.foreveross.atwork.modules.lite.manager.LiteManager
import com.foreveross.atwork.modules.lite.module.LiteBindConfig
import com.foreveross.atwork.modules.main.service.HandleLoginService
import com.foreveross.atwork.support.BackHandledFragment
import com.foreveross.atwork.utils.AtworkToast
import kotlinx.android.synthetic.main.fragment_lite_bind_scan.*

class LiteBindScanFragment : BackHandledFragment() {

    private lateinit var liteBindConfig: LiteBindConfig

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_lite_bind_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initUI()
        registerListener()
    }

    override fun findViews(view: View) {

    }


    private fun initData() {
        arguments?.getParcelableDirectly<LiteBindConfig>()?.let {
            liteBindConfig = it
        }
    }

    private fun initUI() {
        tvBindNotifyContent.text = getStrings(R.string.bind_lite_config, liteBindConfig.domainName)

        tvCancel.background.asType<GradientDrawable>()?.mutate()?.alpha = 127
    }

    private fun registerListener() {
        tvCancel.setOnClickListener { onBackPressed() }

        tvOK.setOnClickListener {
            LiteManager.updateBindConfig(liteBindConfig);
            AtworkToast.showResToast(R.string.bind_successfully)

            AtworkApplicationLike.clearData()
            HandleLoginService.toLoginHandle(activity, null, false)
        }

    }

    override fun onBackPressed(): Boolean {
        finish()
        return false
    }
}