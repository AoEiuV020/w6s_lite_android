package com.foreveross.atwork.modules.workbench.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.app.AppBundles
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCard
import com.foreveross.atwork.modules.workbench.model.WorkbenchAppContainerItemDataWrapper
import com.foreveross.atwork.utils.AppIconHelper
import kotlinx.android.synthetic.main.component_workbench_item_app_container_1.view.*

class WorkbenchAppContainer1ItemView: RelativeLayout{

    var workbenchCard: WorkbenchCard? = null
    lateinit var ivAppRemoveView: ImageView

    constructor(context: Context?) : super(context) {
        findViews()
        initViews()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        findViews()
        initViews()
    }


    private fun findViews() {
        LayoutInflater.from(context).inflate(R.layout.component_workbench_item_app_container_1, this)
        this.ivAppRemoveView = ivAppRemove

    }


    private fun initViews() {


    }

    fun refreshView(itemDataWrapper: WorkbenchAppContainerItemDataWrapper, workbenchCard: WorkbenchCard?) {
        if(null == itemDataWrapper.appBundle) {
            ivAppIcon.setImageResource(R.mipmap.icon_item_workbench_card_app_add)
            tvTitle.text = AtworkApplicationLike.getResourceString(R.string.admin_click_to_set)
            tvTitle.setTextColor(ContextCompat.getColor(context, R.color.common_text_color_999))

            ivAppRemove.isVisible = false


        } else {
            ivAppRemove.isVisible = workbenchCard?.adminDisplay ?: false
            refreshView(itemDataWrapper.appBundle!!)
        }
    }

    fun refreshView(appBundle: AppBundles) {
        tvTitle.text = appBundle.getTitleI18n(BaseApplicationLike.baseContext)
        AppIconHelper.setAppIcon(context, appBundle, ivAppIcon, true)
        tvTitle.setTextColor(ContextCompat.getColor(context, R.color.common_text_color))

    }
}