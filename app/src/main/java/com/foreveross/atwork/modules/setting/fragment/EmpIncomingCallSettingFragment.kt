package com.foreveross.atwork.modules.setting.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.shared.EmpIncomingCallShareInfo
import com.foreveross.atwork.infrastructure.utils.AppUtil
import com.foreveross.atwork.infrastructure.utils.rom.FloatWindowPermissionUtil
import com.foreveross.atwork.support.BackHandledFragment
import kotlinx.android.synthetic.main.fragment_emp_incoming_call_setting.*

/**
 *  create by reyzhang22 at 2019-08-21
 */
class EmpIncomingCallSettingFragment: BackHandledFragment() {

    private lateinit var tvTitle: TextView
    private lateinit var ivBack: ImageView

    private var isGoingToPermissionPage = false;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_emp_incoming_call_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData();
        registerListener();
    }

    override fun findViews(view: View) {
        tvTitle = view.findViewById(R.id.title_bar_common_title)
        ivBack = view.findViewById(R.id.title_bar_common_back)

    }

    private fun initData() {
        tvTitle.text = getStrings(R.string.emp_incoming_call_assistant)

        refreshUI()
    }


    private fun registerListener() {
        ivBack.setOnClickListener { onBackPressed() }

        llOpenEmpIncomingCallLayout.setOnClickListener {

            if (!FloatWindowPermissionUtil.isFloatWindowOpAllowed(mActivity)) {
                AtworkAlertDialog(mActivity).setBrightBtnText(R.string.go_to_setting)
                        .setContent(String.format(getString(R.string.emp_assistant_permission_content), AppUtil.getAppName(mActivity)))
                        .setClickBrightColorListener {
                            isGoingToPermissionPage = true
                            FloatWindowPermissionUtil.requestFloatPermission(this)
                        }.setType(AtworkAlertDialog.Type.SIMPLE).show()

            } else {
                EmpIncomingCallShareInfo.getInstance().setEmpIncomingCallAssistantSetting(AtworkApplicationLike.baseContext, true)
                refreshUI()
            }

        }

        llCloseEmpIncomingCallLayout.setOnClickListener {
            EmpIncomingCallShareInfo.getInstance().setEmpIncomingCallAssistantSetting(AtworkApplicationLike.baseContext, false)
            refreshUI()
        }

    }

    override fun onResume() {
        super.onResume()
        object: Handler() {}.postDelayed({
            if (isGoingToPermissionPage && FloatWindowPermissionUtil.isFloatWindowOpAllowed(mActivity)) {
                EmpIncomingCallShareInfo.getInstance().setEmpIncomingCallAssistantSetting(AtworkApplicationLike.baseContext, true)
                refreshUI()
                isGoingToPermissionPage = false;
            }
        }, 1000)

    }

    private fun refreshUI() {
        if(EmpIncomingCallShareInfo.getInstance().getEmpIncomingCallAssistantSetting(AtworkApplicationLike.baseContext)) {
            ivOpenEmpIncoming.setImageResource(R.mipmap.icon_sync_msgs_setting_select)
            ivCloseEmpIncoming.setImageResource(R.mipmap.icon_sync_msgs_setting_unselect)
            return
        }

        ivOpenEmpIncoming.setImageResource(R.mipmap.icon_sync_msgs_setting_unselect)
        ivCloseEmpIncoming.setImageResource(R.mipmap.icon_sync_msgs_setting_select)
    }


    override fun onBackPressed(): Boolean {
        finish()
        return true
    }

}