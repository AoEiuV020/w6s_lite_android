package com.foreveross.atwork.modules.bugFix.manager

import android.content.Context
import com.foreverht.db.service.W6sBaseRepository
import com.foreverht.db.service.dbHelper.ConfigSettingDBHelper
import com.foreverht.db.service.repository.ConfigSettingRepository
import com.foreverht.db.service.repository.CustomerMessageNoticeRepository
import com.foreverht.db.service.repository.SessionRepository
import com.foreveross.atwork.AtworkApplicationLike
import com.foreveross.atwork.api.sdk.configSetting.conversationSetting.ConversionConfigSettingSyncNetService
import com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model.ConversionConfigSettingItem
import com.foreveross.atwork.api.sdk.configSetting.conversationSetting.model.ConversionConfigSettingParticipant
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.Session
import com.foreveross.atwork.infrastructure.model.SessionTop
import com.foreveross.atwork.infrastructure.model.setting.BusinessCase
import com.foreveross.atwork.infrastructure.plugin.bugFix.IW6sBugFixManager
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo
import com.foreveross.atwork.infrastructure.shared.bugFix.W6sBugFixPersonShareInfo
import com.foreveross.atwork.infrastructure.utils.ListUtil
import com.foreveross.atwork.infrastructure.utils.LogUtil
import com.foreveross.atwork.modules.app.manager.AppManager
import com.foreveross.atwork.manager.OrganizationManager
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap

object W6sBugFixCoreManager: IW6sBugFixManager {



    @JvmStatic
    fun getInstance(): IW6sBugFixManager {
        return this
    }



    /**
     * 强制更新下应用数据
     */
    override fun fixedForcedCheckAppRefresh() {

        val context = AtworkApplicationLike.baseContext


        var orgCodesNeedForcedCheckAppRefresh = W6sBugFixPersonShareInfo.getOrgCodesNeedForcedCheckAppRefresh(context)

        if(null == orgCodesNeedForcedCheckAppRefresh) {
            OrganizationManager.getInstance().queryLoginOrgCodeList {

                orgCodesNeedForcedCheckAppRefresh = HashSet<String>(it)
                W6sBugFixPersonShareInfo.setOrgCodesNeedForcedCheckAppRefresh(context, orgCodesNeedForcedCheckAppRefresh!!)
                forcedCheckAppsUpdate(context, orgCodesNeedForcedCheckAppRefresh!!)

            }

        } else {

            forcedCheckAppsUpdate(context, orgCodesNeedForcedCheckAppRefresh!!)

        }


    }

    /**
     * 指定的组织是否需要做应用刷新检查
     * */
    override fun isNeedForcedCheckAppRefresh(orgCode: String): Boolean {
        val orgCodesNeedForcedCheckAppRefresh = W6sBugFixPersonShareInfo.getOrgCodesNeedForcedCheckAppRefresh(BaseApplicationLike.baseContext)
        if (null != orgCodesNeedForcedCheckAppRefresh && orgCodesNeedForcedCheckAppRefresh.contains(orgCode)) {
            return true
        }

        return false
    }

    private fun forcedCheckAppsUpdate(context: Context, orgCodesNeedForcedCheckAppRefresh: Set<String>) {
        val currentOrg = PersonalShareInfo.getInstance().getCurrentOrg(context)
        if (orgCodesNeedForcedCheckAppRefresh.contains(currentOrg)) {
            AppManager.getInstance().appCheckUpdateController.checkAppsUpdate(currentOrg, null, object : AppManager.CheckAppListUpdateListener {
                override fun networkFail(errorCode: Int, errorMsg: String?) {
                }

                override fun refresh(needUpdate: Boolean) {
                    val orgCodesNeedForcedCheckAppRefreshHashSet = HashSet<String>(orgCodesNeedForcedCheckAppRefresh)
                    orgCodesNeedForcedCheckAppRefreshHashSet.remove(currentOrg)

                    W6sBugFixPersonShareInfo.setOrgCodesNeedForcedCheckAppRefresh(context, orgCodesNeedForcedCheckAppRefreshHashSet)


                }

            })
        }
    }


    /**
     * 此处为了让 setting 表的business_case_也设为关联 key, 敏捷版本因为处于3.14.0, 数据库版本是17, 而最新主
     * 干的数据库版本是20, 为了兼容后续的整个升级流程, 此处不用升级数据库的方法来修改字段, 后续优化升级数据库流程
     * 代码来避免这种情况出现
     * */
    override fun fixedSettingDbPrimaryKeyInMinjieVersion() {
//        if(!CustomerHelper.isMinJie(AtworkApplicationLike.baseContext)) {
//            return
//        }


        if(W6sBugFixPersonShareInfo.hasUpdatedSettingDbPrimaryKeyForBusinessCaseValue(AtworkApplicationLike.baseContext)) {
           return
        }
        try {

            LogUtil.e("setting 表升级")

            if (!ConfigSettingRepository().tableExists(ConfigSettingDBHelper.TABLE_NAME)) {
                val db = W6sBaseRepository.getWritableDatabase()
                db.execSQL(ConfigSettingDBHelper.SQL_EXEC)
            }

            LogUtil.e("setting 表升级完成")


            W6sBugFixPersonShareInfo.setUpdateSettingDbPrimaryKeyForBusinessCaseValue(AtworkApplicationLike.baseContext, true)
        } catch (e: Exception) {
            LogUtil.e("setting 表升级异常")
        }



    }


    /**
     * 把旧版本的session 置顶以及勿打扰迁移到 setting 表去
     * */
    override fun fixedSessionTopAndShieldData(): Boolean {
        val context = AtworkApplicationLike.baseContext

        if(W6sBugFixPersonShareInfo.hasMakeCompatibleForSessionTopAndShield(context)) {
            return true
        }

        val beginTime = System.currentTimeMillis()

        val shieldIdsOldData = CustomerMessageNoticeRepository.getsInstance().shieldIds
        val sessionsOldData = SessionRepository.getInstance().querySessionsLocalTopOrShield(shieldIdsOldData)

        val configSettingsNewData = ChatSessionDataWrap.getInstance().queryAllSessionSettingsLocalSync()

        val requestList = arrayListOf<ConversionConfigSettingItem>()


        sessionsOldData.forEach { session->

            if(SessionTop.LOCAL_TOP == session.top) {

                if(null == configSettingsNewData[session.identifier]?.find { BusinessCase.SESSION_TOP == it.mBusinessCase && 1 == it.mValue }) {
                    val conversionConfigSettingItem = findConversionConfigSettingItem(session, requestList)

                    conversionConfigSettingItem.stickyEnabled = true


                }

            }



            if(shieldIdsOldData.contains(session.identifier)) {
                if(null == configSettingsNewData[session.identifier]?.find { BusinessCase.SESSION_SHIELD == it.mBusinessCase && 1 == it.mValue }) {
                    val conversionConfigSettingItem = findConversionConfigSettingItem(session, requestList)

                    conversionConfigSettingItem.notifyEnabled = false


                }

            }
        }



        if(ListUtil.isEmpty(requestList)) {
            W6sBugFixPersonShareInfo.setMakeCompatibleForSessionTopAndShield(context, true)
            return true

        }

        val httpResult = ConversionConfigSettingSyncNetService.setConversationsSetting(context, requestList)
        if (httpResult.isRequestSuccess) {
            W6sBugFixPersonShareInfo.setMakeCompatibleForSessionTopAndShield(context, true)
            return true

        }


        LogUtil.e("fixedSessionTopAndShieldData duration -> ${System.currentTimeMillis() - beginTime}")

        return false

    }

    private fun findConversionConfigSettingItem(session: Session, requestList: ArrayList<ConversionConfigSettingItem>): ConversionConfigSettingItem {
        var conversionConfigSettingItem = requestList
                .find { it.participant!!.getSessionIdFromClientId() == session.identifier }

        if (null == conversionConfigSettingItem) {
            conversionConfigSettingItem = ConversionConfigSettingItem()
            conversionConfigSettingItem.participant = ConversionConfigSettingParticipant(session)



            requestList.add(conversionConfigSettingItem)

        }
        return conversionConfigSettingItem
    }

}