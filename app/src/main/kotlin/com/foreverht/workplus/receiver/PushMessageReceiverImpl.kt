package com.foreverht.workplus.receiver

import android.content.Context
import android.util.Log
import com.vivo.push.model.UPSNotificationMessage
import com.vivo.push.sdk.OpenClientPushMessageReceiver

class PushMessageReceiverImpl: OpenClientPushMessageReceiver() {

    override fun onNotificationMessageClicked(context: Context?, message: UPSNotificationMessage?) {

    }


    override fun onReceiveRegId(context: Context?, regId: String?) {
        Log.e("vivo", "vivo regId = $regId")
    }

}