package com.foreveross.atwork.modules.configSettings.manager

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import com.foreverht.db.service.repository.ConfigSettingRepository
import com.foreverht.threadGear.AsyncTaskThreadPool
import com.foreverht.threadGear.DbThreadPoolExecutor
import com.foreverht.threadGear.HighPriorityCachedTreadPoolExecutor
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener
import com.foreveross.atwork.api.sdk.BaseNetWorkListener
import com.foreveross.atwork.api.sdk.configSetting.conversationSetting.ConversionConfigSettingSyncNetService
import com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model.ConversionConfigSettingItem
import com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model.ConversionConfigSettingParticipant
import com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model.request.ConversionConfigSettingRequest
import com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model.response.ConversionConfigSettingResponse
import com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model.response.ConversionConfigSettingsResponse
import com.foreveross.atwork.api.sdk.configSetting.userSetting.UserConfigSettingSyncNetService
import com.foreveross.atwork.api.sdk.configSetting.userSetting.model.UserConfigChatSettings
import com.foreveross.atwork.api.sdk.configSetting.userSetting.model.UserConfigSettings
import com.foreveross.atwork.api.sdk.configSetting.userSetting.model.UserConfigSettingsResponse
import com.foreveross.atwork.api.sdk.net.HttpResult
import com.foreveross.atwork.api.sdk.users.UserSyncNetService
import com.foreveross.atwork.api.sdk.util.NetworkHttpResultErrorHandler
import com.foreveross.atwork.infrastructure.model.Session
import com.foreveross.atwork.infrastructure.model.setting.BusinessCase
import com.foreveross.atwork.infrastructure.model.setting.ConfigSetting
import com.foreveross.atwork.infrastructure.model.setting.SourceType
import com.foreveross.atwork.infrastructure.newmessage.post.notify.conversation.ConversationNotifyMessage
import com.foreveross.atwork.infrastructure.newmessage.post.notify.conversation.ConversationSettingsChangedConversationNotifyMessage
import com.foreveross.atwork.infrastructure.newmessage.post.notify.conversation.ConversationSettingsResetConversationNotifyMessage
import com.foreveross.atwork.infrastructure.newmessage.post.notify.user.UserNotifyMessage
import com.foreveross.atwork.infrastructure.newmessage.post.notify.user.UserSettingsChangedUserNotifyMessage
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.infrastructure.shared.bugFix.W6sBugFixPersonShareInfo
import com.foreveross.atwork.infrastructure.utils.JsonUtil
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.infrastructure.utils.StringUtils
import com.foreveross.atwork.manager.listener.BaseQueryListener
import com.foreveross.atwork.modules.bugFix.manager.W6sBugFixCoreManager
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap
import com.foreveross.atwork.modules.chat.model.TranslateLanguageType
import com.foreveross.atwork.modules.chat.util.SessionRefreshHelper

/**
 * Created by dasunsy on 2017/3/31.
 */

object ConfigSettingsManager: IConfigSettingsManager {

    var syncingConversationConfigSettings = false
    var syncingUserConfigSettings = false

    override fun clear() {
        syncingConversationConfigSettings = false
        syncingUserConfigSettings = false
    }

    /**
     * 同步会话跟用户的设置项(消息勿扰, 群聊助手等)
     * */
    override fun syncConfigSettings() {

        syncConversationConfigSettings()
        syncUserConfigSettings()
    }

    @SuppressLint("StaticFieldLeak")
    private fun syncConversationConfigSettings() {
        if(syncingConversationConfigSettings) {
            return
        }

        val context = AtworkApplicationLike.baseContext
        if(notNeedSyncConversationConfigSettings(context)) {
           return
        }

        syncingConversationConfigSettings = true

        object : AsyncTask<Void, Void, HttpResult?>() {
            override fun doInBackground(vararg params: Void?): HttpResult? {
                val result = W6sBugFixCoreManager.fixedSessionTopAndShieldData()
//                if(!result) {
//                    return null
//                }

                return syncConversationConfigSettingsSync()
            }

            override fun onPostExecute(result: HttpResult?) {
                if (true == result?.isRequestSuccess) {
                    SessionRefreshHelper.notifyRefreshSession()
                }

                syncingConversationConfigSettings = false

            }

        }.executeOnExecutor(HighPriorityCachedTreadPoolExecutor.getInstance())
    }

    private fun notNeedSyncConversationConfigSettings(context: Context) =
            W6sBugFixPersonShareInfo.hasMakeCompatibleForSessionTopAndShield(context) && LoginUserInfo.getInstance().getLoginSyncStatus(context).mConversationSettingResult

    fun syncConversationConfigSettingsSync(): HttpResult {
        val result = ConversionConfigSettingSyncNetService.getConversationsSetting(AtworkApplicationLike.baseContext)

        if (result.isRequestSuccess) {
            val conversionConfigSettingsResponse = result.resultResponse as ConversionConfigSettingsResponse
            val conversionConfigSettingList: List<ConversionConfigSettingItem>? = conversionConfigSettingsResponse.result?.conversations


            if (!ListUtil.isEmpty(conversionConfigSettingList)) {
                //                        ChatSessionDataWrap.getInstance().clearSessionConfigMap()
                //                        ConfigSettingRepository.batchRemoveConfigSettings(arrayListOf(BusinessCase.SESSION_TOP.toString(), BusinessCase.SESSION_SHIELD.toString()))

                val configSettings = parse(conversionConfigSettingList)

                setSessionSettingLocal(configSettings) {
                    if (it) {
                        LoginUserInfo.getInstance().setConversionSettingSyncStatus(AtworkApplicationLike.baseContext, true)
                    }
                }

            } else {
                LoginUserInfo.getInstance().setConversionSettingSyncStatus(AtworkApplicationLike.baseContext, true)
            }


        }


        return result
    }

    @SuppressLint("StaticFieldLeak")
    private fun syncUserConfigSettings() {
        if(syncingUserConfigSettings) {
            return
        }

        if(LoginUserInfo.getInstance().getLoginSyncStatus(AtworkApplicationLike.baseContext).mUserSettingResult) {
            return
        }

        syncingUserConfigSettings = true

        object : AsyncTask<Void, Void, HttpResult>() {
            override fun doInBackground(vararg params: Void?): HttpResult {
                val result = UserConfigSettingSyncNetService.getUserSettings(AtworkApplicationLike.baseContext)



                return result
            }

            override fun onPostExecute(result: HttpResult) {

                if (result.isRequestSuccess) {
                    val response = result.resultResponse as UserConfigSettingsResponse
                    val userConfigChatSettings: UserConfigChatSettings? = response.result?.chatSetting

                    userConfigChatSettings?.let {
                        PersonalShareInfo.getInstance().setSettingDiscussionHelper(AtworkApplicationLike.baseContext, it.chatAssistantEnabled)
                        LoginUserInfo.getInstance().setUserSettingSyncStatus(AtworkApplicationLike.baseContext, true)

                        SessionRefreshHelper.notifyRefreshSession()
                    }



                }

                syncingUserConfigSettings = false


            }

        }.executeOnExecutor(HighPriorityCachedTreadPoolExecutor.getInstance())

    }

    private fun parse(conversionConfigSettingList: List<ConversionConfigSettingItem>?): ArrayList<ConfigSetting> {
        val configSettings = arrayListOf<ConfigSetting>()
        for (conversionConfigSettingItem in conversionConfigSettingList!!) {
            val configSettingShield = ConfigSetting(conversionConfigSettingItem.participant?.getSessionIdFromClientId(), SourceType.valueStringOf(conversionConfigSettingItem.participant?.type), BusinessCase.SESSION_SHIELD)
            val configSettingTop = ConfigSetting(conversionConfigSettingItem.participant?.clientId, SourceType.valueStringOf(conversionConfigSettingItem.participant?.type), BusinessCase.SESSION_TOP)
            val configSettingTranslation = ConfigSetting(conversionConfigSettingItem.participant?.clientId, SourceType.valueStringOf(conversionConfigSettingItem.participant?.type), BusinessCase.SESSION_TRANSLATION)
            if (true == conversionConfigSettingItem.notifyEnabled) {
                configSettingShield.mValue = 0
            } else {
                configSettingShield.mValue = 1
            }


            if (true == conversionConfigSettingItem.stickyEnabled) {
                configSettingTop.mValue = 1
            } else {
                configSettingTop.mValue = 0
            }
            if (!StringUtils.isEmpty(conversionConfigSettingItem.language)) {
                configSettingTranslation.mValue = TranslateLanguageType.TranslateLanguage.getTranslateLanguageValue(conversionConfigSettingItem.language)

            } else {
                configSettingTranslation.mValue = TranslateLanguageType.TranslateLanguage.NO.value
            }

            configSettings.add(configSettingShield)
            configSettings.add(configSettingTop)
            configSettings.add(configSettingTranslation)
        }
        return configSettings
    }


    /**
     * 提交用户设置(群聊助手等)
     *
     * @param context
     * @param userSettings
     * @param listener
     * */
    @SuppressLint("StaticFieldLeak")
    override fun setUserSettings(context: Context, userSettings: UserConfigSettings, listener: BaseCallBackNetWorkListener) {
        object : AsyncTask<Void, Void, HttpResult>() {
            override fun doInBackground(vararg params: Void?): HttpResult {
                return UserConfigSettingSyncNetService.setUserSettings(context, userSettings)
            }

            override fun onPostExecute(result: HttpResult) {
                if(result.isRequestSuccess) {
                    listener.onSuccess()
                    return
                }

                NetworkHttpResultErrorHandler.handleHttpError(result, listener)


            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())
    }

    fun setDevicesMode(context: Context?, silently: Boolean, listener: BaseCallBackNetWorkListener) {
        object : AsyncTask<Void, Void, HttpResult>() {
            override fun doInBackground(vararg params: Void?): HttpResult {
                return UserConfigSettingSyncNetService.setDeviceSettings(context, silently)
            }

            override fun onPostExecute(result: HttpResult) {
                if (result.isNetSuccess) {
                    listener.onSuccess()
                    return
                }
                NetworkHttpResultErrorHandler.handleHttpError(result, listener)
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())
    }

    fun logoutPc(context: Context?, listener: BaseCallBackNetWorkListener) {
        object: AsyncTask<Void, Void, HttpResult>() {
            override fun doInBackground(vararg params: Void?): HttpResult {
                return UserConfigSettingSyncNetService.logoutPc(context)
            }

            override fun onPostExecute(result: HttpResult) {
                if (result.isNetSuccess) {
                    listener.onSuccess()
                    return
                }
                NetworkHttpResultErrorHandler.handleHttpError(result, listener)
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())
    }

    @SuppressLint("StaticFieldLeak")
    override fun getConfigSettingFromDb(sourceId: String, sourceType: SourceType, businessCase: BusinessCase, listener: BaseQueryListener<ConfigSetting?>) {
        object : AsyncTask<Void, Void, ConfigSetting?>() {
            override fun doInBackground(vararg params: Void): ConfigSetting? {
                return ConfigSettingRepository.getConfigSetting(sourceId, sourceType, businessCase)
            }

            override fun onPostExecute(configSetting: ConfigSetting?) {
                listener.onSuccess(configSetting)
            }
        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())
    }



    @SuppressLint("StaticFieldLeak")
    override fun insertOrUpdateConfigSetting(configSetting: ConfigSetting) {
        object : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg params: Void): Void? {
                ConfigSettingRepository.insertOrUpdateConfigSetting(configSetting)
                return null
            }
        }.executeOnExecutor(DbThreadPoolExecutor.getInstance())
    }




    /**
     * 用户设置更改的通知处理
     * */
    override fun receiveSettingChangedUserNotifyMessage(userNotifyMessage: UserNotifyMessage, online: Boolean) {
        when(userNotifyMessage) {
            is UserSettingsChangedUserNotifyMessage -> {

                userNotifyMessage.userSettings?.chatSettings?.chatAssistantEnabled?.let {
                    PersonalShareInfo.getInstance().setSettingDiscussionHelper(AtworkApplicationLike.baseContext, it)
                    if (online) {
                        SessionRefreshHelper.notifyRefreshSession()
                    }
                }

            }
        }
    }

    /**
     * 用户设置更改的通知处理
     * */
    override fun receiveConversationSettingChangedNotifyMessage(conversationnotifymessage: ConversationNotifyMessage, online: Boolean) {
        when(conversationnotifymessage) {
            is ConversationSettingsChangedConversationNotifyMessage -> {
                val configSettings = parse(conversationnotifymessage)

                val result = setSessionSettingLocalSync(configSettings)
                if(result && online) {
                    SessionRefreshHelper.notifyRefreshSession()
                }


            }


            is ConversationSettingsResetConversationNotifyMessage -> {
                LoginUserInfo.getInstance().setConversionSettingSyncStatus(AtworkApplicationLike.baseContext, false)
                syncConversationConfigSettings()
            }
        }
    }

    private fun parse(conversationNotifyMessage: ConversationSettingsChangedConversationNotifyMessage): ArrayList<ConfigSetting> {
        val notifyEnabled = conversationNotifyMessage.conversationSettings?.notifyEnabled
        val stickyEnabled = conversationNotifyMessage.conversationSettings?.stickyEnabled
        val clientId = conversationNotifyMessage.conversationSettings?.participant?.clientId
        val domainId = conversationNotifyMessage.conversationSettings?.participant?.domainId
        val type = conversationNotifyMessage.conversationSettings?.participant?.type

        val configSettings = arrayListOf<ConfigSetting>()

        if (null != notifyEnabled) {
            val configSettingShield = ConfigSetting(ConversionConfigSettingParticipant.getSessionIdFromClientId(clientId), SourceType.valueStringOf(type), BusinessCase.SESSION_SHIELD)
            if (notifyEnabled) {
                configSettingShield.mValue = 0
            } else {
                configSettingShield.mValue = 1

            }

            configSettings.add(configSettingShield)

        }


        if (null != stickyEnabled) {
            val configSettingTop = ConfigSetting(ConversionConfigSettingParticipant.getSessionIdFromClientId(clientId), SourceType.valueStringOf(type), BusinessCase.SESSION_TOP)
            if (stickyEnabled) {
                configSettingTop.mValue = 1
            } else {
                configSettingTop.mValue = 0

            }

            configSettings.add(configSettingTop)


        }
        return configSettings
    }

    @SuppressLint("StaticFieldLeak")
    override fun setSessionSettingLocal(configSetting: ConfigSetting, finishHandle: ((Boolean) -> Unit)?) {


        object : AsyncTask<Void, Void, Boolean>() {
            override fun doInBackground(vararg params: Void): Boolean {
                //保存到缓存中
                ChatSessionDataWrap.getInstance().insertSettingCheck(configSetting)
                //保存到本地数据库中
                return ConfigSettingRepository.insertOrUpdateConfigSetting(configSetting)
            }

            override fun onPostExecute(result: Boolean) {
                finishHandle?.invoke(result)
            }

        }.executeOnExecutor(DbThreadPoolExecutor.getInstance())


    }

    @SuppressLint("StaticFieldLeak")
    override fun setSessionSettingLocal(configSettings: List<ConfigSetting>, finishHandle: ((Boolean) -> Unit)?) {
        object : AsyncTask<Void, Void, Boolean>() {
            override fun doInBackground(vararg params: Void): Boolean {
                return setSessionSettingLocalSync(configSettings)
            }

            override fun onPostExecute(result: Boolean) {
                finishHandle?.invoke(result)
            }

        }.executeOnExecutor(DbThreadPoolExecutor.getInstance())

    }


    override fun setSessionSettingLocalSync(configSettings: List<ConfigSetting>): Boolean {
        ChatSessionDataWrap.getInstance().insertSettingCheck(configSettings)
        return ConfigSettingRepository.batchInsertOrUpdateConfigSetting(configSettings)
    }

    //查询本地session设置
    @SuppressLint("StaticFieldLeak")
    override fun querySessionSetting(querySetting: ConfigSetting, onGetSetting: (ConfigSetting?) -> Unit) {

        //根据sourceId和要查询的类型（智能翻译）获取本地缓存的ConfigSetting。如果为空，则异步查询；否则，将缓存的数据返回
        val configSettingCache: ConfigSetting? = ChatSessionDataWrap.getInstance().getSetting(querySetting.mSourceId, querySetting.mBusinessCase)
        if(null != configSettingCache) {
            onGetSetting(configSettingCache)
            return
        }
        //如果在ChatSessionDataWrap的本地缓存的ConfigSetting没有的话，再查一下本地数据库（object）
        object : AsyncTask<Void, Void, ConfigSetting>() {
            override fun doInBackground(vararg params: Void): ConfigSetting? {
                var configSetting: ConfigSetting? = ChatSessionDataWrap.getInstance().getSettingCheck(querySetting.mSourceId, querySetting.mBusinessCase)
                if(null != configSetting) {


                    return configSetting
                }

                //如果在本地数据库还查找不到要查询的类型（智能翻译）的话，就查整个session，并把查询的值放入到缓存中
                configSetting = ConfigSettingRepository.getConfigSetting(querySetting.mSourceId, querySetting.mSourceType, querySetting.mBusinessCase)

                if (null != configSetting) {
                    ChatSessionDataWrap.getInstance().insertSettingCheck(configSetting)
                }

                return configSetting

            }

            override fun onPostExecute(result: ConfigSetting?) {
                onGetSetting(result)
            }

        }.executeOnExecutor(DbThreadPoolExecutor.getInstance())



    }


    /**
     * 设置会话配置
     *
     * @param request
     * @param listener
     * */
    @SuppressLint("StaticFieldLeak")
    override fun setConversationSettingRemote(request: ConversionConfigSettingRequest, listener: BaseCallBackNetWorkListener) {
        object : AsyncTask<Void, Void, HttpResult>() {
            override fun doInBackground(vararg params: Void): HttpResult {

                return UserSyncNetService.getInstance().setConversationSetting(AtworkApplicationLike.baseContext, JsonUtil.toJson(request), request.participant?.type, request?.language)
            }

            override fun onPostExecute(result: HttpResult) {
                if(result.isRequestSuccess) {
                    listener.onSuccess()
                    return
                }


                NetworkHttpResultErrorHandler.handleHttpError(result, listener)
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())

    }


    /**
     * 获取服务器最新的会话配置
     *
     * @param participant
     * @param listener
     * */
    @SuppressLint("StaticFieldLeak")
    override fun getConversationSettingRemote(participant: ConversionConfigSettingParticipant, listener: BaseNetWorkListener<ConversionConfigSettingItem>) {
        object : AsyncTask<Void, Void, HttpResult>() {
            override fun doInBackground(vararg params: Void): HttpResult {
                return UserSyncNetService.getInstance().getConversationSetting(AtworkApplicationLike.baseContext, participant.clientId, participant.domainIid, participant.type)
            }

            override fun onPostExecute(result: HttpResult) {
                if(result.isRequestSuccess) {
                    val conversationSettingResponse = result.resultResponse as ConversionConfigSettingResponse
                    listener.onSuccess(conversationSettingResponse.result)
                    return
                }


                NetworkHttpResultErrorHandler.handleHttpError(result, listener)
            }

        }.executeOnExecutor(AsyncTaskThreadPool.getInstance())

    }


    /**
     * 会话是否勿打扰
     * */
    override fun isSessionShield(session: Session, getResult: (Boolean) -> Unit) {
        val querySetting = ConfigSetting(session.identifier, SourceType.valueOf(session.type), BusinessCase.SESSION_SHIELD)

        querySessionSetting(querySetting) {
            if(1 == it?.mValue) {
                getResult(true)
                return@querySessionSetting
            }

            getResult(false)

        }
    }


    /**
     * 会话是否置顶
     * */
    override fun isSessionTop(session: Session, getResult: (Boolean) -> Unit) {
        val querySetting = ConfigSetting(session.identifier, SourceType.valueOf(session.type), BusinessCase.SESSION_TOP)

        querySessionSetting(querySetting) {
            if(1 == it?.mValue) {
                getResult(true)
                return@querySessionSetting
            }

            getResult(false)

        }

    }

    /**
     * 会话是否智能翻译
     * */
    override fun isSessionTranslation(session: Session, getResult: (Boolean) -> Unit) {
        val querySetting = ConfigSetting(session.identifier, SourceType.valueOf(session.type), BusinessCase.SESSION_TRANSLATION)

        querySessionSetting(querySetting) {
            if(1 == it?.mValue) {
                getResult(true)
                return@querySessionSetting
            }

            getResult(false)

        }

    }

}
