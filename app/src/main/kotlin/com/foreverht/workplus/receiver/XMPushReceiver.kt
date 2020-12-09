package com.foreverht.workplus.receiver

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo
import com.foreveross.atwork.infrastructure.utils.Logger
import com.foreveross.atwork.infrastructure.utils.ServiceCompat
import com.foreveross.atwork.modules.main.activity.MainActivity
import com.foreveross.atwork.modules.route.activity.SchemaRouteActivity
import com.foreveross.atwork.services.ImSocketService
import com.xiaomi.mipush.sdk.MiPushClient
import com.xiaomi.mipush.sdk.MiPushCommandMessage
import com.xiaomi.mipush.sdk.MiPushMessage
import com.xiaomi.mipush.sdk.PushMessageReceiver

class XMPushReceiver : PushMessageReceiver() {

    val TAG : String = "XMPushReceiver";

    lateinit var messageContent : String;

    //接收服务器发送的透传消息
    override fun onReceivePassThroughMessage(context: Context, message: MiPushMessage) {
        messageContent = message.content;
        Logger.d(TAG, messageContent);
    }

    //接收服务器发来的通知栏消息（消息到达客户端时触发，并且可以接收应用在前台时不弹出通知的通知消息），
    override fun onNotificationMessageArrived(context: Context, message: MiPushMessage) {
        ServiceCompat.startServiceCompat(context, ImSocketService::class.java)
        messageContent = message.content
        Logger.d(TAG, messageContent)
    }

    //接收服务器发来的通知栏消息（用户点击通知栏时触发）
    override fun onNotificationMessageClicked(context: Context, message: MiPushMessage) {
        messageContent = message.content
        Logger.d(TAG, messageContent)

        var extras = message.extra
        var intent = Intent()
        var bundles = Bundle()
        var from = extras.get("from")
        var type = extras.get("type")
        bundles.putString("type", type)
        if ("MEETING".equals(type)) {
            intent  = Intent(context, MainActivity::class.java)
        } else {
            intent  = Intent(context,  SchemaRouteActivity::class.java)
        }
        bundles.putString("from", from)
        bundles.putString("to", extras.get("to"))
        bundles.putString("display_name", extras.get("display_name"))
        bundles.putString("display", extras.get("display_avatar"))
        bundles.putString("route_url", extras.get("route_url"))
        intent.putExtras(bundles)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent);
    }

    override fun onCommandResult(context: Context, message: MiPushCommandMessage) {
        messageContent = message.command;
        Logger.d(TAG, messageContent);

    }

    override fun onReceiveRegisterResult(context: Context, message: MiPushCommandMessage) {
        messageContent = message.command;
        Logger.d(TAG, messageContent + " and regId = " + MiPushClient.getRegId(context));
        val pushToken = MiPushClient.getRegId(context);
        if (!TextUtils.isEmpty(pushToken)) {
            CommonShareInfo.setXiaomiPushToken(context, pushToken);
        }

    }

}