package com.foreveross.atwork.modules.route.action

import android.content.Context
import android.content.Intent
import com.foreverht.db.service.repository.UnreadMessageRepository
import com.foreveross.atwork.api.sdk.UrlConstantManager
import com.foreveross.atwork.infrastructure.model.Session
import com.foreveross.atwork.infrastructure.model.SessionType
import com.foreveross.atwork.infrastructure.model.WebViewControlAction
import com.foreveross.atwork.infrastructure.newmessage.post.notify.OrgNotifyMessage
import com.foreveross.atwork.infrastructure.support.AtworkConfig
import com.foreveross.atwork.manager.OrganizationManager
import com.foreveross.atwork.modules.app.activity.WebViewActivity
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap
import com.foreveross.atwork.modules.chat.service.ChatService
import com.foreveross.atwork.modules.chat.util.SessionRefreshHelper
import com.foreveross.atwork.modules.organization.activity.OrgApplyingActivity
import com.foreveross.atwork.modules.route.model.RouteParams
import java.util.*
import java.util.concurrent.Executors

/**
 *  create by reyzhang22 at 2019-10-29
 */
class ApplyRouteAction(routeParams: RouteParams?) : RouteAction(routeParams) {

    override fun action(context: Context) {

        OrganizationManager.getInstance().queryLoginAdminOrg(context, object: OrganizationManager.OnQueryLoginAdminOrgSyncListener{
            override fun onSuccess(orgCodeList: MutableList<String>) {
                if (orgCodeList == null) {
                    return;
                }
                val intentJump: Intent
                if (orgCodeList.size > 1) {
                    intentJump = OrgApplyingActivity.getIntent(context)
                } else {
                    //跳转到网页审批
                    if (orgCodeList.size == 0) {
                        return
                    }
                    val orgCode = orgCodeList.get(0)
                    val url = String.format(UrlConstantManager.getInstance().applyOrgsUrl, orgCode, AtworkConfig.DOMAIN_ID)
                    val webViewControlAction =
                            WebViewControlAction.newAction().setUrl(url).setNeedShare(false)
                    intentJump = WebViewActivity.getIntent(context, webViewControlAction)

                    Executors.newSingleThreadExecutor().execute {
                        val session = Session()
                        session.identifier = routeParams?.getFrom()
                        session.mDomainId = AtworkConfig.DOMAIN_ID
                        session.type = SessionType.toType(routeParams?.getType())

                        ChatService.sendSessionReceiptsSync(context, session, HashSet(UnreadMessageRepository.getInstance().queryUnreadOrgApplyCountByOrgCode(orgCode)))

                        UnreadMessageRepository.getInstance().removeUnreadOrg(orgCode)
                        ChatSessionDataWrap.getInstance().getSessionSafely(OrgNotifyMessage.FROM, null).clearUnread()
                        SessionRefreshHelper.notifyRefreshSessionAndCount()
                    }

                }
                context.startActivity(intentJump)
            }

        })
    }
}