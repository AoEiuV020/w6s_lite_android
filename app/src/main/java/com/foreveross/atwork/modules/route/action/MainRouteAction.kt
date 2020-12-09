package com.foreveross.atwork.modules.route.action

import android.content.Context
import android.content.Intent
import com.foreveross.atwork.R
import com.foreveross.atwork.modules.login.activity.BasicLoginActivity
import com.foreveross.atwork.modules.main.activity.MainActivity
import com.foreveross.atwork.modules.main.service.HandleLoginService
import com.foreveross.atwork.support.BaseActivity

class MainRouteAction : RouteAction() {

    override fun action(context: Context) {
        val schemaActionIntent = HandleLoginService.getInstance().getSchemaRouteIntent(context)
        val mainActionIntent = getActionIntent(context)

        if(null != schemaActionIntent) {
            val intents = arrayOf(mainActionIntent, schemaActionIntent)
            context.startActivities(intents)

            HandleLoginService.getInstance().clearSchemaRouteAction()

            if (context is BaseActivity) {
                context.clearActionIntent()
            }


        } else {
            context.startActivity(mainActionIntent)

            if(context is BaseActivity) {
                context.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left)
            }
        }

        BaseActivity.triggerFinishChain(BasicLoginActivity.TAG_FINISH_LOGIN)

    }

    override fun getActionIntent(context: Context): Intent {
        return Intent(context, MainActivity::class.java)
    }
}