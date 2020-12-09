package com.foreveross.atwork.modules.workbench.fragment.admin

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.foreverht.workplus.ui.component.dialogFragment.CommonPopSelectData
import com.foreverht.workplus.ui.component.dialogFragment.W6sSelectDialogFragment
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener
import com.foreveross.atwork.component.ProgressDialogHelper
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCardType
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchGridType
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardDetailData
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchData
import com.foreveross.atwork.infrastructure.utils.DensityUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.infrastructure.utils.extension.asType
import com.foreveross.atwork.listener.TextWatcherAdapter
import com.foreveross.atwork.modules.workbench.manager.WorkbenchAdminManager
import com.foreveross.atwork.support.BackHandledPushOrPullFragment
import com.foreveross.atwork.utils.EditTextUtil
import com.foreveross.atwork.utils.ErrorHandleUtil
import kotlinx.android.synthetic.main.fragment_workbench_admin_card_modify.*
import kotlinx.android.synthetic.main.fragment_workbench_admin_card_modify.rlRoot
import kotlinx.android.synthetic.main.fragment_workbench_admin_card_modify.swPutaway
import kotlinx.android.synthetic.main.fragment_workbench_admin_card_modify.tvCancel
import kotlinx.android.synthetic.main.fragment_workbench_admin_card_modify.tvSure

class WorkbenchAdminModifyCardFragment: BackHandledPushOrPullFragment() {

    private lateinit var workbenchCardDetailData: WorkbenchCardDetailData
    private lateinit var workbenchData: WorkbenchData

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_workbench_admin_card_modify, container, false)
    }

    override fun getAnimationRoot(): View = llContentRoot

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val result = initData()

        if (result) {
            initViews()
            registerListener()
        }
    }

    override fun findViews(view: View) {

    }

    private fun initData(): Boolean {
        arguments?.getParcelable<WorkbenchCardDetailData>(WorkbenchCardDetailData::class.java.toString())?.let {
            workbenchCardDetailData = it
        }

        arguments?.getParcelable<WorkbenchData>(WorkbenchData::class.java.toString())?.let {
            workbenchData = it
        }

        if(!::workbenchCardDetailData.isInitialized) {
            toastOver(R.string.error_happened)
            finish()
            return false
        }

        if(!::workbenchData.isInitialized) {
            toastOver(R.string.error_happened)
            finish()
            return false
        }

        return true
    }

    private fun initViews() {
        etCardName.etInput.setText(workbenchCardDetailData.name)
        etCardEnName.etInput.setText(workbenchCardDetailData.enName)
        etCardTwName.etInput.setText(workbenchCardDetailData.twName)

        swPutaway.isChecked = !workbenchCardDetailData.disabled

        refreshSureBtnStatus()

        refreshDefinitionHandleView()



        EditTextUtil.setEditTextMaxStringLengthInput(etCardName.etInput, 50, true)
        EditTextUtil.setEditTextMaxStringLengthInput(etCardEnName.etInput, 50, true)
        EditTextUtil.setEditTextMaxStringLengthInput(etCardTwName.etInput, 50, true)
    }

    private fun refreshDefinitionHandleView() {
        when (WorkbenchCardType.parse(workbenchCardDetailData.type)) {
            WorkbenchCardType.APP_CONTAINER_0 -> {
                rlSelectGrid.background.asType<GradientDrawable>()?.run {
                    mutate()
                    setStroke(DensityUtil.dip2px(1F), ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.common_bg_gray))
                }

                tvGridName.text = getSelectGridTypeItem()

                llDefinitionHandleRoot.isVisible = true
                llDefinitionEntryGrid.isVisible = true
            }

            WorkbenchCardType.APP_CONTAINER_1 -> {
                rlSelectList.background.asType<GradientDrawable>()?.run {
                    mutate()
                    setStroke(DensityUtil.dip2px(1F), ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.common_bg_gray))
                }
                tvListName.text = getSelectListCountItem()

                llDefinitionHandleRoot.isVisible = true
                llDefinitionList.isVisible = true
            }
        }
    }

    private fun getSelectListCountItem() =
            workbenchCardDetailData.workbenchCardDisplayDefinitions?.listCount?.toString() ?: "3"

    private fun getSelectGridTypeItem(): String {
        val gridType = WorkbenchGridType.parse(workbenchCardDetailData.workbenchCardDisplayDefinitions?.entrySize
                ?: "1_4")
        return getSelectGridTypeItem(gridType)
    }

    private fun getSelectGridTypeItem(gridType: WorkbenchGridType): String {
        return when (gridType) {
            WorkbenchGridType.TYPE_1_4 -> getString(R.string.entry_type_gird_name, "1*4 ")
            WorkbenchGridType.TYPE_2_4 -> getString(R.string.entry_type_gird_name, "2*4 ")
            WorkbenchGridType.TYPE_N_4 -> getString(R.string.entry_type_gird_name, getString(R.string.common_no_limit))
        }
    }

    private fun registerListener() {
        tvCancel.setOnClickListener { onBackPressed() }

        rlRoot.setOnClickListener { onBackPressed() }

        swPutaway.setOnClickNotPerformToggle { swPutaway.toggle() }

        tvSure.setOnClickListener { doSureAction() }

        etCardName.setInputTextWatcher(object : TextWatcherAdapter() {
            override fun afterTextChanged(text: Editable?) {
                refreshSureBtnStatus()
            }
        })

        rlSelectList.setOnClickListener { popListTypeSelectDialog() }
        
        rlSelectGrid.setOnClickListener { popGridTypeSelectDialog() }

    }

    private fun popListTypeSelectDialog() {
        val dataList = arrayListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 15, 20).map { it.toString() }
        val selectData = getSelectListCountItem()

        val w6sSelectDialogFragment = W6sSelectDialogFragment()
        fragmentManager?.let {
            w6sSelectDialogFragment
                .setData(CommonPopSelectData(dataList, selectData))
                .setOnClickItemListener(object : W6sSelectDialogFragment.OnClickItemListener {
                    override fun onClick(position: Int, value: String) {
                        workbenchCardDetailData.workbenchCardDisplayDefinitions?.listCount = dataList[position].toInt()
                        refreshDefinitionHandleView()
                    }

                })
                .show(it, "w6sSelectDialogFragment")
        }
    }


    private fun popGridTypeSelectDialog() {
        val girdTypeList = arrayListOf(WorkbenchGridType.TYPE_1_4, WorkbenchGridType.TYPE_2_4)
        when (WorkbenchCardType.parse(workbenchCardDetailData.type)) {
            WorkbenchCardType.APP_CONTAINER_0-> {
                girdTypeList.add(WorkbenchGridType.TYPE_N_4)
            }
        }
        val dataList = girdTypeList.map { getSelectGridTypeItem(it) }

        val selectData = getSelectGridTypeItem()

        val w6sSelectDialogFragment = W6sSelectDialogFragment()
        fragmentManager?.let {
            w6sSelectDialogFragment
                .setData(CommonPopSelectData(dataList, selectData))
                .setOnClickItemListener(object : W6sSelectDialogFragment.OnClickItemListener {
                    override fun onClick(position: Int, value: String) {
                        workbenchCardDetailData.workbenchCardDisplayDefinitions?.entrySize = girdTypeList[position].toString()
                        refreshDefinitionHandleView()
                    }

                })
                .show(it, "w6sSelectDialogFragment")
        }
    }

    private fun doSureAction() {
        val progressDialogHelper = ProgressDialogHelper(activity)
        progressDialogHelper.show()

        workbenchCardDetailData.name = etCardName.text.toString()
        workbenchCardDetailData.enName = etCardEnName.text.toString()
        workbenchCardDetailData.twName = etCardTwName.text.toString()
        workbenchCardDetailData.disabled = !swPutaway.isChecked

        WorkbenchAdminManager.modifyCard(
                context = AtworkApplicationLike.baseContext,
                orgCode = workbenchCardDetailData.orgCode,
                workbenchCardDetailData = workbenchCardDetailData,
                listener = object : BaseCallBackNetWorkListener {
                    override fun onSuccess() {
                        progressDialogHelper.dismiss()

                        onBackPressed()
                        toastOver(R.string.setting_success)
                    }

                    override fun networkFail(errorCode: Int, errorMsg: String?) {
                        progressDialogHelper.dismiss()
                        ErrorHandleUtil.handleError(errorCode, errorMsg)
                    }

                }
        )
    }

    private fun refreshSureBtnStatus() {
        if (inputInfoLegal()) {
            tvSure.setTextColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.common_blue_bg))
            tvSure.isEnabled = true
        } else {
            tvSure.setTextColor(ContextCompat.getColor(AtworkApplicationLike.baseContext, R.color.common_text_color_999))
            tvSure.isEnabled = false
        }
    }


    private fun inputInfoLegal(): Boolean {
        if(StringUtils.isEmpty(etCardName.text)) {
            return false
        }

        return true
    }

}