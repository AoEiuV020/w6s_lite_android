package com.foreveross.atwork.infrastructure.model.workbench

import com.foreveross.atwork.infrastructure.model.i18n.I18nInfo
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardHeaderButtonData
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.google.gson.annotations.SerializedName

class WorkbenchCardHeaderButton(

        @SerializedName("type")
        var type: WorkbenchCardHeaderButtonType = WorkbenchCardHeaderButtonType.ICON,

        @SerializedName("icon")
        var icon: String? = null,

        @SerializedName("name")
        var name: String? = null,

        @SerializedName("en_name")
        var enName: String? = null,

        @SerializedName("tw_name")
        var twName: String? = null,

        @SerializedName("url")
        var url: String? = null

): I18nInfo() {

    override fun getStringName(): String? {
        return name
    }

    override fun getStringTwName(): String? {
        return twName
    }

    override fun getStringEnName(): String? {
        return enName
    }

    fun parse(workbenchCardHeaderButtonData: WorkbenchCardHeaderButtonData) {
        this.type = WorkbenchCardHeaderButtonType.parse(workbenchCardHeaderButtonData.type)
        this.icon = workbenchCardHeaderButtonData.icon
        this.name = workbenchCardHeaderButtonData.name
        this.twName = workbenchCardHeaderButtonData.twName
        this.enName = workbenchCardHeaderButtonData.enName
        this.url = workbenchCardHeaderButtonData.url
    }


    fun isLegal(): Boolean {
        if (WorkbenchCardHeaderButtonType.ICON == type) {
            if (StringUtils.isEmpty(icon)) {
                return false
            }

            return true
        }

        if (StringUtils.isEmpty(name)) {
            return false
        }

        return true
    }
}


enum class WorkbenchCardHeaderButtonType {
    TEXT,

    ICON;

    companion object {

        fun parse(typeStr: String?): WorkbenchCardHeaderButtonType {

            return when (typeStr?.toUpperCase()) {
                "TEXT_LINK" -> TEXT
                "ICON" -> ICON
                else -> ICON
            }


        }
    }

}