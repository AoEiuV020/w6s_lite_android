package com.foreverht.workplus.receiver

import android.text.TextUtils
import android.util.Log
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo
import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage


class HuaweiPushReceiver : HmsMessageService {
    constructor() : super()

    override fun onMessageReceived(message: RemoteMessage?) {
        if (message == null) {
            return
        }
        val notification = message.notification

    }

//    override fun onToken(context: Context, token: String) {
//        super.onToken(context, token)
//        Logger.i("HMS", "token =" + token);
//        CommonShareInfo.setHuaweiPushToken(context, token)
//    }
//
//    override fun onPushMsg(context: Context, msg: ByteArray, token: String) {
//        super.onPushMsg(context, msg, token)
//        if (!TextUtils.isEmpty(token)) {
//            CommonShareInfo.setHuaweiPushToken(context, token)
//        }
//        Logger.i("HMS", "onPushMsg content = " + String(msg))
//    }
//
//    override fun onEvent(context: Context, event: Event, extras: Bundle) {
////        ServiceCompat.startServiceCompat(context, ImSocketService::class.java)
//        super.onEvent(context, event, extras)
//        Logger.e("hello", extras.toString());
//    }

    override fun onNewToken(token: String?) {
        if (!TextUtils.isEmpty(token)) {
            Log.i("HMS", "push token = $token")
            CommonShareInfo.setHuaweiPushToken(AtworkApplicationLike.baseContext, token)
        }
    }
}