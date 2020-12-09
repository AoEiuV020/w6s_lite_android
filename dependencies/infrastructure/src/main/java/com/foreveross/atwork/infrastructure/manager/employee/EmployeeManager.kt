package com.foreveross.atwork.infrastructure.manager.employee

import com.foreveross.atwork.infrastructure.plugin.WorkplusPluginCore
import com.foreveross.atwork.infrastructure.plugin.employee.IEmployeeManager

class EmployeeManager {
    companion object {

        @JvmStatic
        fun getInstance(): IEmployeeManager {
            return WorkplusPluginCore.getPluginAndCheckRegisterInstance(IEmployeeManager::class.java, "com.foreveross.atwork.manager.EmployeeManager")
        }


    }

}