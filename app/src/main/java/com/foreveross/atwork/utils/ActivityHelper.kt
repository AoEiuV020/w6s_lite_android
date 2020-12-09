package com.foreveross.atwork.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.foreveross.atwork.R
import com.foreveross.atwork.support.AtworkBaseActivity

fun Context.startActivityByPushAnimation(intent: Intent) {
    if(this is AtworkBaseActivity) {
        startActivity(intent, false)

    } else {
        startActivity(intent)
    }

    if(this is Activity) {
        //界面切换效果
        overridePendingTransition(R.anim.in_from_bottom, 0)
    }


}


fun Context.startActivityByNoAnimation(intent: Intent) {
    if(this is AtworkBaseActivity) {
        startActivity(intent, false)

    } else {
        startActivity(intent)
    }

    if(this is Activity) {
        //界面切换效果
        overridePendingTransition(0, 0)
    }


}

fun Activity.startActivityForResultByNoAnimation(intent: Intent, requestCode: Int) {
    if(this is AtworkBaseActivity) {
        startActivityForResult(intent, requestCode, false)

    } else {
        startActivityForResult(intent, requestCode)
    }

    overridePendingTransition(0, 0)
}


fun Fragment.startActivityForResultByNoAnimation(intent: Intent, requestCode: Int) {
    startActivityForResult(intent, requestCode)
    this.activity?.overridePendingTransition(0, 0)
}

