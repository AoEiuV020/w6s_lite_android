package com.foreveross.atwork.infrastructure.model.workbench

import com.foreveross.atwork.infrastructure.model.i18n.I18nInfo
import com.foreveross.atwork.infrastructure.model.workbench.data.WorkbenchCardSubModuleData
import com.google.gson.annotations.SerializedName

class WorkbenchCardSubModule(

        @SerializedName("type")
        var type: WorkbenchCardSubModuleType = WorkbenchCardSubModuleType.TEXT,

        @SerializedName("icon")
        var icon: String? = null,

        @SerializedName("name")
        var name: String? = null,

        @SerializedName("en_name")
        var enName: String? = null,

        @SerializedName("tw_name")
        var twName: String? = null,

        @SerializedName("widget_id")
        var widgetId: Long? = null

) : I18nInfo() {

    override fun getStringName(): String? {
        return name
    }

    override fun getStringTwName(): String? {
        return twName
    }

    override fun getStringEnName(): String? {
        return enName
    }

    fun parse(workbenchCardSubModuleData: WorkbenchCardSubModuleData) {
        this.type = WorkbenchCardSubModuleType.parse(workbenchCardSubModuleData.type)
        this.icon = workbenchCardSubModuleData.icon
        this.name = workbenchCardSubModuleData.name
        this.twName = workbenchCardSubModuleData.twName
        this.enName = workbenchCardSubModuleData.enName
        this.widgetId = workbenchCardSubModuleData.widgetId
    }
}