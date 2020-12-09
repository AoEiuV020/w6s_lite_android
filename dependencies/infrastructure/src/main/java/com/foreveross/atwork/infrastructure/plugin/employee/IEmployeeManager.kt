package com.foreveross.atwork.infrastructure.plugin.employee

import android.content.Context
import com.foreveross.atwork.infrastructure.model.Employee
import com.foreveross.atwork.infrastructure.plugin.WorkplusPlugin

interface IEmployeeManager: WorkplusPlugin {

    fun queryEmpInSync(context: Context, userId: String, orgCode: String): Employee?

    fun queryLoginUserEmailList(getEmailList: (emails: List<String>?)-> Unit)

}