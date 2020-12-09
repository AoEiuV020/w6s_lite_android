package com.foreveross.atwork.modules.workbench.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCardHeaderButton
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCardHeaderButtonType
import com.foreveross.atwork.utils.ImageCacheHelper
import com.foreveross.atwork.utils.WorkplusTextSizeChangeHelper
import kotlinx.android.synthetic.main.component_workbench_custom_icon_or_text_view.view.*

class WorkbenchCustomTextOrIconView : FrameLayout {

    constructor(context: Context) : super(context) {
        findViews()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        findViews()
    }

    private fun findViews() {
        LayoutInflater.from(context).inflate(R.layout.component_workbench_custom_icon_or_text_view, this)

        WorkplusTextSizeChangeHelper.handleViewEnlargedTextSizeStatus(ivCustomIcon)

    }


    fun switchType(workbenchCustomTextOrIconViewType: WorkbenchCustomTextOrIconViewType) {
        when (workbenchCustomTextOrIconViewType) {
            WorkbenchCustomTextOrIconViewType.TEXT -> {
                ivCustomIcon.visibility = View.GONE
                tvCustomText.visibility = View.VISIBLE
            }

            WorkbenchCustomTextOrIconViewType.ICON -> {
                ivCustomIcon.visibility = View.VISIBLE
                tvCustomText.visibility = View.GONE
            }
        }
    }


    fun refreshView(headerButton: WorkbenchCardHeaderButton) {
        when (headerButton.type) {
            WorkbenchCardHeaderButtonType.ICON -> {
                refreshIconView(headerButton)

                switchType(WorkbenchCustomTextOrIconViewType.ICON)

            }
            WorkbenchCardHeaderButtonType.TEXT -> {
                tvCustomText.text = headerButton.getNameI18n(context)
                switchType(WorkbenchCustomTextOrIconViewType.TEXT)
            }
        }


    }

    private fun refreshIconView(headerButton: WorkbenchCardHeaderButton) {
        var isHttpUrl = false
        headerButton.icon?.startsWith("http")?.let {
            isHttpUrl = it
        }

        if (isHttpUrl) {
            ImageCacheHelper.displayImage(headerButton.icon, ivCustomIcon, ImageCacheHelper.getRectOptions(R.mipmap.appstore_loading_icon_size))
        } else {
            ImageCacheHelper.displayImageByMediaId(headerButton.icon, ivCustomIcon, ImageCacheHelper.getRectOptions(R.mipmap.appstore_loading_icon_size))
        }
    }


}