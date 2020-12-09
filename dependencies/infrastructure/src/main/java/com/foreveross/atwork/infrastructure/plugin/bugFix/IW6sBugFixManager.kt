package com.foreveross.atwork.infrastructure.plugin.bugFix

import com.foreveross.atwork.infrastructure.plugin.WorkplusPlugin

interface IW6sBugFixManager: WorkplusPlugin {


    /**
     * 此处为了让 setting 表的business_case_也设为关联 key, 敏捷版本因为处于3.14.0, 数据库版本是17, 而最新主
     * 干的数据库版本是20, 为了兼容后续的整个升级流程, 此处不用升级数据库的方法来修改字段, 后续优化升级数据库流程
     * 代码来避免这种情况出现
     * */
    fun fixedSettingDbPrimaryKeyInMinjieVersion()


    /**
     * 把旧版本的session 置顶以及勿打扰迁移到 setting 表去
     * */
    fun fixedSessionTopAndShieldData(): Boolean


    /**
     * 强制更新下应用数据
    */
    fun fixedForcedCheckAppRefresh()

    /**
     * 指定的组织是否需要做应用刷新检查
     * */
    fun isNeedForcedCheckAppRefresh(orgCode: String) : Boolean
}