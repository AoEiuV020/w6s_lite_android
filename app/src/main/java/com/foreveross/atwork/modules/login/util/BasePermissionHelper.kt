package com.foreveross.atwork.modules.login.util

import android.content.Context
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.modules.login.listener.OnAskPermissionListener
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission

class BasePermissionHelper {
    companion object {

        private fun getPermissionsConfig2(): ArrayList<String?>?{
            val permissions: ArrayList<String?> = ArrayList()
            LogUtil.e("abcd", BeeWorks.getInstance().basePermissions.size.toString())
            for(basePermission in BeeWorks.getInstance().basePermissions){
                if(basePermission.enabled){
                    permissions.add(basePermission.item)
                }
            }
            return permissions
        }

        /**
         * 通过AtworkConfig配置的
         */
        @Deprecated("换成用BeeWorks配置文件进行配置基础权限", ReplaceWith("getPermissionsConfig2()"))
        private fun getPermissionsConfig(): ArrayList<String?>? {
            val permissions: ArrayList<String?> = ArrayList()
            if (AtworkConfig.BASE_PERMISSION_CONFIG.isNeedPermissionExternalStorage) {
                permissions.add(Permission.WRITE_EXTERNAL_STORAGE)
            }
            if (AtworkConfig.BASE_PERMISSION_CONFIG.isNeedPermissionCamera) {
                permissions.add(Permission.CAMERA)
            }
            if (AtworkConfig.BASE_PERMISSION_CONFIG.isNeedPermissionReadPhoneState) {
                permissions.add(Permission.READ_PHONE_STATE)
            }
            return permissions
        }

        /**
         * 申请一系列基础权限
         */
        @JvmStatic
        fun requestPermissions(context: Context?, onAskPermissionListener: OnAskPermissionListener) {
            val permissions = getPermissionsConfig2()
            if (permissions!!.size == 0) {
                onAskPermissionListener.onNext()
                return
            }
            for (i in permissions.indices) {
                when {
                    (permissions[i] == "android.permission.WRITE_EXTERNAL_STORAGE") ->
                        requestStoragePermission(context, i == permissions.size - 1, onAskPermissionListener)

                    (i == permissions.size - 1) -> {
                        askPermission(
                                context,
                                object : OnAskPermissionListener {
                                    override fun onNext() {
                                        onAskPermissionListener.onNext()
                                    }
                                },
                                permissions[i]!!
                        )
                    }
                    else -> {
                        try{
                            AndPermission
                                    .with(context)
                                    .runtime()
                                    .permission(permissions[i])
                                    .start()
                        }catch (e: Exception){
                            e.printStackTrace()
                        }

                    }
                }
            }
        }

        private fun requestStoragePermission(context: Context?, isLast: Boolean, onAskPermissionListener: OnAskPermissionListener) {
            askPermission(
                    context,
                    object : OnAskPermissionListener {
                        override fun onNext() {
                            if (isLast) {
                                onAskPermissionListener.onNext()
                            }
                        }
                    },
                    Permission.WRITE_EXTERNAL_STORAGE,
                    Permission.READ_EXTERNAL_STORAGE
            )
        }
        private fun askPermission(context: Context?, onAskPermissionListener: OnAskPermissionListener, vararg permissions: String) {
            try{
                AndPermission
                        .with(context)
                        .runtime()
                        .permission(*permissions)
                        .onGranted { onAskPermissionListener.onNext() }
                        .onDenied { onAskPermissionListener.onNext() }
                        .start()
            }catch (e: Exception){
                e.printStackTrace()
            }

        }
    }

}