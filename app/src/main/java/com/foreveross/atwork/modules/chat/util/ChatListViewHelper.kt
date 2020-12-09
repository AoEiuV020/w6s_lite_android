@file: JvmName("ChatListViewHelper")

package com.foreveross.atwork.modules.chat.util

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import com.foreverht.threadGear.DbThreadPoolExecutor
import com.foreverht.workplus.ui.component.dialogFragment.CommonPopSelectData
import com.foreverht.workplus.ui.component.dialogFragment.W6sSelectDialogFragment
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.R
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener
import com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model.ConversionConfigSettingParticipant
import com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model.request.ConversionConfigSettingRequest
import com.foreveross.atwork.infrastructure.model.Session
import com.foreveross.atwork.infrastructure.model.setting.BusinessCase
import com.foreveross.atwork.infrastructure.model.setting.ConfigSetting
import com.foreveross.atwork.infrastructure.model.setting.SourceType
import com.foreveross.atwork.modules.chat.dao.ChatDaoService
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap
import com.foreveross.atwork.modules.chat.model.TranslateLanguageType
import com.foreveross.atwork.modules.configSettings.manager.ConfigSettingsManager
import com.foreveross.atwork.modules.file.service.FileTransferService
import com.foreveross.atwork.utils.ErrorHandleUtil
import java.util.*


fun popChatItemListDialog(fragment: Fragment, session: Session, refreshViewTotally: () -> Unit?) {


    val itemList = ArrayList<String>()

    val bundle = Bundle()

    //群聊助手/内宣号/服务号订阅消息不用未读功能
    if (Session.WORKPLUS_DISCUSSION_HELPER != session.identifier
            && Session.COMPONENT_ANNOUNCE_APP != session.identifier
            && Session.WORKPLUS_SUMMARY_HELPER != session.identifier) {
        if (session.havingUnread()) {
            itemList.add(AtworkApplicationLike.getResourceString(R.string.menu_tag_read))

        } else {
            itemList.add(AtworkApplicationLike.getResourceString(R.string.menu_tag_unread))

        }
    }

    if (!session.isRemoteTop) {
        if (ChatSessionDataWrap.getInstance().isTop(session.identifier)) {
            itemList.add(AtworkApplicationLike.getResourceString(R.string.menu_cancel_top_chat))

        } else {
            itemList.add(AtworkApplicationLike.getResourceString(R.string.menu_top_chat))
        }



    }


    //文件助手/群聊助手/内宣号/服务号订阅消息不用免打扰相关功能
    if (Session.WORKPLUS_DISCUSSION_HELPER != session.identifier
            && Session.COMPONENT_ANNOUNCE_APP != session.identifier
            && !FileTransferService.needVariation(session.identifier)
            && Session.WORKPLUS_SUMMARY_HELPER != session.identifier) {

        if(ChatSessionDataWrap.getInstance().isShield(session.identifier)) {
            itemList.add(AtworkApplicationLike.getResourceString(R.string.close_notice_shield))

        } else {
            itemList.add(AtworkApplicationLike.getResourceString(R.string.open_notice_shield))

        }
    }

    if(!session.isRemoteTop) {
        //服务号订阅消息不用删除功能
        if(Session.WORKPLUS_SUMMARY_HELPER != session.identifier) {
            itemList.add(AtworkApplicationLike.getResourceString(R.string.menu_delete))
        }
    }


    val w6sSelectDialogFragment = W6sSelectDialogFragment()
    fragment.fragmentManager?.let {
        w6sSelectDialogFragment.setData(CommonPopSelectData(itemList, null))
                .setDialogWidth(148)
                .setOnClickItemListener(object : W6sSelectDialogFragment.OnClickItemListener{
                    override fun onClick(position: Int, item: String) {
                        if (TextUtils.isEmpty(item)) {
                            return
                        }
                        if (item == AtworkApplicationLike.getResourceString(R.string.menu_top_chat)) {

                            setTop(session, true, refreshViewTotally)
                            return

                        }

                        if (item == AtworkApplicationLike.getResourceString(R.string.menu_cancel_top_chat)) {
                            setTop(session, false, refreshViewTotally)
                            return

                        }



                        if(item == AtworkApplicationLike.getResourceString(R.string.open_notice_shield)) {

                            setShield(session, notifyEnabled = false, refreshViewTotally = refreshViewTotally)

                            return
                        }

                        if(item == AtworkApplicationLike.getResourceString(R.string.close_notice_shield)) {

                            setShield(session, notifyEnabled= true, refreshViewTotally = refreshViewTotally)

                            return
                        }


                        if (item == AtworkApplicationLike.getResourceString(R.string.menu_delete)) {
                            ChatSessionDataWrap.getInstance().removeSessionSafely(session.identifier)

                            if (Session.WORKPLUS_DISCUSSION_HELPER == session.identifier) {
//                PersonalShareInfo.getInstance().setHideDiscussionHelper(AtworkApplication.baseContext, true)

                                DbThreadPoolExecutor.getInstance().execute {
                                    ChatSessionDataWrap.getInstance().shieldDiscussionSessions
                                            .map {
                                                it.identifier
                                            }
                                            .let {
                                                ChatSessionDataWrap.getInstance().removeSessionsSync(it, true)

                                            }
                                }


                            }

                            if(Session.COMPONENT_ANNOUNCE_APP == session.identifier) {
                                DbThreadPoolExecutor.getInstance().execute {
                                    ChatSessionDataWrap.getInstance().announceAppSessions
                                            .map {
                                                it.identifier
                                            }
                                            .let {
                                                ChatSessionDataWrap.getInstance().removeSessionsSync(it, true)

                                            }
                                }

                            }

                        } else if (item == AtworkApplicationLike.getResourceString(R.string.menu_tag_read)) {
                            ChatSessionDataWrap.getInstance().emptySessionUnread(AtworkApplicationLike.baseContext, session)

                        } else if (item == AtworkApplicationLike.getResourceString(R.string.menu_tag_unread)) {
                            session.addFakeNoticeUnread()
                            ChatDaoService.getInstance().sessionRefresh(session)

                        }
                        refreshViewTotally()
                        SessionRefreshHelper.notifyRefreshCount()
                    }
                })
                .show(it, "SESSION_POP")
    }

}

private fun setShield(session: Session, notifyEnabled: Boolean, refreshViewTotally: () -> Unit?) {
    val conversationsRequest = ConversionConfigSettingRequest()
    val participant = ConversionConfigSettingParticipant(session.identifier, session.domainId, session.type)

    conversationsRequest.participant = participant
    conversationsRequest.notifyEnabled = notifyEnabled

    ConfigSettingsManager.setConversationSettingRemote(conversationsRequest, object : BaseCallBackNetWorkListener {
        override fun onSuccess() {
            val configSetting = ConfigSetting()
            configSetting.mSourceId = session.identifier
            configSetting.mSourceType = SourceType.valueOf(session.type)
            configSetting.mBusinessCase = BusinessCase.SESSION_SHIELD

            if (!notifyEnabled) {
                configSetting.mValue = 1
            } else {
                configSetting.mValue = 0

            }

            ConfigSettingsManager.setSessionSettingLocal(configSetting) { result ->
                refreshViewTotally()
                SessionRefreshHelper.notifyRefreshCount()
            }

        }

        override fun networkFail(errorCode: Int, errorMsg: String) {
            ErrorHandleUtil.handleError(errorCode, errorMsg)
        }
    })
}


private fun setTop(session: Session, setTop: Boolean, refreshViewTotally: () -> Unit?) {
    val conversationsRequest = ConversionConfigSettingRequest()
    val participant = ConversionConfigSettingParticipant(session)

    conversationsRequest.participant = participant
    conversationsRequest.stickyEnabled = setTop
    conversationsRequest.language = querySessionSettingTranslationLanguage(session)

    ConfigSettingsManager.setConversationSettingRemote(conversationsRequest, object : BaseCallBackNetWorkListener {
        override fun onSuccess() {
            setTopLocal(session, setTop, refreshViewTotally)
        }

        override fun networkFail(errorCode: Int, errorMsg: String) {
            ErrorHandleUtil.handleError(errorCode, errorMsg)
        }
    })
}

//获取本地session缓存的语种
private fun querySessionSettingTranslationLanguage(session: Session) : String {

    val configSetting = ConfigSetting()
    configSetting.mSourceId = session.identifier
    configSetting.mSourceType = SourceType.valueOf(session.type)
    configSetting.mBusinessCase = BusinessCase.SESSION_TRANSLATION

    var strTranslationShortName =""

    ConfigSettingsManager.querySessionSetting(configSetting) { configSettingReceived ->
        if ( null != configSettingReceived) {
            if (1 == configSettingReceived.mValue) {
                strTranslationShortName =  TranslateLanguageType.TranslateLanguage.getTranslateLanguageShortName(configSettingReceived.mValue);
            } else {
                strTranslationShortName = ""
            }
        }
    }
    return strTranslationShortName
}


private fun setTopLocal(session: Session, top: Boolean, refreshViewTotally: () -> Unit?) {
    val configSetting = ConfigSetting(session.identifier, SourceType.valueOf(session.type), BusinessCase.SESSION_TOP)

    if (top) {
        configSetting.mValue = 1
    } else {
        configSetting.mValue = 0

    }

    ConfigSettingsManager.setSessionSettingLocal(configSetting) { result ->

        refreshViewTotally()
        SessionRefreshHelper.notifyRefreshCount()

    }

}

