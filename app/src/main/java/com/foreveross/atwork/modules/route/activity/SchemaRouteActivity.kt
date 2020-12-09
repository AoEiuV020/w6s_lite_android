package com.foreveross.atwork.modules.route.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage.DISPLAY_AVATAR
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage.DISPLAY_NAME
import com.foreveross.atwork.infrastructure.newmessage.post.NotifyPostMessage.OPERATION
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.modules.chat.activity.ChatDetailActivity.TYPE
import com.foreveross.atwork.modules.main.service.HandleLoginService
import com.foreveross.atwork.modules.route.manager.RouteActionConsumer
import com.foreveross.atwork.modules.route.manager.RouteActionFactory
import com.foreveross.atwork.modules.route.model.RouteParams

class SchemaRouteActivity : Activity() {

    companion object {

        @JvmStatic
        fun route(context: Context, uriStr: String) {
            val intent = getRouteIntent(context, uriStr)

            context.startActivity(intent)
        }

        @JvmStatic
        fun getRouteIntent(context: Context, uriStr: String): Intent {
            val intent = Intent(context, SchemaRouteActivity::class.java)
            intent.data = Uri.parse(uriStr)
            return intent
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val routeParams = buildRouteParams()
        if(null == routeParams) {
            finish()
            return
        }


        if (StringUtils.isEmpty(LoginUserInfo.getInstance().getLoginUserAccessToken(this))) {
            HandleLoginService.toLoginHandle(this, null, false)
            HandleLoginService.getInstance().schemaRouteAction = RouteActionFactory.produce(routeParams)
            finish()
            return
        }

        RouteActionConsumer.route(this, routeParams)
        finish()

    }


    private fun buildRouteParams(): RouteParams? {
        var routeParams = decodeUri()

        if (routeParams == null && intent != null) {
            routeParams = decodeIntent()
        }
        return routeParams
    }

    private fun decodeIntent(): RouteParams? {
        if (intent == null) {
            return null
        }
        return RouteParams.newRouteParams().apply {
            type = intent.getStringExtra(TYPE)
            from = if (!TextUtils.isEmpty(type) && type.equals("discussion", ignoreCase = true)) intent.getStringExtra(PostTypeMessage.TO) else intent.getStringExtra(PostTypeMessage.FROM)
            to = if (!TextUtils.isEmpty(type) && type.equals("discussion", ignoreCase = true)) intent.getStringExtra(PostTypeMessage.FROM) else intent.getStringExtra(PostTypeMessage.TO)
            displayName = intent.getStringExtra(DISPLAY_NAME)
            displayAvatar = intent.getStringExtra(DISPLAY_AVATAR)
            operation = intent.getStringExtra(OPERATION)
            targetUrl = intent.getStringExtra("route_url")
        }.build()
    }

    private fun decodeUri(): RouteParams? {
        val uri = intent.data ?: return null

        return RouteParams.newRouteParams().apply {
            type = uri.getQueryParameter(TYPE)
            from = if (!TextUtils.isEmpty(type) && type.equals("discussion", ignoreCase = true)) uri.getQueryParameter(PostTypeMessage.TO) else uri.getQueryParameter(PostTypeMessage.FROM)
            to = if (!TextUtils.isEmpty(type) && type.equals("discussion", ignoreCase = true)) uri.getQueryParameter(PostTypeMessage.FROM) else uri.getQueryParameter(PostTypeMessage.TO)
            displayName = uri.getQueryParameter(DISPLAY_NAME)
            displayAvatar = uri.getQueryParameter(DISPLAY_AVATAR)
            operation = uri.getQueryParameter(OPERATION)
            targetUrl = uri.getQueryParameter("route_url")
        }.build()
    }



}