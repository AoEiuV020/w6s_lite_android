@file:JvmName("TemplateHelper")

package com.foreveross.atwork.modules.chat.util


import android.content.Context
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.infrastructure.model.Session
import com.foreveross.atwork.infrastructure.model.WebViewControlAction
import com.foreveross.atwork.infrastructure.model.app.App
import com.foreveross.atwork.infrastructure.model.app.AppBundles
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TemplateMessage
import com.foreveross.atwork.infrastructure.utils.chat.TemplateDataHelper
import com.foreveross.atwork.modules.app.manager.AppManager
import com.foreveross.atwork.modules.app.route.routeUrl
import com.foreveross.atwork.utils.ErrorHandleUtil

fun routeWebview(context: Context, templateActions: TemplateMessage.TemplateActions, templateDatas: List<TemplateMessage.TemplateData>, session: Session) {

    AppManager.getInstance().queryApp(AtworkApplicationLike.baseContext, session.identifier, session.orgId, object : AppManager.GetAppFromMultiListener {
        override fun onSuccess(app: App) {

            doRouteWebview(context, app.mainBundle, templateActions, templateDatas, session)

        }


        override fun networkFail(errorCode: Int, errorMsg: String?) {
            ErrorHandleUtil.handleError(errorCode, errorMsg)

            doRouteWebview(context, null , templateActions, templateDatas, session)

        }

    })


}


private fun doRouteWebview(context: Context, appBundle: AppBundles?, templateActions: TemplateMessage.TemplateActions, templateDatas: List<TemplateMessage.TemplateData>, session: Session) {

    val actionSchema = getActionUrl(templateActions.mValue, templateDatas)
    val action = WebViewControlAction.newAction()
            .setUrl(actionSchema)
            .setNeedClose(true)
            .setNeedShare(true)
            .setNeedChangeStatusBar(true)

    if (true == appBundle?.isLightAppBundle) {
        action.setLightApp(appBundle)
    }

    routeUrl(context, action)
}


private fun getActionUrl(actionValue: String, templateDatas: List<TemplateMessage.TemplateData>): String {
    return if (!actionValue.contains(TemplateDataHelper.INDEX_DATA)) {
        actionValue
    } else getReplacedActionValue(actionValue, templateDatas)
}

private fun getReplacedActionValue(content: String, templateDatas: List<TemplateMessage.TemplateData>): String {
    var content = content
    for (templateData in templateDatas) {
        val key = StringBuilder().append(TemplateDataHelper.INDEX_PREFIX).append(templateData.mKey).append(TemplateDataHelper.INDEX_DATA).toString()
        if (!content.contains(key)) {
            continue
        }
        content = content.replace(key, templateData.mValue)
//        break
    }
    return content
}