package com.foreveross.atwork.modules.device.manager

import android.content.Context
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener
import com.foreveross.atwork.api.sdk.BaseNetWorkListener
import com.foreveross.atwork.api.sdk.device.model.request.ModifyLoginDeviceInfoRequest
import com.foreveross.atwork.infrastructure.model.device.LoginDeviceInfo

interface ILoginDeviceManager {


    /**
     * 查询登录设备列表
     * */
    fun queryLoginDevices(context: Context, listener: BaseNetWorkListener<List<LoginDeviceInfo>>)


    /**
     * 设置设备免认证状态
     * */
    fun setLoginDevicesWithoutAuthStatus(context: Context, ids: List<String>, listener: BaseCallBackNetWorkListener)

    /**
     * 设置设备需要认证状态
     * */
    fun setLoginDevicesNeedAuthStatus(context: Context, ids: List<String>, listener: BaseCallBackNetWorkListener)



    /**
     * 删除设备
     * */
    fun removeLoginDevices(context: Context, ids: List<String>, listener: BaseCallBackNetWorkListener)

    /**
     * 修改登录设备信息, 如设备名称
     * */
    fun modifyLoginDeviceInfo(context: Context, modifyLoginDeviceInfoRequest: ModifyLoginDeviceInfoRequest, listener: BaseCallBackNetWorkListener)

}