package com.foreveross.atwork.modules.device.manager

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import com.foreverht.threadGear.AsyncTaskThreadPool
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener
import com.foreveross.atwork.api.sdk.BaseNetWorkListener
import com.foreveross.atwork.api.sdk.device.LoginDeviceSyncNetService
import com.foreveross.atwork.api.sdk.device.model.request.ModifyLoginDeviceInfoRequest
import com.foreveross.atwork.api.sdk.device.model.response.QueryLoginDevicesInfoResponse
import com.foreveross.atwork.api.sdk.net.HttpResult
import com.foreveross.atwork.api.sdk.util.NetworkHttpResultErrorHandler
import com.foreveross.atwork.infrastructure.model.device.LoginDeviceInfo

object LoginDeviceManager: ILoginDeviceManager {


    /**
     * 查询登录设备列表
     * */
    @SuppressLint("StaticFieldLeak")
    override fun queryLoginDevices(context: Context, listener: BaseNetWorkListener<List<LoginDeviceInfo>>) {
        object : AsyncTask<Void, Void, HttpResult>() {

            override fun doInBackground(vararg params: Void?): HttpResult {
                return LoginDeviceSyncNetService.queryLoginDevices(context)
            }

            override fun onPostExecute(result: HttpResult) {
                if(result.isRequestSuccess) {
                    val queryLoginDevicesInfoResponse = result.resultResponse as QueryLoginDevicesInfoResponse
                    if(null != queryLoginDevicesInfoResponse.devices) {
                        listener.onSuccess(queryLoginDevicesInfoResponse.devices)
                        return
                    }
                }


                NetworkHttpResultErrorHandler.handleHttpError(result, listener)

            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())
    }

    /**
     * 设置设备免认证状态
     * */
    @SuppressLint("StaticFieldLeak")
    override fun setLoginDevicesWithoutAuthStatus(context: Context, ids: List<String>, listener: BaseCallBackNetWorkListener) {
        object : AsyncTask<Void, Void, HttpResult>() {

            override fun doInBackground(vararg params: Void?): HttpResult {
                return LoginDeviceSyncNetService.setLoginDevicesWithoutAuthStatus(context, ids)
            }

            override fun onPostExecute(result: HttpResult) {
                if(result.isRequestSuccess) {
                    listener.onSuccess()
                    return
                }


                NetworkHttpResultErrorHandler.handleHttpError(result, listener)

            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())

    }

    /**
     * 设置设备需要认证状态
     * */
    @SuppressLint("StaticFieldLeak")
    override fun setLoginDevicesNeedAuthStatus(context: Context, ids: List<String>, listener: BaseCallBackNetWorkListener) {

        object : AsyncTask<Void, Void, HttpResult>() {

            override fun doInBackground(vararg params: Void?): HttpResult {
                return LoginDeviceSyncNetService.setLoginDevicesNeedAuthStatus(context, ids)
            }

            override fun onPostExecute(result: HttpResult) {
                if(result.isRequestSuccess) {
                    listener.onSuccess()
                    return
                }


                NetworkHttpResultErrorHandler.handleHttpError(result, listener)

            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())


    }

    /**
     * 删除设备
     * */
    @SuppressLint("StaticFieldLeak")
    override fun removeLoginDevices(context: Context, ids: List<String>, listener: BaseCallBackNetWorkListener) {
        object : AsyncTask<Void, Void, HttpResult>() {

            override fun doInBackground(vararg params: Void?): HttpResult {
                return LoginDeviceSyncNetService.removeLoginDevices(context, ids)
            }

            override fun onPostExecute(result: HttpResult) {
                if(result.isRequestSuccess) {
                    listener.onSuccess()
                    return
                }


                NetworkHttpResultErrorHandler.handleHttpError(result, listener)

            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())

    }


    /**
     * 修改登录设备信息, 如设备名称
     * */
    @SuppressLint("StaticFieldLeak")
    override fun modifyLoginDeviceInfo(context: Context, modifyLoginDeviceInfoRequest: ModifyLoginDeviceInfoRequest, listener: BaseCallBackNetWorkListener) {

        object : AsyncTask<Void, Void, HttpResult>() {
            override fun doInBackground(vararg params: Void?): HttpResult {
                return LoginDeviceSyncNetService.modifyLoginDeviceInfo(context, modifyLoginDeviceInfoRequest)
            }

            override fun onPostExecute(result: HttpResult) {
                if(result.isRequestSuccess) {
                    listener.onSuccess()
                    return
                }


                NetworkHttpResultErrorHandler.handleHttpError(result, listener)

            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())
    }


}