package com.foreveross.atwork.modules.workbench.component

import android.Manifest
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCardType
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchSearchCard
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.ViewUtil
import com.foreveross.atwork.modules.qrcode.activity.QrcodeScanActivity
import com.foreveross.atwork.modules.search.activity.NewSearchActivity
import com.foreveross.atwork.modules.search.model.NewSearchControlAction
import com.foreveross.atwork.modules.search.model.SearchContent
import com.foreveross.atwork.modules.voip.utils.VoipHelper
import com.foreveross.atwork.modules.workbench.manager.WorkbenchManager
import com.foreveross.atwork.utils.AtworkToast
import com.foreveross.atwork.utils.AtworkUtil
import kotlinx.android.synthetic.main.component_workbench_custom_icon_or_text_view.view.*
import kotlinx.android.synthetic.main.component_workbench_search_card.view.*

class WorkbenchSearchCardView: RelativeLayout, IWorkbenchCardRefreshView<WorkbenchSearchCard> {

    override lateinit var workbenchCard: WorkbenchSearchCard



    constructor(context: Context?) : super(context) {
        findViews()
        initViews()
        registerListener()
    }


    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        findViews()
        initViews()
        registerListener()
    }


    private fun findViews() {
        LayoutInflater.from(context).inflate(R.layout.component_workbench_search_card, this)

    }

    private fun initViews() {
        vWorkbenchCustomTextOrIconViewLeft.isVisible = false

        vWorkbenchCustomTextOrIconViewRight1.switchType(WorkbenchCustomTextOrIconViewType.ICON)
        vWorkbenchCustomTextOrIconViewRight1.vWorkbenchCustomTextOrIconViewRight1.ivCustomIcon.setImageResource(R.mipmap.icon_scan_new)
        ViewUtil.setSize(vWorkbenchCustomTextOrIconViewRight1.vWorkbenchCustomTextOrIconViewRight1.ivCustomIcon, DensityUtil.dip2px(23f), DensityUtil.dip2px(23f))
        vWorkbenchCustomTextOrIconViewRight1.isVisible = true

    }

    private fun registerListener() {
        doRegisterListener()

    }

    private fun doRegisterListener() {
        tvSearch.setOnClickListener {

            WorkbenchManager.handleClickAction(workbenchCard) {
                routeSearch()
            }

        }


        vWorkbenchCustomTextOrIconViewRight1.setOnClickListener {
            WorkbenchManager.handleClickAction(workbenchCard) {
                handleClickCustomTextOrIconViewRight1()
            }

        }
    }

    private fun handleClickCustomTextOrIconViewRight1() {
        if (VoipHelper.isHandlingVoipCall()) {
            AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip)
            return
        }


        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(context as Activity, arrayOf(Manifest.permission.CAMERA), object : PermissionsResultAction() {
            override fun onGranted() {
                val intent = QrcodeScanActivity.getIntent(context)
                context.startActivity(intent)
            }

            override fun onDenied(permission: String) {
                AtworkUtil.popAuthSettingAlert(context, permission)
            }
        })
    }

    private fun routeSearch() {
        val newSearchControlAction = NewSearchControlAction()

        val searchContentList = mutableListOf(SearchContent.SEARCH_USER
                , SearchContent.SEARCH_DISCUSSION
                , SearchContent.SEARCH_APP
                , SearchContent.SEARCH_MESSAGES)

        if (DomainSettingsManager.getInstance().handleFileAssistantEnable()) {
            searchContentList.add(SearchContent.SEARCH_DEVICE)

        }

        newSearchControlAction.searchContentList = searchContentList.toTypedArray()

        val intent = NewSearchActivity.getIntent(context, newSearchControlAction)
        context.startActivity(intent)
    }

    override fun refresh(workbenchCard: WorkbenchSearchCard) {
        this.workbenchCard = workbenchCard
    }

    override fun refreshView(workbenchCard: WorkbenchSearchCard) {

    }

    override fun getViewType(): Int = WorkbenchCardType.SEARCH.hashCode()

}