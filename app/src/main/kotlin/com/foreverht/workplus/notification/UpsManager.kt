package com.foreverht.workplus.notification

import android.app.Application
import android.text.TextUtils
import android.util.Log
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.infrastructure.utils.rom.RomUtil
import com.heytap.mcssdk.callback.PushCallback
import com.heytap.mcssdk.mode.SubscribeResult
import com.huawei.hms.push.HmsMessaging
import com.meizu.cloud.pushsdk.PushManager
import com.meizu.cloud.pushsdk.util.MzSystemUtils
import com.vivo.push.PushClient
import com.xiaomi.mipush.sdk.MiPushClient

class UpsManager {

    companion object UpsInstance{
        var instance : UpsManager = UpsManager()
    }

    fun startUpsPush(application: Application) {
        startHMSPush(application)
        startXMPush(application)
        startMzPush(application)
        startVivoPush(application)
        startOppoPush(application)
    }

    private fun startHMSPush(application: Application) {
        if (!RomUtil.isHuawei()) {
            return;
        }
        HmsMessaging.getInstance(application).isAutoInitEnabled = true
    }

    private fun startVivoPush(application: Application) {
        val pushClient = PushClient.getInstance(application.applicationContext)
        if (pushClient.isSupport) {
            pushClient.initialize ();
            pushClient.checkManifest()
            pushClient.turnOnPush {
                Log.e("vivo", "push regId = " + pushClient.regId )
                if (!TextUtils.isEmpty(pushClient.regId)) {
                    CommonShareInfo.setVIVOPushToken(AtworkApplicationLike.baseContext, pushClient.regId)
                }

            }

        }

    }

    private fun startXMPush(application: Application) {
        if (!RomUtil.isXiaomi()) {
            return
        }
        MiPushClient.registerPush(application, AtworkConfig.XIAOMI_PUSH_APP_ID, AtworkConfig.XIAOMI_PUSH_APP_KEY)
    }

    private fun startMzPush(application: Application) {
        if (!MzSystemUtils.isBrandMeizu(application)) {
            return
        }
        PushManager.register(application, AtworkConfig.MEIZU_PUSH_APP_ID, AtworkConfig.MEIZU_PUSH_APP_KEY)
    }


    private fun startOppoPush(application: Application) {
        if (!com.heytap.mcssdk.PushManager.isSupportPush(application)) {
            return
        }
        com.heytap.mcssdk.PushManager.getInstance().register(application, AtworkConfig.OPPO_PUSH_APP_ID, AtworkConfig.OPPO_PUSH_APP_KEY, object: PushCallback{
            override fun onGetPushStatus(p0: Int, p1: Int) {

            }

            override fun onSetPushTime(p0: Int, p1: String?) {
            }

            override fun onGetNotificationStatus(p0: Int, p1: Int) {
            }

            override fun onSetAliases(p0: Int, p1: MutableList<SubscribeResult>?) {
            }

            override fun onUnsetAliases(p0: Int, p1: MutableList<SubscribeResult>?) {
            }

            override fun onUnsetUserAccounts(p0: Int, p1: MutableList<SubscribeResult>?) {
            }

            override fun onGetAliases(p0: Int, p1: MutableList<SubscribeResult>?) {
            }

            override fun onUnsetTags(p0: Int, p1: MutableList<SubscribeResult>?) {
            }

            override fun onRegister(p0: Int, regId: String?) {
                if (TextUtils.isEmpty(regId)) {
                    return
                }
                CommonShareInfo.setOPPOPushToken(BaseApplicationLike.baseContext, regId)
            }

            override fun onSetUserAccounts(p0: Int, p1: MutableList<SubscribeResult>?) {
            }

            override fun onSetTags(p0: Int, p1: MutableList<SubscribeResult>?) {
            }

            override fun onGetUserAccounts(p0: Int, p1: MutableList<SubscribeResult>?) {
            }

            override fun onGetTags(p0: Int, p1: MutableList<SubscribeResult>?) {
            }

            override fun onUnRegister(p0: Int) {
            }

        })
    }

}