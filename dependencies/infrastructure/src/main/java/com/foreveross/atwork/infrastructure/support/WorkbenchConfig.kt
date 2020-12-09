package com.foreveross.atwork.infrastructure.support

import java.util.*

class WorkbenchConfig: BaseConfig() {

    var isNeedCustomBtn = true

    var isNeedInitJump = true

    var isNeedTitleBtnIfNoData = true

    var noDataViewHeight = -1f

    var needGuideByImage = false

    override fun parse(pro: Properties) {

        pro.getProperty("WORKBENCH_NEED_CUSTOM_BTN")?.apply {
            isNeedCustomBtn = toBoolean()
        }


        pro.getProperty("WORKBENCH_NEED_NEED_INIT_JUMP")?.apply {
            isNeedInitJump = toBoolean()
        }

        pro.getProperty("WORKBENCH_NEED_TITLE_BTN_IF_NO_DATA")?.apply {
            isNeedTitleBtnIfNoData = toBoolean()
        }

        pro.getProperty("WORKBENCH_NO_DATA_VIEW_HEIGHT")?.apply {
            noDataViewHeight = toFloat()
        }


        pro.getProperty("WORKBENCH_NEED_GUIDE_BY_IMAGE")?.apply {
            needGuideByImage = toBoolean()
        }

    }
}