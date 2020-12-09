package com.foreveross.atwork.infrastructure.support

import java.util.*

class EmployeeConfig: BaseConfig() {

    /**
     * 显示职位信息(非自己)
     */
    var showPeerJobTitle = true


    /**
     * 显示职位信息(自己)
     */
    var showSelfJobTitle = true


    /**
     * 显示雇员信息(个人资料底部的雇员信息)
     * */
    var showEmpInfo = true

    /**
     * 是否能修改空信息
     * */
    var canModifyPropertyEmpty = false


    override fun parse(pro: Properties) {
        pro.getProperty("EMPLOYEE_SHOW_PEER_JOB_TITLE")?.apply {
            showPeerJobTitle = toBoolean()
        }


        pro.getProperty("EMPLOYEE_SHOW_SELF_JOB_TITLE")?.apply {
            showSelfJobTitle = toBoolean()
        }


        pro.getProperty("EMPLOYEE_SHOW_INFO")?.apply {
            showEmpInfo = toBoolean()
        }


        pro.getProperty("EMPLOYEE_CAN_MODIFY_PROPERTY_EMPTY")?.apply {
            canModifyPropertyEmpty = toBoolean()
        }


    }

    override fun parse() {

    }
}