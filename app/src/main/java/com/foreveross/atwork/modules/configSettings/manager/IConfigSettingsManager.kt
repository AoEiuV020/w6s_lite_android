package com.foreveross.atwork.modules.configSettings.manager

import android.content.Context
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener
import com.foreveross.atwork.api.sdk.BaseNetWorkListener
import com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model.ConversionConfigSettingItem
import com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model.ConversionConfigSettingParticipant
import com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model.request.ConversionConfigSettingRequest
import com.foreveross.atwork.api.sdk.configSetting.userSetting.model.UserConfigSettings
import com.foreveross.atwork.infrastructure.model.Session
import com.foreveross.atwork.infrastructure.model.setting.BusinessCase
import com.foreveross.atwork.infrastructure.model.setting.ConfigSetting
import com.foreveross.atwork.infrastructure.model.setting.SourceType
import com.foreveross.atwork.infrastructure.newmessage.post.notify.conversation.ConversationNotifyMessage
import com.foreveross.atwork.infrastructure.newmessage.post.notify.user.UserNotifyMessage
import com.foreveross.atwork.manager.listener.BaseQueryListener

interface IConfigSettingsManager {

    fun clear()

    fun getConfigSettingFromDb(sourceId: String, sourceType: SourceType, businessCase: BusinessCase, listener: BaseQueryListener<ConfigSetting?>)

    fun insertOrUpdateConfigSetting(configSetting: ConfigSetting)


    /**
     * 同步会话跟用户的设置项(消息勿扰, 群聊助手等)
     * */
    fun syncConfigSettings()


    /**
     * 提交用户设置(群聊助手等)
     *
     * @param context
     * @param userSettings
     * @param listener
     * */
    fun setUserSettings(context: Context, userSettings: UserConfigSettings, listener: BaseCallBackNetWorkListener)

    /**
     * 用户设置更改的通知处理
     * */
    fun receiveSettingChangedUserNotifyMessage(conversationNotifyMessage: UserNotifyMessage, online: Boolean)


    /**
     * 用户设置更改的通知处理
     * */
    fun receiveConversationSettingChangedNotifyMessage(userNotifyMessage: ConversationNotifyMessage, online: Boolean)


    fun setSessionSettingLocal(configSetting: ConfigSetting, finishHandle: ((Boolean)-> Unit)? = null)

    fun setSessionSettingLocalSync(configSettings: List<ConfigSetting>): Boolean

    fun setSessionSettingLocal(configSettings: List<ConfigSetting>, finishHandle: ((Boolean)-> Unit)? = null)


    fun querySessionSetting(querySetting: ConfigSetting, onGetSetting:(ConfigSetting?) -> Unit )



    /**
     * 设置会话配置(远程)
     *
     * @param request
     * @param listener
     * */
    fun setConversationSettingRemote(request: ConversionConfigSettingRequest, listener: BaseCallBackNetWorkListener)


    /**
     * 获取服务器最新的会话配置
     *
     * @param participant
     * @param listener
     * */
    fun getConversationSettingRemote(participant: ConversionConfigSettingParticipant, listener: BaseNetWorkListener<ConversionConfigSettingItem>)

    /**
     * 会话是否勿打扰
     * */
    fun isSessionShield(session: Session, getResult: ((Boolean)-> Unit))


    /**
     * 会话是否置顶
     * */
    fun isSessionTop(session: Session, getResult: ((Boolean)-> Unit))

    /**
     * 会话是否智能翻译
     * */
    fun isSessionTranslation(session: Session, getResult: ((Boolean)-> Unit))
}