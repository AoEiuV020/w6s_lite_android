package com.foreverht.workplus.receiver

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.foreveross.atwork.R
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo
import com.foreveross.atwork.modules.chat.activity.ChatDetailActivity
import com.foreveross.atwork.modules.main.activity.MainActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.meizu.cloud.pushsdk.MzPushMessageReceiver
import com.meizu.cloud.pushsdk.handler.MzPushMessage
import com.meizu.cloud.pushsdk.notification.PushNotificationBuilder
import com.meizu.cloud.pushsdk.platform.message.*

class MzPushReceiver: MzPushMessageReceiver() {
    override fun onRegister(contex: Context?, pushToken: String?) {

    }

    override fun onSubTagsStatus(contex: Context?, status: SubTagsStatus?) {

    }

    override fun onRegisterStatus(contex: Context?, status: RegisterStatus?) {
        Log.e("MZPush", "mz push token = " + status?.pushId);
        CommonShareInfo.setMeiZuPushToken(contex, status?.pushId)
    }

    override fun onUnRegisterStatus(contex: Context?, status: UnRegisterStatus?) {
    }

    override fun onSubAliasStatus(contex: Context?, alias: SubAliasStatus?) {
    }

    override fun onUnRegister(contex: Context?, isUnRegister: Boolean) {
    }

    override fun onPushStatus(contex: Context?, status: PushSwitchStatus?) {
    }

    override fun onUpdateNotificationBuilder(pushNotificationBuilder: PushNotificationBuilder?) {
        pushNotificationBuilder?.setmStatusbarIcon(R.mipmap.icon_notice_small)
    }

    override fun onNotificationClicked(context: Context?, mzMessage: MzPushMessage?) {
        Log.e("MZPush", "content = " + mzMessage?.content + " selfDefineContentString = " + mzMessage?.selfDefineContentString)
        val extras: HashMap<String, String> = Gson().fromJson(mzMessage?.selfDefineContentString, object: TypeToken<HashMap<String, String>>(){}.type)
        var intent = Intent()
        var bundles = Bundle()
        var from = extras?.get("from")
        var type = extras?.get("type")
        bundles.putString("type", type)
        if ("MEETING".equals(type)) {
            intent  = Intent(context, MainActivity::class.java)
        } else {
            intent  = Intent(context,  ChatDetailActivity::class.java)
        }
        bundles.putString("from", from)
        bundles.putString("to", extras?.get("to"))
        bundles.putString("display_name", extras?.get("display_name"))
        bundles.putString("display", extras?.get("display_avatar"))
        Log.e("MZPush", "display_name = " + extras?.get("display_name"))
        intent.putExtras(bundles)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(intent);

    }
}